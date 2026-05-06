<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
        <el-form-item label="任务名称" prop="jobName">
          <el-input
            v-model="queryParams.jobName"
            placeholder="请输入任务名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="任务组名" prop="jobGroup">
          <el-input
            v-model="queryParams.jobGroup"
            placeholder="请输入任务组名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="执行状态" prop="status">
          <DictSelect
            v-model="queryParams.status"
            code="sys_common_status"
            placeholder="全部"
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item label="执行时间" prop="createTime">
          <el-date-picker
            v-model="queryParams.createTime"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="~"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
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
            v-hasPerm="['monitor:job:remove']"
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleDelete"
          >
            删除
          </el-button>
          <el-button
            v-hasPerm="['monitor:job:remove']"
            type="danger"
            plain
            :icon="DeleteFilled"
            @click="handleClean"
          >
            清空
          </el-button>
          <el-button
            v-hasPerm="['monitor:job:export']"
            type="warning"
            :icon="Download"
            @click="handleExport"
          >
            导出
          </el-button>
          <el-button type="info" :icon="Back" @click="handleBack">返回任务列表</el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        class="table-section__content"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="日志编号" prop="jobLogId" width="90" align="center" />
        <el-table-column label="任务名称" prop="jobName" min-width="140" show-overflow-tooltip />
        <el-table-column label="任务组名" min-width="120">
          <template #default="{ row }">
            <DictTag v-model="row.jobGroup" code="sys_job_group" />
          </template>
        </el-table-column>
        <el-table-column
          label="调用目标"
          prop="invokeTarget"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column label="日志信息" prop="jobMessage" min-width="180" show-overflow-tooltip />
        <el-table-column label="执行状态" width="100" align="center">
          <template #default="{ row }">
            <DictTag v-model="row.status" code="sys_common_status" />
          </template>
        </el-table-column>
        <el-table-column label="执行时间" prop="createTime" min-width="170" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="调度日志详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志编号">{{ detailData.jobLogId }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ detailData.jobName }}</el-descriptions-item>
        <el-descriptions-item label="任务组名">{{ detailData.jobGroup }}</el-descriptions-item>
        <el-descriptions-item label="执行时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="调用目标" :span="2">
          {{ detailData.invokeTarget }}
        </el-descriptions-item>
        <el-descriptions-item label="日志信息" :span="2">
          {{ detailData.jobMessage }}
        </el-descriptions-item>
        <el-descriptions-item label="执行状态">
          <DictTag v-model="detailData.status" code="sys_common_status" />
        </el-descriptions-item>
        <el-descriptions-item label="异常信息" :span="2">
          {{ detailData.exceptionInfo || "-" }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "JobLog",
  inheritAttrs: false,
});

import JobAPI from "@/api/monitor/job";
import JobLogAPI from "@/api/monitor/jobLog";
import type { JobLogItem, JobLogQueryParams } from "@/types/api";
import type { FormInstance } from "element-plus";
import { Back, Delete, DeleteFilled, Download, Refresh, Search } from "@element-plus/icons-vue";
import { downloadFile } from "@/utils/download";
import router from "@/router";
import { useRoute } from "vue-router";

const route = useRoute();
const queryFormRef = ref<FormInstance>();
const loading = ref(false);
const total = ref(0);
const tableData = ref<JobLogItem[]>([]);
const selectedIds = ref<number[]>([]);
const detailVisible = ref(false);
const detailData = ref<Partial<JobLogItem>>({});

const queryParams = reactive<JobLogQueryParams>({
  page: 1,
  pageSize: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined,
  createTime: undefined,
});

function fetchData() {
  loading.value = true;
  JobLogAPI.getPage(queryParams)
    .then((data) => {
      tableData.value = data.items ?? [];
      total.value = data.total ?? 0;
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery() {
  queryParams.page = 1;
  fetchData();
}

function handleResetQuery() {
  queryFormRef.value?.resetFields();
  queryParams.page = 1;
  queryParams.createTime = undefined;
  fetchData();
}

function handleSelectionChange(rows: JobLogItem[]) {
  selectedIds.value = rows.map((row) => Number(row.jobLogId)).filter((id) => !Number.isNaN(id));
}

function handleDelete() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning("请先选择要删除的日志");
    return;
  }
  const ids = selectedIds.value.join(",");
  ElMessageBox.confirm(`确认删除日志编号为 ${ids} 的数据？`, "警告", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(
    () => {
      JobLogAPI.deleteByIds(ids).then(() => {
        ElMessage.success("删除成功");
        fetchData();
      });
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

function handleClean() {
  ElMessageBox.confirm("确认清空所有调度日志？", "警告", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(
    () => {
      JobLogAPI.clean().then(() => {
        ElMessage.success("清空成功");
        fetchData();
      });
    },
    () => {
      ElMessage.info("已取消清空");
    }
  );
}

function handleDetail(row: JobLogItem) {
  detailData.value = row;
  detailVisible.value = true;
}

function handleBack() {
  router.push("/monitor/job");
}

function handleExport() {
  JobLogAPI.export(queryParams).then((response: any) => {
    downloadFile(response, `job_log_${Date.now()}.xlsx`);
  });
}

async function initJobFilterFromRoute() {
  const routeJobId = Number(route.params.jobId ?? 0);
  if (!routeJobId || Number.isNaN(routeJobId)) {
    fetchData();
    return;
  }
  try {
    const jobData = await JobAPI.getDetail(routeJobId);
    queryParams.jobName = jobData.jobName;
    queryParams.jobGroup = jobData.jobGroup;
  } finally {
    fetchData();
  }
}

onMounted(() => {
  initJobFilterFromRoute();
});
</script>
