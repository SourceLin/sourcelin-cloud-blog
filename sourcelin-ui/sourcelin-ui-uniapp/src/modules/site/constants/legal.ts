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
    subtitle: '本协议是你与本站之间的简单约定。请仔细阅读，注册即表示同意。',
    updatedAt: '2026年2月1日',
    footerHint: '如有疑问，请通过关于页面的联系方式与我联系。',
    sections: [
      {
        title: '一、站点性质',
        paragraphs: [
          '1.1 本站为个人独立站点，非商业、非经营性网站。',
          '1.2 本站仅用于技术交流、个人学习和经验分享。',
          '1.3 本站由个人维护，不保证服务的持续性和稳定性。'
        ]
      },
      {
        title: '二、账号注册',
        paragraphs: [
          '2.1 注册仅用于发表评论和管理个人发布内容。',
          '2.2 账号仅限本人使用，禁止转让、出售或共享。',
          '2.3 请妥善保管账号密码，因密码泄露造成的损失由用户自行承担。'
        ]
      },
      {
        title: '三、禁止内容',
        paragraphs: [
          '3.1 禁止发布违反国家法律法规的内容。',
          '3.2 禁止发布色情、暴力、赌博、诈骗等违法信息。',
          '3.3 禁止发布侵犯他人知识产权的内容。',
          '3.4 禁止发布垃圾信息、恶意广告和骚扰内容。'
        ]
      },
      {
        title: '四、内容管理',
        paragraphs: [
          '4.1 本站有权对发布的内容进行审核和管理。',
          '4.2 对于违反协议的内容，本站有权删除、屏蔽或修改。',
          '4.3 对于严重违规的用户，本站有权禁言或注销账号。',
          '4.4 用户对自己的发布内容负责，文责自负。'
        ]
      },
      {
        title: '五、免责声明',
        paragraphs: [
          '5.1 本站不对用户发布内容的准确性、完整性负责。',
          '5.2 本站不对因服务中断、数据丢失等造成的损失负责。',
          '5.3 用户因使用本站服务而产生的任何纠纷，由用户自行解决。'
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
    subtitle: '本站非常重视你的隐私。本政策说明收集哪些信息、如何使用和保护这些信息。',
    updatedAt: '2026年2月1日',
    footerHint: '如有疑问，请通过关于页面的联系方式与我联系。',
    sections: [
      {
        title: '一、信息收集',
        paragraphs: [
          '1.1 注册时收集：邮箱地址、昵称。',
          '1.2 发布内容：你发布的文章、评论等内容。',
          '1.3 技术信息：浏览器类型、访问时间、IP地址（用于安全防护）。',
          '1.4 不收集：身份证号、人脸信息、银行卡信息等敏感个人信息。'
        ]
      },
      {
        title: '二、信息使用',
        paragraphs: [
          '2.1 用于账号识别和登录验证。',
          '2.2 用于密码找回和安全验证。',
          '2.3 用于展示你的发布内容和评论。',
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
          '4.1 使用 Cookie 来保持登录状态和记住偏好设置。',
          '4.2 可以清除浏览器 Cookie，但可能需要重新登录。',
          '4.3 不使用 Cookie 进行广告追踪。'
        ]
      },
      {
        title: '五、用户权利',
        paragraphs: [
          '5.1 可以在个人主页修改或删除个人信息。',
          '5.2 可以删除自己发布的文章和评论。',
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
