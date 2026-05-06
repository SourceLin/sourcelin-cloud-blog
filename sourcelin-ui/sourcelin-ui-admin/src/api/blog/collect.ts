import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, CollectItem } from "@/types/api";

const CollectAPI = createBlogCrudApi<CollectItem, BlogQueryParams>("/blog/admin/collect");

export default CollectAPI;
