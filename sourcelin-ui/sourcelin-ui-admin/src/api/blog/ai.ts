import request from "@/utils/request";
import type { PageResult } from "@/types/api";
export interface AiAdminPageQuery {
  page?: number;
  pageSize?: number;
  abilityCode?: string;
  providerType?: string;
  enabled?: string;
  status?: string;
  bizId?: string;
  [key: string]: unknown;
}
export interface AiListResult<T> {
  items: T[];
}

export interface AiInvokeResult {
  requestId: string;
  abilityCode: string;
  bizId: string;
  resultType: string;
  content: string;
  adoptStatus: string;
  recordId?: number;
  success: boolean;
  errorCode?: string;
  errorMessage?: string;
}

export interface AiRecord {
  id: number;
  abilityCode: string;
  bizId: string;
  aiResult: string;
  status: "pending" | "adopted" | "discarded";
  adoptUserId?: string;
  adoptTime?: string;
  createTime?: string;
}

export type AiRecordListResult = PageResult<AiRecord>;

// ---- AI 能力配置 ----

export interface AiAbilityConfig {
  id?: number;
  abilityCode: string;
  abilityName: string;
  appCode?: string;
  domainCode?: string;
  providerType: string;
  providerAppId?: string;
  providerWorkflowId?: string;
  endpointUrl?: string;
  authConfigRef?: string;
  apiKey?: string;
  promptTemplate?: string;
  invokeMode: string;
  chatMode?: string;
  enabled: string;
  version?: number;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ProviderTypeOption {
  value: string;
  label: string;
}

// ---- AI 调用日志 ----

export interface AiCallLog {
  id: number;
  requestId: string;
  appCode?: string;
  domainCode?: string;
  abilityCode: string;
  bizId: string;
  userId: string;
  tenantId?: string;
  providerType: string;
  providerTraceId?: string;
  invokeMode: string;
  requestSummary?: string;
  responseSummary?: string;
  status: string;
  errorCode?: string;
  errorMessage?: string;
  latencyMs?: number;
  tokenUsage?: number;
  costAmount?: number;
  createTime?: string;
}

// ---- AI 异步任务 ----

export interface AiTaskStatus {
  taskId: string;
  requestId?: string;
  abilityCode?: string;
  bizId?: string;
  userId?: string;
  status: string;
  progress?: number;
  resultSnapshot?: string;
  errorMessage?: string;
  startedTime?: string;
  finishedTime?: string;
}

export interface AiTask {
  id: number;
  taskId: string;
  requestId: string;
  appCode?: string;
  domainCode?: string;
  abilityCode: string;
  bizId: string;
  userId: string;
  tenantId?: string;
  status: string;
  progress: number;
  inputSnapshot?: string;
  resultSnapshot?: string;
  errorMessage?: string;
  retryCount?: number;
  callbackUrl?: string;
  startedTime?: string;
  finishedTime?: string;
  createTime?: string;
  updateTime?: string;
}

export interface AiTaskEvent {
  id: number;
  taskId: string;
  eventType: string;
  eventMessage?: string;
  eventPayload?: string;
  createTime?: string;
}

const AiAPI = {
  /**
   * 获取 AI 功能状态
   */
  async getAiStatus(): Promise<boolean> {
    try {
      const res = await request<unknown, { enabled: boolean }>({
        url: `/blog/admin/blog/ai/status`,
        method: "get",
      });
      return res?.enabled ?? false;
    } catch {
      return false;
    }
  },

  /**
   * 同步调用 AI 能力
   */
  invokeAbility(abilityCode: string, data: { bizId?: string | number; [key: string]: unknown }) {
    const { bizId, ...input } = data;
    return request<unknown, AiInvokeResult>({
      url: `/blog/admin/blog/ai/abilities/${abilityCode}/invoke`,
      method: "post",
      params: bizId ? { bizId } : undefined,
      data: input,
    });
  },

  /**
   * 创建 AI 异步任务
   */
  createAsyncTask(data: {
    abilityCode: string;
    bizId?: string | number;
    input?: Record<string, unknown>;
  }) {
    return request<unknown, AiTaskStatus>({
      url: `/blog/admin/blog/ai/tasks`,
      method: "post",
      data: {
        abilityCode: data.abilityCode,
        bizId: data.bizId != null ? String(data.bizId) : undefined,
        input: data.input ?? {},
      },
    });
  },

  /**
   * 查询 AI 异步任务状态
   */
  getTaskStatus(taskId: string) {
    return request<unknown, AiTaskStatus>({
      url: `/blog/admin/blog/ai/tasks/${taskId}`,
      method: "get",
    });
  },

  /**
   * 采纳 AI 结果并回写到正式字段
   */
  adoptResult(recordId: number | string) {
    return request<unknown, boolean>({
      url: `/blog/admin/blog/ai/records/${recordId}/adopt`,
      method: "post",
    });
  },

  /**
   * 忽略/放弃 AI 结果
   */
  discardResult(recordId: number | string) {
    return request<unknown, boolean>({
      url: `/blog/admin/blog/ai/records/${recordId}/discard`,
      method: "post",
    });
  },

  /**
   * 获取记录列表
   */
  getRecordList(params?: AiAdminPageQuery) {
    return request<unknown, AiRecordListResult>({
      url: `/blog/admin/blog/ai/records`,
      method: "get",
      params,
    });
  },

  // ==================== AI 能力配置 CRUD ====================

  /**
   * 获取 Provider 类型枚举列表
   */
  getProviderTypes() {
    return request<unknown, AiListResult<ProviderTypeOption>>({
      url: `/blog/admin/blog/ai/ability-configs/provider-types`,
      method: "get",
    });
  },

  /**
   * 获取能力配置列表
   */
  getAbilityConfigList(params?: AiAdminPageQuery) {
    return request<unknown, PageResult<AiAbilityConfig>>({
      url: `/blog/admin/blog/ai/ability-configs`,
      method: "get",
      params,
    });
  },

  /**
   * 新增能力配置
   */
  createAbilityConfig(data: AiAbilityConfig) {
    return request<unknown, number>({
      url: `/blog/admin/blog/ai/ability-configs`,
      method: "post",
      data,
    });
  },

  /**
   * 更新能力配置
   */
  updateAbilityConfig(data: AiAbilityConfig) {
    return request<unknown, number>({
      url: `/blog/admin/blog/ai/ability-configs`,
      method: "put",
      data,
    });
  },

  /**
   * 删除能力配置
   */
  deleteAbilityConfig(id: number) {
    return request<unknown, number>({
      url: `/blog/admin/blog/ai/ability-configs/${id}`,
      method: "delete",
    });
  },

  // ==================== AI 调用日志 ====================

  /**
   * 获取调用日志列表
   */
  getCallLogList(params?: AiAdminPageQuery) {
    return request<unknown, PageResult<AiCallLog>>({
      url: `/blog/admin/blog/ai/call-logs`,
      method: "get",
      params,
    });
  },

  // ==================== AI 异步任务 ====================

  /**
   * 获取异步任务列表
   */
  getTaskList(params?: AiAdminPageQuery) {
    return request<unknown, PageResult<AiTask>>({
      url: `/blog/admin/blog/ai/tasks`,
      method: "get",
      params,
    });
  },

  /**
   * 获取任务事件流水
   */
  getTaskEvents(taskId: string) {
    return request<unknown, AiListResult<AiTaskEvent>>({
      url: `/blog/admin/blog/ai/tasks/${taskId}/events`,
      method: "get",
    });
  },

  /**
   * 重试失败任务
   */
  retryTask(taskId: string) {
    return request<unknown, string>({
      url: `/blog/admin/blog/ai/tasks/${taskId}/retry`,
      method: "post",
    });
  },
};

export default AiAPI;
