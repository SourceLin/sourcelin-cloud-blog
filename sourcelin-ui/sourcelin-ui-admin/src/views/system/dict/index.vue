<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="字典名称" prop="dictName">
          <el-input
            v-model="queryParams.dictName"
            placeholder="请输入字典名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="字典类型" prop="dictType">
          <el-input
            v-model="queryParams.dictType"
            placeholder="请输入字典类型"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="table-section">
      <div class="table-section__toolbar">
        <div class="table-section__toolbar--actions">
          <el-button
            v-hasPerm="['system:dict:add']"
            type="success"
            :icon="Plus"
            @click="openCreate"
          >
            新增
          </el-button>
          <el-button
            v-hasPerm="['system:dict:remove']"
            type="danger"
            :disabled="ids.length === 0"
            :icon="Delete"
            @click="handleDelete()"
          >
            删除
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        highlight-current-row
        :data="tableData"
        border
        class="table-section__content"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典名称" prop="dictName" min-width="180" />
        <el-table-column label="字典类型" prop="dictType" min-width="180" />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <DictTag v-model="row.status" code="sys_normal_disable" />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="180" />
        <el-table-column fixed="right" label="操作" align="center" width="240">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click.stop="openDictData(row)">
              <template #icon>
                <Collection />
              </template>
              字典数据
            </el-button>
            <el-button
              v-hasPerm="['system:dict:edit']"
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click.stop="openEdit(row.dictId)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:dict:remove']"
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click.stop="handleDelete(row.dictId)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        @pagination="fetchData"
      />
    </el-card>

    <el-dialog
      v-model="dialogState.visible"
      :title="dialogState.title"
      width="520px"
      @close="closeDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="formData.dictName" placeholder="请输入字典名称" />
        </el-form-item>

        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="formData.dictType" placeholder="请输入字典类型" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <DictSelect v-model="formData.status" code="sys_normal_disable" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Dict",
  inheritAttrs: false,
});

import DictAPI from "@/api/system/dict";
import type { DictTypeQueryParams, DictTypeItem, DictTypeForm } from "@/types/api";
import type { FormInstance, FormRules } from "element-plus";
import router from "@/router";
import { Delete, Edit, Plus, Refresh, Search } from "@element-plus/icons-vue";

const queryFormRef = ref<FormInstance>();
const dataFormRef = ref<FormInstance>();

const queryParams = reactive<DictTypeQueryParams>({
  page: 1,
  pageSize: 10,
});

const tableData = ref<DictTypeItem[]>([]);
const total = ref(0);
const loading = ref(false);
const ids = ref<string[]>([]);

const dialogState = reactive({
  title: "",
  visible: false,
});

const formData = reactive<DictTypeForm>({
  status: "0",
});

const rules: FormRules = {
  dictName: [{ required: true, message: "请输入字典名称", trigger: "blur" }],
  dictType: [{ required: true, message: "请输入字典类型", trigger: "blur" }],
};

function fetchData(): void {
  loading.value = true;
  DictAPI.getPage(queryParams)
    .then((data) => {
      tableData.value = data.items;
      total.value = data.total ?? 0;
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery(): void {
  queryParams.page = 1;
  fetchData();
}

function handleResetQuery(): void {
  queryFormRef.value?.resetFields();
  queryParams.page = 1;
  fetchData();
}

function handleSelectionChange(selection: DictTypeItem[]): void {
  ids.value = selection.map((item) => item.dictId);
}

function resetForm(): void {
  dataFormRef.value?.clearValidate();
  formData.dictId = undefined;
  formData.dictName = undefined;
  formData.dictType = undefined;
  formData.status = "0";
  formData.remark = undefined;
}

function openCreate(): void {
  resetForm();
  dialogState.visible = true;
  dialogState.title = "新增字典";
}

function openEdit(dictId: string): void {
  resetForm();
  dialogState.visible = true;
  dialogState.title = "修改字典";
  DictAPI.getFormData(dictId).then((data) => {
    Object.assign(formData, data);
  });
}

function handleSubmit(): void {
  dataFormRef.value?.validate((isValid) => {
    if (!isValid) return;
    loading.value = true;
    const dictId = formData.dictId;
    if (dictId) {
      DictAPI.update(dictId, formData)
        .then(() => {
          ElMessage.success("修改成功");
          closeDialog();
          handleQuery();
        })
        .finally(() => (loading.value = false));
      return;
    }
    DictAPI.create(formData)
      .then(() => {
        ElMessage.success("新增成功");
        closeDialog();
        handleQuery();
      })
      .finally(() => (loading.value = false));
  });
}

function closeDialog(): void {
  dialogState.visible = false;
  dataFormRef.value?.resetFields();
  resetForm();
}

function handleDelete(dictId?: string): void {
  const targetIds = dictId ? dictId : ids.value.join(",");
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
      DictAPI.deleteByIds(targetIds).then(() => {
        ElMessage.success("删除成功");
        handleResetQuery();
      });
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

function openDictData(row: DictTypeItem): void {
  try {
    const route = router.resolve({
      name: "DictItem",
      query: { dictType: row.dictType, title: `【${row.dictName}】字典数据` },
    });
    if (route.matched.length === 0) {
      ElMessage.error("路由未注册，请刷新页面后重试");
      return;
    }
    router.push(route);
  } catch (error) {
    console.error("路由跳转失败:", error);
    ElMessage.error("页面跳转失败，请刷新页面后重试");
  }
}

onMounted(() => {
  handleQuery();
});
</script>
