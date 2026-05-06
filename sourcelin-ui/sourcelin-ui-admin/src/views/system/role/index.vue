<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="关键字">
          <el-input
            v-model="queryParams.keywords"
            placeholder="角色名称"
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
            v-hasPerm="'system:role:add'"
            type="success"
            :icon="Plus"
            @click="handleCreateClick()"
          >
            新增
          </el-button>
          <el-button
            v-hasPerm="'system:role:remove'"
            type="danger"
            :disabled="ids.length === 0"
            :icon="Delete"
            @click="handleBatchDelete()"
          >
            删除
          </el-button>
        </div>
      </div>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="roleList"
        highlight-current-row
        border
        class="table-section__content"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="角色名称" prop="roleName" min-width="100" />
        <el-table-column label="角色编码" prop="roleKey" width="150" />

        <el-table-column label="数据权限" align="center" width="140" prop="dataScopeLabel" />

        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <DictTag v-model="scope.row.status" code="sys_normal_disable" />
          </template>
        </el-table-column>

        <el-table-column label="排序" align="center" width="80" prop="roleSort" />

        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
              v-if="!isSuperAdminRole(scope.row)"
              v-hasPerm="'system:role:edit'"
              type="primary"
              size="small"
              link
              :icon="Position"
              @click="handleAssignPermClick(scope.row)"
            >
              分配权限
            </el-button>
            <el-button
              v-hasPerm="'system:role:edit'"
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEditClick(scope.row.roleId)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="'system:role:remove'"
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(scope.row.roleId)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        @pagination="fetchList"
      />
    </el-card>

    <!-- 角色表单弹窗 -->
    <el-dialog
      v-model="dialogState.visible"
      :title="dialogState.title"
      width="600px"
      @close="closeDialog"
    >
      <el-form ref="roleFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="角色编码" prop="roleKey">
          <el-input v-model="formData.roleKey" placeholder="请输入角色编码" />
        </el-form-item>

        <el-form-item label="数据权限" prop="dataScope">
          <el-select v-model="formData.dataScope" placeholder="请选择数据权限" style="width: 100%">
            <el-option key="1" label="全部数据" value="1" />
            <el-option key="2" label="部门及子部门数据" value="2" />
            <el-option key="3" label="本部门数据" value="3" />
            <el-option key="4" label="本人数据" value="4" />
            <el-option key="5" label="自定义部门数据" value="5" />
          </el-select>
        </el-form-item>

        <!-- 自定义部门选择 -->
        <el-form-item v-if="formData.dataScope === '5'" label="选择部门" prop="deptIds">
          <el-tree-select
            v-model="formData.deptIds"
            :data="deptOptions"
            multiple
            :render-after-expand="false"
            check-strictly
            placeholder="请选择部门"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <DictSelect v-model="formData.status" code="sys_normal_disable" />
        </el-form-item>

        <el-form-item label="排序" prop="roleSort">
          <el-input-number
            v-model="formData.roleSort"
            controls-position="right"
            :min="0"
            style="width: 100px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确定</el-button>
          <el-button @click="closeDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-drawer
      v-model="assignPermDialogVisible"
      :title="'【' + checkedRole.roleName + '】权限分配'"
      :size="drawerSize"
    >
      <div class="flex-x-between">
        <el-input v-model="permKeywords" clearable class="w-[150px]" placeholder="菜单权限名称">
          <template #prefix>
            <Search />
          </template>
        </el-input>

        <div class="flex-center ml-5">
          <el-button type="primary" size="small" plain @click="togglePermTree">
            <template #icon>
              <Switch />
            </template>
            {{ isExpanded ? "收缩" : "展开" }}
          </el-button>
          <el-checkbox
            v-model="parentChildLinked"
            class="ml-5"
            @change="handleParentChildLinkedChange"
          >
            父子联动
          </el-checkbox>

          <el-tooltip placement="bottom">
            <template #content>
              如果只需勾选菜单权限，不需要勾选子菜单或者按钮权限，请关闭父子联动
            </template>
            <el-icon class="ml-1 color-[--el-color-primary] inline-block cursor-pointer">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </div>
      </div>

      <el-tree
        ref="permTreeRef"
        node-key="value"
        show-checkbox
        :data="menuPermOptions"
        :filter-node-method="handlePermFilter"
        :default-expand-all="true"
        :check-strictly="!parentChildLinked"
        class="mt-5"
      >
        <template #default="{ data }">
          {{ data.label }}
        </template>
      </el-tree>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-hasPerm="'system:role:edit'" type="primary" @click="handleAssignPermSubmit">
            确定
          </el-button>
          <el-button @click="assignPermDialogVisible = false">取消</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from "@/store/modules/app";
import { DeviceEnum } from "@/enums/settings";

import RoleAPI from "@/api/system/role";
import type { RoleItem, RoleForm, RoleQueryParams } from "@/types/api";
import MenuAPI from "@/api/system/menu";
import DeptAPI from "@/api/system/dept";
import { Delete, Edit, Plus, Position, Refresh, Search } from "@element-plus/icons-vue";

defineOptions({
  name: "Role",
  inheritAttrs: false,
});

const appStore = useAppStore();

const queryFormRef = ref();
const roleFormRef = ref();
const permTreeRef = ref();

const loading = ref(false);
const ids = ref<string[]>([]);
const total = ref(0);

const queryParams = reactive<RoleQueryParams>({
  page: 1,
  pageSize: 10,
});

// 角色表格数据
const roleList = ref<RoleItem[]>([]);
// 菜单权限下拉
const menuPermOptions = ref<OptionItem[]>([]);
// 部门下拉选项
const deptOptions = ref<OptionItem[]>([]);

// 弹窗
const dialogState = reactive({
  title: "",
  visible: false,
});

const drawerSize = computed(() => (appStore.device === DeviceEnum.DESKTOP ? "600px" : "90%"));

// 角色表单
const formData = reactive<RoleForm>({
  roleSort: 1,
  status: "0",
  dataScope: "1",
});

const rules = reactive({
  roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
  roleKey: [{ required: true, message: "请输入角色编码", trigger: "blur" }],
  dataScope: [{ required: true, message: "请选择数据权限", trigger: "blur" }],
  deptIds: [{ required: true, message: "请选择部门", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
});

// 选中的角色
interface CheckedRole {
  roleId?: string;
  roleName?: string;
}
const checkedRole = ref<CheckedRole>({});
const assignPermDialogVisible = ref(false);

const permKeywords = ref("");
const isExpanded = ref(true);

const parentChildLinked = ref(true);

/**
 * 加载角色列表数据
 */
async function fetchList(): Promise<void> {
  loading.value = true;
  try {
    const data = await RoleAPI.getPage(queryParams);
    roleList.value = data.items;
    total.value = data.total ?? 0;
  } finally {
    loading.value = false;
  }
}

// 查询（重置页码后获取数据）
function handleQuery(): void {
  queryParams.page = 1;
  fetchList();
}

/**
 * 重置查询条件
 */
function resetQuery(): void {
  queryFormRef.value?.resetFields();
}

/**
 * 重置查询条件并重新查询
 */
function handleResetQuery(): void {
  resetQuery();
  handleQuery();
}

// 行复选框选中
function handleSelectionChange(selection: any): void {
  ids.value = selection.map((item: any) => item.roleId);
}

/**
 * 打开表单弹窗
 */
function openDialog(): void {
  dialogState.visible = true;
}

/**
 * 关闭表单弹窗
 */
function closeDialog(): void {
  dialogState.visible = false;
  resetForm();
}

/**
 * 重置表单数据和验证状态
 */
function resetForm(): void {
  roleFormRef.value?.resetFields();
  roleFormRef.value?.clearValidate();

  formData.roleId = undefined;
  formData.roleSort = 1;
  formData.status = "0";
  formData.dataScope = "1";
  formData.deptIds = undefined;
  formData.menuIds = undefined;
  formData.roleName = undefined;
  formData.roleKey = undefined;
  formData.remark = undefined;
}

/**
 * 新增按钮点击事件
 */
async function handleCreateClick(): Promise<void> {
  dialogState.title = "新增角色";
  if (deptOptions.value.length === 0) {
    deptOptions.value = await DeptAPI.getOptions();
  }
  openDialog();
}

/**
 * 编辑按钮点击事件
 * @param roleId 角色ID
 */
async function handleEditClick(roleId?: string): Promise<void> {
  if (!roleId) return;
  dialogState.title = "修改角色";
  if (deptOptions.value.length === 0) {
    deptOptions.value = await DeptAPI.getOptions();
  }
  const data = await RoleAPI.getFormData(roleId);
  Object.assign(formData, data);
  openDialog();
}

// 提交角色表单
async function handleSubmit(): Promise<void> {
  const valid = await roleFormRef.value?.validate().then(
    () => true,
    () => false
  );
  if (!valid) return;

  const submitData = { ...formData };
  if (submitData.dataScope !== "5") {
    submitData.deptIds = undefined;
  }

  loading.value = true;
  try {
    const roleId = formData.roleId;
    if (roleId) {
      await RoleAPI.update(roleId, submitData);
      ElMessage.success("修改成功");
    } else {
      await RoleAPI.create(submitData);
      ElMessage.success("新增成功");
    }
    closeDialog();
    handleResetQuery();
  } finally {
    loading.value = false;
  }
}

// 删除角色
function handleDelete(roleId?: string): void {
  const roleIds = roleId ? String(roleId) : ids.value.join(",");
  if (!roleIds) {
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
      RoleAPI.deleteByIds(roleIds)
        .then(() => {
          ElMessage.success("删除成功");
          handleResetQuery();
        })
        .finally(() => (loading.value = false));
    },
    () => {
      ElMessage.info("已取消删除");
    }
  );
}

/**
 * 批量删除按钮点击事件
 */
function handleBatchDelete(): void {
  handleDelete();
}

function isSuperAdminRole(role: RoleItem): boolean {
  return role.roleKey === "admin" || role.roleId === "1";
}

// 打开分配菜单权限弹窗
async function handleAssignPermClick(row: RoleItem): Promise<void> {
  const roleId = row.roleId;
  if (roleId) {
    assignPermDialogVisible.value = true;
    loading.value = true;

    checkedRole.value.roleId = roleId;
    checkedRole.value.roleName = row.roleName;

    // 获取所有的菜单
    menuPermOptions.value = await MenuAPI.getOptions();

    // 回显角色已拥有的菜单
    RoleAPI.getRoleMenuIds(roleId)
      .then((data) => {
        const checkedMenuIds = data;
        checkedMenuIds.forEach((menuId) => permTreeRef.value!.setChecked(menuId, true, false));
      })
      .finally(() => {
        loading.value = false;
      });
  }
}

// 分配菜单权限提交
function handleAssignPermSubmit() {
  const roleId = checkedRole.value.roleId;
  if (roleId) {
    const checkedMenuIds: string[] = permTreeRef
      .value!.getCheckedNodes(false, true)
      .map((node: any) => String(node.value));

    loading.value = true;
    RoleAPI.updateRoleMenus(roleId, checkedMenuIds)
      .then(() => {
        ElMessage.success("分配权限成功");
        assignPermDialogVisible.value = false;
        handleResetQuery();
      })
      .finally(() => {
        loading.value = false;
      });
  }
}

// 展开/收缩 菜单权限树
function togglePermTree(): void {
  isExpanded.value = !isExpanded.value;
  if (permTreeRef.value) {
    Object.values(permTreeRef.value.store.nodesMap).forEach((node: any) => {
      if (isExpanded.value) {
        node.expand();
      } else {
        node.collapse();
      }
    });
  }
}

// 权限筛选
watch(permKeywords, (val) => {
  permTreeRef.value!.filter(val);
});

function handlePermFilter(
  value: string,
  data: {
    [key: string]: any;
  }
) {
  if (!value) return true;
  return data.label.includes(value);
}

// 父子菜单节点是否联动
function handleParentChildLinkedChange(val: any): void {
  parentChildLinked.value = val;
}

onMounted(() => {
  handleQuery();
});
</script>
