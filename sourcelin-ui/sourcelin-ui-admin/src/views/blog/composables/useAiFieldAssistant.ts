import { ref, onBeforeUnmount } from "vue";
import { ElMessage } from "element-plus";
import AiAPI from "@/api/blog/ai";
import type { FormField } from "../shared/types/module-list-shell";
export function useAiFieldAssistant(
  props: Readonly<{ field: FormField; form: Record<string, unknown> }>
) {
  const enabled = ref(false),
    loading = ref(false),
    visible = ref(false),
    original = ref(""),
    suggestion = ref("");
  let disposed = false;
  onBeforeUnmount(() => {
    disposed = true;
  });
  async function loadStatus() {
    enabled.value = await AiAPI.getAiStatus();
  }
  async function generate() {
    const ability = props.field.aiAbility;
    if (!ability || loading.value) return;
    if (
      String(props.form.content || "")
        .replace(/<[^>]+>/g, "")
        .trim().length < 10
    ) {
      ElMessage.warning("请先填写文章正文");
      return;
    }
    loading.value = true;
    try {
      const input = { title: props.form.title, content: props.form.content };
      let content: string;
      if (ability.async) {
        const task = await AiAPI.createAsyncTask({ abilityCode: ability.code, input });
        content = "";
        for (let attempt = 0; attempt < 60; attempt++) {
          await new Promise((done) => setTimeout(done, 2000));
          if (disposed) return;
          const result = await AiAPI.getTaskStatus(task.taskId);
          if (result.status === "failed") throw new Error(result.errorMessage || "生成失败");
          if (result.status === "succeeded") {
            content = result.resultSnapshot || "";
            break;
          }
        }
        if (!content) throw new Error("生成超时，请稍后重试");
      } else {
        const result = await AiAPI.invokeAbility(ability.code, input);
        if (!result.success) throw new Error(result.errorMessage || "生成失败");
        content = result.content;
      }
      if (disposed) return;
      original.value = String(props.form[props.field.prop] ?? "");
      suggestion.value = content;
      visible.value = true;
    } catch (error) {
      if (!disposed) ElMessage.error(error instanceof Error ? error.message : "生成失败");
    } finally {
      loading.value = false;
    }
  }
  function parseTags(value: string): unknown {
    try {
      return JSON.parse(value);
    } catch {
      return null;
    }
  }
  function apply() {
    if (props.field.aiAbility?.code === "article_tags") {
      const raw: unknown = suggestion.value.trim().startsWith("[")
        ? parseTags(suggestion.value)
        : suggestion.value.split(",");
      if (!Array.isArray(raw)) {
        ElMessage.warning("标签建议格式无效");
        return;
      }
      const allowed = new Set((props.field.options || []).map((option) => Number(option.value)));
      props.form[props.field.prop] = raw
        .map((value) => Number(value))
        .filter((value) => Number.isFinite(value) && allowed.has(value));
    } else props.form[props.field.prop] = suggestion.value;
    visible.value = false;
    ElMessage.success("建议已填入，请保存文章以提交修改");
  }
  function discard() {
    visible.value = false;
  }
  return { enabled, loading, visible, original, suggestion, generate, apply, discard, loadStatus };
}
