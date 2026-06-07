import { http } from '@/utils/request';

export interface MobileCapabilities {
  client: string;
  reviewSafeMode: boolean;
  articleReadEnabled: boolean;
  searchEnabled: boolean;
  profileEnabled: boolean;
  favoriteEnabled: boolean;
  readingHistoryEnabled: boolean;
  settingsEnabled: boolean;
  policyEnabled: boolean;
  aboutEnabled: boolean;
  friendLinkEnabled: boolean;
  navigationEnabled: boolean;
  articlePublishEnabled: boolean;
  commentEnabled: boolean;
  communityEnabled: boolean;
  sayPublishEnabled: boolean;
  treeholeEnabled: boolean;
  interactionCenterEnabled: boolean;
  followEnabled: boolean;
  messageCenterEnabled: boolean;
  userHomeEnabled: boolean;
  userUploadEnabled: boolean;
}

export function fetchMobileCapabilities(client: string): Promise<MobileCapabilities> {
  return http.get<MobileCapabilities>(
    '/front/mobile/capabilities',
    { client },
    { isToken: false }
  );
}
