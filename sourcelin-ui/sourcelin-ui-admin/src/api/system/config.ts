import request from "@/utils/request";
import type { ConfigQueryParams, ConfigForm, ConfigItem } from "@/types/api";

const CONFIG_BASE_URL = "/system/config";

function mapConfigItem(item: Record<string, any>): ConfigItem {
  return {
    id: item.configId != null ? String(item.configId) : undefined,
    configName: item.configName,
    configKey: item.configKey,
    configValue: item.configValue,
    remark: item.remark,
  };
}

function buildConfigPayload(data: ConfigForm) {
  return {
    configId: data.id ? Number(data.id) : undefined,
    configName: data.configName,
    configKey: data.configKey,
    configValue: data.configValue,
    remark: data.remark,
  };
}

const ConfigAPI = {
  /** 获取配置分页数据 */
  async getPage(queryParams?: ConfigQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${CONFIG_BASE_URL}/list`,
      method: "get",
      params: {
        page: queryParams?.page ?? 1,
        pageSize: queryParams?.pageSize ?? 10,
        configName: queryParams?.keywords,
      },
    });
    const items = data.items.map(mapConfigItem);
    return {
      ...data,
      items,
    } as PageResult<ConfigItem>;
  },

  /** 获取配置表单数据 */
  async getFormData(id: string) {
    const data = await request<any, Record<string, any>>({
      url: `${CONFIG_BASE_URL}/${id}`,
      method: "get",
    });
    return mapConfigItem(data) as ConfigForm;
  },

  /** 新增配置 */
  create(data: ConfigForm) {
    return request({ url: `${CONFIG_BASE_URL}`, method: "post", data: buildConfigPayload(data) });
  },

  /** 修改配置 */
  update(id: string, data: ConfigForm) {
    return request({
      url: `${CONFIG_BASE_URL}`,
      method: "put",
      data: {
        ...buildConfigPayload(data),
        configId: Number(id),
      },
    });
  },

  /** 删除配置 */
  deleteById(id: string) {
    return request({ url: `${CONFIG_BASE_URL}/${id}`, method: "delete" });
  },

  /** 刷新配置缓存 */
  refreshCache() {
    return request({ url: `${CONFIG_BASE_URL}/refreshCache`, method: "delete" });
  },

  /** 根据键名查询配置值 */
  getByKey(configKey: string) {
    return request<string>({
      url: `${CONFIG_BASE_URL}/configKey/${configKey}`,
      method: "get",
    });
  },

  /** 根据键名更新配置值 */
  updateByKey(configKey: string, configValue: string) {
    return request({
      url: `${CONFIG_BASE_URL}/key/${configKey}`,
      method: "put",
      data: configValue,
    });
  },
};

export default ConfigAPI;
