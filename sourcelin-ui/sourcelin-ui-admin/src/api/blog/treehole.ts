import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, TreeholeItem } from "@/types/api";

const TreeholeAPI = createBlogCrudApi<TreeholeItem, BlogQueryParams>("/blog/admin/treehole");

export default TreeholeAPI;
