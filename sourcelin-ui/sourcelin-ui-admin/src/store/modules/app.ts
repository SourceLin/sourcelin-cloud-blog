import { store } from "@/store";
import { DeviceEnum, SidebarStatus } from "@/enums";
import { STORAGE_KEYS } from "@/constants";

export const useAppStore = defineStore("app", () => {
  const device = useStorage(STORAGE_KEYS.DEVICE, DeviceEnum.DESKTOP);
  const sidebarStatus = useStorage(STORAGE_KEYS.SIDEBAR_STATUS, SidebarStatus.CLOSED);
  const sidebar = reactive({
    opened: sidebarStatus.value === SidebarStatus.OPENED,
    withoutAnimation: false,
  });
  const activeTopMenuPath = useStorage(STORAGE_KEYS.ACTIVE_TOP_MENU_PATH, "");

  function toggleSidebar() {
    sidebar.opened = !sidebar.opened;
    sidebarStatus.value = sidebar.opened ? SidebarStatus.OPENED : SidebarStatus.CLOSED;
  }

  function closeSideBar() {
    sidebar.opened = false;
    sidebarStatus.value = SidebarStatus.CLOSED;
  }

  function openSideBar() {
    sidebar.opened = true;
    sidebarStatus.value = SidebarStatus.OPENED;
  }

  function toggleDevice(val: string) {
    device.value = val;
  }

  function activeTopMenu(val: string) {
    activeTopMenuPath.value = val;
  }

  return {
    device,
    sidebar,
    activeTopMenu,
    toggleDevice,
    toggleSidebar,
    closeSideBar,
    openSideBar,
    activeTopMenuPath,
  };
});

export function useAppStoreHook() {
  return useAppStore(store);
}
