<template>
  <div class="app-container">
    <el-alert
      title="词库以 Nacos 配置为准。修改词表后请递增版本号并保存，再执行“从配置重建内存”。"
      type="info"
      :closable="false"
      class="mb-12"
      show-icon
    />

    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>评论机审词库</span>
          <div class="header-actions">
            <el-button v-hasPerm="'blog:moderation:query'" :loading="loading" @click="loadCurrent">
              重新加载
            </el-button>
            <el-button
              v-hasPerm="'blog:moderation:edit'"
              type="success"
              :loading="refreshing"
              @click="handleRefresh"
            >
              从配置重建内存
            </el-button>
            <el-button
              v-hasPerm="'blog:moderation:edit'"
              type="primary"
              :loading="saving"
              @click="handleSave"
            >
              应用词库到内存
            </el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border class="mb-12" size="small">
        <el-descriptions-item label="配置版本">
          {{ meta.version ?? "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="内存加载版本">
          {{ meta.loadedVersion ?? "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="干净内容自动过审">
          {{ meta.autoPassOnClean ? "是" : "否" }}
        </el-descriptions-item>
        <el-descriptions-item label="AI审核开关">
          {{ meta.aiEnabled ? "开" : "关" }}
        </el-descriptions-item>
      </el-descriptions>

      <el-form label-width="130px">
        <el-form-item label="保存版本号">
          <el-input-number v-model="formState.version" :min="1" :step="1" />
          <span class="form-tip">词表有变更时请递增版本号。</span>
        </el-form-item>
        <el-form-item label="白名单 allow">
          <el-input
            v-model="formState.allowText"
            type="textarea"
            :rows="6"
            placeholder="每行一个关键字"
          />
        </el-form-item>
        <el-form-item label="黑名单 block">
          <el-input
            v-model="formState.blockText"
            type="textarea"
            :rows="8"
            placeholder="每行一个关键字"
          />
        </el-form-item>
        <el-form-item label="灰名单 suspect">
          <el-input
            v-model="formState.suspectText"
            type="textarea"
            :rows="6"
            placeholder="每行一个关键字"
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import ModerationAPI from "@/api/blog/moderation";
import type { ModerationItem } from "@/types/api";

defineOptions({
  name: "BlogModeration",
  inheritAttrs: false,
});

const loading = ref(false);
const refreshing = ref(false);
const saving = ref(false);

const meta = reactive({
  version: 1,
  loadedVersion: 1,
  autoPassOnClean: true,
  aiEnabled: false,
});

const formState = reactive({
  version: 1,
  allowText: "",
  blockText: "",
  suspectText: "",
});

function joinLines(lines?: string[]) {
  return (lines || []).join("\n");
}

function splitLines(raw: string): string[] {
  return String(raw || "")
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean);
}

function applyPayload(data?: ModerationItem) {
  const payload = data || {};
  meta.version = Number(payload.version ?? 1);
  meta.loadedVersion = Number(payload.loadedVersion ?? meta.version);
  meta.autoPassOnClean = Boolean(payload.autoPassOnClean);
  meta.aiEnabled = Boolean(payload.aiEnabled);
  formState.version = Number(payload.version ?? 1);
  formState.allowText = joinLines(payload.allow);
  formState.blockText = joinLines(payload.block);
  formState.suspectText = joinLines(payload.suspect);
}

function loadCurrent() {
  loading.value = true;
  ModerationAPI.getCurrentKeyword()
    .then((data) => {
      applyPayload(data);
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleRefresh() {
  refreshing.value = true;
  ModerationAPI.refreshKeyword()
    .then(() => {
      ElMessage.success("已从配置重建词库");
      loadCurrent();
    })
    .finally(() => {
      refreshing.value = false;
    });
}

function handleSave() {
  saving.value = true;
  ModerationAPI.saveKeyword({
    version: formState.version,
    allow: splitLines(formState.allowText),
    block: splitLines(formState.blockText),
    suspect: splitLines(formState.suspectText),
  })
    .then(() => {
      ElMessage.success("词库保存成功");
      loadCurrent();
    })
    .finally(() => {
      saving.value = false;
    });
}

onMounted(() => {
  loadCurrent();
});
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.mb-12 {
  margin-bottom: 12px;
}

.form-tip {
  margin-left: 12px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
