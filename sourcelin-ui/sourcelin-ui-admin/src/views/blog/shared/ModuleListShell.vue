<template>
  <div class="app-container">
    <ModuleQueryForm
      :query-params="queryParams"
      :query-fields="queryFields"
      @query="handleQuery"
      @reset="handleReset"
    />

    <ModuleTableSection
      :loading="loading"
      :display-rows="displayRows"
      :id-key="props.idKey"
      :tree-default-expand-all="treeDefaultExpandAll"
      :tree-children-key="treeChildrenKey"
      :columns="columns"
      :total="total"
      :query-params="queryParams"
      :selected-ids="selectedIds"
      :enable-create="enableCreate"
      :enable-edit="enableEdit"
      :enable-delete="enableDelete"
      :has-create-permission="hasCreatePermission"
      :has-edit-permission="hasEditPermission"
      :has-delete-permission="hasDeletePermission"
      :row-actions="rowActions"
      :resolve-element-icon-name="resolveElementIconName"
      :resolve-tag-type="resolveTagType"
      :resolve-tag-label="resolveTagLabel"
      :resolve-button-type="resolveButtonType"
      :is-row-action-visible="isRowActionVisible"
      :edit-text="editText"
      @create="openCreate"
      @delete="handleDelete"
      @selection-change="handleSelectionChange"
      @pagination="fetchData"
      @edit="openEdit"
      @row-action="handleRowAction"
    />

    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="760px"
      destroy-on-close
      @close="closeDialog"
    >
      <el-form ref="formRef" :model="formState" :rules="formRules" label-width="110px">
        <el-row :gutter="12">
          <el-col
            v-for="field in formFields"
            v-show="!isFieldHidden(field)"
            :key="field.prop"
            :span="field.span || 12"
          >
            <el-form-item :label="field.label" :prop="field.prop">
              <el-input
                v-if="field.type === 'input' || !field.type"
                v-model="formState[field.prop]"
                :placeholder="field.placeholder || `请输入${field.label}`"
                :disabled="isFieldDisabled(field)"
                clearable
              />
              <el-input
                v-else-if="field.type === 'textarea'"
                v-model="formState[field.prop]"
                type="textarea"
                :rows="field.rows || 4"
                :placeholder="field.placeholder || `请输入${field.label}`"
                :disabled="isFieldDisabled(field)"
              />
              <el-input-number
                v-else-if="field.type === 'number'"
                v-model="formState[field.prop]"
                :min="field.min ?? 0"
                :max="field.max ?? 999999"
                :disabled="isFieldDisabled(field)"
                controls-position="right"
                style="width: 100%"
              />
              <el-select
                v-else-if="field.type === 'select'"
                v-model="formState[field.prop]"
                :placeholder="field.placeholder || `请选择${field.label}`"
                :multiple="field.multiple"
                :disabled="isFieldDisabled(field)"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="option in field.options || []"
                  :key="`${field.prop}-${option.value}`"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-date-picker
                v-else-if="field.type === 'datetime'"
                v-model="formState[field.prop]"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                format="YYYY-MM-DD HH:mm:ss"
                :placeholder="field.placeholder || `请选择${field.label}`"
                :disabled="isFieldDisabled(field)"
                clearable
                style="width: 100%"
              />
              <el-tree-select
                v-else-if="field.type === 'treeSelect'"
                v-model="formState[field.prop]"
                :data="getTreeSelectData(field)"
                :props="getTreeSelectProps(field)"
                check-strictly
                filterable
                clearable
                :render-after-expand="false"
                :placeholder="field.placeholder || `请选择${field.label}`"
                :disabled="isFieldDisabled(field)"
                style="width: 100%"
              />
              <IconSelect
                v-else-if="field.type === 'icon'"
                v-model="formState[field.prop]"
                width="100%"
                popover-width="420px"
              />
              <el-switch
                v-else-if="field.type === 'switch'"
                v-model="formState[field.prop]"
                :active-value="field.activeValue ?? 1"
                :inactive-value="field.inactiveValue ?? 0"
                :disabled="isFieldDisabled(field)"
              />
              <SingleImageUpload
                v-else-if="field.type === 'image' && !field.multiple"
                v-model:model-value="formState[field.prop]"
              />
              <MultiImageUpload
                v-else-if="field.type === 'image' && field.multiple"
                v-model:model-value="formState[field.prop]"
              />
              <WangEditor
                v-else-if="field.type === 'editor'"
                v-model:model-value="formState[field.prop]"
                height="320px"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
          <el-button @click="closeDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from "element-plus";
import { useUserStore } from "@/store";
import IconSelect from "@/components/IconSelect/index.vue";
import { isElementPlusIconName } from "@/utils/element-icons";
import MultiImageUpload from "@/components/Upload/MultiImageUpload.vue";
import SingleImageUpload from "@/components/Upload/SingleImageUpload.vue";
import WangEditor from "@/components/WangEditor/index.vue";
import ModuleQueryForm from "./components/ModuleQueryForm.vue";
import ModuleTableSection from "./components/ModuleTableSection.vue";
import type {
  CrudApi,
  ElButtonType,
  FormField,
  QueryField,
  RowAction,
  TableColumn,
  TreeConfig,
} from "./types/module-list-shell";

defineOptions({
  name: "BlogModuleListShell",
  inheritAttrs: false,
});

const props = withDefaults(
  defineProps<{
    title: string;
    description?: string;
    moduleApi: CrudApi;
    idKey?: string;
    queryFields?: QueryField[];
    columns: TableColumn[];
    formFields?: FormField[];
    initialForm?: Record<string, any>;
    permissions?: {
      add?: string;
      edit?: string;
      remove?: string;
    };
    enableCreate?: boolean;
    enableEdit?: boolean;
    enableDelete?: boolean;
    rowActions?: RowAction[];
    serializeForm?: (form: Record<string, any>) => Record<string, any>;
    deserializeForm?: (row: Record<string, any>) => Record<string, any>;
    treeConfig?: TreeConfig;
    editText?: string;
  }>(),
  {
    description: "",
    idKey: "id",
    queryFields: () => [{ prop: "keywords", label: "关键字", type: "input" }],
    formFields: () => [],
    initialForm: () => ({}),
    permissions: () => ({}),
    enableCreate: true,
    enableEdit: true,
    enableDelete: true,
    rowActions: () => [],
    treeConfig: () => ({
      enabled: false,
      parentKey: "parentId",
      childrenKey: "children",
      defaultExpandAll: true,
    }),
  }
);

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const items = ref<Record<string, any>[]>([]);
const total = ref(0);
const selectedIds = ref<Array<string | number>>([]);
const editingId = ref<string | number>("");
const formRef = ref<FormInstance>();
const formState = reactive<Record<string, any>>({});

const queryParams = reactive<Record<string, any>>({
  page: 1,
  pageSize: 10,
});

const dialog = reactive({
  visible: false,
  title: "",
  mode: "create" as "create" | "edit",
});

const hasCreatePermission = computed(() => checkPermission(props.permissions?.add));
const hasEditPermission = computed(() => checkPermission(props.permissions?.edit));
const hasDeletePermission = computed(() => checkPermission(props.permissions?.remove));
const isTreeTable = computed(() => Boolean(props.treeConfig?.enabled));
const treeParentKey = computed(() => props.treeConfig?.parentKey || "parentId");
const treeChildrenKey = computed(() => props.treeConfig?.childrenKey || "children");
const treeDefaultExpandAll = computed(() => props.treeConfig?.defaultExpandAll !== false);

const displayRows = computed(() => {
  if (!isTreeTable.value) {
    return items.value;
  }
  return buildTreeRows(items.value, props.idKey, treeParentKey.value, treeChildrenKey.value);
});

const formRules = computed<FormRules>(() => {
  const rules: FormRules = {};
  props.formFields.forEach((field) => {
    if (field.required && !isFieldHidden(field)) {
      rules[field.prop] = [
        {
          required: true,
          message:
            field.type === "select" || field.type === "datetime" || field.type === "treeSelect"
              ? `请选择${field.label}`
              : `请输入${field.label}`,
          trigger:
            field.type === "select" || field.type === "datetime" || field.type === "treeSelect"
              ? "change"
              : "blur",
        },
      ];
    }
  });
  return rules;
});

function checkPermission(permission?: string): boolean {
  if (!permission) {
    return true;
  }
  const roles = userStore.userInfo?.roles || [];
  const perms = userStore.userInfo?.perms || [];
  if (roles.includes("ROOT") || perms.includes("*:*:*")) {
    return true;
  }
  return perms.includes(permission);
}

function initQueryParams() {
  props.queryFields.forEach((field) => {
    if (!(field.prop in queryParams)) {
      queryParams[field.prop] = undefined;
    }
  });
}

function resetFormState(row?: Record<string, any>) {
  Object.keys(formState).forEach((key) => {
    delete formState[key];
  });
  Object.assign(formState, props.initialForm || {});
  if (row) {
    const normalized = props.deserializeForm ? props.deserializeForm(row) : row;
    Object.assign(formState, normalized);
  }
  props.formFields.forEach((field) => {
    if (field.type !== "treeSelect") return;
    const rootVal = field.treeRootValue ?? 0;
    const v = formState[field.prop];
    if (v === undefined || v === null || v === "") {
      formState[field.prop] = rootVal;
    }
  });
}

function fetchData() {
  loading.value = true;
  props.moduleApi
    .getPage(queryParams)
    .then((data) => {
      items.value = data.items ?? [];
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

function handleReset() {
  props.queryFields.forEach((field) => {
    queryParams[field.prop] = undefined;
  });
  queryParams.page = 1;
  fetchData();
}

function extractRowId(row: Record<string, any>): string | number | undefined {
  const candidateKeys = [props.idKey, "id", "userId", "articleId", "commentId", "linkId"];
  for (const key of candidateKeys) {
    const value = row[key];
    if (value !== undefined && value !== null && value !== "") {
      return value as string | number;
    }
  }
  return undefined;
}

function handleSelectionChange(selection: Record<string, any>[]) {
  selectedIds.value = selection
    .map((item) => extractRowId(item))
    .filter((item): item is string | number => item !== undefined);
}

function openCreate() {
  if (!props.moduleApi.create || props.formFields.length === 0) {
    ElMessage.warning("当前模块未配置新增表单");
    return;
  }
  dialog.visible = true;
  dialog.title = `新增${props.title}`;
  dialog.mode = "create";
  editingId.value = "";
  resetFormState();
  nextTick(() => formRef.value?.clearValidate());
}

function openEdit(row: Record<string, any>) {
  if (!props.moduleApi.update || props.formFields.length === 0) {
    ElMessage.warning("当前模块未配置编辑表单");
    return;
  }
  const id = extractRowId(row);
  if (id === undefined) {
    ElMessage.warning("未找到记录主键");
    return;
  }
  dialog.visible = true;
  dialog.title = `${props.editText || "编辑"}${props.title}`;
  dialog.mode = "edit";
  editingId.value = id;
  if (props.moduleApi.getFormData) {
    props.moduleApi.getFormData(id).then((data) => {
      resetFormState(data);
    });
  } else {
    resetFormState(row);
  }
  nextTick(() => formRef.value?.clearValidate());
}

function closeDialog() {
  dialog.visible = false;
  editingId.value = "";
  formRef.value?.clearValidate();
}

function isFieldHidden(field: FormField): boolean {
  if (dialog.mode === "create" && field.hiddenOnCreate) return true;
  if (dialog.mode === "edit" && field.hiddenOnEdit) return true;
  if (field.visibleWhen && !field.visibleWhen(formState)) return true;
  return false;
}

function isFieldDisabled(field: FormField): boolean {
  if (typeof field.disabled === "function") {
    return field.disabled(formState);
  }
  return Boolean(field.disabled);
}

function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (!valid) return;
    const parentKey = treeParentKey.value;
    if (dialog.mode === "edit" && editingId.value !== "") {
      const pid = formState[parentKey];
      if (pid !== undefined && pid !== null && String(pid) === String(editingId.value)) {
        ElMessage.error("上级不能为当前节点");
        return;
      }
    }
    const raw: Record<string, any> = { ...formState };
    props.formFields.forEach((f) => {
      if (f.type === "treeSelect") {
        const rv = f.treeRootValue ?? 0;
        if (raw[f.prop] === null || raw[f.prop] === "") {
          raw[f.prop] = rv;
        }
      }
    });
    const payload = props.serializeForm ? props.serializeForm(raw) : raw;
    submitting.value = true;
    const request =
      dialog.mode === "create"
        ? props.moduleApi.create?.(payload)
        : props.moduleApi.update?.(editingId.value, payload);
    Promise.resolve(request)
      .then(() => {
        ElMessage.success(dialog.mode === "create" ? "新增成功" : "修改成功");
        closeDialog();
        fetchData();
      })
      .catch((err: any) => {
        // 统一错误兜底：展示后端返回的具体业务错误，或默认提示
        const msg = err?.response?.data?.message || err?.message || "操作失败，请稍后重试";
        ElMessage.error(msg);
      })
      .finally(() => {
        submitting.value = false;
      });
  });
}

function handleDelete(row?: Record<string, any>) {
  if (!props.moduleApi.deleteByIds) {
    ElMessage.warning("当前模块不支持删除");
    return;
  }
  const targetIds = row ? [extractRowId(row)] : selectedIds.value;
  const validIds = targetIds.filter((item): item is string | number => item !== undefined);
  if (validIds.length === 0) {
    ElMessage.warning("请先选择要删除的记录");
    return;
  }
  ElMessageBox.confirm("确认删除所选记录吗？", "提示", { type: "warning" }).then(() => {
    loading.value = true;
    props.moduleApi.deleteByIds!(validIds.length === 1 ? validIds[0] : validIds)
      .then(() => {
        ElMessage.success("删除成功");
        fetchData();
      })
      .catch((err: any) => {
        // 统一错误兜底：展示后端返回的具体业务错误，或默认提示
        const msg = err?.response?.data?.message || err?.message || "删除失败，请稍后重试";
        ElMessage.error(msg);
      })
      .finally(() => {
        loading.value = false;
      });
  });
}

function resolveTagLabel(column: TableColumn, value: unknown): string {
  const conf = column.tagMap?.[String(value)] ?? column.tagMap?.[value as any];
  if (!conf) return String(value ?? "-");
  if (typeof conf === "string") return conf;
  return conf.label;
}

function resolveTagType(
  column: TableColumn,
  value: unknown
): "success" | "warning" | "danger" | "info" | "primary" {
  const conf = column.tagMap?.[String(value)] ?? column.tagMap?.[value as any];
  if (!conf || typeof conf === "string") return "info";
  const candidate = conf.type;
  if (
    candidate === "success" ||
    candidate === "warning" ||
    candidate === "danger" ||
    candidate === "info" ||
    candidate === "primary"
  ) {
    return candidate;
  }
  return "info";
}

function resolveButtonType(type?: string): ElButtonType {
  if (!type) return "primary";
  if (
    type === "" ||
    type === "default" ||
    type === "text" ||
    type === "success" ||
    type === "warning" ||
    type === "danger" ||
    type === "info" ||
    type === "primary"
  ) {
    return type;
  }
  return "primary";
}

function isRowActionVisible(action: RowAction, row: Record<string, any>) {
  if (!action.visible) {
    return true;
  }
  return action.visible(row);
}

function resolveElementIconName(icon?: string | null) {
  if (!icon) return "";
  return isElementPlusIconName(icon) ? icon : "";
}

function getSelfAndDescendantIds(
  flat: Record<string, any>[],
  rootId: string | number,
  idKey: string,
  parentKey: string
): Set<string> {
  const result = new Set<string>();
  const queue: string[] = [String(rootId)];
  while (queue.length) {
    const id = queue.shift()!;
    result.add(id);
    flat.forEach((row) => {
      if (String(row[parentKey]) === id) {
        const cid = String(row[idKey]);
        if (!result.has(cid)) {
          queue.push(cid);
        }
      }
    });
  }
  return result;
}

function getTreeSelectProps(field: FormField) {
  const valueKey = field.treeValueProp || props.idKey;
  const labelKey = field.treeLabelProp || "name";
  return {
    value: valueKey,
    label: labelKey,
    children: treeChildrenKey.value,
  };
}

function getTreeSelectData(field: FormField): Record<string, any>[] {
  const valueKey = field.treeValueProp || props.idKey;
  const labelKey = field.treeLabelProp || "name";
  const rootVal = field.treeRootValue ?? 0;
  const rootLabel = field.treeRootLabel ?? "顶级";
  const parentKey = treeParentKey.value;
  const childrenKey = treeChildrenKey.value;
  let flat = items.value.map((row) => ({ ...row }));
  if (
    dialog.mode === "edit" &&
    editingId.value !== "" &&
    field.prop === parentKey &&
    isTreeTable.value
  ) {
    const excluded = getSelfAndDescendantIds(flat, editingId.value, props.idKey, parentKey);
    flat = flat.filter((row) => !excluded.has(String(row[props.idKey])));
  }
  const tree = buildTreeRows(flat, props.idKey, parentKey, childrenKey);
  return [
    {
      [valueKey]: rootVal,
      [labelKey]: rootLabel,
      [childrenKey]: tree,
    },
  ];
}

function buildTreeRows(
  sourceRows: Record<string, any>[],
  idKey: string,
  parentKey: string,
  childrenKey: string
) {
  if (sourceRows.length === 0) {
    return [];
  }

  const hasNestedChildren = sourceRows.some(
    (row) => Array.isArray(row[childrenKey]) && row[childrenKey].length > 0
  );
  if (hasNestedChildren) {
    return sourceRows;
  }

  const nodeMap = new Map<string, Record<string, any>>();
  sourceRows.forEach((row) => {
    const rawId = row[idKey];
    if (rawId === undefined || rawId === null || rawId === "") {
      return;
    }
    nodeMap.set(String(rawId), { ...row, [childrenKey]: [] });
  });

  const roots: Record<string, any>[] = [];
  nodeMap.forEach((node) => {
    const parentId = node[parentKey];
    const isRoot =
      parentId === undefined ||
      parentId === null ||
      parentId === "" ||
      parentId === 0 ||
      parentId === "0";

    if (isRoot) {
      roots.push(node);
      return;
    }

    const parentNode = nodeMap.get(String(parentId));
    if (!parentNode) {
      roots.push(node);
      return;
    }

    parentNode[childrenKey].push(node);
  });

  const cleanupEmptyChildren = (nodes: Record<string, any>[]) => {
    nodes.forEach((node) => {
      const children = node[childrenKey] as Record<string, any>[];
      if (!Array.isArray(children) || children.length === 0) {
        delete node[childrenKey];
        return;
      }
      cleanupEmptyChildren(children);
    });
  };

  cleanupEmptyChildren(roots);
  return roots;
}

function handleRowAction(action: RowAction, row: Record<string, any>) {
  const exec = () => Promise.resolve(action.onClick(row)).then(() => fetchData());
  if (action.confirmText) {
    ElMessageBox.confirm(action.confirmText, "提示", { type: "warning" }).then(exec);
    return;
  }
  exec();
}

onMounted(() => {
  initQueryParams();
  fetchData();
});
</script>

<style scoped lang="scss">
.icon-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.icon-cell__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  min-width: 18px;
  color: var(--el-color-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
