import { createBlogCrudApi } from "./base";
import type { BlogQueryParams, NavbarItem } from "@/types/api";

const NavbarAPI = createBlogCrudApi<NavbarItem, BlogQueryParams>("/blog/admin/navbar");

export default NavbarAPI;
