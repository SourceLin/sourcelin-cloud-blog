<template>
  <div v-loading="pageLoading" class="app-container config-page">
    <el-tabs v-model="activeTab" class="config-tabs">
      <!-- 1. 图形化参数设置工作台 -->
      <el-tab-pane label="图形化配置工作台" name="gui">
        <div class="gui-container">
          <el-tabs tab-position="left" class="gui-sidebar-tabs">
            <!-- 安全与自助策略 -->
            <el-tab-pane label="安全与自助策略">
              <div class="gui-pane-content">
                <el-form :model="guiForm" label-width="160px" class="gui-form">
                  <!-- 子卡片 1：准入规则 -->
                  <div class="settings-sub-card">
                    <div class="settings-sub-card__title">访客与注册准入规则</div>
                    <el-form-item>
                      <template #label>
                        <span>
                          用户自助注册
                          <el-tooltip
                            content="是否允许访客在博客及管理登录页自助注册账号。"
                            placement="top"
                          >
                            <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                          </el-tooltip>
                        </span>
                      </template>
                      <el-switch v-model="guiForm.registerUser" />
                    </el-form-item>
                  </div>

                  <!-- 子卡片 2：密码安全 -->
                  <div class="settings-sub-card">
                    <div class="settings-sub-card__title">系统密码安全策略</div>
                    <el-form-item>
                      <template #label>
                        <span>
                          强制修改初始密码
                          <el-tooltip
                            content="开启后，若账号从未更新过初始密码，登录系统后将弹出修改密码引导。"
                            placement="top"
                          >
                            <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                          </el-tooltip>
                        </span>
                      </template>
                      <el-radio-group v-model="guiForm.initPasswordModify">
                        <el-radio value="0">关闭提示</el-radio>
                        <el-radio value="1">开启强改提示</el-radio>
                      </el-radio-group>
                    </el-form-item>

                    <el-form-item>
                      <template #label>
                        <span>
                          账号初始密码
                          <el-tooltip content="系统管理员新增账号时的缺省密码。" placement="top">
                            <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                          </el-tooltip>
                        </span>
                      </template>
                      <el-input
                        v-model="guiForm.initPassword"
                        placeholder="如 123456"
                        style="max-width: 320px"
                        clearable
                      />
                    </el-form-item>

                    <el-form-item>
                      <template #label>
                        <span>
                          密码更新周期 (天)
                          <el-tooltip
                            content="强制更新密码的周期，设为 0 表示永不过期。"
                            placement="top"
                          >
                            <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                          </el-tooltip>
                        </span>
                      </template>
                      <el-input-number
                        v-model="guiForm.passwordValidateDays"
                        :min="0"
                        :max="365"
                        style="max-width: 180px"
                      />
                    </el-form-item>
                  </div>

                  <!-- 子卡片 3：黑名单拦截 -->
                  <div class="settings-sub-card">
                    <div class="settings-sub-card__title">访问流量限制</div>
                    <el-form-item>
                      <template #label>
                        <span>
                          登录 IP 黑名单
                          <el-tooltip
                            content="拦截匹配指定规则的 IP 登录博客后台。"
                            placement="top"
                          >
                            <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                          </el-tooltip>
                        </span>
                      </template>
                      <el-input
                        v-model="guiForm.blackIPList"
                        type="textarea"
                        :rows="3"
                        placeholder="设置登录IP黑名单限制，多个以分号分隔，支持通配符（如 192.168.1.*; 10.0.0.*）"
                        style="max-width: 480px"
                      />
                    </el-form-item>
                  </div>
                </el-form>
              </div>
            </el-tab-pane>

            <!-- 界面与皮肤外观 -->
            <el-tab-pane label="界面皮肤与外观">
              <div class="gui-pane-content">
                <el-form :model="guiForm" label-width="160px" class="gui-form">
                  <!-- 子卡片 1：主题色彩 -->
                  <div class="settings-sub-card">
                    <div class="settings-sub-card__title">系统色彩与主题</div>
                    <el-form-item label="主框架默认皮肤">
                      <el-select v-model="guiForm.skinName" style="max-width: 320px">
                        <el-option label="默认蓝色 (skin-blue)" value="skin-blue" />
                        <el-option label="清新绿色 (skin-green)" value="skin-green" />
                        <el-option label="高雅紫色 (skin-purple)" value="skin-purple" />
                        <el-option label="烈焰红色 (skin-red)" value="skin-red" />
                        <el-option label="明朗黄色 (skin-yellow)" value="skin-yellow" />
                      </el-select>
                    </el-form-item>

                    <el-form-item label="侧边栏主题颜色">
                      <el-select v-model="guiForm.sideTheme" style="max-width: 320px">
                        <el-option label="经典深色 (theme-dark)" value="theme-dark" />
                        <el-option label="极简浅色 (theme-light)" value="theme-light" />
                      </el-select>
                    </el-form-item>
                  </div>

                  <!-- 子卡片 2：UI 策略配置 -->
                  <div class="settings-sub-card">
                    <div class="settings-sub-card__title">UI 高级运行策略</div>
                    <el-form-item>
                      <template #label>
                        <span>
                          UI 默认配置 JSON
                          <el-tooltip
                            content="配置管理后台 UI 的初始默认配置 JSON 串。"
                            placement="top"
                          >
                            <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                          </el-tooltip>
                        </span>
                      </template>
                      <el-input
                        v-model="guiForm.uiDefaults"
                        type="textarea"
                        :rows="4"
                        placeholder='{"themePreference":"system","layout":"left"...}'
                        style="max-width: 480px"
                      />
                    </el-form-item>

                    <el-form-item label="UI 运行策略 JSON">
                      <el-input
                        v-model="guiForm.uiPolicy"
                        type="textarea"
                        :rows="3"
                        style="max-width: 480px"
                      />
                    </el-form-item>

                    <el-form-item label="UI 主题色预设">
                      <el-input
                        v-model="guiForm.uiPresets"
                        type="textarea"
                        :rows="2"
                        style="max-width: 480px"
                      />
                    </el-form-item>
                  </div>
                </el-form>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div class="gui-form-actions" style="margin-left: 248px">
          <el-button type="primary" :loading="guiSubmitting" @click="handleGuiSubmit">
            保存修改
          </el-button>
          <el-button @click="fetchData">放弃更改</el-button>
        </div>
      </el-tab-pane>

      <!-- 2. 全量元数据表格维护 -->
      <el-tab-pane label="元数据全量表" name="table">
        <div class="filter-section">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="关键字" prop="keywords">
              <el-input
                v-model="queryParams.keywords"
                placeholder="请输入配置键或名称"
                clearable
                style="width: 240px"
                @keyup.enter="handleQuery"
              />
            </el-form-item>

            <el-form-item class="search-buttons">
              <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
              <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="table-section" style="margin-top: 18px">
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
            style="width: 100%"
          >
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column key="configName" label="配置名称" prop="configName" min-width="120" />
            <el-table-column key="configKey" label="配置项" prop="configKey" min-width="120" />
            <el-table-column key="configValue" label="配置值" prop="configValue" min-width="120" />
            <el-table-column
              key="remark"
              label="描述"
              prop="remark"
              min-width="160"
              show-overflow-tooltip
            />
            <el-table-column fixed="right" label="操作" width="160" align="center">
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
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 动态配置新增与编辑模态弹窗 -->
    <el-dialog
      v-model="dialogState.visible"
      :title="dialogState.title"
      width="520px"
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
          <el-input v-model="formData.configValue" placeholder="请输入配置值" :maxlength="200" />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input
            v-model="formData.remark"
            :rows="3"
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
import { ref, reactive, computed, onMounted } from "vue";
import { useDebounceFn } from "@vueuse/core";
import {
  Delete,
  Edit,
  Plus,
  Refresh,
  RefreshLeft,
  Search,
  QuestionFilled,
} from "@element-plus/icons-vue";
import ConfigAPI from "@/api/system/config";
import type { ConfigForm, ConfigItem, ConfigQueryParams } from "@/types/api";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import "@/styles/config.scss";

defineOptions({
  name: "Config",
  inheritAttrs: false,
});

const activeTab = ref("gui");
const queryFormRef = ref<FormInstance>();
const dataFormRef = ref<FormInstance>();

const queryParams = reactive<ConfigQueryParams>({
  page: 1,
  pageSize: 10,
  keywords: "",
});

const pageData = ref<ConfigItem[]>([]);
const guiItems = ref<ConfigItem[]>([]);
const total = ref(0);
const loading = ref(false);

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

// GUI 表单映射响应式数据
const guiForm = reactive({
  registerUser: false,
  initPasswordModify: "0",
  initPassword: "",
  passwordValidateDays: 0,
  blackIPList: "",
  skinName: "skin-blue",
  sideTheme: "theme-dark",
  uiDefaults: "",
  uiPolicy: "",
  uiPresets: "",
});

const guiSubmitting = ref(false);

const pageLoading = computed(() => loading.value || guiSubmitting.value);

const rules: FormRules = {
  configName: [{ required: true, message: "请输入系统配置名称", trigger: "blur" }],
  configKey: [{ required: true, message: "请输入系统配置键", trigger: "blur" }],
  configValue: [{ required: true, message: "请输入系统配置值", trigger: "blur" }],
};

function mapPageDataToGuiForm() {
  guiItems.value.forEach((item) => {
    const key = item.configKey;
    const val = item.configValue;
    if (!key) return;

    if (key === "sys.account.registerUser") {
      guiForm.registerUser = val === "true";
    } else if (key === "sys.account.initPasswordModify") {
      guiForm.initPasswordModify = val || "0";
    } else if (key === "sys.user.initPassword") {
      guiForm.initPassword = val || "";
    } else if (key === "sys.account.passwordValidateDays") {
      guiForm.passwordValidateDays = Number(val ?? 0) || 0;
    } else if (key === "sys.login.blackIPList") {
      guiForm.blackIPList = val || "";
    } else if (key === "sys.index.skinName") {
      guiForm.skinName = val || "skin-blue";
    } else if (key === "sys.index.sideTheme") {
      guiForm.sideTheme = (val || "theme-dark").replace(/"/g, "");
    } else if (key === "sys.admin.ui.defaults") {
      guiForm.uiDefaults = val || "";
    } else if (key === "sys.admin.ui.policy") {
      guiForm.uiPolicy = val || "";
    } else if (key === "sys.admin.ui.presets") {
      guiForm.uiPresets = val || "";
    }
  });
}

async function loadGuiData(): Promise<void> {
  const first = await ConfigAPI.getPage({ page: 1, pageSize: 100 });
  const items = [...first.items];
  for (let page = 2; page <= first.totalPages; page++) {
    const result = await ConfigAPI.getPage({ page, pageSize: 100 });
    items.push(...result.items);
  }
  guiItems.value = items;
  mapPageDataToGuiForm();
}

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

// 核心图形表单一键回写并刷新系统缓存
async function handleGuiSubmit() {
  guiSubmitting.value = true;
  try {
    const updateMap: Record<string, string> = {
      "sys.account.registerUser": String(guiForm.registerUser),
      "sys.account.initPasswordModify": guiForm.initPasswordModify,
      "sys.user.initPassword": guiForm.initPassword,
      "sys.account.passwordValidateDays": String(guiForm.passwordValidateDays),
      "sys.login.blackIPList": guiForm.blackIPList,
      "sys.index.skinName": guiForm.skinName,
      "sys.index.sideTheme": `"${guiForm.sideTheme}"`,
      "sys.admin.ui.defaults": guiForm.uiDefaults,
      "sys.admin.ui.policy": guiForm.uiPolicy,
      "sys.admin.ui.presets": guiForm.uiPresets,
    };

    const promises = Object.keys(updateMap).map(async (key) => {
      const matched = guiItems.value.find((item) => item.configKey === key);
      if (matched && matched.id) {
        const cleanOldVal = (matched.configValue || "").replace(/"/g, "");
        const cleanNewVal = updateMap[key].replace(/"/g, "");
        if (cleanOldVal === cleanNewVal) {
          return;
        }

        await ConfigAPI.update(matched.id, {
          id: matched.id,
          configName: matched.configName,
          configKey: key,
          configValue: updateMap[key],
          remark: matched.remark,
        });
      }
    });

    await Promise.all(promises);
    ElMessage.success("系统参数配置已批量保存");
    await ConfigAPI.refreshCache();
    ElMessage.success("缓存刷新同步完成");
    fetchData();
  } catch (err: unknown) {
    console.error("更新核心配置失败", err);
    ElMessage.error(
      (err instanceof Error ? err.message : "") || "部分配置保存失败，请检查网络后重试"
    );
  } finally {
    guiSubmitting.value = false;
  }
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
    ElMessage.success("系统缓存刷新成功");
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
  void loadGuiData();
  fetchData();
});
</script>
