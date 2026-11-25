<template>
  <div class="app-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" size="default">
        <el-form-item label="能力编码">
          <el-input v-model="queryParams.abilityCode" placeholder="如 article_summary" clearable />
        </el-form-item>
        <el-form-item label="调用状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
            <el-option label="超时" value="timeout" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务ID">
          <el-input
            v-model="queryParams.bizId"
            placeholder="文章ID"
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="requestId" label="请求ID" width="200" show-overflow-tooltip />
      <el-table-column prop="abilityCode" label="能力编码" width="150" />
      <el-table-column prop="providerType" label="Provider" width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small">{{ row.providerType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="invokeMode" label="模式" width="70" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="latencyMs" label="耗时(ms)" width="100" align="center" />
      <el-table-column prop="tokenUsage" label="Token" width="90" align="center" />
      <el-table-column prop="errorMessage" label="错误信息" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createTime" label="调用时间" width="170" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showDetail(row as AiCallLog)">
            详情
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="调用日志详情" width="700px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="请求ID" :span="2">{{ detail.requestId }}</el-descriptions-item>
        <el-descriptions-item label="能力编码">{{ detail.abilityCode }}</el-descriptions-item>
        <el-descriptions-item label="Provider">{{ detail.providerType }}</el-descriptions-item>
        <el-descriptions-item label="调用模式">{{ detail.invokeMode }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 'success' ? 'success' : 'danger'" size="small">
            {{ detail.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detail.latencyMs }}</el-descriptions-item>
        <el-descriptions-item label="Token 用量">
          {{ detail.tokenUsage || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="业务ID">{{ detail.bizId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="错误码">{{ detail.errorCode || "-" }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">
          {{ detail.errorMessage || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="请求摘要" :span="2">
          <div class="log-content">{{ detail.requestSummary || "-" }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="响应摘要" :span="2">
          <div class="log-content">{{ detail.responseSummary || "-" }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="调用时间" :span="2">
          {{ detail.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import AiAPI, { type AiCallLog } from "@/api/blog/ai";

defineOptions({ name: "BlogAiCallLog" });

const loading = ref(false);
const total = ref(0);
const tableData = ref<AiCallLog[]>([]);
const detailVisible = ref(false);
const detail = ref<AiCallLog>({} as AiCallLog);

const queryParams = reactive({
  page: 1,
  pageSize: 20,
  abilityCode: "",
  status: "",
  bizId: "",
});

async function fetchList() {
  loading.value = true;
  try {
    const params: Record<string, string | number> = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
    };
    if (queryParams.abilityCode) params.abilityCode = queryParams.abilityCode;
    if (queryParams.status) params.status = queryParams.status;
    if (queryParams.bizId) params.bizId = queryParams.bizId;
    const result = await AiAPI.getCallLogList(params);
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
  queryParams.bizId = "";
  fetchList();
}

function showDetail(row: AiCallLog) {
  detail.value = row;
  detailVisible.value = true;
}

onMounted(() => {
  fetchList();
});
</script>

<style scoped>
.log-content {
  max-height: 200px;
  padding: 8px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 12px;
  word-break: break-all;
  white-space: pre-wrap;
  background: #fafafa;
  border-radius: 4px;
}
</style>
