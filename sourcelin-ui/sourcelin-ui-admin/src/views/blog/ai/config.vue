<template>
  <div class="app-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" size="default">
        <el-form-item label="能力编码">
          <el-input v-model="queryParams.abilityCode" placeholder="如 article_summary" clearable />
        </el-form-item>
        <el-form-item label="Provider">
          <el-select
            v-model="queryParams.providerType"
            placeholder="全部"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="pt in providerTypes"
              :key="pt.value"
              :label="pt.label"
              :value="pt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.enabled"
            placeholder="全部"
            clearable
            style="width: 100px"
          >
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="openCreate">新增能力</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="abilityCode" label="能力编码" width="180" />
      <el-table-column prop="abilityName" label="能力名称" width="150" />
      <el-table-column prop="providerType" label="Provider" width="130" align="center">
        <template #default="{ row }">
          <el-tag :type="providerTagType(row.providerType)" size="small">
            {{ providerLabel(row.providerType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="providerAppId" label="模型/AppID" width="160" />
      <el-table-column prop="endpointUrl" label="端点地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="invokeMode" label="调用模式" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="invokeModeTagType(row.invokeMode)" size="small">
            {{ row.invokeMode }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" width="70" align="center">
        <template #default="{ row }">
          <el-tag :type="row.enabled === '1' ? 'success' : 'info'" size="small">
            {{ row.enabled === "1" ? "启用" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      layout="total, prev, pager, next, sizes"
      @change="fetchList"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="700px"
      destroy-on-close
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="能力编码" prop="abilityCode">
              <el-input
                v-model="form.abilityCode"
                placeholder="如 article_summary"
                :disabled="dialog.isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="能力名称" prop="abilityName">
              <el-input v-model="form.abilityName" placeholder="如 生成文章摘要" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Provider 类型" prop="providerType">
              <el-select v-model="form.providerType" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="pt in providerTypes"
                  :key="pt.value"
                  :label="pt.label"
                  :value="pt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模型/AppID" prop="providerAppId">
              <el-input v-model="form.providerAppId" placeholder="如 deepseek-chat" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="端点地址" prop="endpointUrl">
              <el-input v-model="form.endpointUrl" placeholder="如 https://api.deepseek.com" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="API Key" prop="apiKey">
              <el-input
                v-model="form.apiKey"
                type="password"
                show-password
                placeholder="API 密钥"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调用模式" prop="invokeMode">
              <el-select v-model="form.invokeMode" style="width: 100%">
                <el-option label="同步 (sync)" value="sync" />
                <el-option label="流式 (sse)" value="sse" />
                <el-option label="异步 (async)" value="async" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.providerType === 'dify'" label="应用模式" prop="chatMode">
              <el-select v-model="form.chatMode" placeholder="请选择应用模式" style="width: 100%">
                <el-option label="工作流（无状态）" value="workflow" />
                <el-option label="聊天助手（多轮对话）" value="chat" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用状态" prop="enabled">
              <el-switch v-model="form.enabled" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Prompt 模板" prop="promptTemplate">
              <el-input
                v-model="form.promptTemplate"
                type="textarea"
                :rows="5"
                placeholder="支持 {title} {content} {question} 等变量占位"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button @click="dialog.visible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import AiAPI, { type AiAbilityConfig, type ProviderTypeOption } from "@/api/blog/ai";

defineOptions({ name: "BlogAiConfig" });

type TagType = "primary" | "success" | "warning" | "info" | "danger";

const loading = ref(false);
const total = ref(0);
const submitting = ref(false);
const tableData = ref<AiAbilityConfig[]>([]);
const providerTypes = ref<ProviderTypeOption[]>([]);
const formRef = ref<FormInstance>();

const queryParams = reactive({
  page: 1,
  pageSize: 20,
  abilityCode: "",
  providerType: "",
  enabled: "",
});

const dialog = reactive({
  visible: false,
  title: "",
  isEdit: false,
});

const form = reactive<AiAbilityConfig>({
  abilityCode: "",
  abilityName: "",
  providerType: "",
  providerAppId: "",
  endpointUrl: "",
  apiKey: "",
  promptTemplate: "",
  invokeMode: "sync",
  chatMode: "workflow",
  enabled: "1",
  remark: "",
});

const formRules: FormRules = {
  abilityCode: [{ required: true, message: "请输入能力编码", trigger: "blur" }],
  abilityName: [{ required: true, message: "请输入能力名称", trigger: "blur" }],
  providerType: [{ required: true, message: "请选择 Provider 类型", trigger: "change" }],
  invokeMode: [{ required: true, message: "请选择调用模式", trigger: "change" }],
};

function providerTagType(type: string): TagType {
  if (type === "direct_llm") return "success";
  if (type === "dify") return "primary";
  if (type === "fastgpt") return "warning";
  return "info";
}

function invokeModeTagType(mode: string): TagType {
  if (mode === "sse") return "success";
  if (mode === "async") return "warning";
  return "info";
}

function providerLabel(type: string) {
  const found = providerTypes.value.find((p) => p.value === type);
  return found?.label || type;
}

async function fetchList() {
  loading.value = true;
  try {
    const params: Record<string, string | number> = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
    };
    if (queryParams.abilityCode) params.abilityCode = queryParams.abilityCode;
    if (queryParams.providerType) params.providerType = queryParams.providerType;
    if (queryParams.enabled) params.enabled = queryParams.enabled;
    const result = await AiAPI.getAbilityConfigList(params);
    tableData.value = result.items;
    total.value = result.total;
  } catch (e: unknown) {
    ElMessage.error((e instanceof Error ? e.message : "") || "加载失败");
  } finally {
    loading.value = false;
  }
}

function resetQuery() {
  queryParams.page = 1;
  queryParams.abilityCode = "";
  queryParams.providerType = "";
  queryParams.enabled = "";
  fetchList();
}

function resetForm() {
  formRef.value?.resetFields();
  Object.assign(form, {
    abilityCode: "",
    abilityName: "",
    providerType: "",
    providerAppId: "",
    endpointUrl: "",
    apiKey: "",
    promptTemplate: "",
    invokeMode: "sync",
    chatMode: "workflow",
    enabled: "1",
    remark: "",
  });
}

function openCreate() {
  dialog.isEdit = false;
  dialog.title = "新增 AI 能力配置";
  resetForm();
  dialog.visible = true;
}

function openEdit(row: AiAbilityConfig) {
  dialog.isEdit = true;
  dialog.title = "编辑 AI 能力配置";
  resetForm();
  Object.assign(form, { ...row, apiKey: "" });
  dialog.visible = true;
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    if (dialog.isEdit) {
      await AiAPI.updateAbilityConfig({ ...form });
      ElMessage.success("更新成功");
    } else {
      await AiAPI.createAbilityConfig({ ...form });
      ElMessage.success("新增成功");
    }
    dialog.visible = false;
    fetchList();
  } catch (e: unknown) {
    ElMessage.error((e instanceof Error ? e.message : "") || "操作失败");
  } finally {
    submitting.value = false;
  }
}

function handleDelete(row: AiAbilityConfig) {
  ElMessageBox.confirm(`确定删除能力「${row.abilityName}」(${row.abilityCode}) 吗？`, "删除确认", {
    type: "warning",
  }).then(async () => {
    try {
      await AiAPI.deleteAbilityConfig(row.id!);
      ElMessage.success("删除成功");
      fetchList();
    } catch (e: unknown) {
      ElMessage.error((e instanceof Error ? e.message : "") || "删除失败");
    }
  });
}

onMounted(async () => {
  try {
    providerTypes.value = (await AiAPI.getProviderTypes()).items;
  } catch {
    providerTypes.value = [
      { value: "direct_llm", label: "直连大模型" },
      { value: "dify", label: "Dify 编排平台" },
      { value: "fastgpt", label: "FastGPT 知识库" },
    ];
  }
  fetchList();
});
</script>
