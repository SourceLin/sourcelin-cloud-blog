import { computed, ref } from 'vue';
import { fetchAboutInfo, fetchSiteBrandInfo } from '../api/site.api';
import type {
  AboutContactItem,
  AboutFactItem,
  AboutInfo,
  SiteBrandInfo
} from '@/modules/site/types/site';
import { resolveBrandImageUrl } from '@/utils/url';

const CONTACT_VISIBILITY: Record<string, number> = {
  email: 1,
  qq: 2,
  wechat: 5,
  qqGroup: 6
};

function pickText(...values: Array<string | undefined>): string {
  for (const value of values) {
    if (typeof value === 'string' && value.trim()) {
      return value.trim();
    }
  }
  return '';
}

function isVisibleContact(showList: number[] | undefined, code: number): boolean {
  if (!Array.isArray(showList) || showList.length === 0) {
    return true;
  }
  return showList.includes(code);
}

export function useAboutPage() {
  const about = ref<AboutInfo | null>(null);
  const siteBrand = ref<SiteBrandInfo | null>(null);
  const loading = ref(false);

  const siteName = computed(() =>
    pickText(about.value?.webName, siteBrand.value?.webName, siteBrand.value?.siteName, '圆圈博客')
  );

  const authorName = computed(() => pickText(about.value?.author, siteBrand.value?.author, 'Sourcelin'));

  const authorTitle = computed(() => pickText(about.value?.authorInfo, siteBrand.value?.authorInfo));

  const siteBio = computed(() =>
    pickText(about.value?.bio, siteBrand.value?.bio, siteBrand.value?.footer, '记录内容、灵感与轻互动。')
  );

  const siteSummary = computed(() =>
    pickText(about.value?.summary, `${siteName.value} 移动端聚焦阅读、互动和回访体验。`)
  );

  const logoUrl = computed(() =>
    resolveBrandImageUrl(
      siteBrand.value?.logo,
      siteBrand.value?.avatar,
      about.value?.avatar
    )
  );

  const avatarUrl = computed(() => pickText(about.value?.avatar, siteBrand.value?.avatar));

  const showAuthorAvatar = computed(() => {
    const avatar = avatarUrl.value;
    if (!avatar) {
      return false;
    }
    return resolveBrandImageUrl(avatar) !== logoUrl.value;
  });

  const showList = computed(() => {
    const fromAbout = about.value?.showList;
    if (Array.isArray(fromAbout) && fromAbout.length) {
      return fromAbout;
    }
    return siteBrand.value?.showList;
  });

  const contactItems = computed((): AboutContactItem[] => {
    const data = about.value;
    if (!data) {
      return [];
    }
    const items: AboutContactItem[] = [];
    const pushContact = (key: string, label: string, value: string | undefined) => {
      const normalized = pickText(value);
      if (!normalized) {
        return;
      }
      const visibilityCode = CONTACT_VISIBILITY[key];
      if (visibilityCode !== undefined && !isVisibleContact(showList.value, visibilityCode)) {
        return;
      }
      items.push({ key, label, value: normalized });
    };

    pushContact('email', '联系邮箱', data.email);
    pushContact('qq', 'QQ', data.qqNumber);
    pushContact('wechat', '微信', data.wechat);
    pushContact('qqGroup', 'QQ 群', data.qqGroup);
    pushContact('github', 'GitHub', data.github);
    pushContact('gitee', 'Gitee', data.gitee);

    return items;
  });

  const siteFacts = computed((): AboutFactItem[] => {
    const data = about.value;
    if (!data) {
      return [];
    }
    const facts: AboutFactItem[] = [{ label: '站点名称', value: siteName.value }];
    const webUrl = pickText(data.webUrl);
    if (webUrl) {
      facts.push({ label: '站点地址', value: webUrl });
    }
    facts.push({
      label: '备案号',
      value: pickText(data.recordNum, siteBrand.value?.recordNum, '未设置')
    });
    return facts;
  });

  async function loadAboutPage(): Promise<void> {
    loading.value = true;
    try {
      const [aboutInfo, brandInfo] = await Promise.all([
        fetchAboutInfo(),
        fetchSiteBrandInfo().catch(() => null)
      ]);
      about.value = aboutInfo;
      siteBrand.value = brandInfo;
    } finally {
      loading.value = false;
    }
  }

  return {
    about,
    siteBrand,
    loading,
    siteName,
    authorName,
    authorTitle,
    siteBio,
    siteSummary,
    logoUrl,
    avatarUrl,
    showAuthorAvatar,
    contactItems,
    siteFacts,
    loadAboutPage
  };
}
