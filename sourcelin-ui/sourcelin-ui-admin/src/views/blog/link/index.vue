<template>
  <ModuleListShell
    title="友链管理"
    description="友链新增、审核上架/下架与下架原因管理。"
    :module-api="LinkAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:link:add',
      edit: 'blog:link:edit',
      remove: 'blog:link:remove',
    }"
    :row-actions="rowActions"
  />
</template>

<script setup lang="ts">
import LinkAPI from "@/api/blog/link";
import ModuleListShell from "../shared/ModuleListShell.vue";
import { useLinkReview } from "../composables/useLinkReview";

defineOptions({
  name: "BlogLink",
  inheritAttrs: false,
});

const { queryFields, columns, formFields, initialForm, rowActions } = useLinkReview();
</script>
