<template>
  <ModuleListShell
    ref="shell"
    title="AI 结果记录"
    :module-api="moduleApi"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="[]"
    :enable-create="false"
    :enable-edit="false"
    :enable-delete="false"
    :row-actions="actions"
  />
  <el-drawer v-model="visible" title="AI 结果对比" size="min(90vw, 760px)">
    <div v-loading="loading" class="comparison">
      <section>
        <h3>当前文章</h3>
        <h4>{{ article?.title || "未关联文章" }}</h4>
        <p>{{ article?.summary }}</p>
        <el-image v-if="article?.avatar" :src="article.avatar" fit="contain" />
        <p>{{ article?.content }}</p>
      </section>
      <section>
        <h3>AI 建议</h3>
        <el-image
          v-if="selected?.abilityCode === 'article_cover_generate'"
          :src="selected.aiResult"
          fit="contain"
        />
        <p v-else>{{ selected?.aiResult }}</p>
      </section>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button
        v-if="selected?.status === 'pending'"
        v-hasPerm="'blog:article:edit'"
        type="primary"
        @click="adopt"
      >
        采纳
      </el-button>
    </template>
  </el-drawer>
</template>
<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import AiAPI, { type AiRecord } from "@/api/blog/ai";
import ArticleAPI from "@/api/blog/article";
import type { ArticleItem } from "@/types/api";
import ModuleListShell from "../shared/ModuleListShell.vue";
import type { QueryField, TableColumn, RowAction } from "../shared/types/module-list-shell";
const shell = ref<InstanceType<typeof ModuleListShell> | null>(null);
const visible = ref(false),
  loading = ref(false);
const selected = ref<AiRecord | null>(null),
  article = ref<ArticleItem | null>(null);
const moduleApi = { getPage: AiAPI.getRecordList };
const queryFields: QueryField[] = [
  { prop: "abilityCode", label: "能力编码" },
  { prop: "bizId", label: "文章编号" },
  {
    prop: "status",
    label: "状态",
    type: "select",
    options: [
      { label: "待采纳", value: "pending" },
      { label: "已采纳", value: "adopted" },
      { label: "已放弃", value: "discarded" },
    ],
  },
];
const columns: TableColumn[] = [
  { prop: "id", label: "记录编号", width: 90 },
  { prop: "abilityCode", label: "能力", minWidth: 160 },
  { prop: "bizId", label: "文章编号", width: 110 },
  {
    prop: "aiResult",
    label: "生成结果",
    minWidth: 240,
    formatter: (row) => String(row.aiResult || "").slice(0, 100),
  },
  { prop: "status", label: "状态", width: 100 },
  { prop: "createTime", label: "创建时间", width: 180 },
];
async function show(row: Record<string, unknown>) {
  selected.value = {
    id: Number(row.id),
    abilityCode: String(row.abilityCode),
    bizId: String(row.bizId || ""),
    aiResult: String(row.aiResult || ""),
    status:
      row.status === "adopted" ? "adopted" : row.status === "discarded" ? "discarded" : "pending",
  };
  visible.value = true;
  article.value = null;
  loading.value = true;
  try {
    if (selected.value.bizId) article.value = await ArticleAPI.getFormData(selected.value.bizId);
  } finally {
    loading.value = false;
  }
}
async function adopt() {
  if (!selected.value) return;
  await AiAPI.adoptResult(selected.value.id);
  ElMessage.success("已采纳");
  visible.value = false;
  shell.value?.refresh();
}
const actions: RowAction[] = [
  { key: "compare", label: "查看对比", onClick: show },
  {
    key: "adopt",
    label: "采纳",
    permission: "blog:article:edit",
    visible: (row) => row.status === "pending",
    confirmText: "确认将 AI 建议写入正式文章？",
    onClick: async (row) => {
      await AiAPI.adoptResult(Number(row.id));
      ElMessage.success("已采纳");
      shell.value?.refresh();
    },
  },
  {
    key: "discard",
    label: "放弃",
    permission: "blog:article:edit",
    visible: (row) => row.status === "pending",
    onClick: async (row) => {
      await AiAPI.discardResult(Number(row.id));
      ElMessage.success("已放弃");
      shell.value?.refresh();
    },
  },
];
</script>
<style scoped>
.comparison {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}
.comparison p {
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}
</style>
