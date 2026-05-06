import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, CommentItem } from "@/types/api";

const CommentAPI = createBlogCrudApi<CommentItem, BlogQueryParams>("/blog/admin/comment");

export default CommentAPI;
