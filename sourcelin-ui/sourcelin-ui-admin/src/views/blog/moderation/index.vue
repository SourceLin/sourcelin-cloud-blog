<template>
  <div class="app-container blog-config-page">
    <div class="config-card">
      <div class="settings-container" style="min-height: auto">
        <!-- 应用 is-full 样式解锁 860px 宽度上限，完全自适应铺满 -->
        <div
          class="settings-pane-content is-full"
          style="flex: 1; padding: 36px 44px; background: var(--el-bg-color-overlay)"
        >
          <div class="settings-section-head">
            <h3 class="settings-section-title">评论机审词库</h3>
            <p class="settings-section-desc">
              配置评论机审黑白名单与审核灰名单关键字。修改词表后请递增版本号并保存，再重建内存生效。
            </p>
          </div>

          <el-alert
            title="词库以 Nacos 配置为准。修改词表后请递增版本号并保存，再执行“从配置重建内存”。"
            type="info"
            :closable="false"
            class="mb-12"
            show-icon
            style="margin-bottom: 20px; border-radius: 8px"
          />

          <el-form label-width="110px" class="settings-form" label-position="top">
            <!-- 子卡片 1：运行态指标盘 -->
            <div class="settings-sub-card" style="margin-bottom: 22px">
              <div class="settings-sub-card__title">词库加载运行状态</div>
              <div
                style="
                  display: grid;
                  grid-template-columns: repeat(4, 1fr);
                  gap: 16px;
                  margin-top: 10px;
                "
              >
                <div
                  style="
                    padding: 14px;
                    text-align: center;
                    background: var(--el-fill-color-blank);
                    border: 1px solid var(--el-border-color-extra-light);
                    border-radius: 10px;
                  "
                >
                  <div style="font-size: 12px; color: var(--el-text-color-secondary)">配置版本</div>
                  <div
                    style="
                      margin-top: 6px;
                      font-size: 20px;
                      font-weight: 700;
                      color: var(--el-text-color-primary);
                    "
                  >
                    {{ meta.version ?? "-" }}
                  </div>
                </div>
                <div
                  style="
                    padding: 14px;
                    text-align: center;
                    background: var(--el-fill-color-blank);
                    border: 1px solid var(--el-border-color-extra-light);
                    border-radius: 10px;
                  "
                >
                  <div style="font-size: 12px; color: var(--el-text-color-secondary)">
                    内存加载版本
                  </div>
                  <div
                    style="
                      margin-top: 6px;
                      font-size: 20px;
                      font-weight: 700;
                      color: var(--el-text-color-primary);
                    "
                  >
                    {{ meta.loadedVersion ?? "-" }}
                  </div>
                </div>
                <div
                  style="
                    padding: 14px;
                    text-align: center;
                    background: var(--el-fill-color-blank);
                    border: 1px solid var(--el-border-color-extra-light);
                    border-radius: 10px;
                  "
                >
                  <div style="font-size: 12px; color: var(--el-text-color-secondary)">
                    干净内容自动过审
                  </div>
                  <div style="margin-top: 8px">
                    <el-tag
                      :type="meta.autoPassOnClean ? 'success' : 'info'"
                      size="small"
                      effect="plain"
                      style="font-weight: 600"
                    >
                      {{ meta.autoPassOnClean ? "开启" : "关闭" }}
                    </el-tag>
                  </div>
                </div>
                <div
                  style="
                    padding: 14px;
                    text-align: center;
                    background: var(--el-fill-color-blank);
                    border: 1px solid var(--el-border-color-extra-light);
                    border-radius: 10px;
                  "
                >
                  <div style="font-size: 12px; color: var(--el-text-color-secondary)">
                    AI 审核开关
                  </div>
                  <div style="margin-top: 8px">
                    <el-tag
                      :type="meta.aiEnabled ? 'primary' : 'info'"
                      size="small"
                      effect="plain"
                      style="font-weight: 600"
                    >
                      {{ meta.aiEnabled ? "已启用" : "已禁用" }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>

            <!-- 子卡片 2：版本控制 -->
            <div class="settings-sub-card" style="margin-bottom: 22px">
              <div class="settings-sub-card__title">版本管理</div>
              <el-form-item label="保存版本号" label-position="left">
                <el-input-number v-model="formState.version" :min="1" :step="1" />
                <span
                  style="
                    margin-left: 12px;
                    font-size: 12px;
                    vertical-align: middle;
                    color: var(--el-text-color-placeholder);
                  "
                >
                  词表内容有变更时请手动递增保存版本。
                </span>
              </el-form-item>
            </div>

            <!-- 子卡片 3：敏感词配置（横向三栏规则看板） -->
            <div class="settings-sub-card">
              <div class="settings-sub-card__title">规则敏感词库内容</div>
              <el-row :gutter="20">
                <el-col :xs="24" :md="8">
                  <el-form-item label="白名单 allow">
                    <el-input
                      v-model="formState.allowText"
                      type="textarea"
                      :rows="18"
                      placeholder="每行一个白名单关键字，匹配的内容将跳过机审限制。"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-form-item label="黑名单 block">
                    <el-input
                      v-model="formState.blockText"
                      type="textarea"
                      :rows="18"
                      placeholder="每行一个黑名单关键字，匹配的内容将直接判定为违规。"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-form-item label="灰名单 suspect">
                    <el-input
                      v-model="formState.suspectText"
                      type="textarea"
                      :rows="18"
                      placeholder="每行一个灰名单关键字，匹配的内容将被打入人工审核抽屉。"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>

          <!-- 底部动作条 -->
          <div class="form-actions" style="margin-top: 24px">
            <el-button
              v-hasPerm="'blog:moderation:edit'"
              type="primary"
              :loading="saving"
              @click="handleSave"
            >
              应用词库到内存
            </el-button>
            <el-button
              v-hasPerm="'blog:moderation:edit'"
              type="success"
              plain
              :loading="refreshing"
              @click="handleRefresh"
            >
              从配置重建内存
            </el-button>
            <el-button v-hasPerm="'blog:moderation:query'" :loading="loading" @click="loadCurrent">
              重新加载
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import ModerationAPI from "@/api/blog/moderation";
import type { ModerationItem } from "@/types/api";
import { ElMessage } from "element-plus";
import "@/styles/blog-config.scss";

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

// 从配置重建内存词库
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

// 应用词库
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
