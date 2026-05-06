import request from "@/utils/request";
import type { NoticeQueryParams, NoticeForm, NoticeItem, NoticeDetail } from "@/types/api";

const NOTICE_BASE_URL = "/system/notice";

const mapNoticeStatusToPublishStatus = (status?: string): number => {
  if (status === "0") {
    return 1;
  }
  if (status === "1") {
    return -1;
  }
  return 0;
};

const mapPublishStatusToNoticeStatus = (publishStatus?: number): string => {
  if (publishStatus === 1) {
    return "0";
  }
  return "1";
};

function mapNoticeItem(item: Record<string, any>): NoticeItem {
  return {
    id: item.noticeId != null ? String(item.noticeId) : "",
    title: item.noticeTitle,
    content: item.noticeContent,
    type: item.noticeType != null ? Number(item.noticeType) : 1,
    level: "L",
    publishStatus: mapNoticeStatusToPublishStatus(item.status),
    isRead: 0,
    publishTime: item.status === "0" ? (item.updateTime ?? item.createTime) : undefined,
    revokeTime: item.status === "1" ? item.updateTime : undefined,
    createTime: item.createTime,
    publisherName: item.createBy,
    targetType: 1,
  };
}

function buildNoticePayload(data: NoticeForm) {
  return {
    noticeId: data.id ? Number(data.id) : undefined,
    noticeTitle: data.title,
    noticeType: data.type != null ? String(data.type) : "1",
    noticeContent: data.content,
    status:
      data.status != null
        ? mapPublishStatusToNoticeStatus(data.status)
        : mapPublishStatusToNoticeStatus(data.publishStatus),
  };
}

const NoticeAPI = {
  /** 获取通知公告分页数据 */
  async getPage(queryParams?: NoticeQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${NOTICE_BASE_URL}/list`,
      method: "get",
      params: {
        page: queryParams?.page ?? 1,
        pageSize: queryParams?.pageSize ?? 10,
        noticeTitle: queryParams?.title,
        status:
          queryParams?.publishStatus !== undefined
            ? mapPublishStatusToNoticeStatus(queryParams.publishStatus)
            : undefined,
      },
    });
    const items = data.items.map(mapNoticeItem);
    return {
      ...data,
      items,
    } as PageResult<NoticeItem>;
  },

  /** 获取通知公告表单数据 */
  async getFormData(id: string) {
    const data = await request<any, Record<string, any>>({
      url: `${NOTICE_BASE_URL}/${id}`,
      method: "get",
    });
    return {
      id: data.noticeId != null ? String(data.noticeId) : undefined,
      title: data.noticeTitle,
      content: data.noticeContent,
      type: data.noticeType != null ? Number(data.noticeType) : 1,
      level: "L",
      status: mapNoticeStatusToPublishStatus(data.status),
      publishStatus: mapNoticeStatusToPublishStatus(data.status),
      targetType: 1,
      targetUsers: [],
    } as NoticeForm;
  },

  /** 添加通知公告 */
  create(data: NoticeForm) {
    return request({ url: `${NOTICE_BASE_URL}`, method: "post", data: buildNoticePayload(data) });
  },

  /** 更新通知公告 */
  update(id: string, data: NoticeForm) {
    return request({
      url: `${NOTICE_BASE_URL}`,
      method: "put",
      data: {
        ...buildNoticePayload(data),
        noticeId: Number(id),
      },
    });
  },

  /** 批量删除通知公告，多个以英文逗号(,)分割 */
  deleteByIds(ids: string) {
    return request({ url: `${NOTICE_BASE_URL}/${ids}`, method: "delete" });
  },

  /** 发布通知 */
  async publish(id: string) {
    const form = await this.getFormData(id);
    return this.update(id, {
      ...form,
      publishStatus: 1,
      status: 1,
    });
  },

  /** 撤回通知 */
  async revoke(id: string) {
    const form = await this.getFormData(id);
    return this.update(id, {
      ...form,
      publishStatus: -1,
      status: -1,
    });
  },

  /** 查看通知 */
  async getDetail(id: string) {
    const data = await request<any, Record<string, any>>({
      url: `${NOTICE_BASE_URL}/${id}`,
      method: "get",
    });
    return {
      id: data.noticeId != null ? String(data.noticeId) : undefined,
      title: data.noticeTitle,
      content: data.noticeContent,
      type: data.noticeType != null ? Number(data.noticeType) : undefined,
      level: "L",
      publishStatus: mapNoticeStatusToPublishStatus(data.status),
      publisherName: data.createBy,
      publishTime: data.status === "0" ? (data.updateTime ?? data.createTime) : undefined,
      createTime: data.createTime,
    } as NoticeDetail;
  },

  /** 全部已读 */
  readAll() {
    return Promise.resolve(true);
  },

  /** 获取我的通知分页列表 */
  getMyNoticePage(queryParams?: NoticeQueryParams) {
    return this.getPage(queryParams);
  },
};

export default NoticeAPI;
