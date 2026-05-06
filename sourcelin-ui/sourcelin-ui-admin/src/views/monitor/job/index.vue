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
        <el-form-item label="任务状态" prop="status">
          <DictSelect
            v-model="queryParams.status"
            code="sys_job_status"
            placeholder="全部"
            clearable
            style="width: 140px"
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
            v-hasPerm="['monitor:job:add']"
            type="success"
            :icon="Plus"
            @click="openCreate"
          >
            新增
          </el-button>
          <el-button
            v-hasPerm="['monitor:job:edit']"
            type="primary"
            :icon="Edit"
            :disabled="selectedIds.length !== 1"
            @click="openEditBySelection"
          >
            修改
          </el-button>
          <el-button
            v-hasPerm="['monitor:job:remove']"
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleDelete()"
          >
            删除
          </el-button>
          <el-button
            v-hasPerm="['monitor:job:export']"
            type="warning"
            :icon="Download"
            @click="handleExport"
          >
            导出
          </el-button>
          <el-button
            v-hasPerm="['monitor:job:query']"
            type="info"
            :icon="Tickets"
            @click="openLogBySelection"
          >
            日志
          </el-button>
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
        <el-table-column label="任务编号" prop="jobId" width="90" align="center" />
        <el-table-column label="任务名称" prop="jobName" min-width="150" show-overflow-tooltip />
        <el-table-column label="任务组名" min-width="120">
          <template #default="{ row }">
            <DictTag v-model="row.jobGroup" code="sys_job_group" />
          </template>
        </el-table-column>
        <el-table-column
          label="调用目标"
          prop="invokeTarget"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column label="Cron" prop="cronExpression" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="0"
              inactive-value="1"
              :disabled="!hasStatusPerm"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="下次执行时间" prop="nextValidTime" min-width="170" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-hasPerm="['monitor:job:edit']"
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click="openEdit(row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPerm="['monitor:job:remove']"
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button
              v-hasPerm="['monitor:job:changeStatus']"
              type="success"
              link
              size="small"
              :icon="CaretRight"
              @click="handleRun(row)"
            >
              执行一次
            </el-button>
            <el-button
              v-hasPerm="['monitor:job:query']"
              type="info"
              link
              size="small"
              :icon="Tickets"
              @click="openLog(row)"
            >
              日志
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
      width="760px"
      @close="closeDialog"
    >
      <el-form ref="jobFormRef" :model="formData" :rules="rules" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="jobName">
              <el-input v-model="formData.jobName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务分组" prop="jobGroup">
              <el-input v-model="formData.jobGroup" placeholder="如：DEFAULT" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用方法" prop="invokeTarget">
              <el-input v-model="formData.invokeTarget" placeholder="请输入调用目标字符串" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Cron 表达式" prop="cronExpression">
              <el-input v-model="formData.cronExpression" placeholder="如：0/30 * * * * ?" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行策略" prop="misfirePolicy">
              <el-radio-group v-model="formData.misfirePolicy">
                <el-radio-button label="1">立即执行</el-radio-button>
                <el-radio-button label="2">执行一次</el-radio-button>
                <el-radio-button label="3">放弃执行</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否并发" prop="concurrent">
              <el-radio-group v-model="formData.concurrent">
                <el-radio-button label="0">允许</el-radio-button>
                <el-radio-button label="1">禁止</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="formData.jobId != null" :span="12">
            <el-form-item label="任务状态" prop="status">
              <DictSelect v-model="formData.status" code="sys_job_status" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>
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
  name: "Job",
  inheritAttrs: false,
});

import JobAPI from "@/api/monitor/job";
import type { JobForm, JobItem, JobQueryParams } from "@/types/api";
import type { FormInstance, FormRules } from "element-plus";
import { downloadFile } from "@/utils/download";
import { useUserStore } from "@/store";
import {
  CaretRight,
  Delete,
  Download,
  Edit,
  Plus,
  Refresh,
  Search,
  Tickets,
} from "@element-plus/icons-vue";
import router from "@/router";

const queryFormRef = ref<FormInstance>();
const jobFormRef = ref<FormInstance>();
const loading = ref(false);
const total = ref(0);
const tableData = ref<JobItem[]>([]);
const selectedIds = ref<number[]>([]);

const queryParams = reactive<JobQueryParams>({
  page: 1,
  pageSize: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined,
});

const dialogState = reactive({
  visible: false,
  title: "新增任务",
});

const defaultFormData: JobForm = {
  jobId: undefined,
  jobName: undefined,
  jobGroup: "DEFAULT",
  invokeTarget: undefined,
  cronExpression: undefined,
  misfirePolicy: "1",
  concurrent: "1",
  status: "0",
  remark: undefined,
};

const formData = reactive<JobForm>({ ...defaultFormData });

const rules: FormRules = {
  jobName: [{ required: true, message: "请输入任务名称", trigger: "blur" }],
  jobGroup: [{ required: true, message: "请输入任务分组", trigger: "blur" }],
  invokeTarget: [{ required: true, message: "请输入调用目标字符串", trigger: "blur" }],
  cronExpression: [{ required: true, message: "请输入 Cron 表达式", trigger: "blur" }],
};

const hasStatusPerm = computed(() => {
  const userStore = useUserStore();
  const perms = Array.isArray(userStore.userInfo.perms) ? userStore.userInfo.perms : [];
  const roles = Array.isArray(userStore.userInfo.roles) ? userStore.userInfo.roles : [];
  return (
    roles.includes("admin") || perms.includes("*:*:*") || perms.includes("monitor:job:changeStatus")
  );
});

function fetchData() {
  loading.value = true;
  JobAPI.getPage(queryParams)
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
  fetchData();
}

function handleSelectionChange(rows: JobItem[]) {
  selectedIds.value = rows.map((row) => Number(row.jobId)).filter((id) => !Number.isNaN(id));
}

function resetFormData() {
  Object.assign(formData, defaultFormData);
  jobFormRef.value?.clearValidate();
}

function openCreate() {
  resetFormData();
  dialogState.title = "新增任务";
  dialogState.visible = true;
}

function openEditBySelection() {
  if (selectedIds.value.length !== 1) {
    ElMessage.warning("请选择一条数据");
    return;
  }
  openEdit({ jobId: selectedIds.value[0] });
}

function openEdit(row: Partial<JobItem>) {
  if (!row.jobId) return;
  resetFormData();
  dialogState.title = "编辑任务";
  dialogState.visible = true;
  JobAPI.getDetail(Number(row.jobId)).then((data) => {
    Object.assign(formData, defaultFormData, data);
  });
}

function closeDialog() {
  dialogState.visible = false;
  jobFormRef.value?.resetFields();
  resetFormData();
}

function handleSubmit() {
  jobFormRef.value?.validate((valid) => {
    if (!valid) return;
    loading.value = true;
    const submitAction = formData.jobId ? JobAPI.update(formData) : JobAPI.create(formData);
    submitAction
      .then(() => {
        ElMessage.success(formData.jobId ? "修改成功" : "新增成功");
        closeDialog();
        fetchData();
      })
      .finally(() => {
        loading.value = false;
      });
  });
}

function handleDelete(row?: JobItem) {
  const jobIds = row?.jobId ? String(row.jobId) : selectedIds.value.join(",");
  if (!jobIds) {
    ElMessage.warning("请先选择要删除的任务");
    return;
  }
  ElMessageBox.confirm(`确认删除任务编号为 ${jobIds} 的数据？`, "警告", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(
    () => {
      JobAPI.deleteByIds(jobIds).then(() => {
        ElMessage.success("删除成功");
        fetchData();
      });
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

function handleStatusChange(row: JobItem) {
  const jobId = Number(row.jobId);
  if (!jobId) return;
  const newStatus = String(row.status ?? "1");
  const prevStatus = newStatus === "0" ? "1" : "0";
  const actionText = newStatus === "0" ? "启用" : "暂停";

  ElMessageBox.confirm(`确认要${actionText}任务“${row.jobName ?? "-"}”吗？`, "提示", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(
    () => {
      JobAPI.changeStatus(jobId, newStatus).then(() => {
        ElMessage.success(`${actionText}成功`);
      });
    },
    () => {
      row.status = prevStatus;
    }
  );
}

function handleRun(row: JobItem) {
  const jobId = Number(row.jobId);
  if (!jobId) return;
  ElMessageBox.confirm(`确认立即执行一次任务“${row.jobName ?? "-"}”吗？`, "提示", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(
    () => {
      JobAPI.run(jobId, row.jobGroup).then(() => {
        ElMessage.success("执行成功");
      });
    },
    () => {
      ElMessage.info("已取消");
    }
  );
}

function openLog(row: Partial<JobItem>) {
  const jobId = Number(row.jobId ?? 0);
  router.push({
    name: "JobLog",
    params: { jobId: jobId > 0 ? jobId : 0 },
  });
}

function openLogBySelection() {
  const selectedJobId = selectedIds.value.length === 1 ? selectedIds.value[0] : 0;
  openLog({ jobId: selectedJobId });
}

function handleExport() {
  JobAPI.export(queryParams).then((response: any) => {
    downloadFile(response, `job_${Date.now()}.xlsx`);
  });
}

onMounted(() => {
  fetchData();
});
</script>
