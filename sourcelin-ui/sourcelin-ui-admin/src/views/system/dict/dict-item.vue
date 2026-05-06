<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input
            v-model="queryParams.dictLabel"
            placeholder="请输入字典标签"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <DictSelect
            v-model="queryParams.status"
            code="sys_normal_disable"
            clearable
            placeholder="全部"
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="never" class="table-section">
      <div class="table-section__toolbar">
        <div class="table-section__toolbar--actions">
          <el-button
            v-hasPerm="['system:dict:add']"
            type="success"
            :icon="Plus"
            @click="openDialog()"
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
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典标签" prop="dictLabel" min-width="160" />
        <el-table-column label="字典键值" prop="dictValue" min-width="160" />
        <el-table-column label="排序" prop="dictSort" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <DictTag v-model="row.status" code="sys_normal_disable" />
          </template>
        </el-table-column>
        <el-table-column label="样式" width="110">
          <template #default="{ row }">
            <el-tag :type="toElTagType(row.listClass)">
              {{ row.dictLabel }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" align="center" width="220">
          <template #default="{ row }">
            <el-button
              v-hasPerm="['system:dict:edit']"
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click.stop="openDialog(row)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:dict:remove']"
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click.stop="handleDelete(row.dictCode)"
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
      width="620px"
      @close="closeDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="108px">
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="formData.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="formData.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <DictSelect v-model="formData.status" code="sys_normal_disable" />
        </el-form-item>
        <el-form-item label="排序" prop="dictSort">
          <el-input-number v-model="formData.dictSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="样式编码" prop="listClass">
          <el-select v-model="formData.listClass" placeholder="请选择样式编码">
            <el-option v-for="item in listClassOptions" :key="item.value" :value="item.value">
              <div class="flex items-center gap-8px">
                <span>{{ item.value }} - {{ item.label }}</span>
                <el-tag :type="toElTagType(item.value)">
                  {{ formData.dictLabel || "预览" }}
                </el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <DictSelect v-model="formData.isDefault" code="sys_yes_no" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
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
import DictAPI from "@/api/system/dict";
import type { DictItemQueryParams, DictItem, DictItemForm } from "@/types/api";
import type { FormInstance, FormRules, TagProps } from "element-plus";
import { Delete, Edit, Plus, Refresh, Search } from "@element-plus/icons-vue";

const route = useRoute();
const dictType = ref(String(route.query.dictType ?? ""));

const queryFormRef = ref<FormInstance>();
const dataFormRef = ref<FormInstance>();

const queryParams = reactive<DictItemQueryParams>({
  page: 1,
  pageSize: 10,
});

const tableData = ref<DictItem[]>([]);
const total = ref(0);
const loading = ref(false);
const ids = ref<string[]>([]);

const dialogState = reactive({
  title: "",
  visible: false,
});

const formData = reactive<DictItemForm>({
  dictType: "",
  dictSort: 1,
  status: "0",
  listClass: "N",
  isDefault: "N",
});

const listClassOptions = [
  { value: "N", label: "默认" },
  { value: "P", label: "主要" },
  { value: "S", label: "成功" },
  { value: "W", label: "警告" },
  { value: "I", label: "信息" },
  { value: "D", label: "危险" },
] as const;

const rules: FormRules = {
  dictLabel: [{ required: true, message: "请输入字典标签", trigger: "blur" }],
  dictValue: [{ required: true, message: "请输入字典键值", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "change" }],
};

function toElTagType(code?: string): TagProps["type"] {
  switch (code) {
    case "P":
      return "primary";
    case "S":
      return "success";
    case "W":
      return "warning";
    case "I":
      return "info";
    case "D":
      return "danger";
    case "N":
    default:
      return "info";
  }
}

function fetchData(): void {
  if (!dictType.value) {
    tableData.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  DictAPI.getDictItemPage(dictType.value, queryParams)
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

function handleSelectionChange(selection: DictItem[]): void {
  ids.value = selection.map((item) => item.dictCode);
}

function resetForm(): void {
  dataFormRef.value?.clearValidate();
  formData.dictCode = undefined;
  formData.dictType = dictType.value;
  formData.dictLabel = undefined;
  formData.dictValue = undefined;
  formData.status = "0";
  formData.dictSort = 1;
  formData.listClass = "N";
  formData.isDefault = "N";
  formData.remark = undefined;
}

function openDialog(row?: DictItem): void {
  resetForm();
  dialogState.visible = true;
  dialogState.title = row ? "编辑字典值" : "新增字典值";
  if (!row?.dictCode) return;
  DictAPI.getDictItemFormData(dictType.value, row.dictCode).then((data) => {
    Object.assign(formData, data);
  });
}

function handleSubmit(): void {
  dataFormRef.value?.validate((isValid) => {
    if (!isValid) return;
    loading.value = true;
    formData.dictType = dictType.value;
    const dictCode = formData.dictCode;
    if (dictCode) {
      DictAPI.updateDictItem(dictType.value, dictCode, formData)
        .then(() => {
          ElMessage.success("修改成功");
          closeDialog();
          handleQuery();
        })
        .finally(() => (loading.value = false));
      return;
    }
    DictAPI.createDictItem(dictType.value, formData)
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

function handleDelete(dictCode?: string): void {
  const targetIds = dictCode ? dictCode : ids.value.join(",");
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
      DictAPI.deleteDictItems(dictType.value, targetIds).then(() => {
        ElMessage.success("删除成功");
        handleResetQuery();
      });
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

onMounted(() => {
  handleQuery();
});
</script>
