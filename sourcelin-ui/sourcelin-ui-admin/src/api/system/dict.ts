import request from "@/utils/request";
import type {
  DictTypeQueryParams,
  DictTypeItem,
  DictTypeForm,
  DictItemQueryParams,
  DictItem,
  DictItemForm,
  DictItemOption,
  OptionItem,
} from "@/types/api";

const DICT_TYPE_BASE_URL = "/system/dict/type";
const DICT_DATA_BASE_URL = "/system/dict/data";

function mapListClassToTagType(
  code?: string
): "primary" | "success" | "warning" | "info" | "danger" | undefined {
  switch (code) {
    case "P":
      return "primary";
    case "S":
      return "success";
    case "W":
      return "warning";
    case "I":
      return "info";
    case "D":
      return "danger";
    case "N":
    default:
      return undefined;
  }
}

function normalizeDictType(item: Record<string, any>): DictTypeItem {
  return {
    dictId: item.dictId != null ? String(item.dictId) : "",
    dictName: item.dictName,
    dictType: item.dictType,
    status: item.status != null ? String(item.status) : "0",
    remark: item.remark,
    createTime: item.createTime,
  };
}

function normalizeDictItem(item: Record<string, any>): DictItem {
  return {
    dictCode: item.dictCode != null ? String(item.dictCode) : "",
    dictType: item.dictType,
    dictLabel: item.dictLabel,
    dictValue: item.dictValue,
    dictSort: item.dictSort != null ? Number(item.dictSort) : undefined,
    status: item.status != null ? String(item.status) : "0",
    listClass: item.listClass,
    isDefault: item.isDefault,
    remark: item.remark,
  };
}

function buildDictTypePayload(data: DictTypeForm) {
  return {
    dictId: data.dictId ? Number(data.dictId) : undefined,
    dictName: data.dictName,
    dictType: data.dictType,
    status: data.status,
    remark: data.remark,
  };
}

function buildDictDataPayload(dictType: string, data: DictItemForm) {
  return {
    dictCode: data.dictCode ? Number(data.dictCode) : undefined,
    dictType,
    dictLabel: data.dictLabel,
    dictValue: data.dictValue,
    dictSort: data.dictSort,
    status: data.status,
    listClass: data.listClass ?? "N",
    isDefault: data.isDefault ?? "N",
    remark: data.remark,
  };
}

const DictAPI = {
  /** 字典分页列表 */
  async getPage(queryParams: DictTypeQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${DICT_TYPE_BASE_URL}/list`,
      method: "get",
      params: {
        page: queryParams.page ?? 1,
        pageSize: queryParams.pageSize,
        dictName: queryParams.dictName,
        dictType: queryParams.dictType,
        status: queryParams.status,
      },
    });
    const items = data.items.map(normalizeDictType);
    return {
      ...data,
      items,
    } as PageResult<DictTypeItem>;
  },

  /** 字典列表 */
  async getList() {
    const list = await request<any, Array<Record<string, any>>>({
      url: `${DICT_TYPE_BASE_URL}/optionselect`,
      method: "get",
    });
    return list.map((item) => ({
      value: item.dictType,
      label: item.dictName,
    })) as OptionItem[];
  },

  /** 字典表单数据 */
  async getFormData(dictId: string) {
    const data = await request<any, Record<string, any>>({
      url: `${DICT_TYPE_BASE_URL}/${dictId}`,
      method: "get",
    });
    return normalizeDictType(data) as DictTypeForm;
  },

  /** 新增字典 */
  create(data: DictTypeForm) {
    return request({
      url: `${DICT_TYPE_BASE_URL}`,
      method: "post",
      data: buildDictTypePayload(data),
    });
  },

  /** 修改字典 */
  update(dictId: string, data: DictTypeForm) {
    return request({
      url: `${DICT_TYPE_BASE_URL}`,
      method: "put",
      data: {
        ...buildDictTypePayload(data),
        dictId: Number(dictId),
      },
    });
  },

  /** 删除字典 */
  deleteByIds(ids: string) {
    return request({ url: `${DICT_TYPE_BASE_URL}/${ids}`, method: "delete" });
  },

  /** 获取字典项分页列表 */
  async getDictItemPage(dictType: string, queryParams: DictItemQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${DICT_DATA_BASE_URL}/list`,
      method: "get",
      params: {
        page: queryParams.page ?? 1,
        pageSize: queryParams.pageSize,
        dictType,
        dictLabel: queryParams.dictLabel,
        status: queryParams.status,
      },
    });
    const items = data.items.map(normalizeDictItem);
    return {
      ...data,
      items,
    } as PageResult<DictItem>;
  },

  /** 获取字典项列表 */
  async getDictItems(dictType: string) {
    const items = await request<any, Array<Record<string, any>>>({
      url: `${DICT_DATA_BASE_URL}/type/${dictType}`,
      method: "get",
    });
    return (items ?? []).map((item) => ({
      value: item.dictValue,
      label: item.dictLabel,
      listClass: item.listClass,
      tagType: mapListClassToTagType(item.listClass),
    })) as DictItemOption[];
  },

  /** 新增字典项 */
  createDictItem(dictType: string, data: DictItemForm) {
    return request({
      url: `${DICT_DATA_BASE_URL}`,
      method: "post",
      data: buildDictDataPayload(dictType, data),
    });
  },

  /** 获取字典项表单数据 */
  async getDictItemFormData(_dictType: string, dictCode: string) {
    const data = await request<any, Record<string, any>>({
      url: `${DICT_DATA_BASE_URL}/${dictCode}`,
      method: "get",
    });
    return normalizeDictItem(data) as DictItemForm;
  },

  /** 修改字典项 */
  updateDictItem(dictType: string, dictCode: string, data: DictItemForm) {
    return request({
      url: `${DICT_DATA_BASE_URL}`,
      method: "put",
      data: {
        ...buildDictDataPayload(dictType, data),
        dictCode: Number(dictCode),
      },
    });
  },

  /** 删除字典项 */
  deleteDictItems(_dictType: string, ids: string) {
    return request({ url: `${DICT_DATA_BASE_URL}/${ids}`, method: "delete" });
  },
};

export default DictAPI;
