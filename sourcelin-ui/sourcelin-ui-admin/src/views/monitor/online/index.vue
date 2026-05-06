<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
        <el-form-item label="登录地址" prop="ipaddr">
          <el-input
            v-model="queryParams.ipaddr"
            placeholder="请输入登录地址"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="用户名称" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="登录端" prop="loginType">
          <el-select
            v-model="queryParams.loginType"
            placeholder="全部"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in loginTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="table-section">
      <el-table v-loading="loading" :data="tableData" border class="table-section__content">
        <el-table-column type="index" label="序号" width="70" align="center">
          <template #default="scope">
            {{ (queryParams.page - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="会话编号" prop="tokenId" min-width="230" show-overflow-tooltip />
        <el-table-column label="登录名称" prop="userName" min-width="120" />
        <el-table-column label="登录端" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getLoginTypeTagType(row.loginType)" effect="light">
              {{ getLoginTypeLabel(row.loginType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主机地址" prop="ipaddr" min-width="120" />
        <el-table-column label="登录地点" prop="loginLocation" min-width="140" />
        <el-table-column label="浏览器" prop="browser" min-width="120" />
        <el-table-column label="操作系统" prop="os" min-width="120" />
        <el-table-column label="登录时间" min-width="170">
          <template #default="{ row }">
            {{ formatLoginTime(row.loginTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-hasPerm="[
                'monitor:online:forceLogout',
                'monitor:online:forceLogout:admin',
                'monitor:online:forceLogout:blog',
              ]"
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click="handleForceLogout(row)"
            >
              强退
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
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Online",
  inheritAttrs: false,
});

import OnlineAPI from "@/api/monitor/online";
import type { OnlineUserItem, OnlineUserQueryParams } from "@/types/api";
import { dayjs, type FormInstance } from "element-plus";
import { Delete, Refresh, Search } from "@element-plus/icons-vue";

const queryFormRef = ref<FormInstance>();
const loading = ref(false);
const total = ref(0);
const tableData = ref<OnlineUserItem[]>([]);
const LOGIN_TYPE_OPTIONS = [
  { label: "后台管理(admin)", value: "admin", tagType: "success" },
  { label: "前台博客(blog)", value: "blog", tagType: "warning" },
  { label: "APP(app)", value: "app", tagType: "info" },
] as const;

const loginTypeOptions = LOGIN_TYPE_OPTIONS.map((item) => ({
  label: item.label,
  value: item.value,
}));

const queryParams = reactive<OnlineUserQueryParams>({
  page: 1,
  pageSize: 10,
  ipaddr: undefined,
  userName: undefined,
  loginType: undefined,
});

function formatLoginTime(timestamp?: number) {
  if (!timestamp) return "-";
  return dayjs(timestamp).format("YYYY-MM-DD HH:mm:ss");
}

function getLoginTypeLabel(loginType?: string) {
  return LOGIN_TYPE_OPTIONS.find((item) => item.value === loginType)?.label ?? "-";
}

function getLoginTypeTagType(loginType?: string): "success" | "warning" | "info" {
  return LOGIN_TYPE_OPTIONS.find((item) => item.value === loginType)?.tagType ?? "info";
}

function fetchData() {
  loading.value = true;
  OnlineAPI.getPage(queryParams)
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

function handleForceLogout(row: OnlineUserItem) {
  if (!row.tokenId) return;
  ElMessageBox.confirm(
    `确认强退用户“${row.userName ?? "-"}”(${getLoginTypeLabel(row.loginType)})？`,
    "提示",
    {
      type: "warning",
      confirmButtonText: "确定",
      cancelButtonText: "取消",
    }
  ).then(
    () => {
      OnlineAPI.forceLogout(row.tokenId, row.loginType).then(() => {
        ElMessage.success("强退成功");
        fetchData();
      });
    },
    () => {
      ElMessage.info("已取消");
    }
  );
}

onMounted(() => {
  fetchData();
});
</script>
