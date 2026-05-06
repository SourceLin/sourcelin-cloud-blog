<template>
  <ModuleListShell
    title="博客用户管理"
    description="博客前台用户治理，支持用户信息维护与密码重置。"
    :module-api="BlogUserAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:user:add',
      edit: 'blog:user:edit',
      remove: 'blog:user:remove',
    }"
    :row-actions="rowActions"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessageBox } from "element-plus";
import BlogUserAPI from "@/api/blog/user";
import DictAPI from "@/api/system/dict";
import ModuleListShell from "../shared/ModuleListShell.vue";
import type { DictItemOption } from "@/types/api";

defineOptions({
  name: "BlogUser",
  inheritAttrs: false,
});

const userTypeItems = ref<DictItemOption[]>([]);
const userStatusItems = ref<DictItemOption[]>([]);
const userSexItems = ref<DictItemOption[]>([]);

function normalizeOptionValue(value: string | number) {
  return typeof value === "string" && /^\d+$/.test(value) ? Number(value) : value;
}

function buildTagMap(items: DictItemOption[]) {
  return Object.fromEntries(
    items.map((item) => [
      String(item.value),
      {
        label: item.label,
        type: item.tagType ?? "info",
      },
    ])
  );
}

const userTypeOptions = computed(() =>
  userTypeItems.value.map((item) => ({
    label: item.label,
    value: normalizeOptionValue(item.value),
  }))
);

const userStatusOptions = computed(() =>
  userStatusItems.value.map((item) => ({
    label: item.label,
    value: normalizeOptionValue(item.value),
  }))
);

const userSexOptions = computed(() =>
  userSexItems.value.map((item) => ({
    label: item.label,
    value: normalizeOptionValue(item.value),
  }))
);

const queryFields = computed(() => [
  { prop: "username", label: "用户账号", type: "input", placeholder: "请输入用户账号" },
  { prop: "nickname", label: "用户昵称", type: "input", placeholder: "请输入用户昵称" },
  { prop: "phone", label: "手机号", type: "input", placeholder: "请输入手机号" },
  { prop: "userType", label: "用户类型", type: "select", options: userTypeOptions.value },
  { prop: "sex", label: "性别", type: "select", options: userSexOptions.value },
  { prop: "userStatus", label: "账号状态", type: "select", options: userStatusOptions.value },
]);

const columns = computed(() => [
  { prop: "avatar", label: "头像", type: "image", width: 90, align: "center" as const },
  { prop: "username", label: "用户账号", minWidth: 120 },
  { prop: "nickname", label: "用户昵称", minWidth: 120 },
  { prop: "email", label: "邮箱", minWidth: 180 },
  { prop: "phone", label: "手机", width: 120 },
  {
    prop: "userType",
    label: "类型",
    type: "tag",
    width: 100,
    align: "center" as const,
    tagMap: buildTagMap(userTypeItems.value),
  },
  {
    prop: "sex",
    label: "性别",
    type: "tag",
    width: 90,
    align: "center" as const,
    tagMap: buildTagMap(userSexItems.value),
  },
  {
    prop: "userStatus",
    label: "状态",
    type: "tag",
    width: 90,
    align: "center" as const,
    tagMap: buildTagMap(userStatusItems.value),
  },
  { prop: "loginIp", label: "最后登录IP", width: 130, align: "center" as const },
  { prop: "loginDate", label: "最后登录时间", minWidth: 170 },
  { prop: "createTime", label: "创建时间", minWidth: 170 },
]);

const formFields = computed(() => [
  { prop: "username", label: "用户账号", type: "input", required: true },
  { prop: "nickname", label: "用户昵称", type: "input", required: true },
  { prop: "email", label: "邮箱", type: "input" },
  { prop: "phone", label: "手机号", type: "input" },
  { prop: "avatar", label: "头像", type: "image", span: 24 },
  {
    prop: "userType",
    label: "用户类型",
    type: "select",
    options: userTypeOptions.value,
    required: true,
  },
  { prop: "sex", label: "性别", type: "select", options: userSexOptions.value },
  {
    prop: "userStatus",
    label: "状态",
    type: "select",
    options: userStatusOptions.value,
    required: true,
  },
  { prop: "introduction", label: "简介", type: "textarea", span: 24, rows: 4 },
]);

const initialForm = {
  userType: 1,
  sex: 2,
  userStatus: 0,
};

async function handleResetPassword(row: Record<string, any>) {
  const { value } = await ElMessageBox.prompt(`请输入用户【${row.username}】的新密码`, "重置密码", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPattern: /^.{6,30}$/,
    inputErrorMessage: "密码长度应为 6~30 位",
  });
  await BlogUserAPI.resetPassword(row.id, value);
  ElMessage.success("密码重置成功");
}

const rowActions = [
  {
    key: "resetPwd",
    label: "重置密码",
    permission: "blog:user:resetPwd",
    confirmText: "",
    onClick: (row: Record<string, any>) => handleResetPassword(row),
  },
];

onMounted(async () => {
  [userTypeItems.value, userStatusItems.value, userSexItems.value] = await Promise.all([
    DictAPI.getDictItems("blog_user_type"),
    DictAPI.getDictItems("blog_user_status"),
    DictAPI.getDictItems("sys_user_sex"),
  ]);
});
</script>
