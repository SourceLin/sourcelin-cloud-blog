export type SelectOption = {
  label: string;
  value: string | number;
};

export type ElTagType = "success" | "warning" | "danger" | "info" | "primary";
export type ElButtonType = "" | "default" | "text" | ElTagType;

export type QueryField = {
  prop: string;
  label: string;
  type?: "input" | "select" | string;
  placeholder?: string;
  width?: string;
  options?: SelectOption[];
};

export type FormField = {
  prop: string;
  label: string;
  type?:
    | "input"
    | "textarea"
    | "number"
    | "select"
    | "switch"
    | "image"
    | "editor"
    | "icon"
    | "treeSelect"
    | string;
  placeholder?: string;
  options?: SelectOption[];
  multiple?: boolean;
  rows?: number;
  span?: number;
  min?: number;
  max?: number;
  required?: boolean;
  activeValue?: string | number | boolean;
  inactiveValue?: string | number | boolean;
  hiddenOnCreate?: boolean;
  hiddenOnEdit?: boolean;
  disabled?: boolean | ((form: Record<string, any>) => boolean);
  visibleWhen?: (form: Record<string, any>) => boolean;
  /** treeSelect：根节点展示文案 */
  treeRootLabel?: string;
  /** treeSelect：根节点值（通常为 0 表示顶级） */
  treeRootValue?: string | number;
  /** treeSelect：节点 value 字段，默认与 idKey 一致 */
  treeValueProp?: string;
  /** treeSelect：节点 label 字段 */
  treeLabelProp?: string;
};

export type TagValueConfig =
  | string
  | {
      label: string;
      type?: ElTagType | string;
    };

export type TableColumn = {
  prop: string;
  label: string;
  minWidth?: number;
  width?: number;
  align?: "left" | "center" | "right" | string;
  type?: "image" | "tag" | "icon" | string;
  tagMap?: Record<string | number, TagValueConfig>;
  formatter?: (row: Record<string, any>) => string;
};

export type CrudApi = {
  getPage: (
    queryParams: Record<string, any>
  ) => Promise<import("@/types/api").PageResult<Record<string, any>>>;
  getFormData?: (id: string | number) => Promise<Record<string, any>>;
  create?: (data: Record<string, any>) => Promise<any>;
  update?: (id: string | number, data: Record<string, any>) => Promise<any>;
  deleteByIds?: (ids: string | number | Array<string | number>) => Promise<any>;
};

export type RowAction = {
  key: string;
  label: string;
  type?: ElTagType | string;
  permission?: string;
  confirmText?: string;
  visible?: (row: Record<string, any>) => boolean;
  onClick: (row: Record<string, any>) => Promise<void> | void;
};

export type TreeConfig = {
  enabled?: boolean;
  parentKey?: string;
  childrenKey?: string;
  defaultExpandAll?: boolean;
};
