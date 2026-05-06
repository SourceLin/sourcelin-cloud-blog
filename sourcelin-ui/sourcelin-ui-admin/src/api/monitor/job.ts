import request from "@/utils/request";
import type { JobForm, JobItem, JobQueryParams } from "@/types/api";

const JOB_BASE_URL = "/schedule/job";

function mapJobItem(item: Record<string, any>): JobItem {
  return {
    jobId: item.jobId != null ? Number(item.jobId) : undefined,
    jobName: item.jobName,
    jobGroup: item.jobGroup,
    invokeTarget: item.invokeTarget,
    cronExpression: item.cronExpression,
    misfirePolicy: item.misfirePolicy,
    concurrent: item.concurrent,
    status: item.status != null ? String(item.status) : undefined,
    createTime: item.createTime,
    nextValidTime: item.nextValidTime,
    remark: item.remark,
  };
}

function buildQueryParams(queryParams: JobQueryParams) {
  return {
    page: queryParams.page ?? 1,
    pageSize: queryParams.pageSize,
    jobName: queryParams.jobName,
    jobGroup: queryParams.jobGroup,
    status: queryParams.status,
  };
}

function buildPayload(data: JobForm) {
  return {
    jobId: data.jobId,
    jobName: data.jobName,
    jobGroup: data.jobGroup,
    invokeTarget: data.invokeTarget,
    cronExpression: data.cronExpression,
    misfirePolicy: data.misfirePolicy ?? "1",
    concurrent: data.concurrent ?? "1",
    status: data.status ?? "0",
    remark: data.remark,
  };
}

const JobAPI = {
  /** 获取定时任务分页列表 */
  async getPage(queryParams: JobQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${JOB_BASE_URL}/list`,
      method: "get",
      params: buildQueryParams(queryParams),
    });
    const items = (data.items ?? []).map(mapJobItem);
    return {
      ...data,
      items,
    } as PageResult<JobItem>;
  },

  /** 获取定时任务详情 */
  async getDetail(jobId: number) {
    const data = await request<any, Record<string, any>>({
      url: `${JOB_BASE_URL}/${jobId}`,
      method: "get",
    });
    return mapJobItem(data) as JobForm;
  },

  /** 新增定时任务 */
  create(data: JobForm) {
    return request({
      url: `${JOB_BASE_URL}`,
      method: "post",
      data: buildPayload(data),
    });
  },

  /** 更新定时任务 */
  update(data: JobForm) {
    return request({
      url: `${JOB_BASE_URL}`,
      method: "put",
      data: buildPayload(data),
    });
  },

  /** 删除定时任务 */
  deleteByIds(jobIds: string) {
    return request({
      url: `${JOB_BASE_URL}/${jobIds}`,
      method: "delete",
    });
  },

  /** 修改定时任务状态 */
  changeStatus(jobId: number, status: string) {
    return request({
      url: `${JOB_BASE_URL}/changeStatus`,
      method: "put",
      data: { jobId, status },
    });
  },

  /** 立即执行一次 */
  run(jobId: number, jobGroup?: string) {
    return request({
      url: `${JOB_BASE_URL}/run`,
      method: "put",
      data: { jobId, jobGroup },
    });
  },

  /** 导出任务列表 */
  export(queryParams: JobQueryParams) {
    return request({
      url: `${JOB_BASE_URL}/export`,
      method: "post",
      params: buildQueryParams(queryParams),
      responseType: "blob",
    });
  },
};

export default JobAPI;
