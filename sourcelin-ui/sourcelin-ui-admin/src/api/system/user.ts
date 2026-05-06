import request from "@/utils/request";
import type {
  UserInfo,
  UserForm,
  UserQueryParams,
  UserItem,
  UserProfileDetail,
  UserProfileForm,
  PasswordChangeForm,
  OptionItem,
} from "@/types/api";

const USER_BASE_URL = "/system/user";

interface CurrentUserInfoPayload {
  user?: Record<string, any>;
  roles?: string[];
  permissions?: string[];
}

interface UserDetailPayload {
  user?: Record<string, any>;
  roleIds?: number[];
  postIds?: number[];
}

function mapUserInfo(payload?: CurrentUserInfoPayload): UserInfo {
  const user = payload?.user ?? {};
  return {
    userId: user.userId != null ? String(user.userId) : undefined,
    userName: user.userName ?? "",
    nickName: user.nickName ?? "",
    avatar: user.avatar,
    roles: payload?.roles ?? [],
    perms: payload?.permissions ?? [],
  };
}

function mapPageItem(item: Record<string, any>): UserItem {
  return {
    userId: item.userId != null ? String(item.userId) : "",
    userName: item.userName ?? "",
    nickName: item.nickName ?? "",
    avatar: item.avatar,
    createTime: item.createTime,
    deptName: item.deptName ?? item.dept?.deptName,
    email: item.email,
    sex: item.sex != null ? String(item.sex) : undefined,
    phonenumber: item.phonenumber,
    roleNames: Array.isArray(item.roles)
      ? item.roles
          .map((role: any) => role?.roleName)
          .filter(Boolean)
          .join(",")
      : item.roleNames,
    status: item.status != null ? String(item.status) : undefined,
  };
}

function mapUserForm(payload?: UserDetailPayload): UserForm {
  const user = payload?.user ?? {};
  return {
    userId: user.userId != null ? String(user.userId) : undefined,
    userName: user.userName ?? "",
    nickName: user.nickName ?? "",
    deptId: user.deptId != null ? String(user.deptId) : undefined,
    email: user.email,
    phonenumber: user.phonenumber,
    sex: user.sex != null ? String(user.sex) : undefined,
    status: user.status != null ? String(user.status) : undefined,
    avatar: user.avatar,
    roleIds: payload?.roleIds ?? [],
    postIds: payload?.postIds ?? [],
  };
}

function mapProfile(payload?: Record<string, any>): UserProfileDetail {
  const user = payload?.user ?? {};
  return {
    userId: user.userId != null ? String(user.userId) : undefined,
    userName: user.userName ?? "",
    nickName: user.nickName ?? "",
    avatar: user.avatar,
    sex: user.sex != null ? String(user.sex) : undefined,
    phonenumber: user.phonenumber,
    email: user.email,
    deptName: user.dept?.deptName,
    roleNames: payload?.roleGroup ?? "",
    createTime: user.createTime,
  };
}

function buildUserQueryParams(queryParams: UserQueryParams) {
  const [beginTime, endTime] = queryParams.createTime ?? [];
  return {
    page: queryParams.page ?? 1,
    pageSize: queryParams.pageSize,
    userName: queryParams.userName,
    phonenumber: queryParams.phonenumber,
    status: queryParams.status,
    deptId: queryParams.deptId,
    params: {
      beginTime,
      endTime,
    },
  };
}

function buildUserPayload(data: UserForm) {
  return {
    userId: data.userId,
    userName: data.userName,
    nickName: data.nickName,
    deptId: data.deptId,
    email: data.email,
    phonenumber: data.phonenumber,
    sex: data.sex,
    status: data.status,
    avatar: data.avatar,
    roleIds: data.roleIds,
    postIds: data.postIds,
    password: data.password,
    remark: data.remark,
  };
}

const UserAPI = {
  /**
   * 获取当前登录用户信息
   */
  async getInfo() {
    const data = await request<any, CurrentUserInfoPayload>({
      url: `${USER_BASE_URL}/getInfo`,
      method: "get",
    });
    return mapUserInfo(data);
  },

  /**
   * 获取用户分页列表
   */
  async getPage(queryParams: UserQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${USER_BASE_URL}/list`,
      method: "get",
      params: buildUserQueryParams(queryParams),
    });
    const items = data.items.map(mapPageItem);
    return {
      ...data,
      items,
    } as PageResult<UserItem>;
  },

  /**
   * 获取用户表单详情
   */
  async getFormData(userId: string) {
    const data = await request<any, UserDetailPayload>({
      url: `${USER_BASE_URL}/${userId}`,
      method: "get",
    });
    return mapUserForm(data);
  },

  /**
   * 添加用户
   */
  create(data: UserForm) {
    return request({
      url: `${USER_BASE_URL}`,
      method: "post",
      data: buildUserPayload(data),
    });
  },

  /**
   * 修改用户
   */
  update(_id: string, data: UserForm) {
    return request({
      url: `${USER_BASE_URL}`,
      method: "put",
      data: buildUserPayload(data),
    });
  },

  /**
   * 修改用户密码
   */
  resetPassword(id: string, password: string) {
    return request({
      url: `${USER_BASE_URL}/resetPwd`,
      method: "put",
      data: {
        userId: id,
        password,
      },
    });
  },

  /**
   * 批量删除用户
   */
  deleteByIds(ids: string) {
    return request({
      url: `${USER_BASE_URL}/${ids}`,
      method: "delete",
    });
  },

  /** 下载用户导入模板 */
  downloadTemplate() {
    return request({
      url: `${USER_BASE_URL}/importTemplate`,
      method: "post",
      responseType: "blob",
    });
  },

  /**
   * 导出用户
   */
  export(queryParams: UserQueryParams) {
    return request({
      url: `${USER_BASE_URL}/export`,
      method: "post",
      params: buildUserQueryParams(queryParams),
      responseType: "blob",
    });
  },

  /**
   * 导入用户
   */
  import(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return request<any, string>({
      url: `${USER_BASE_URL}/importData`,
      method: "post",
      params: {
        updateSupport: false,
      },
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /** 获取个人中心用户信息 */
  async getProfile() {
    const data = await request<any, Record<string, any>>({
      url: `${USER_BASE_URL}/profile`,
      method: "get",
    });
    return mapProfile(data);
  },

  /** 修改个人中心用户信息 */
  updateProfile(data: UserProfileForm) {
    return request({
      url: `${USER_BASE_URL}/profile`,
      method: "put",
      data: {
        nickName: data.nickName,
        sex: data.sex,
        avatar: data.avatar,
      },
    });
  },

  /** 修改个人中心用户密码 */
  changePassword(data: PasswordChangeForm) {
    return request({
      url: `${USER_BASE_URL}/profile/updatePwd`,
      method: "put",
      params: {
        oldPassword: data.oldPassword,
        newPassword: data.newPassword,
      },
    });
  },

  /** 发送短信验证码（当前后端未启用） */
  sendMobileCode() {
    return Promise.reject(new Error("当前后端未提供手机号验证码接口"));
  },

  /** 绑定或更换手机号（当前后端未启用） */
  bindOrChangeMobile() {
    return Promise.reject(new Error("当前后端未提供手机号绑定接口"));
  },

  /** 解绑手机号（当前后端未启用） */
  unbindMobile() {
    return Promise.reject(new Error("当前后端未提供手机号解绑接口"));
  },

  /** 发送邮箱验证码（当前后端未启用） */
  sendEmailCode() {
    return Promise.reject(new Error("当前后端未提供邮箱验证码接口"));
  },

  /** 绑定或更换邮箱（当前后端未启用） */
  bindOrChangeEmail() {
    return Promise.reject(new Error("当前后端未提供邮箱绑定接口"));
  },

  /** 解绑邮箱（当前后端未启用） */
  unbindEmail() {
    return Promise.reject(new Error("当前后端未提供邮箱解绑接口"));
  },

  /**
   * 获取用户下拉列表
   */
  async getOptions() {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${USER_BASE_URL}/list`,
      method: "get",
      params: {
        page: 1,
        pageSize: 1000,
      },
    });
    return data.items.map((item) => ({
      value: String(item.userId),
      label: item.nickName || item.userName,
    })) as OptionItem[];
  },
};

export default UserAPI;
