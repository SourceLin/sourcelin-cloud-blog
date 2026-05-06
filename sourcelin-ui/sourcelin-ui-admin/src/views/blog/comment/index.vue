<template>
  <ModuleListShell
    title="评论管理"
    description="评论审核、来源追踪与删除治理。"
    :module-api="CommentAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:comment:add',
      edit: 'blog:comment:edit',
      remove: 'blog:comment:remove',
    }"
    :row-actions="rowActions"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import CommentAPI from "@/api/blog/comment";
import DictAPI from "@/api/system/dict";
import ModuleListShell from "../shared/ModuleListShell.vue";
import { useCommentAudit } from "../composables/useCommentAudit";
import type { DictItemOption } from "@/types/api";

defineOptions({
  name: "BlogComment",
  inheritAttrs: false,
});

const commentSourceItems = ref<DictItemOption[]>([]);

function normalizeOptionValue(value: string | number) {
  return typeof value === "string" && /^\d+$/.test(value) ? Number(value) : value;
}

const commentSourceOptions = computed(() =>
  commentSourceItems.value.map((item) => ({
    label: item.label,
    value: normalizeOptionValue(item.value),
  }))
);

const commentSourceTagMap = computed(() =>
  Object.fromEntries(
    commentSourceItems.value.map((item) => [
      String(item.value),
      {
        label: item.label,
        type: item.tagType ?? "info",
      },
    ])
  )
);

const { queryFields, columns, formFields, initialForm, rowActions } = useCommentAudit(
  commentSourceOptions,
  commentSourceTagMap
);

onMounted(async () => {
  commentSourceItems.value = await DictAPI.getDictItems("blog_comment_source");
});
</script>
