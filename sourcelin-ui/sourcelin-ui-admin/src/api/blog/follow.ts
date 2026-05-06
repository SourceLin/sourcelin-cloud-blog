import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, FollowItem } from "@/types/api";

const FollowAPI = createBlogCrudApi<FollowItem, BlogQueryParams>("/blog/admin/follow");

export default FollowAPI;
