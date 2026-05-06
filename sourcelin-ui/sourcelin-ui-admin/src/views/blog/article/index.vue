<template>
  <ModuleListShell
    title="文章管理"
    description="支持文章查询、编辑、状态与推荐属性管理。"
    :module-api="ArticleAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:article:add',
      edit: 'blog:article:edit',
      remove: 'blog:article:remove',
    }"
    :row-actions="rowActions"
    :serialize-form="serializeForm"
    :deserialize-form="deserializeForm"
  />
</template>

<script setup lang="ts">
import ArticleAPI from "@/api/blog/article";
import ModuleListShell from "../shared/ModuleListShell.vue";
import { useArticleTable } from "../composables/useArticleTable";

defineOptions({
  name: "BlogArticle",
  inheritAttrs: false,
});

const {
  queryFields,
  columns,
  formFields,
  initialForm,
  serializeForm,
  deserializeForm,
  rowActions,
} = useArticleTable();
</script>
