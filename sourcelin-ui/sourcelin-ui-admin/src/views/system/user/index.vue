<!-- 用户管理 -->
<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 部门树 -->
      <el-col :lg="4" :xs="24" class="mb-[12px]">
        <UserDeptTree v-model="queryParams.deptId" @node-click="handleQuery" />
      </el-col>

      <!-- 用户列表 -->
      <el-col :lg="20" :xs="24">
        <!-- 搜索区域 -->
        <div class="filter-section">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
            <el-form-item label="用户名" prop="userName">
              <el-input
                v-model="queryParams.userName"
                placeholder="请输入用户名"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>

            <el-form-item label="状态" prop="status">
              <DictSelect
                v-model="queryParams.status"
                code="sys_normal_disable"
                placeholder="全部"
                clearable
                style="width: 100px"
              />
            </el-form-item>

            <el-form-item label="创建时间">
              <el-date-picker
                v-model="queryParams.createTime"
                :editable="false"
                type="daterange"
                range-separator="~"
                start-placeholder="开始时间"
                end-placeholder="截止时间"
                value-format="YYYY-MM-DD"
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
                v-hasPerm="['system:user:add']"
                type="success"
                :icon="Plus"
                @click="handleCreateClick"
              >
                新增
              </el-button>
              <el-button
                v-hasPerm="'system:user:remove'"
                type="danger"
                :icon="Delete"
                :disabled="!hasSelection"
                @click="handleDelete()"
              >
                删除
              </el-button>
            </div>
            <div class="table-section__toolbar--tools">
              <el-button v-hasPerm="'system:user:import'" :icon="Upload" @click="openImportDialog">
                导入
              </el-button>

              <el-button v-hasPerm="'system:user:export'" :icon="Download" @click="exportUsers">
                导出
              </el-button>
            </div>
          </div>

          <el-table
            v-loading="loading"
            :data="userList"
            border
            stripe
            highlight-current-row
            class="table-section__content"
            row-key="userId"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户名" prop="userName" />
            <el-table-column label="昵称" width="200" align="center" prop="nickName" />
            <el-table-column label="性别" width="100" align="center">
              <template #default="scope">
                <DictTag v-model="scope.row.sex" code="sys_user_sex" />
              </template>
            </el-table-column>
            <el-table-column label="部门" width="120" align="center" prop="deptName" />
            <el-table-column label="角色" align="center" prop="roleNames" min-width="160" />
            <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
            <el-table-column label="邮箱" align="center" prop="email" width="160" />
            <el-table-column label="状态" align="center" prop="status" width="80">
              <template #default="scope">
                <DictTag v-model="scope.row.status" code="sys_normal_disable" />
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
            <el-table-column label="操作" fixed="right" width="220">
              <template #default="scope">
                <el-button
                  v-hasPerm="'system:user:edit'"
                  type="primary"
                  :icon="RefreshLeft"
                  size="small"
                  link
                  @click="handleResetPassword(scope.row)"
                >
                  重置密码
                </el-button>
                <el-button
                  v-hasPerm="'system:user:edit'"
                  type="primary"
                  :icon="Edit"
                  link
                  size="small"
                  @click="handleEditClick(scope.row.userId)"
                >
                  编辑
                </el-button>
                <el-button
                  v-hasPerm="'system:user:remove'"
                  type="danger"
                  :icon="Delete"
                  link
                  size="small"
                  @click="handleDelete(scope.row.userId)"
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
      </el-col>
    </el-row>

    <!-- 用户表单 -->
    <el-drawer
      v-model="dialogState.visible"
      :title="dialogState.title"
      append-to-body
      :size="drawerSize"
      @close="closeDialog"
    >
      <el-form ref="userFormRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="formData.userName"
            :readonly="!!formData.userId"
            placeholder="请输入用户名"
          />
        </el-form-item>

        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="formData.nickName" placeholder="请输入用户昵称" />
        </el-form-item>

        <el-form-item label="所属部门" prop="deptId">
          <el-tree-select
            v-model="formData.deptId"
            placeholder="请选择所属部门"
            :data="deptOptions"
            filterable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <DictSelect v-model="formData.sex" code="sys_user_sex" />
        </el-form-item>

        <el-form-item
          v-if="dialogState.mode === DialogMode.CREATE"
          label="初始密码"
          prop="password"
        >
          <el-input
            v-model="formData.password"
            show-password
            type="password"
            placeholder="请输入初始密码（至少6位）"
          />
        </el-form-item>

        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择">
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="formData.phonenumber" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" maxlength="50" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            inline-prompt
            active-text="正常"
            inactive-text="停用"
            :active-value="USER_STATUS_ENABLED"
            :inactive-value="USER_STATUS_DISABLED"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 用户导入 -->
    <UserImportDialog v-model="importDialogVisible" @import-success="handleQuery()" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useDebounceFn } from "@vueuse/core";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import type { OptionItem, UserForm, UserQueryParams, UserItem } from "@/types/api";
import { downloadFile } from "@/utils";
import UserAPI from "@/api/system/user";
import DeptAPI from "@/api/system/dept";
import RoleAPI from "@/api/system/role";
import { useUserStore, useAppStore } from "@/store";
import { DeviceEnum, DialogMode } from "@/enums";
import { useTableSelection } from "@/composables";
import {
  Delete,
  Download,
  Edit,
  Plus,
  Refresh,
  RefreshLeft,
  Search,
  Upload,
} from "@element-plus/icons-vue";
import UserDeptTree from "./components/UserDeptTree.vue";
import UserImportDialog from "./components/UserImportDialog.vue";

defineOptions({
  name: "User",
  inheritAttrs: false,
});

const appStore = useAppStore();
const userStore = useUserStore();

// 表单引用
const queryFormRef = ref<FormInstance>();
const userFormRef = ref<FormInstance>();

// 查询参数
const queryParams = reactive<UserQueryParams>({
  page: 1,
  pageSize: 10,
  status: undefined,
});

// 列表数据
const userList = ref<UserItem[]>([]);
const total = ref(0);
const loading = ref(false);

// 弹窗状态
const dialogState = reactive({
  visible: false,
  title: "新增用户",
  mode: DialogMode.CREATE,
});

// 导入弹窗状态
const importDialogVisible = ref(false);

// 表单初始数据
const USER_STATUS_ENABLED = "0";
const USER_STATUS_DISABLED = "1";

const initialFormData: UserForm = {
  status: USER_STATUS_ENABLED,
};

// 表单数据
const formData = reactive<UserForm>({ ...initialFormData });

// 下拉选项
const deptOptions = ref<OptionItem[]>();
const roleOptions = ref<OptionItem[]>();

const drawerSize = computed(() => (appStore.device === DeviceEnum.DESKTOP ? "600px" : "90%"));

const rules: FormRules = {
  userName: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  nickName: [{ required: true, message: "请输入用户昵称", trigger: "blur" }],
  deptId: [{ required: true, message: "请选择所属部门", trigger: "change" }],
  roleIds: [{ required: true, message: "请选择用户角色", trigger: "change" }],
  password: [{ min: 6, message: "初始密码至少6位", trigger: "blur" }],
  email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }],
  phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }],
};

/**
 * 加载用户列表数据
 */
async function fetchList(): Promise<void> {
  loading.value = true;
  try {
    const data = await UserAPI.getPage(queryParams);
    userList.value = data.items;
    total.value = data.total ?? 0;
  } finally {
    loading.value = false;
  }
}

/**
 * 加载表单下拉选项数据
 */
async function loadFormOptions(): Promise<void> {
  [roleOptions.value, deptOptions.value] = await Promise.all([
    RoleAPI.getOptions(),
    DeptAPI.getOptions(),
  ]);
}

const { selectedIds, hasSelection, handleSelectionChange } = useTableSelection<UserItem>("userId");

/**
 * 执行查询（重置页码）
 */
function handleQuery(): void {
  queryParams.page = 1;
  fetchList();
}

/**
 * 重置查询条件
 */
function resetQuery(): void {
  queryFormRef.value?.resetFields();
  queryParams.deptId = undefined;
  queryParams.createTime = undefined;
}

/**
 * 重置查询条件并重新查询
 */
function handleResetQuery(): void {
  resetQuery();
  handleQuery();
}

/**
 * 重置用户密码
 * @param userId 用户ID
 * @param password 新密码
 */
async function resetPassword(userId: string, password: string): Promise<void> {
  await UserAPI.resetPassword(userId, password);
  ElMessage.success("密码重置成功");
}

/**
 * 删除用户
 * @param userIds 用户ID列表，多个ID用逗号分隔
 */
async function deleteUsers(userIds: string): Promise<void> {
  await UserAPI.deleteByIds(userIds);
  ElMessage.success("删除成功");
  handleQuery();
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
  userFormRef.value?.resetFields();
  userFormRef.value?.clearValidate();
  Object.keys(formData).forEach((key) => {
    delete (formData as Record<string, any>)[key];
  });
  Object.assign(formData, initialFormData);
}

/**
 * 重置密码按钮点击事件
 * @param row 用户数据
 */
function handleResetPassword(row: UserItem): void {
  ElMessageBox.prompt(`请输入用户【${row.userName}】的新密码`, "重置密码", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPattern: /.{6,}/,
    inputErrorMessage: "密码至少需要6位字符",
  }).then(
    (result: any) => resetPassword(row.userId, result.value),
    () => {
      /* 用户取消 */
    }
  );
}

/**
 * 新增按钮点击事件
 */
async function handleCreateClick(): Promise<void> {
  dialogState.title = "新增用户";
  dialogState.mode = DialogMode.CREATE;
  await loadFormOptions();
  openDialog();
}

/**
 * 编辑按钮点击事件
 * @param id 用户ID
 */
async function handleEditClick(id: string): Promise<void> {
  dialogState.title = "修改用户";
  dialogState.mode = DialogMode.EDIT;
  await loadFormOptions();
  const data = await UserAPI.getFormData(id);
  Object.assign(formData, data);
  openDialog();
}

/**
 * 提交表单（防抖处理）
 */
const handleSubmit = useDebounceFn(async () => {
  const valid = await userFormRef.value?.validate().then(
    () => true,
    () => false
  );
  if (!valid) return;

  loading.value = true;
  try {
    if (formData.userId) {
      await UserAPI.update(formData.userId, formData);
      ElMessage.success("修改用户成功");
    } else {
      await UserAPI.create(formData);
      ElMessage.success("新增用户成功");
    }
    closeDialog();
    handleQuery();
  } finally {
    loading.value = false;
  }
}, 300);

/**
 * 删除按钮点击事件
 * @param id 用户ID，不传则删除选中的用户
 */
function handleDelete(id?: string): void {
  const userIds = id ?? selectedIds.value.join(",");
  if (!userIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  // 安全检查：防止删除当前登录用户
  const currentUserId = userStore.userInfo?.userId;
  if (currentUserId) {
    const isCurrentUserInList = id
      ? id === String(currentUserId)
      : selectedIds.value.some((selectedId) => String(selectedId) === String(currentUserId));
    if (isCurrentUserInList) {
      ElMessage.error("不能删除当前登录用户");
      return;
    }
  }

  ElMessageBox.confirm("确认删除选中的用户吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => deleteUsers(userIds),
    () => {
      /* 用户取消 */
    }
  );
}

/**
 * 导出用户列表
 */
async function exportUsers(): Promise<void> {
  const response = await UserAPI.export(queryParams);
  downloadFile(response);
  ElMessage.success("导出成功");
}

/**
 * 打开导入弹窗
 */
function openImportDialog(): void {
  importDialogVisible.value = true;
}

onMounted(() => {
  handleQuery();
});
</script>

<style scoped lang="scss"></style>
