import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, TagItem } from "@/types/api";

const TagAPI = createBlogCrudApi<TagItem, BlogQueryParams>("/blog/admin/tag");

export default TagAPI;
