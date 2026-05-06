<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="关键字" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="菜单名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="table-section">
      <div class="table-section__toolbar">
        <div class="table-section__toolbar--actions">
          <el-button
            v-hasPerm="['system:menu:add']"
            type="success"
            :icon="Plus"
            @click="openDialog('0')"
          >
            新增
          </el-button>
        </div>
      </div>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        row-key="menuId"
        :data="menuTableData"
        :tree-props="{
          children: 'children',
          hasChildren: 'hasChildren',
        }"
        class="table-section__content"
        @row-click="handleRowClick"
      >
        <el-table-column label="菜单名称" min-width="200">
          <template #default="scope">
            <div class="menu-name-cell">
              <span class="menu-name-cell__icon">
                <template v-if="isElementPlusIconName(scope.row.icon)">
                  <el-icon style="vertical-align: -0.15em">
                    <component :is="scope.row.icon" />
                  </el-icon>
                </template>
                <template v-else-if="scope.row.icon">
                  <span :class="`i-svg:${scope.row.icon}`" />
                </template>
              </span>
              <span class="menu-name-cell__text">{{ scope.row.menuName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="类型" align="center" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.menuType === MenuTypeEnum.CATALOG" type="warning">目录</el-tag>
            <el-tag v-if="scope.row.menuType === MenuTypeEnum.MENU" type="success">菜单</el-tag>
            <el-tag v-if="scope.row.menuType === MenuTypeEnum.BUTTON" type="danger">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="路由名称" align="left" width="150" prop="routeName" />
        <el-table-column label="路由路径" align="left" width="150" prop="path" />
        <el-table-column label="组件路径" align="left" width="250" prop="component" />
        <el-table-column label="权限标识" align="center" width="200" prop="perms" />

        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <DictTag v-model="scope.row.visible" code="sys_show_hide" />
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" width="80" prop="orderNum" />
        <el-table-column fixed="right" align="center" label="操作" width="220">
          <template #default="scope">
            <el-button
              v-if="
                scope.row.menuType == MenuTypeEnum.CATALOG ||
                scope.row.menuType == MenuTypeEnum.MENU
              "
              v-hasPerm="['system:menu:add']"
              type="primary"
              link
              size="small"
              :icon="Plus"
              @click.stop="openDialog(scope.row.menuId)"
            >
              新增
            </el-button>

            <el-button
              v-hasPerm="['system:menu:edit']"
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click.stop="openDialog(undefined, scope.row.menuId)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="['system:menu:remove']"
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click.stop="handleDelete(scope.row.menuId)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer
      v-model="dialogState.visible"
      :title="dialogState.title"
      :size="drawerSize"
      @close="closeDialog"
    >
      <el-form ref="menuFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="父级菜单" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            placeholder="选择上级菜单"
            :data="menuOptions"
            filterable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>

        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
        </el-form-item>

        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="formData.menuType" @change="handleMenuTypeChange">
            <el-radio :value="MenuTypeEnum.CATALOG">目录</el-radio>
            <el-radio :value="MenuTypeEnum.MENU">菜单</el-radio>
            <el-radio :value="MenuTypeEnum.BUTTON">按钮</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="formData.menuType == MenuTypeEnum.CATALOG || formData.menuType == MenuTypeEnum.MENU"
          label="是否外链"
          prop="isFrame"
        >
          <el-radio-group v-model="formData.isFrame">
            <el-radio value="0">是</el-radio>
            <el-radio value="1">否</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="formData.menuType == MenuTypeEnum.MENU && !isExternalLink && !isInnerLinkMenu"
          prop="routeName"
        >
          <template #label>
            <div class="flex-y-center">
              路由名称
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  如果需要开启缓存，需保证页面 defineOptions 中的 name 与此处一致，建议使用驼峰式
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-input v-model="formData.routeName" placeholder="User" />
        </el-form-item>

        <el-form-item
          v-if="formData.menuType == MenuTypeEnum.CATALOG || formData.menuType == MenuTypeEnum.MENU"
          prop="path"
        >
          <template #label>
            <div class="flex-y-center">
              路由路径
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  定义应用中不同页面对应的 URL 路径，目录需以 / 开头，菜单项不用。例如：系统管理目录
                  /system，系统管理下的用户管理菜单 user。
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-input
            v-if="formData.menuType == MenuTypeEnum.CATALOG"
            v-model="formData.path"
            placeholder="system"
          />
          <el-input v-else v-model="formData.path" placeholder="user 或 https://example.com" />
        </el-form-item>

        <el-form-item
          v-if="formData.menuType == MenuTypeEnum.MENU && !isExternalLink && !isInnerLinkMenu"
          prop="component"
        >
          <template #label>
            <div class="flex-y-center">
              组件路径
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  组件页面完整路径，相对于 src/views/，如 system/user/index，缺省后缀 .vue
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>

          <el-input v-model="formData.component" placeholder="system/user/index" style="width: 95%">
            <template v-if="formData.menuType == MenuTypeEnum.MENU" #prepend>src/views/</template>
            <template v-if="formData.menuType == MenuTypeEnum.MENU" #append>.vue</template>
          </el-input>
        </el-form-item>

        <el-form-item
          v-if="formData.menuType == MenuTypeEnum.MENU && !isExternalLink && !isInnerLinkMenu"
        >
          <template #label>
            <div class="flex-y-center">
              路由参数
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  组件页面使用 `useRoute().query.参数名` 获取路由参数值。
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>

          <div v-if="!formData.params || formData.params.length === 0">
            <el-button type="success" plain @click="formData.params = [{ key: '', value: '' }]">
              添加路由参数
            </el-button>
          </div>

          <div v-else>
            <div v-for="(item, index) in formData.params" :key="index">
              <el-input v-model="item.key" placeholder="参数名" style="width: 100px" />

              <span class="mx-1">=</span>

              <el-input v-model="item.value" placeholder="参数名" style="width: 100px" />

              <el-icon
                v-if="formData.params.indexOf(item) === formData.params.length - 1"
                class="ml-2 cursor-pointer color-[var(--el-color-success)]"
                style="vertical-align: -0.15em"
                @click="formData.params.push({ key: '', value: '' })"
              >
                <CirclePlusFilled />
              </el-icon>
              <el-icon
                class="ml-2 cursor-pointer color-[var(--el-color-danger)]"
                style="vertical-align: -0.15em"
                @click="formData.params.splice(formData.params.indexOf(item), 1)"
              >
                <DeleteFilled />
              </el-icon>
            </div>
          </div>
        </el-form-item>

        <el-form-item
          v-if="formData.menuType !== MenuTypeEnum.BUTTON"
          prop="visible"
          label="显示状态"
        >
          <DictSelect v-model="formData.visible" code="sys_show_hide" />
        </el-form-item>

        <el-form-item
          v-if="formData.menuType === MenuTypeEnum.MENU && !isExternalLink && !isInnerLinkMenu"
          label="缓存页面"
        >
          <el-radio-group v-model="formData.keepAlive">
            <el-radio :value="1">开启</el-radio>
            <el-radio :value="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="排序" prop="orderNum">
          <el-input-number
            v-model="formData.orderNum"
            style="width: 100px"
            controls-position="right"
            :min="0"
          />
        </el-form-item>

        <!-- 权限标识 -->
        <el-form-item v-if="formData.menuType == MenuTypeEnum.BUTTON" label="权限标识" prop="perms">
          <el-input v-model="formData.perms" placeholder="system:user:add" />
        </el-form-item>

        <el-form-item v-if="formData.menuType !== MenuTypeEnum.BUTTON" label="图标" prop="icon">
          <!-- 图标选择器 -->
          <icon-select v-model="formData.icon" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确定</el-button>
          <el-button @click="closeDialog">取消</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from "@/store/modules/app";
import { DeviceEnum } from "@/enums/settings";
import MenuAPI from "@/api/system/menu";
import type { MenuQueryParams, MenuForm, MenuItem, OptionItem } from "@/types/api";
import type { FormInstance, FormRules } from "element-plus";
import { MenuScopeEnum, MenuTypeEnum } from "@/enums/business";
import { Delete, Edit, Plus, Refresh, Search } from "@element-plus/icons-vue";
import { isElementPlusIconName } from "@/utils/element-icons";

defineOptions({
  name: "SysMenu",
  inheritAttrs: false,
});

const appStore = useAppStore();

// 表单引用
const queryFormRef = ref<FormInstance>();
const menuFormRef = ref<FormInstance>();

// 查询参数
const queryParams = reactive<MenuQueryParams>({});

// 列表数据
const menuTableData = ref<MenuItem[]>([]);
const menuOptions = ref<OptionItem[]>([]);
const loading = ref(false);

// 弹窗状态
const dialogState = reactive({
  title: "新增菜单",
  visible: false,
});

// 表单数据
const initialMenuFormData = ref<MenuForm>({
  menuId: undefined,
  parentId: "0",
  visible: "0",
  status: "0",
  scope: MenuScopeEnum.PLATFORM,
  orderNum: 1,
  menuType: MenuTypeEnum.MENU,
  isCache: "0",
  isFrame: "1",
  keepAlive: 1,
  params: [],
});
const formData = ref({ ...initialMenuFormData.value });
const selectedMenuId = ref<string | undefined>();

// 抽屉宽度
const drawerSize = computed(() => (appStore.device === DeviceEnum.DESKTOP ? "600px" : "90%"));

const isHttpPath = computed(
  () => !!(formData.value.path && /^https?:\/\//.test(formData.value.path))
);

/** 是否新窗口外链：is_frame=0 且 path 为 http(s) */
const isExternalLink = computed(() => {
  if (formData.value.menuType !== MenuTypeEnum.MENU) {
    return false;
  }
  return formData.value.isFrame === "0" && isHttpPath.value;
});

/** 是否系统内嵌外链：is_frame=1 且 path 为 http(s)（InnerLink 语义） */
const isInnerLinkMenu = computed(() => {
  if (formData.value.menuType !== MenuTypeEnum.MENU) {
    return false;
  }
  return formData.value.isFrame === "1" && isHttpPath.value;
});

/** 将 sys_menu.query JSON 转为表单键值行 */
function queryStringToParams(q?: string): NonNullable<MenuForm["params"]> {
  if (!q?.trim()) {
    return [];
  }
  try {
    const o = JSON.parse(q) as Record<string, unknown>;
    if (!o || typeof o !== "object" || Array.isArray(o)) {
      return [];
    }
    return Object.entries(o).map(([key, value]) => ({
      key,
      value: value == null ? "" : String(value),
    }));
  } catch {
    return [];
  }
}

// 验证规则
const validateRouteName = (_: unknown, value: string, callback: (error?: Error) => void) => {
  if (
    formData.value.menuType === MenuTypeEnum.MENU &&
    !isExternalLink.value &&
    !isInnerLinkMenu.value &&
    !value
  ) {
    callback(new Error("请输入路由名称"));
    return;
  }
  callback();
};
const validateComponent = (_: unknown, value: string, callback: (error?: Error) => void) => {
  if (
    formData.value.menuType === MenuTypeEnum.MENU &&
    !isExternalLink.value &&
    !isInnerLinkMenu.value &&
    !value
  ) {
    callback(new Error("请输入组件路径"));
    return;
  }
  callback();
};
const rules: FormRules = {
  parentId: [{ required: true, message: "请选择父级菜单", trigger: "blur" }],
  menuName: [{ required: true, message: "请输入菜单名称", trigger: "blur" }],
  menuType: [{ required: true, message: "请选择菜单类型", trigger: "blur" }],
  routeName: [{ validator: validateRouteName, trigger: "blur" }],
  path: [{ required: true, message: "请输入路由路径", trigger: "blur" }],
  component: [{ validator: validateComponent, trigger: "blur" }],
  visible: [{ required: true, message: "请选择显示状态", trigger: "change" }],
};

/**
 * 加载菜单列表数据
 */
function fetchData(): void {
  loading.value = true;
  MenuAPI.getList(queryParams)
    .then((data) => {
      menuTableData.value = data;
    })
    .finally(() => {
      loading.value = false;
    });
}

/**
 * 查询按钮点击事件
 */
function handleQuery(): void {
  fetchData();
}

/**
 * 重置查询
 */
function handleResetQuery(): void {
  queryFormRef.value?.resetFields();
  fetchData();
}

/**
 * 行点击事件
 */
function handleRowClick(row: MenuItem): void {
  selectedMenuId.value = row.menuId;
}

/**
 * 打开弹窗
 * @param parentId 父菜单ID
 * @param menuId 菜单ID（编辑时传入）
 */
function openDialog(parentId?: string, menuId?: string): void {
  MenuAPI.getOptions()
    .then((data) => {
      menuOptions.value = [{ value: "0", label: "顶级菜单", children: data }];
    })
    .then(() => {
      dialogState.visible = true;
      if (menuId) {
        dialogState.title = "编辑菜单";
        MenuAPI.getFormData(menuId).then((data) => {
          initialMenuFormData.value = { ...data };
          const fromQuery = queryStringToParams(data.query);
          formData.value = {
            ...data,
            keepAlive: data.isCache === "0" ? 1 : 0,
            params: fromQuery.length > 0 ? fromQuery : (data.params ?? []),
          };
        });
      } else {
        dialogState.title = "新增菜单";
        formData.value.parentId = parentId?.toString();
      }
    });
}

/**
 * 菜单类型切换事件
 */
function handleMenuTypeChange(): void {
  if (formData.value.menuType !== initialMenuFormData.value.menuType) {
    if (formData.value.menuType === MenuTypeEnum.MENU) {
      if (initialMenuFormData.value.menuType === MenuTypeEnum.CATALOG) {
        formData.value.component = "";
      } else {
        formData.value.path = initialMenuFormData.value.path;
        formData.value.component = initialMenuFormData.value.component;
      }
    }
  }
}

/**
 * 提交表单
 */
function handleSubmit(): void {
  menuFormRef.value?.validate((isValid) => {
    if (isValid) {
      const menuId = formData.value.menuId;
      if (menuId) {
        if (formData.value.parentId == menuId) {
          ElMessage.error("父级菜单不能为当前菜单");
          return;
        }
        MenuAPI.update(menuId, formData.value).then(() => {
          ElMessage.success("修改成功");
          closeDialog();
          fetchData();
        });
      } else {
        MenuAPI.create(formData.value).then(() => {
          ElMessage.success("新增成功");
          closeDialog();
          fetchData();
        });
      }
    }
  });
}

/**
 * 删除菜单
 * @param menuId 菜单ID
 */
function handleDelete(menuId?: string): void {
  if (!menuId) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => {
      loading.value = true;
      MenuAPI.deleteById(menuId)
        .then(() => {
          ElMessage.success("删除成功");
          fetchData();
        })
        .finally(() => {
          loading.value = false;
        });
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

/**
 * 关闭弹窗
 */
function closeDialog(): void {
  dialogState.visible = false;
  menuFormRef.value?.resetFields();
  menuFormRef.value?.clearValidate();
  formData.value = {
    menuId: undefined,
    parentId: "0",
    visible: "0",
    status: "0",
    scope: MenuScopeEnum.PLATFORM,
    orderNum: 1,
    menuType: MenuTypeEnum.MENU,
    isCache: "0",
    isFrame: "1",
    keepAlive: 1,
    params: [],
  };
}

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.menu-name-cell {
  display: inline-flex;
  align-items: center;
  max-width: 100%;
}

.menu-name-cell__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  min-width: 18px;
  margin-right: 6px;
}

.menu-name-cell__text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
