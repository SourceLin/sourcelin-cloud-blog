<template>
  <div class="app-container file-manager-page">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--blue">
          <div class="stat-card__icon">
            <el-icon><Files /></el-icon>
          </div>
          <div class="stat-card__info">
            <div class="stat-card__title">全部文件</div>
            <div class="stat-card__value">{{ total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--green">
          <div class="stat-card__icon">
            <el-icon><Checked /></el-icon>
          </div>
          <div class="stat-card__info">
            <div class="stat-card__title">有效文件 (ACTIVE)</div>
            <div class="stat-card__value">{{ activeCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--orange">
          <div class="stat-card__icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-card__info">
            <div class="stat-card__title">临时文件 (TEMP)</div>
            <div class="stat-card__value">{{ tempCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--purple">
          <div class="stat-card__icon">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stat-card__info">
            <div class="stat-card__title">总存储估算</div>
            <div class="stat-card__value">{{ formattedTotalSize }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 过滤搜索 -->
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="文件名" prop="fileName">
          <el-input
            v-model="queryParams.fileName"
            placeholder="请输入原始名或系统名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="临时 (TEMP)" value="TEMP" />
            <el-option label="激活 (ACTIVE)" value="ACTIVE" />
            <el-option label="待删除 (PENDING_DELETE)" value="PENDING_DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="归属类型" prop="ownerType">
          <el-select
            v-model="queryParams.ownerType"
            placeholder="全部归属"
            clearable
            style="width: 150px"
          >
            <el-option label="管理员 (ADMIN)" value="ADMIN" />
            <el-option label="博客用户 (BLOG)" value="BLOG" />
          </el-select>
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <el-card shadow="hover" class="table-section">
      <div class="table-section__toolbar">
        <div class="table-section__toolbar--actions">
          <el-upload
            action="#"
            :http-request="handleCustomUpload"
            :show-file-list="false"
            :disabled="uploading"
          >
            <el-button type="primary" :icon="Upload" :loading="uploading">
              {{ uploading ? "上传中..." : "上传新文件" }}
            </el-button>
          </el-upload>
        </div>
      </div>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        class="table-section__content"
        border
      >
        <el-table-column label="ID" prop="fileId" width="80" align="center" />
        <el-table-column label="文件图标" width="90" align="center">
          <template #default="scope">
            <div class="file-icon-wrapper">
              <el-icon v-if="isImage(scope.row.fileExt)" size="28" color="#409eff">
                <Picture />
              </el-icon>
              <el-icon v-else-if="isVideo(scope.row.fileExt)" size="28" color="#67c23a">
                <VideoPlay />
              </el-icon>
              <el-icon v-else-if="isZip(scope.row.fileExt)" size="28" color="#e6a23c">
                <FolderOpened />
              </el-icon>
              <el-icon v-else size="28" color="#909399"><Document /></el-icon>
              <span class="file-ext-badge">{{ scope.row.fileExt || "?" }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="原始文件名"
          prop="originalName"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column label="系统存储名" prop="fileName" min-width="180" show-overflow-tooltip />
        <el-table-column label="文件大小" width="100" align="right">
          <template #default="scope">
            {{ formatBytes(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" effect="light">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="归属域" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.ownerType === 'ADMIN' ? 'primary' : 'warning'" effect="plain">
              {{ scope.row.ownerType }} ({{ scope.row.ownerId }})
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" align="center" />
        <el-table-column fixed="right" label="操作" width="220" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              :icon="Download"
              @click="handleDownload(scope.row)"
            >
              下载
            </el-button>
            <el-button
              v-if="isImage(scope.row.fileExt)"
              type="success"
              size="small"
              link
              :icon="View"
              @click="handlePreviewImage(scope.row)"
            >
              预览
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row.fileId)"
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

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewState.visible" title="图片预览" width="600px" align-center>
      <div class="image-preview-container">
        <el-image :src="previewState.url" fit="contain" style="width: 100%; max-height: 500px">
          <template #placeholder>
            <div class="image-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              加载中...
            </div>
          </template>
        </el-image>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "FileManager",
  inheritAttrs: false,
});

import { ref, reactive, computed, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type TagProps,
  type UploadRequestOptions,
} from "element-plus";
import {
  Search,
  Refresh,
  Upload,
  Download,
  Delete,
  View,
  Files,
  Checked,
  Clock,
  DataLine,
  Picture,
  VideoPlay,
  FolderOpened,
  Document,
  Loading,
} from "@element-plus/icons-vue";
import FileAPI from "@/api/file";
import type { FileInfoDetail, FileInfoPageQuery } from "@/types/api";

const queryFormRef = ref<FormInstance>();
const loading = ref(false);
const uploading = ref(false);

const queryParams = reactive<FileInfoPageQuery>({
  page: 1,
  pageSize: 10,
  fileName: "",
  status: "",
  ownerType: "",
});

const pageData = ref<FileInfoDetail[]>([]);
const total = ref(0);

// 统计指标
const activeCount = computed(
  () => pageData.value.filter((f) => f.status === "ACTIVE").length || total.value - tempCount.value
);
const tempCount = computed(() => pageData.value.filter((f) => f.status === "TEMP").length);
const totalSizeSum = computed(
  () =>
    pageData.value.reduce((acc, f) => acc + (f.fileSize || 0), 0) *
    (total.value / (pageData.value.length || 1))
);
const formattedTotalSize = computed(() => formatBytes(totalSizeSum.value || 1024 * 1024 * 12.5));

// 预览状态
const previewState = reactive({
  visible: false,
  url: "",
});

function fetchData(): void {
  loading.value = true;
  FileAPI.list(queryParams)
    .then((data) => {
      pageData.value = data.items || [];
      total.value = data.total ?? 0;
    })
    .catch((err) => {
      console.error(err);
      ElMessage.error("获取文件列表失败");
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
  queryParams.page = 1;
  queryParams.fileName = "";
  queryParams.status = "";
  queryParams.ownerType = "";
  fetchData();
}

// 模拟或真实上传
function handleCustomUpload(options: UploadRequestOptions) {
  const file = options.file;
  uploading.value = true;
  return FileAPI.uploadFile(file)
    .then(() => {
      ElMessage.success("上传文件成功");
      handleResetQuery();
    })
    .catch((err) => {
      ElMessage.error(err.message || "上传文件失败");
      return Promise.reject(err);
    })
    .finally(() => {
      uploading.value = false;
    });
}

function handleDownload(row: FileInfoDetail): void {
  if (!row.accessUrl) {
    ElMessage.warning("该文件没有有效的访问 URL");
    return;
  }
  // 如果是相对路径拼上 base url
  let url = row.accessUrl;
  if (!url.startsWith("http") && !url.startsWith("//")) {
    url = `${import.meta.env.VITE_APP_BASE_API || ""}/file/download/${row.fileId}`;
  } else {
    // 强制转换为下载流
    url = `${import.meta.env.VITE_APP_BASE_API || ""}/file/download/${row.fileId}`;
  }
  FileAPI.download(url, row.originalName || row.fileName);
}

function handlePreviewImage(row: FileInfoDetail): void {
  let url = row.accessUrl;
  if (!url.startsWith("http") && !url.startsWith("//")) {
    url = `${import.meta.env.VITE_APP_BASE_API || ""}${url}`;
  }
  previewState.url = url;
  previewState.visible = true;
}

function handleDelete(fileId: number): void {
  ElMessageBox.confirm("确定要删除此文件吗？如果是临时文件删除后将不可恢复。", "确认删除", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    FileAPI.delete(fileId)
      .then(() => {
        ElMessage.success("删除文件成功");
        fetchData();
      })
      .catch((err) => {
        ElMessage.error(err.message || "删除文件失败");
      })
      .finally(() => {
        loading.value = false;
      });
  });
}

// 辅助方法
function formatBytes(bytes?: number): string {
  if (bytes === undefined || bytes === null || isNaN(bytes)) return "0 B";
  if (bytes === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
}

function getStatusTagType(status?: string): TagProps["type"] {
  switch (status) {
    case "ACTIVE":
      return "success";
    case "TEMP":
      return "warning";
    case "PENDING_DELETE":
      return "danger";
    default:
      return "info";
  }
}

function isImage(ext?: string): boolean {
  if (!ext) return false;
  return ["png", "jpg", "jpeg", "gif", "bmp", "webp"].includes(ext.toLowerCase());
}

function isVideo(ext?: string): boolean {
  if (!ext) return false;
  return ["mp4", "webm", "ogg"].includes(ext.toLowerCase());
}

function isZip(ext?: string): boolean {
  if (!ext) return false;
  return ["zip", "rar", "tar", "gz", "7z"].includes(ext.toLowerCase());
}

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.file-manager-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
}

.stat-cards {
  margin-bottom: 8px;
}

.stat-card {
  --stat-color: #409eff;
  display: flex;
  align-items: center;
  border-left: 5px solid var(--stat-color);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }

  &--blue {
    --stat-color: #409eff;
  }

  &--green {
    --stat-color: #67c23a;
  }

  &--orange {
    --stat-color: #e6a23c;
  }

  &--purple {
    --stat-color: #909399;
  }

  :deep(.el-card__body) {
    display: flex;
    align-items: center;
    width: 100%;
    gap: 16px;
  }

  &__icon {
    font-size: 32px;
    color: var(--stat-color);
    background-color: rgba(var(--stat-color), 0.1);
    padding: 12px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__title {
    font-size: 14px;
    color: #909399;
  }

  &__value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
  }
}

.filter-section {
  background-color: #fff;
  padding: 18px 18px 0;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.table-section {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

  &__toolbar {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.file-icon-wrapper {
  position: relative;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.file-ext-badge {
  position: absolute;
  bottom: -4px;
  right: -4px;
  background-color: #606266;
  color: #fff;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 4px;
  text-transform: uppercase;
  font-weight: bold;
  transform: scale(0.85);
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.image-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}
</style>
