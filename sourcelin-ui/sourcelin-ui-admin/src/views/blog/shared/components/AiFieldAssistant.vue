<template>
  <el-button
    v-if="enabled"
    size="small"
    :loading="loading"
    :disabled="field.disabled === true"
    @click="generate"
  >
    AI {{ field.label }}
  </el-button>
  <el-dialog v-model="visible" title="AI 建议" width="min(90vw, 680px)" append-to-body>
    <div class="ai-comparison">
      <section>
        <h4>当前内容</h4>
        <p>{{ original }}</p>
      </section>
      <section>
        <h4>AI 建议</h4>
        <el-image v-if="field.aiAbility?.async && suggestion" :src="suggestion" fit="contain" />
        <p v-else>{{ suggestion }}</p>
      </section>
    </div>
    <template #footer>
      <el-button @click="discard">放弃</el-button>
      <el-button type="primary" @click="apply">采纳建议</el-button>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { onMounted } from "vue";
import type { FormField } from "../types/module-list-shell";
import { useAiFieldAssistant } from "../../composables/useAiFieldAssistant";
const props = defineProps<{ field: FormField; form: Record<string, unknown> }>();
const { enabled, loading, visible, original, suggestion, generate, apply, discard, loadStatus } =
  useAiFieldAssistant(props);
onMounted(loadStatus);
</script>
<style scoped>
.ai-comparison {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}
.ai-comparison section {
  padding: 16px;
  background: var(--el-fill-color-light);
  border-radius: 12px;
}
.ai-comparison p {
  max-height: 360px;
  overflow: auto;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}
</style>
