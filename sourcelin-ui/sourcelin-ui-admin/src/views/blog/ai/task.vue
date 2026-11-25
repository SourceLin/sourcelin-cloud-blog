<template>
  <div class="app-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" size="default">
        <el-form-item label="能力编码">
          <el-input
            v-model="queryParams.abilityCode"
            placeholder="如 article_cover_generate"
            clearable
          />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 130px">
            <el-option label="等待中" value="pending" />
            <el-option label="运行中" value="running" />
            <el-option label="结果待核对" value="unconfirmed" />
            <el-option label="成功" value="succeeded" />
            <el-option label="失败" value="failed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="taskId" label="任务ID" width="200" show-overflow-tooltip />
      <el-table-column prop="abilityCode" label="能力编码" width="150" />
      <el-table-column prop="bizId" label="业务ID" width="100" align="center" />
      <el-table-column prop="status" label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度" width="100" align="center">
        <template #default="{ row }">
          <el-progress
            :percentage="row.progress || 0"
            :status="progressStatus(row.status)"
            :stroke-width="8"
            style="width: 80px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="retryCount" label="重试次数" width="90" align="center" />
      <el-table-column prop="errorMessage" label="错误信息" min-width="180" show-overflow-tooltip />
      <el-table-column prop="startedTime" label="开始时间" width="170" />
      <el-table-column prop="finishedTime" label="完成时间" width="170" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showEvents(row)">事件</el-button>
          <el-button
            v-if="row.status === 'failed'"
            link
            type="warning"
            size="small"
            @click="handleRetry(row)"
          >
            重试
          </el-button>
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

    <!-- 事件流水弹窗 -->
    <el-dialog v-model="eventsVisible" title="任务事件流水" width="700px" destroy-on-close>
      <el-timeline>
        <el-timeline-item
          v-for="evt in taskEvents"
          :key="evt.id"
          :timestamp="evt.createTime"
          :type="eventTimelineType(evt.eventType)"
          placement="top"
        >
          <el-card shadow="hover">
            <el-tag :type="eventTagType(evt.eventType)" size="small" style="margin-bottom: 4px">
              {{ evt.eventType }}
            </el-tag>
            <p style="margin: 4px 0 0; color: #606266">{{ evt.eventMessage }}</p>
            <p
              v-if="evt.eventPayload"
              style="
                max-height: 120px;
                margin: 4px 0 0;
                overflow-y: auto;
                font-size: 12px;
                color: #909399;
                white-space: pre-wrap;
              "
            >
              {{ evt.eventPayload }}
            </p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="taskEvents.length === 0" description="暂无事件记录" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import AiAPI, { type AiTask, type AiTaskEvent } from "@/api/blog/ai";

defineOptions({ name: "BlogAiTask" });

type TagType = "primary" | "success" | "warning" | "info" | "danger";
type ProgressStatus = "" | "success" | "exception" | "warning";

const loading = ref(false);
const total = ref(0);
const tableData = ref<AiTask[]>([]);
const eventsVisible = ref(false);
const taskEvents = ref<AiTaskEvent[]>([]);

const queryParams = reactive({
  page: 1,
  pageSize: 20,
  abilityCode: "",
  status: "",
});

const statusMap: Record<string, string> = {
  pending: "等待中",
  running: "运行中",
  unconfirmed: "结果待核对",
  succeeded: "成功",
  failed: "失败",
  cancelled: "已取消",
};

function statusLabel(status: string) {
  return statusMap[status] || status;
}

function statusTagType(status: string): TagType {
  if (status === "pending") return "info";
  if (status === "running") return "warning";
  if (status === "succeeded") return "success";
  if (status === "failed") return "danger";
  return "info";
}

function progressStatus(status: string): ProgressStatus {
  if (status === "failed") return "exception";
  if (status === "succeeded") return "success";
  return "";
}

function eventTagType(type: string): TagType {
  if (type === "CREATED") return "info";
  if (type === "RUNNING") return "warning";
  if (type === "SUCCEEDED") return "success";
  if (type === "FAILED") return "danger";
  return "info";
}

function eventTimelineType(type: string): "primary" | "success" | "warning" | "danger" {
  if (type === "CREATED") return "primary";
  if (type === "RUNNING") return "warning";
  if (type === "SUCCEEDED") return "success";
  if (type === "FAILED") return "danger";
  return "primary";
}

async function fetchList() {
  loading.value = true;
  try {
    const params: Record<string, string | number> = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
    };
    if (queryParams.abilityCode) params.abilityCode = queryParams.abilityCode;
    if (queryParams.status) params.status = queryParams.status;
    const result = await AiAPI.getTaskList(params);
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
  queryParams.status = "";
  fetchList();
}

async function showEvents(row: AiTask) {
  try {
    taskEvents.value = (await AiAPI.getTaskEvents(row.taskId)).items;
    eventsVisible.value = true;
  } catch (e: unknown) {
    ElMessage.error((e instanceof Error ? e.message : "") || "加载事件失败");
  }
}

function handleRetry(row: AiTask) {
  ElMessageBox.confirm(`确定重试任务「${row.taskId}」吗？`, "重试确认", { type: "warning" }).then(
    async () => {
      try {
        await AiAPI.retryTask(row.taskId);
        ElMessage.success("任务已重新加入队列");
        fetchList();
      } catch (e: unknown) {
        ElMessage.error((e instanceof Error ? e.message : "") || "重试失败");
      }
    }
  );
}

onMounted(() => {
  fetchList();
});
</script>
