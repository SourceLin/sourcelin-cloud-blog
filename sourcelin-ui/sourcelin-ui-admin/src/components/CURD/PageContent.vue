<template>
  <div
    class="rounded bg-[var(--el-bg-color)] border border-[var(--el-border-color)] p-5 h-full md:flex flex-1 flex-col md:overflow-auto"
  >
    <PageToolbar
      :toolbar-left-btn="toolbarLeftBtn"
      :toolbar-right-btn="toolbarRightBtn"
      :cols="cols"
      :remove-ids="removeIds"
      @toolbar="handleToolbar"
    />

    <PageTableContainer
      ref="tableContainerRef"
      :content-config="contentConfig"
      :loading="loading"
      :page-data="pageData"
      :pk="pk"
      :cols="cols"
      :table-toolbar-btn="tableToolbarBtn"
      :has-button-perm="hasButtonPerm"
      :handle-modify="handleModify"
      :handle-operate="handleOperate"
      :is-element-plus-icon-name="isElementPlusIconName"
      @selection-change="handleSelectionChange"
      @filter-change="handleFilterChange"
    >
      <template v-for="(_, name) in $slots" #[name]="slotProps">
        <slot :name="name" v-bind="slotProps" />
      </template>
    </PageTableContainer>

    <PagePagination
      :show-pagination="showPagination"
      :pagination="pagination"
      :page-position="contentConfig?.pagePosition"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 导出弹窗 -->
    <el-dialog
      v-model="exportsModalVisible"
      :align-center="true"
      title="导出数据"
      width="600px"
      style="padding-right: 0"
      @close="handleCloseExportsModal"
    >
      <!-- 滚动 -->
      <el-scrollbar max-height="60vh">
        <!-- 表单 -->
        <el-form
          ref="exportsFormRef"
          style="padding-right: var(--el-dialog-padding-primary)"
          :model="exportsFormData"
          :rules="exportsFormRules"
        >
          <el-form-item label="文件名" prop="filename">
            <el-input v-model="exportsFormData.filename" clearable />
          </el-form-item>
          <el-form-item label="工作表名" prop="sheetname">
            <el-input v-model="exportsFormData.sheetname" clearable />
          </el-form-item>
          <el-form-item label="数据来源" prop="origin">
            <el-select v-model="exportsFormData.origin">
              <el-option label="当前数据 (当前页的数据)" :value="ExportsOriginEnum.CURRENT" />
              <el-option
                label="选中数据 (所有选中的数据)"
                :value="ExportsOriginEnum.SELECTED"
                :disabled="selectionData.length <= 0"
              />
              <el-option
                label="全量数据 (所有分页的数据)"
                :value="ExportsOriginEnum.REMOTE"
                :disabled="contentConfig.exportsAction === undefined"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="字段" prop="fields">
            <el-checkbox-group v-model="exportsFormData.fields">
              <template v-for="col in cols" :key="col.prop">
                <el-checkbox v-if="col.prop" :value="col.prop" :label="col.label" />
              </template>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <!-- 弹窗底部操作按钮 -->
      <template #footer>
        <div style="padding-right: var(--el-dialog-padding-primary)">
          <el-button type="primary" @click="handleExportsSubmit">确定</el-button>
          <el-button @click="handleCloseExportsModal">取消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 导入弹窗 -->
    <el-dialog
      v-model="importModalVisible"
      :align-center="true"
      title="导入数据"
      width="600px"
      style="padding-right: 0"
      @close="handleCloseImportModal"
    >
      <!-- 滚动 -->
      <el-scrollbar max-height="60vh">
        <!-- 表单 -->
        <el-form
          ref="importFormRef"
          style="padding-right: var(--el-dialog-padding-primary)"
          :model="importFormData"
          :rules="importFormRules"
        >
          <el-form-item label="文件" prop="files">
            <el-upload
              ref="uploadRef"
              v-model:file-list="importFormData.files"
              class="w-full"
              accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
              :drag="true"
              :limit="1"
              :auto-upload="false"
              :on-exceed="handleFileExceed"
            >
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
              <div class="el-upload__text">
                <span>将文件拖到此处，或点击上传</span>
                <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  *.xlsx / *.xls
                  <el-link
                    v-if="contentConfig.importTemplate"
                    type="primary"
                    :icon="Download"
                    underline="never"
                    @click="handleDownloadTemplate"
                  >
                    下载模板
                  </el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <!-- 弹窗底部操作按钮 -->
      <template #footer>
        <div style="padding-right: var(--el-dialog-padding-primary)">
          <el-button
            type="primary"
            :disabled="importFormData.files.length === 0"
            @click="handleImportSubmit"
          >
            确定
          </el-button>
          <el-button @click="handleCloseImportModal">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { hasPerm } from "@/utils/auth";
import { useThrottleFn } from "@vueuse/core";
import {
  Delete,
  Download,
  Edit,
  Operation,
  Plus,
  Refresh,
  Search,
  Upload,
  UploadFilled,
  View,
} from "@element-plus/icons-vue";
import {
  genFileId,
  type FormInstance,
  type FormRules,
  type UploadInstance,
  type UploadRawFile,
  type UploadUserFile,
} from "element-plus";
import ExcelJS from "exceljs";
import { reactive, ref, computed } from "vue";
import type { IContentConfig, IObject, IOperateData } from "./types";
import type { IToolsButton } from "./types";
import { isElementPlusIconName } from "@/utils/element-icons";
import PagePagination from "./parts/PagePagination.vue";
import PageTableContainer from "./parts/PageTableContainer.vue";
import PageToolbar from "./parts/PageToolbar.vue";

// 定义接收的属性
const props = defineProps<{ contentConfig: IContentConfig }>();
// 定义自定义事件
const emit = defineEmits<{
  addClick: [];
  exportClick: [];
  searchClick: [];
  toolbarClick: [name: string];
  editClick: [row: IObject];
  filterChange: [data: IObject];
  operateClick: [data: IOperateData];
}>();

// 表格工具栏按钮配置
const config = computed(() => props.contentConfig);
const buttonConfig = reactive<Record<string, IObject>>({
  add: { text: "新增", attrs: { icon: markRaw(Plus), type: "success" }, perm: "add" },
  delete: { text: "删除", attrs: { icon: markRaw(Delete), type: "danger" }, perm: "delete" },
  import: { text: "导入", attrs: { icon: markRaw(Upload), type: "" }, perm: "import" },
  export: { text: "导出", attrs: { icon: markRaw(Download), type: "" }, perm: "export" },
  refresh: { text: "刷新", attrs: { icon: markRaw(Refresh), type: "" }, perm: "*:*:*" },
  filter: { text: "筛选列", attrs: { icon: markRaw(Operation), type: "" }, perm: "*:*:*" },
  search: { text: "搜索", attrs: { icon: markRaw(Search), type: "" }, perm: "search" },
  imports: { text: "批量导入", attrs: { icon: markRaw(Upload), type: "" }, perm: "imports" },
  exports: { text: "批量导出", attrs: { icon: markRaw(Download), type: "" }, perm: "exports" },
  view: { text: "查看", attrs: { icon: markRaw(View), type: "primary" }, perm: "view" },
  edit: { text: "编辑", attrs: { icon: markRaw(Edit), type: "primary" }, perm: "edit" },
});

// 主键
const pk = props.contentConfig.pk ?? "id";
// 权限名称前缀
const authPrefix = computed(() => props.contentConfig.permPrefix);

// 获取按钮权限标识
function getButtonPerm(action: string): string | null {
  // 如果action已经包含完整路径(包含冒号)，则直接使用
  if (action.includes(":")) {
    return action;
  }
  // 否则使用权限前缀组合
  return authPrefix.value ? `${authPrefix.value}:${action}` : null;
}

// 检查是否有权限
function hasButtonPerm(action: string): boolean {
  const perm = getButtonPerm(action);
  // 如果没有设置权限标识，则默认具有权限
  if (!perm) return true;
  return hasPerm(perm);
}

// 创建工具栏按钮
function createToolbar(toolbar: Array<string | IToolsButton>, attr = {}) {
  return toolbar.map((item) => {
    const isString = typeof item === "string";
    return {
      name: isString ? item : item?.name || "",
      text: isString ? buttonConfig[item].text : item?.text,
      attrs: {
        ...attr,
        ...(isString ? buttonConfig[item].attrs : item?.attrs),
      },
      render: isString ? undefined : (item?.render ?? undefined),
      perm: isString
        ? getButtonPerm(buttonConfig[item].perm)
        : item?.perm
          ? getButtonPerm(item.perm as string)
          : "*:*:*",
    };
  });
}

// 左侧工具栏按钮
const toolbarLeftBtn = computed(() => {
  if (!config.value.toolbar || config.value.toolbar.length === 0) return [];
  return createToolbar(config.value.toolbar, {});
});

// 右侧工具栏按钮
const toolbarRightBtn = computed(() => {
  if (!config.value.defaultToolbar || config.value.defaultToolbar.length === 0) return [];
  return createToolbar(config.value.defaultToolbar, { circle: true });
});

// 表格操作工具栏
const tableToolbar = config.value.cols[config.value.cols.length - 1].operat ?? ["edit", "delete"];
const tableToolbarBtn = createToolbar(tableToolbar, { link: true, size: "small" });

// 表格相关
const cols = ref(
  props.contentConfig.cols.map((col) => {
    if (col.initFn) {
      col.initFn(col);
    }
    if (col.show === undefined) {
      col.show = true;
    }
    if (col.prop !== undefined && col.columnKey === undefined && col["column-key"] === undefined) {
      col.columnKey = col.prop;
    }
    if (
      col.type === "selection" &&
      col.reserveSelection === undefined &&
      col["reserve-selection"] === undefined
    ) {
      // 配合表格row-key实现跨页多选
      col.reserveSelection = true;
    }
    return col;
  })
);
// 加载状态
const loading = ref(false);
// 列表数据
const pageData = ref<IObject[]>([]);
// 显示分页
const showPagination = props.contentConfig.pagination !== false;
// 分页配置
const defaultPagination = {
  background: true,
  layout: "total, sizes, prev, pager, next, jumper",
  pageSize: 20,
  pageSizes: [10, 20, 30, 50],
  total: 0,
  currentPage: 1,
};
const pagination = reactive(
  typeof props.contentConfig.pagination === "object"
    ? { ...defaultPagination, ...props.contentConfig.pagination }
    : defaultPagination
);
// 分页相关的请求参数
const request = props.contentConfig.request ?? {
  pageName: "page",
  limitName: "pageSize",
};

const tableContainerRef = ref<InstanceType<typeof PageTableContainer>>();

// 行选中
const selectionData = ref<IObject[]>([]);
// 删除ID集合 用于批量删除
const removeIds = ref<(number | string)[]>([]);
function handleSelectionChange(selection: any[]) {
  selectionData.value = selection;
  removeIds.value = selection.map((item) => item[pk]);
}

// 获取行选中
function getSelectionData() {
  return selectionData.value;
}

// 刷新
function handleRefresh(isRestart = false) {
  fetchPageData(lastFormData, isRestart);
}

// 删除
function handleDelete(id?: number | string) {
  const ids = [id || removeIds.value].join(",");
  if (!ids) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    function () {
      if (props.contentConfig.deleteAction) {
        props.contentConfig.deleteAction(ids).then(
          () => {
            ElMessage.success("删除成功");
            removeIds.value = [];
            // 清空选中项
            tableContainerRef.value?.clearSelection();
            handleRefresh(true);
          },
          () => {
            // 交由全局错误处理
          }
        );
      } else {
        ElMessage.error("未配置deleteAction");
      }
    },
    () => {
      // 用户取消
    }
  );
}

// 导出表单
const fields: string[] = [];
cols.value.forEach((item) => {
  if (item.prop !== undefined) {
    fields.push(item.prop);
  }
});
const enum ExportsOriginEnum {
  CURRENT = "current",
  SELECTED = "selected",
  REMOTE = "remote",
}
const exportsModalVisible = ref(false);
const exportsFormRef = ref<FormInstance>();
const exportsFormData = reactive({
  filename: "",
  sheetname: "",
  fields,
  origin: ExportsOriginEnum.CURRENT,
});
const exportsFormRules: FormRules = {
  fields: [{ required: true, message: "请选择字段" }],
  origin: [{ required: true, message: "请选择数据来源" }],
};
// 打开导出弹窗
function handleOpenExportsModal() {
  exportsModalVisible.value = true;
}
// 导出确认
const handleExportsSubmit = useThrottleFn(() => {
  exportsFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      handleExports();
      handleCloseExportsModal();
    }
  });
}, 3000);
// 关闭导出弹窗
function handleCloseExportsModal() {
  exportsModalVisible.value = false;
  exportsFormRef.value?.resetFields();
  nextTick(() => {
    exportsFormRef.value?.clearValidate();
  });
}
// 导出
function handleExports() {
  const filename = exportsFormData.filename
    ? exportsFormData.filename
    : props.contentConfig.permPrefix || "export";
  const sheetname = exportsFormData.sheetname ? exportsFormData.sheetname : "sheet";
  const workbook = new ExcelJS.Workbook();
  const worksheet = workbook.addWorksheet(sheetname);
  const columns: Partial<ExcelJS.Column>[] = [];
  cols.value.forEach((col) => {
    if (col.label && col.prop && exportsFormData.fields.includes(col.prop)) {
      columns.push({ header: col.label, key: col.prop });
    }
  });
  worksheet.columns = columns;
  if (exportsFormData.origin === ExportsOriginEnum.REMOTE) {
    if (props.contentConfig.exportsAction) {
      props.contentConfig.exportsAction(lastFormData).then((data) => {
        worksheet.addRows(data);
        workbook.xlsx.writeBuffer().then(
          (buffer) => {
            saveXlsx(buffer, filename as string);
          },
          (error) => console.log(error)
        );
      });
    } else {
      ElMessage.error("未配置exportsAction");
    }
  } else {
    worksheet.addRows(
      exportsFormData.origin === ExportsOriginEnum.SELECTED ? selectionData.value : pageData.value
    );
    workbook.xlsx.writeBuffer().then(
      (buffer) => {
        saveXlsx(buffer, filename as string);
      },
      (error) => console.log(error)
    );
  }
}

// 导入表单
let isFileImport = false;
const uploadRef = ref<UploadInstance>();
const importModalVisible = ref(false);
const importFormRef = ref<FormInstance>();
const importFormData = reactive<{
  files: UploadUserFile[];
}>({
  files: [],
});
const importFormRules: FormRules = {
  files: [{ required: true, message: "请选择文件" }],
};
// 打开导入弹窗
function handleOpenImportModal(isFile: boolean = false) {
  importModalVisible.value = true;
  isFileImport = isFile;
}
// 覆盖前一个文件
function handleFileExceed(files: File[]) {
  uploadRef.value!.clearFiles();
  const file = files[0] as UploadRawFile;
  file.uid = genFileId();
  uploadRef.value!.handleStart(file);
}
// 下载导入模板
function handleDownloadTemplate() {
  const importTemplate = props.contentConfig.importTemplate;
  if (typeof importTemplate === "string") {
    window.open(importTemplate);
  } else if (typeof importTemplate === "function") {
    importTemplate().then((response) => {
      const fileData = response.data;
      const fileName = decodeURI(
        response.headers["content-disposition"].split(";")[1].split("=")[1]
      );
      saveXlsx(fileData, fileName);
    });
  } else {
    ElMessage.error("未配置importTemplate");
  }
}
// 导入确认
const handleImportSubmit = useThrottleFn(() => {
  importFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (isFileImport) {
        handleImport();
      } else {
        handleImports();
      }
    }
  });
}, 3000);
// 关闭导入弹窗
function handleCloseImportModal() {
  importModalVisible.value = false;
  importFormRef.value?.resetFields();
  nextTick(() => {
    importFormRef.value?.clearValidate();
  });
}
// 文件导入
function handleImport() {
  const importAction = props.contentConfig.importAction;
  if (importAction === undefined) {
    ElMessage.error("未配置importAction");
    return;
  }
  importAction(importFormData.files[0].raw as File).then(() => {
    ElMessage.success("导入数据成功");
    handleCloseImportModal();
    handleRefresh(true);
  });
}
// 导入
function handleImports() {
  const importsAction = props.contentConfig.importsAction;
  if (importsAction === undefined) {
    ElMessage.error("未配置importsAction");
    return;
  }
  // 获取选择的文件
  const file = importFormData.files[0].raw as File;
  // 创建Workbook实例
  const workbook = new ExcelJS.Workbook();
  // 使用FileReader对象来读取文件内容
  const fileReader = new FileReader();
  // 二进制字符串的形式加载文件
  fileReader.readAsArrayBuffer(file);
  fileReader.onload = (ev) => {
    if (ev.target !== null && ev.target.result !== null) {
      const result = ev.target.result as ArrayBuffer;
      // 从 buffer 中加载并解析数据
      workbook.xlsx.load(result).then(
        (workbook) => {
          // 解析后的数据
          const data = [];
          // 获取第一个worksheet内容
          const worksheet = workbook.getWorksheet(1);
          if (worksheet) {
            // 获取第一行的标题
            const fields: any[] = [];
            worksheet.getRow(1).eachCell((cell) => {
              fields.push(cell.value);
            });
            // 遍历工作表的每一行（从第二行开始，因为第一行通常是标题行）
            for (let rowNumber = 2; rowNumber <= worksheet.rowCount; rowNumber++) {
              const rowData: IObject = {};
              const row = worksheet.getRow(rowNumber);
              // 遍历当前行的每个单元格
              row.eachCell((cell, colNumber) => {
                // 获取标题对应的键，并将当前单元格的值存储到相应的属性名中
                rowData[fields[colNumber - 1]] = cell.value;
              });
              // 将当前行的数据对象添加到数组中
              data.push(rowData);
            }
          }
          if (data.length === 0) {
            ElMessage.error("未解析到数据");
            return;
          }
          importsAction(data).then(() => {
            ElMessage.success("导入数据成功");
            handleCloseImportModal();
            handleRefresh(true);
          });
        },
        (error) => console.log(error)
      );
    } else {
      ElMessage.error("读取文件失败");
    }
  };
}
// 操作人"
function handleToolbar(name: string) {
  switch (name) {
    case "refresh":
      handleRefresh();
      break;
    case "exports":
      handleOpenExportsModal();
      break;
    case "imports":
      handleOpenImportModal();
      break;
    case "search":
      emit("searchClick");
      break;
    case "add":
      emit("addClick");
      break;
    case "delete":
      handleDelete();
      break;
    case "import":
      handleOpenImportModal(true);
      break;
    case "export":
      emit("exportClick");
      break;
    default:
      emit("toolbarClick", name);
      break;
  }
}

// 操作人"
function handleOperate(data: IOperateData) {
  switch (data.name) {
    case "delete":
      if (props.contentConfig?.deleteAction) {
        handleDelete(data.row[pk]);
      } else {
        emit("operateClick", data);
      }
      break;
    default:
      emit("operateClick", data);
      break;
  }
}

// 属性修改
function handleModify(field: string, value: boolean | string | number, row: Record<string, any>) {
  if (props.contentConfig.modifyAction) {
    props.contentConfig.modifyAction({
      [pk]: row[pk],
      field,
      value,
    });
  } else {
    ElMessage.error("未配置modifyAction");
  }
}

// 分页切换
function handleSizeChange(value: number) {
  pagination.pageSize = value;
  handleRefresh();
}
function handleCurrentChange(value: number) {
  pagination.currentPage = value;
  handleRefresh();
}

// 远程数据筛选
let filterParams: IObject = {};
function handleFilterChange(newFilters: any) {
  const filters: IObject = {};
  for (const key in newFilters) {
    const col = cols.value.find((col) => {
      return col.columnKey === key || col["column-key"] === key;
    });
    if (col && col.filterJoin !== undefined) {
      filters[key] = newFilters[key].join(col.filterJoin);
    } else {
      filters[key] = newFilters[key];
    }
  }
  filterParams = { ...filterParams, ...filters };
  emit("filterChange", filterParams);
}

// 获取筛选条件
function getFilterParams() {
  return filterParams;
}

// 获取分页数据
let lastFormData = {};
function fetchPageData(formData: IObject = {}, isRestart = false) {
  loading.value = true;
  // 上一次搜索条件
  lastFormData = formData;
  // 重置页码
  if (isRestart) {
    pagination.currentPage = 1;
  }
  props.contentConfig
    .indexAction(
      showPagination
        ? {
            [request.pageName]: pagination.currentPage,
            [request.limitName]: pagination.pageSize,
            ...getFilterParams(),
            ...formData,
          }
        : {
            ...getFilterParams(),
            ...formData,
          }
    )
    .then((data) => {
      if (showPagination) {
        const pageDataResult = data as { items?: IObject[]; total?: number; page?: number };
        pagination.total = pageDataResult.total ?? 0;
        pagination.currentPage = pageDataResult.page ?? pagination.currentPage;
        pageData.value = pageDataResult.items ?? [];
      } else {
        pageData.value = Array.isArray(data) ? data : [];
      }
    })
    .finally(() => {
      loading.value = false;
    });
}
fetchPageData();

// 导出Excel
function exportPageData(formData: IObject = {}) {
  if (props.contentConfig.exportAction) {
    props.contentConfig.exportAction(formData).then((response) => {
      const fileData = response.data;
      const fileName = decodeURI(
        response.headers["content-disposition"].split(";")[1].split("=")[1]
      );
      saveXlsx(fileData, fileName);
    });
  } else {
    ElMessage.error("未配置exportAction");
  }
}

// 浏览器保存文件
function saveXlsx(fileData: any, fileName: string) {
  const fileType =
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

  const blob = new Blob([fileData], { type: fileType });
  const downloadUrl = window.URL.createObjectURL(blob);

  const downloadLink = document.createElement("a");
  downloadLink.href = downloadUrl;
  downloadLink.download = fileName;

  document.body.appendChild(downloadLink);
  downloadLink.click();

  document.body.removeChild(downloadLink);
  window.URL.revokeObjectURL(downloadUrl);
}

// 暴露的属性和方法
defineExpose({ fetchPageData, exportPageData, getFilterParams, getSelectionData, handleRefresh });
</script>

<style lang="scss" scoped>
.toolbar-left,
.toolbar-right {
  .el-button {
    margin-right: 0 !important;
    margin-left: 0 !important;
  }
}
</style>
