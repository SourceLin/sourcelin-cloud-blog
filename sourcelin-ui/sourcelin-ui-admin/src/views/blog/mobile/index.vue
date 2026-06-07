<template>
  <div class="app-container">
    <div class="page-header">
      <h2 class="page-title">移动端端侧能力配置</h2>
      <el-button :loading="loading" type="primary" plain @click="loadData">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>

    <el-row v-loading="loading" :gutter="20" class="card-row">
      <el-col v-for="item in list" :key="item.id" :xs="24" :sm="24" :md="12" :lg="8" :xl="8">
        <el-card
          :class="['client-card', { 'safe-mode-active': item.reviewSafeMode }]"
          shadow="hover"
        >
          <!-- 卡片 Header -->
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <div :class="['client-icon', item.client]">
                  <!-- 微信小程序 SVG -->
                  <svg
                    v-if="item.client === 'mini'"
                    viewBox="0 0 24 24"
                    width="20"
                    height="20"
                    fill="currentColor"
                  >
                    <path
                      d="M8.28 10.7c-.38 0-.68-.31-.68-.68s.31-.68.68-.68c.37 0 .68.31.68.68 0 .37-.31.68-.68.68zm4.49 0c-.38 0-.68-.31-.68-.68s.31-.68.68-.68.68.31.68.68c-.01.37-.31.68-.68.68zm-4.7 3.32c-.3 0-.54-.24-.54-.54a.54.54 0 0 1 .54-.54c.3 0 .54.24.54.54 0 .3-.24.54-.54.54zm3.08-1.08c-.46 0-.83.37-.83.83s.37.83.83.83.83-.37.83-.83-.37-.83-.83-.83zm4.6 2.08c-.3 0-.54-.24-.54-.54s.24-.54.54-.54.54.24.54.54c0 .3-.24.54-.54.54zm2.18-.75c-.23 0-.42-.19-.42-.42s.19-.42.42-.42c.23 0 .42.19.42.42-.01.23-.2.42-.42.42zm-2.05-1.92c-.38 0-.68.31-.68.68s.31.68.68.68.68-.31.68-.68c0-.37-.3-.68-.68-.68zm-5.7-8.12C5.35 4.23 1 7.79 1 12.1c0 2.41 1.35 4.57 3.46 6.03l-.56 2.05 2.38-1.2c.57.17 1.18.26 1.8.26 1.01 0 1.97-.24 2.82-.67-.78-.89-1.25-2.06-1.25-3.32 0-3.31 3.25-6 7.25-6 .3 0 .6.02.89.06C16.89 6.45 13.56 4.23 9.28 4.23zm7.97 6.02c-3.31 0-6 2.24-6 5s2.69 5 6 5c.53 0 1.05-.06 1.54-.18l1.76 1 .43-1.63c1.37-1.01 2.27-2.52 2.27-4.19 0-2.76-2.69-5-6-5z"
                    />
                  </svg>
                  <!-- 移动端 H5 SVG -->
                  <svg
                    v-else-if="item.client === 'h5'"
                    viewBox="0 0 24 24"
                    width="20"
                    height="20"
                    fill="currentColor"
                  >
                    <path
                      d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"
                    />
                  </svg>
                  <!-- 移动端 App SVG -->
                  <svg v-else viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                    <path
                      d="M17 1.01L7 1c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-1.99-2-1.99zM17 19H7V5h10v14z"
                    />
                  </svg>
                </div>
                <span class="client-title">{{ getClientName(item.client) }}</span>
              </div>
              <div :class="['status-badge', item.reviewSafeMode ? 'warning' : 'active']">
                <span :class="['status-dot', item.reviewSafeMode ? 'warning' : 'active']"></span>
                {{ item.reviewSafeMode ? "过审安全模式" : "全功能运行中" }}
              </div>
            </div>
          </template>

          <!-- 卡片 Content -->
          <div class="card-content">
            <!-- 危险安全操作区 -->
            <div class="danger-zone-bar">
              <div class="switch-item danger-item">
                <div class="switch-label">
                  <span>安全过审模式</span>
                  <div class="tip-text">开启后强制隐藏社交/创作/上传功能，以纯阅读版过审</div>
                </div>
                <el-switch
                  v-model="item.reviewSafeMode"
                  :before-change="() => beforeReviewSafeModeChange(item)"
                  :disabled="!hasEditPerm"
                  active-color="#e6a23c"
                />
              </div>
            </div>

            <!-- 配置开关分类展示 -->
            <el-collapse v-model="activeCollapseNames" class="custom-collapse">
              <!-- 安全与基础 -->
              <el-collapse-item name="basic">
                <template #title>
                  <span class="collapse-title">安全与基础设置</span>
                </template>
                <div class="switch-group">
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>文章阅读功能</span>
                    </div>
                    <el-switch
                      v-model="item.articleReadEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'articleReadEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>搜索文章</span>
                    </div>
                    <el-switch
                      v-model="item.searchEnabled"
                      :disabled="!hasEditPerm || !item.articleReadEnabled"
                      @change="handleSwitchChange(item, 'searchEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>个人资料编辑</span>
                    </div>
                    <el-switch
                      v-model="item.profileEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'profileEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>文章收藏功能</span>
                    </div>
                    <el-switch
                      v-model="item.favoriteEnabled"
                      :disabled="!hasEditPerm || !item.articleReadEnabled"
                      @change="handleSwitchChange(item, 'favoriteEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>阅读历史记录</span>
                    </div>
                    <el-switch
                      v-model="item.readingHistoryEnabled"
                      :disabled="!hasEditPerm || !item.articleReadEnabled"
                      @change="handleSwitchChange(item, 'readingHistoryEnabled')"
                    />
                  </div>
                </div>
              </el-collapse-item>

              <!-- 设置与服务 -->
              <el-collapse-item name="service">
                <template #title>
                  <span class="collapse-title">系统设置与服务</span>
                </template>
                <div class="switch-group">
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>端侧体验设置</span>
                    </div>
                    <el-switch
                      v-model="item.settingsEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'settingsEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>协议与政策说明</span>
                    </div>
                    <el-switch
                      v-model="item.policyEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'policyEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>关于本站</span>
                    </div>
                    <el-switch
                      v-model="item.aboutEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'aboutEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>友情链接</span>
                    </div>
                    <el-switch
                      v-model="item.friendLinkEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'friendLinkEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>全站导航</span>
                    </div>
                    <el-switch
                      v-model="item.navigationEnabled"
                      :disabled="!hasEditPerm"
                      @change="handleSwitchChange(item, 'navigationEnabled')"
                    />
                  </div>
                </div>
              </el-collapse-item>

              <!-- 博主与创作 -->
              <el-collapse-item name="blogger">
                <template #title>
                  <span class="collapse-title">创作与内容生产</span>
                </template>
                <div class="switch-group">
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>博主写文章入口</span>
                      <div class="tip-text">开启后，符合角色条件的用户可见发布入口</div>
                    </div>
                    <el-switch
                      v-model="item.articlePublishEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'articlePublishEnabled')"
                    />
                  </div>
                  <div class="role-input-container">
                    <div class="switch-label">
                      <span>允许写文章的角色</span>
                    </div>
                    <el-input
                      v-model="item.articlePublishRole"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      placeholder="默认 blogger"
                      size="small"
                      style="width: 140px"
                      @input="checkChanged(item)"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>普通用户图片上传</span>
                      <div class="tip-text">控制非博主角色是否可上传文件</div>
                    </div>
                    <el-switch
                      v-model="item.userUploadEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'userUploadEnabled')"
                    />
                  </div>
                </div>
              </el-collapse-item>

              <!-- 社交与互动 -->
              <el-collapse-item name="social">
                <template #title>
                  <span class="collapse-title">社交互动与网络</span>
                </template>
                <div class="switch-group">
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>文章评论与回复</span>
                    </div>
                    <el-switch
                      v-model="item.commentEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'commentEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>说说社区广场 Tab</span>
                    </div>
                    <el-switch
                      v-model="item.communityEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'communityEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>说说广场发布入口</span>
                    </div>
                    <el-switch
                      v-model="item.sayPublishEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode || !item.communityEnabled"
                      @change="handleSwitchChange(item, 'sayPublishEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>匿名树洞投递</span>
                    </div>
                    <el-switch
                      v-model="item.treeholeEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'treeholeEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>互动中心通知</span>
                    </div>
                    <el-switch
                      v-model="item.interactionCenterEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'interactionCenterEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>关注与粉丝机制</span>
                    </div>
                    <el-switch
                      v-model="item.followEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'followEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>消息通知中心</span>
                    </div>
                    <el-switch
                      v-model="item.messageCenterEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'messageCenterEnabled')"
                    />
                  </div>
                  <div class="switch-item">
                    <div class="switch-label">
                      <span>用户公开个人主页</span>
                    </div>
                    <el-switch
                      v-model="item.userHomeEnabled"
                      :disabled="!hasEditPerm || item.reviewSafeMode"
                      @change="handleSwitchChange(item, 'userHomeEnabled')"
                    />
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>

            <!-- 卡片 Footer 操作区 -->
            <div v-if="hasEditPerm" class="card-footer">
              <el-button
                size="small"
                :disabled="!changedStates[item.id!]"
                @click="handleReset(item)"
              >
                撤销修改
              </el-button>
              <el-button
                size="small"
                type="primary"
                :disabled="!changedStates[item.id!]"
                :loading="savingStates[item.id!]"
                @click="handleSave(item)"
              >
                保存此端配置
              </el-button>
            </div>

            <!-- 元数据更新时间 -->
            <div class="client-meta">
              <span>更新时间: {{ item.updateTime || "暂无数据" }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import MobileCapabilityAPI from "@/api/blog/mobile";
import type { MobileCapability } from "@/types/api";
import { hasPerm } from "@/utils/auth";

defineOptions({
  name: "MobileCapability",
  inheritAttrs: false,
});

const loading = ref(false);
const list = ref<MobileCapability[]>([]);
const originalList = ref<MobileCapability[]>([]);

// 折叠面板展开的状态组
const activeCollapseNames = ref(["basic", "service", "blogger", "social"]);

// 独立追踪各个卡片数据的变化状态与提交 Loading
const changedStates = reactive<Record<number, boolean>>({});
const savingStates = reactive<Record<number, boolean>>({});

// 编程式检查当前用户是否有编辑权限
const hasEditPerm = computed(() => hasPerm("blog:mobile:edit"));

function getClientName(client: string) {
  if (client === "mini") return "微信小程序";
  if (client === "h5") return "移动端 H5";
  if (client === "app") return "移动端 App";
  return client;
}

// 检查某个卡片配置是否有被编辑修改
function checkChanged(item: MobileCapability) {
  if (item.id === undefined) return;
  const original = originalList.value.find((o) => o.id === item.id);
  if (!original) {
    changedStates[item.id] = false;
    return;
  }
  // 对比所有与页面状态相关的字段，排除 createTime/updateTime 动态变化字段
  const keys = Object.keys(item).filter(
    (key) => key !== "createTime" && key !== "updateTime"
  ) as Array<keyof MobileCapability>;

  const isDifferent = keys.some((key) => {
    return JSON.stringify(item[key]) !== JSON.stringify(original[key]);
  });
  changedStates[item.id] = isDifferent;
}

function loadData() {
  loading.value = true;
  MobileCapabilityAPI.list()
    .then((data) => {
      list.value = data?.items || [];
      // 深拷贝一份原始数据，做改变检测
      originalList.value = JSON.parse(JSON.stringify(list.value));
      // 重置所有状态标识
      list.value.forEach((item) => {
        if (item.id !== undefined) {
          changedStates[item.id] = false;
          savingStates[item.id] = false;
        }
      });
    })
    .catch(() => {
      ElMessage.error("获取端侧能力配置失败");
    })
    .finally(() => {
      loading.value = false;
    });
}

// “过审安全模式”高风险开关拦截与联动覆盖
function beforeReviewSafeModeChange(item: MobileCapability) {
  return new Promise<boolean>((resolve) => {
    const targetState = !item.reviewSafeMode;
    const actionText = targetState ? "开启" : "关闭";
    ElMessageBox.confirm(
      `确定要${actionText}【${getClientName(item.client)}】的安全过审模式吗？${
        targetState
          ? "开启后将自动下线社交互动与文件上传等敏感功能！"
          : "关闭后将恢复该端的完整运行功能配置。"
      }`,
      "高风险配置警告",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    )
      .then(() => {
        // 如果确认开启过审，联动关闭所有敏感功能，使用户无法违规操作
        if (targetState) {
          item.commentEnabled = false;
          item.communityEnabled = false;
          item.sayPublishEnabled = false;
          item.treeholeEnabled = false;
          item.interactionCenterEnabled = false;
          item.followEnabled = false;
          item.messageCenterEnabled = false;
          item.userHomeEnabled = false;
          item.articlePublishEnabled = false;
          item.userUploadEnabled = false;
        }
        // 由于 before-change 的触发在 v-model 状态真正改变前，我们加微小延迟确保模型状态已应用，随后检查数据差异
        setTimeout(() => {
          checkChanged(item);
        }, 50);
        resolve(true);
      })
      .catch(() => {
        resolve(false);
      });
  });
}

// 普通开关的交互联动处理
function handleSwitchChange(item: MobileCapability, key: keyof MobileCapability) {
  // 如果文章阅读功能被关闭，则子阅读相关的设置应当一并关闭
  if (key === "articleReadEnabled" && !item.articleReadEnabled) {
    item.searchEnabled = false;
    item.favoriteEnabled = false;
    item.readingHistoryEnabled = false;
  }
  // 如果社区说说广场关闭，则说说发布也不复存在
  if (key === "communityEnabled" && !item.communityEnabled) {
    item.sayPublishEnabled = false;
  }
  checkChanged(item);
}

// 恢复单张卡片到修改前的原始数据
function handleReset(item: MobileCapability) {
  if (item.id === undefined) return;
  const id = item.id;
  const original = originalList.value.find((o) => o.id === id);
  if (original) {
    const idx = list.value.findIndex((l) => l.id === id);
    if (idx !== -1) {
      list.value[idx] = JSON.parse(JSON.stringify(original));
      changedStates[id] = false;
    }
  }
}

// 保存单张客户端卡片的更改配置
function handleSave(item: MobileCapability) {
  if (item.id === undefined) return;
  const id = item.id;
  savingStates[id] = true;
  MobileCapabilityAPI.update(item)
    .then(() => {
      ElMessage.success(`${getClientName(item.client)} 能力配置保存成功`);
      changedStates[id] = false;
      // 成功后，同步更新该端原始数据副本
      const idx = originalList.value.findIndex((o) => o.id === id);
      if (idx !== -1) {
        originalList.value[idx] = JSON.parse(JSON.stringify(item));
      }
      // 从后端读取新数据（包含了更新后的 updateTime 等）
      loadData();
    })
    .catch(() => {
      ElMessage.error(`${getClientName(item.client)} 配置保存失败`);
    })
    .finally(() => {
      savingStates[id] = false;
    });
}

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.app-container {
  min-height: calc(100vh - 84px);
  padding: 24px;
  background-color: var(--el-bg-color-page);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;

  .page-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }
}

.card-row {
  margin-bottom: -20px;
}

.client-card {
  margin-bottom: 20px;
  overflow: visible;
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

  &:hover {
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
    transform: translateY(-4px);
  }

  // 过审安全模式下背景与阴影警示特效，采用兼容亮暗主题的 color-mix 渐变
  &.safe-mode-active {
    background: linear-gradient(
      180deg,
      color-mix(in srgb, var(--el-color-warning) 5%, var(--el-bg-color-overlay)) 0%,
      var(--el-bg-color-overlay) 100%
    );
    border-color: var(--el-color-warning);
    box-shadow: 0 4px 16px rgba(230, 162, 60, 0.12);

    &:hover {
      box-shadow: 0 12px 24px rgba(230, 162, 60, 0.22);
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 4px;
}

.header-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.client-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  background-color: var(--el-fill-color-light);
  border-radius: 10px;
  transition: all 0.3s;

  &.mini {
    color: #07c160;
    background-color: rgba(7, 193, 96, 0.08);
  }

  &.h5 {
    color: #9c27b0;
    background-color: rgba(156, 39, 176, 0.08);
  }

  &.app {
    color: #1e90ff;
    background-color: rgba(30, 144, 255, 0.08);
  }
}

.client-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.status-badge {
  display: flex;
  align-items: center;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 600;
  border-radius: 20px;
  transition: all 0.3s;

  &.active {
    color: #67c23a;
    background-color: rgba(103, 194, 58, 0.1);
  }

  &.warning {
    color: #e6a23c;
    background-color: rgba(230, 162, 60, 0.1);
  }
}

.status-dot {
  width: 6px;
  height: 6px;
  margin-right: 6px;
  border-radius: 50%;

  &.active {
    background-color: #67c23a;
    box-shadow: 0 0 8px rgba(103, 194, 58, 0.6);
  }

  &.warning {
    background-color: #e6a23c;
    box-shadow: 0 0 8px rgba(230, 162, 60, 0.6);
    animation: blink-warning 1.5s infinite;
  }
}

@keyframes blink-warning {
  0% {
    box-shadow: 0 0 0 0 rgba(230, 162, 60, 0.6);
    transform: scale(0.9);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(230, 162, 60, 0);
    transform: scale(1.1);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(230, 162, 60, 0);
    transform: scale(0.9);
  }
}

.card-content {
  margin-top: 5px;
}

.danger-zone-bar {
  padding: 12px 14px;
  margin-bottom: 16px;
  background-color: color-mix(in srgb, var(--el-color-warning) 4%, var(--el-bg-color-overlay));
  border: 1px dashed color-mix(in srgb, var(--el-color-warning) 25%, transparent);
  border-radius: 8px;
}

.danger-item {
  padding: 0 !important;
  border: none !important;
}

.custom-collapse {
  border: none;

  :deep(.el-collapse-item__header) {
    height: 40px;
    padding: 10px 4px;
    background-color: transparent;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  :deep(.el-collapse-item__wrap) {
    background-color: transparent;
    border: none;
  }

  :deep(.el-collapse-item__content) {
    padding-top: 4px;
    padding-bottom: 12px;
  }
}

.collapse-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
}

.switch-group {
  padding: 0 4px;
}

.switch-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);

  &:last-child {
    border-bottom: none;
  }
}

.switch-label {
  display: flex;
  flex-direction: column;
  padding-right: 15px;

  span {
    font-size: 13px;
    font-weight: 550;
    color: var(--el-text-color-primary);
  }

  .tip-text {
    margin-top: 3px;
    font-size: 11px;
    line-height: 1.35;
    color: var(--el-text-color-placeholder);
  }
}

.role-input-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);

  .switch-label span {
    font-size: 13px;
    font-weight: 550;
    color: var(--el-text-color-primary);
  }
}

.card-footer {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: flex-end;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px dashed var(--el-border-color-light);
}

.client-meta {
  margin-top: 12px;
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  text-align: right;
}
</style>
