<template>
  <div class="app-container config-page">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="关键字" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="请输入配置键\\配置名称"
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
            v-hasPerm="['system:config:add']"
            type="success"
            :icon="Plus"
            @click="openDialog()"
          >
            新增
          </el-button>
          <el-button
            v-hasPerm="['system:config:refresh']"
            type="primary"
            plain
            :icon="RefreshLeft"
            @click="refreshCache"
          >
            刷新缓存
          </el-button>
        </div>
      </div>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        class="table-section__content"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column key="configName" label="配置名称" prop="configName" min-width="100" />
        <el-table-column key="configKey" label="配置项" prop="configKey" min-width="100" />
        <el-table-column key="configValue" label="配置值" prop="configValue" min-width="100" />
        <el-table-column key="remark" label="描述" prop="remark" min-width="100" />
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
              v-hasPerm="['system:config:edit']"
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="openDialog(scope.row.id)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:config:remove']"
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row.id)"
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
      width="500px"
      @close="closeDialog"
    >
      <el-form
        ref="dataFormRef"
        :model="formData"
        :rules="rules"
        label-suffix=":"
        label-width="100px"
      >
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="formData.configName" placeholder="请输入配置名称" :maxlength="50" />
        </el-form-item>
        <el-form-item label="配置项" prop="configKey">
          <el-input v-model="formData.configKey" placeholder="请输入配置键" :maxlength="50" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="formData.configValue" placeholder="请输入配置值" :maxlength="100" />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input
            v-model="formData.remark"
            :rows="4"
            :maxlength="100"
            show-word-limit
            type="textarea"
            placeholder="请输入描述"
          />
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
  name: "Config",
  inheritAttrs: false,
});

import { useDebounceFn } from "@vueuse/core";
import { Delete, Edit, Plus, Refresh, RefreshLeft, Search } from "@element-plus/icons-vue";
import ConfigAPI from "@/api/system/config";
import type { ConfigForm, ConfigItem, ConfigQueryParams } from "@/types/api";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";

const queryFormRef = ref<FormInstance>();
const dataFormRef = ref<FormInstance>();

const queryParams = reactive<ConfigQueryParams>({
  page: 1,
  pageSize: 10,
  keywords: "",
});

const pageData = ref<ConfigItem[]>([]);
const total = ref(0);
const loading = ref(false);
const selectIds = ref<string[]>([]);

const dialogState = reactive({
  title: "",
  visible: false,
});

const formData = reactive<ConfigForm>({
  id: undefined,
  configName: "",
  configKey: "",
  configValue: "",
  remark: "",
});

const rules: FormRules = {
  configName: [{ required: true, message: "请输入系统配置名称", trigger: "blur" }],
  configKey: [{ required: true, message: "请输入系统配置编码", trigger: "blur" }],
  configValue: [{ required: true, message: "请输入系统配置值", trigger: "blur" }],
};

function fetchData(): void {
  loading.value = true;
  ConfigAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.items;
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

function handleSelectionChange(selection: ConfigItem[]): void {
  selectIds.value = selection.map((item) => item.id).filter(Boolean) as string[];
}

function openDialog(id?: string): void {
  dialogState.visible = true;
  if (id) {
    dialogState.title = "修改系统配置";
    ConfigAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialogState.title = "新增系统配置";
    formData.id = undefined;
  }
}

const refreshCache = useDebounceFn(() => {
  ConfigAPI.refreshCache().then(() => {
    ElMessage.success("刷新成功");
  });
}, 1000);

function handleSubmit(): void {
  dataFormRef.value?.validate((valid) => {
    if (!valid) return;

    loading.value = true;
    const id = formData.id;
    if (id) {
      ConfigAPI.update(id, formData)
        .then(() => {
          ElMessage.success("修改成功");
          closeDialog();
          handleResetQuery();
        })
        .finally(() => (loading.value = false));
      return;
    }

    ConfigAPI.create(formData)
      .then(() => {
        ElMessage.success("新增成功");
        closeDialog();
        handleResetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

function closeDialog(): void {
  dialogState.visible = false;
  dataFormRef.value?.resetFields();
  dataFormRef.value?.clearValidate();
  formData.id = undefined;
}

function handleDelete(id: string): void {
  ElMessageBox.confirm("确认删除该项配置?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    ConfigAPI.deleteById(id)
      .then(() => {
        ElMessage.success("删除成功");
        handleResetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

onMounted(() => {
  handleQuery();
});
</script>

<style scoped lang="scss">
.config-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
