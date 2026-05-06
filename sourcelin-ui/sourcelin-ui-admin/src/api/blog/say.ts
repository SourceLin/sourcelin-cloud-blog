import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, SayItem } from "@/types/api";

const SayAPI = createBlogCrudApi<SayItem, BlogQueryParams>("/blog/admin/say");

export default SayAPI;
