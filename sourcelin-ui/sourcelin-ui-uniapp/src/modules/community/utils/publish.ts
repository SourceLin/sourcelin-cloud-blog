import { getStorage, removeStorage, setStorage } from '@/utils/storage';

export type CommunityRefreshTab = 'says' | 'treeholes';

const COMMUNITY_REFRESH_KEY = 'community.refresh.tab';

export function markCommunityRefresh(tab: CommunityRefreshTab): void {
  setStorage(COMMUNITY_REFRESH_KEY, tab, 120);
}

export function consumeCommunityRefresh(): CommunityRefreshTab | '' {
  const tab = getStorage<CommunityRefreshTab | ''>(COMMUNITY_REFRESH_KEY, '');
  if (tab) {
    removeStorage(COMMUNITY_REFRESH_KEY);
  }
  return tab;
}
