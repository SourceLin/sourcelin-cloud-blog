import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, CategoryItem } from "@/types/api";

const CategoryAPI = createBlogCrudApi<CategoryItem, BlogQueryParams>("/blog/admin/category");

export default CategoryAPI;
