import { createBlogCrudApi } from "./base";
import type { ArticleItem, BlogQueryParams } from "@/types/api";

const ArticleAPI = createBlogCrudApi<ArticleItem, BlogQueryParams>("/blog/admin/article");

export default ArticleAPI;
