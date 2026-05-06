import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, NavigationItem } from "@/types/api";

const NavigationAPI = createBlogCrudApi<NavigationItem, BlogQueryParams>("/blog/admin/navigation");

export default NavigationAPI;
