export type LegalArticleType = 'user-agreement' | 'privacy-policy';

export interface LegalSection {
  title: string;
  paragraphs: string[];
}

export interface LegalArticle {
  type: LegalArticleType;
  chip: string;
  icon: string;
  iconColor: string;
  title: string;
  subtitle: string;
  updatedAt: string;
  footerHint: string;
  sections: LegalSection[];
}

export const LEGAL_ARTICLES: LegalArticle[] = [
  {
    type: 'user-agreement',
    chip: '协议',
    icon: 'compose',
    iconColor: '#3B59FF',
    title: '用户协议',
    subtitle: '本协议用于说明个人博客阅读版小程序的使用边界。登录、收藏与阅读即表示你已阅读并同意。',
    updatedAt: '2026年2月1日',
    footerHint: '如有疑问，请通过关于页面的联系方式与我联系。',
    sections: [
      {
        title: '一、站点性质',
        paragraphs: [
          '1.1 本站为个人维护的博客阅读型小程序，主要用于展示站长公开发布的文章与站点信息。',
          '1.2 当前版本面向游客和普通用户提供阅读、搜索、收藏、阅读历史与基础账号设置能力。',
          '1.3 本站由个人持续维护，可能根据内容安排、服务能力或平台要求调整功能与开放范围。'
        ]
      },
      {
        title: '二、账号注册',
        paragraphs: [
          '2.1 注册与登录仅用于同步收藏、阅读偏好、历史记录和基础资料设置。',
          '2.2 账号仅限本人使用，禁止转让、出售或共享。',
          '2.3 请妥善保管账号密码，因密码泄露造成的损失由用户自行承担。'
        ]
      },
      {
        title: '三、使用边界',
        paragraphs: [
          '3.1 普通用户在小程序内不开放投稿、评论、说说、树洞、上传图片音频等内容生产能力。',
          '3.2 站点文章、导航、友情链接与站务说明等公开内容均由站长统一维护与发布。',
          '3.3 请勿通过任何方式绕过页面限制、接口限制或平台规则获取未开放能力。'
        ]
      },
      {
        title: '四、阅读与收藏',
        paragraphs: [
          '4.1 收藏、阅读历史和主题偏好等数据仅用于提升你在小程序内的阅读体验。',
          '4.2 你可以随时在“我的”页面管理收藏、基础资料和主题设置。',
          '4.3 若发现异常账号使用、频繁请求或影响站点稳定的行为，本站有权限制相关访问。'
        ]
      },
      {
        title: '五、免责声明',
        paragraphs: [
          '5.1 本站公开文章与资料主要用于个人经验分享与技术交流，不构成任何专业建议。',
          '5.2 本站不对因服务中断、网络异常、终端兼容性或第三方平台调整造成的损失负责。',
          '5.3 用户因使用本站服务而产生的争议，双方应优先友好协商解决。'
        ]
      },
      {
        title: '六、协议修改',
        paragraphs: [
          '6.1 本站有权随时修改本协议，修改后会在此页面更新。',
          '6.2 继续使用本站服务即表示接受修改后的协议。'
        ]
      }
    ]
  },
  {
    type: 'privacy-policy',
    chip: '隐私',
    icon: 'locked-filled',
    iconColor: '#7C4DFF',
    title: '隐私政策',
    subtitle: '本站尊重并保护你的个人信息。本政策说明小程序阅读版会收集哪些信息、如何使用以及如何保护。',
    updatedAt: '2026年2月1日',
    footerHint: '如有疑问，请通过关于页面的联系方式与我联系。',
    sections: [
      {
        title: '一、信息收集',
        paragraphs: [
          '1.1 登录与账号同步时收集：邮箱地址、昵称及必要的账号标识信息。',
          '1.2 阅读体验数据：你主动收藏的文章、阅读历史、主题偏好等站内阅读辅助信息。',
          '1.3 技术信息：浏览器类型、访问时间、IP地址（用于安全防护）。',
          '1.4 不收集：身份证号、人脸信息、银行卡信息等与本站阅读服务无关的敏感个人信息。'
        ]
      },
      {
        title: '二、信息使用',
        paragraphs: [
          '2.1 用于账号识别和登录验证。',
          '2.2 用于密码找回、安全验证与基础权限识别。',
          '2.3 用于在“我的”页面展示收藏记录、阅读偏好与基础资料。',
          '2.4 用于站点安全保护和异常检测。'
        ]
      },
      {
        title: '三、信息保护',
        paragraphs: [
          '3.1 密码采用加密存储，无法反向解密。',
          '3.2 不向第三方出售、出租或交换用户信息。',
          '3.3 仅在法律要求或保护站点安全时可能披露信息。',
          '3.4 采取合理措施保护数据安全，但无法保证绝对安全。'
        ]
      },
      {
        title: '四、Cookie 使用',
        paragraphs: [
          '4.1 使用 Cookie 或等效本地缓存保持登录状态并记住阅读偏好设置。',
          '4.2 你可以清除浏览器 Cookie 或小程序缓存，但可能需要重新登录或重新设置偏好。',
          '4.3 不使用 Cookie 或本地缓存进行广告追踪。'
        ]
      },
      {
        title: '五、用户权利',
        paragraphs: [
          '5.1 可以在个人资料页修改基础信息。',
          '5.2 可以删除自己收藏的内容与阅读历史。',
          '5.3 可以联系站长申请注销账号。'
        ]
      },
      {
        title: '六、政策更新',
        paragraphs: [
          '6.1 本政策可能随时更新，更新后会在此页面显示。',
          '6.2 继续使用本站服务即表示接受更新后的政策。'
        ]
      }
    ]
  }
];

export function getLegalArticle(type?: string): LegalArticle | null {
  return LEGAL_ARTICLES.find((item) => item.type === type) || null;
}
