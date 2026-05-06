<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="部门名称" prop="deptName">
          <el-input
            v-model="queryParams.deptName"
            placeholder="请输入部门名称"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="部门状态" prop="status">
          <DictSelect
            v-model="queryParams.status"
            code="sys_normal_disable"
            placeholder="全部"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button class="filter-item" type="primary" :icon="Search" @click="handleQuery">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="table-section">
      <div class="table-section__toolbar">
        <div class="table-section__toolbar--actions">
          <el-button
            v-hasPerm="['system:dept:add']"
            type="success"
            :icon="Plus"
            @click="openDialog()"
          >
            新增
          </el-button>
          <el-button
            v-hasPerm="['system:dept:remove']"
            type="danger"
            :disabled="selectIds.length === 0"
            :icon="Delete"
            @click="handleDelete()"
          >
            删除
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="deptList"
        row-key="deptId"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        class="table-section__content"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="deptName" label="部门名称" min-width="220" />
        <el-table-column prop="leader" label="负责人" width="140" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="orderNum" label="排序" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <DictTag v-model="row.status" code="sys_normal_disable" />
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" align="left" width="220">
          <template #default="{ row }">
            <el-button
              v-hasPerm="['system:dept:add']"
              type="primary"
              link
              size="small"
              :icon="Plus"
              @click.stop="openDialog(row.deptId, undefined)"
            >
              新增
            </el-button>
            <el-button
              v-hasPerm="['system:dept:edit']"
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click.stop="openDialog(row.parentId, row.deptId)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:dept:remove']"
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click.stop="handleDelete(row.deptId)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogState.visible"
      :title="dialogState.title"
      width="680px"
      @closed="closeDialog"
    >
      <el-form ref="deptFormRef" :model="formData" :rules="rules" label-width="92px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            placeholder="选择上级部门"
            :data="deptOptions"
            filterable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="formData.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number
            v-model="formData.orderNum"
            controls-position="right"
            style="width: 120px"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="部门状态" prop="status">
          <DictSelect v-model="formData.status" code="sys_normal_disable" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确定</el-button>
          <el-button @click="closeDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Dept",
  inheritAttrs: false,
});

import DeptAPI from "@/api/system/dept";
import type { DeptItem, DeptForm, DeptQueryParams, OptionItem } from "@/types/api";
import type { FormInstance, FormRules } from "element-plus";
import { Delete, Edit, Plus, Refresh, Search } from "@element-plus/icons-vue";

const queryFormRef = ref<FormInstance>();
const deptFormRef = ref<FormInstance>();

const queryParams = reactive<DeptQueryParams>({});

const deptList = ref<DeptItem[]>([]);
const deptOptions = ref<OptionItem[]>([]);
const loading = ref(false);
const selectIds = ref<string[]>([]);

const dialogState = reactive({
  title: "",
  visible: false,
});

const formData = reactive<DeptForm>({
  status: "0",
  parentId: "0",
  orderNum: 1,
});

const rules: FormRules = {
  parentId: [{ required: true, message: "上级部门不能为空", trigger: "change" }],
  deptName: [{ required: true, message: "部门名称不能为空", trigger: "blur" }],
  orderNum: [{ required: true, message: "显示排序不能为空", trigger: "blur" }],
};

function fetchData(): void {
  loading.value = true;
  DeptAPI.getList(queryParams)
    .then((data) => {
      deptList.value = data;
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery(): void {
  fetchData();
}

function handleResetQuery(): void {
  queryFormRef.value?.resetFields();
  fetchData();
}

function handleSelectionChange(selection: DeptItem[]): void {
  selectIds.value = selection.map((item) => item.deptId).filter(Boolean) as string[];
}

async function openDialog(parentId?: string, deptId?: string): Promise<void> {
  const data = await DeptAPI.getOptions();
  deptOptions.value = [
    {
      value: "0",
      label: "顶级部门",
      children: data,
    },
  ];

  dialogState.visible = true;
  if (deptId) {
    dialogState.title = "修改部门";
    DeptAPI.getFormData(deptId).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialogState.title = "新增部门";
    formData.parentId = parentId || "0";
  }
}

function handleSubmit(): void {
  deptFormRef.value?.validate((valid) => {
    if (!valid) return;
    loading.value = true;
    const deptId = formData.deptId;
    if (deptId) {
      DeptAPI.update(deptId, formData)
        .then(() => {
          ElMessage.success("修改成功");
          closeDialog();
          fetchData();
        })
        .finally(() => (loading.value = false));
      return;
    }
    DeptAPI.create(formData)
      .then(() => {
        ElMessage.success("新增成功");
        closeDialog();
        fetchData();
      })
      .finally(() => (loading.value = false));
  });
}

function handleDelete(deptId?: string): void {
  const targetIds = deptId ? deptId : selectIds.value.join(",");

  if (!targetIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => {
      loading.value = true;
      DeptAPI.deleteByIds(targetIds)
        .then(() => {
          ElMessage.success("删除成功");
          handleResetQuery();
        })
        .finally(() => (loading.value = false));
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

function closeDialog(): void {
  dialogState.visible = false;
  deptFormRef.value?.resetFields();
  deptFormRef.value?.clearValidate();
  formData.deptId = undefined;
  formData.parentId = "0";
  formData.deptName = undefined;
  formData.orderNum = 1;
  formData.status = "0";
  formData.leader = undefined;
  formData.phone = undefined;
  formData.email = undefined;
  formData.remark = undefined;
}

onMounted(() => {
  fetchData();
});
</script>
