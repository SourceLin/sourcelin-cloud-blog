<template>
  <div class="glass-page legal-page">
    <PageShell>
      <SCard
        class="legal-hero"
        variant="lite"
      >
        <div class="legal-hero__main">
          <div class="legal-hero__copy">
            <SSurfaceChip class="article-card-prelude-label" tone="accent">隐私</SSurfaceChip>
            <h1 class="legal-hero__title">隐私政策</h1>
            <p class="legal-hero__subtitle">
              本站非常重视你的隐私。本政策说明收集哪些信息、如何使用和保护这些信息。
            </p>
          </div>
        </div>
      </SCard>

      <SSurfacePanel tag="section" class="legal-content" variant="soft">
        <div class="legal-section">
          <h2 class="legal-section__title">一、信息收集</h2>
          <div class="legal-section__content">
            <p>1.1 注册时收集：邮箱地址、昵称。</p>
            <p>1.2 发布内容：你发布的文章、评论等内容。</p>
            <p>1.3 技术信息：浏览器类型、访问时间、IP地址（用于安全防护）。</p>
            <p>1.4 不收集：身份证号、人脸信息、银行卡信息等敏感个人信息。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">二、信息使用</h2>
          <div class="legal-section__content">
            <p>2.1 用于账号识别和登录验证。</p>
            <p>2.2 用于密码找回和安全验证。</p>
            <p>2.3 用于展示你的发布内容和评论。</p>
            <p>2.4 用于站点安全保护和异常检测。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">三、信息保护</h2>
          <div class="legal-section__content">
            <p>3.1 密码采用加密存储，无法反向解密。</p>
            <p>3.2 不向第三方出售、出租或交换用户信息。</p>
            <p>3.3 仅在法律要求或保护站点安全时可能披露信息。</p>
            <p>3.4 采取合理措施保护数据安全，但无法保证绝对安全。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">四、Cookie 使用</h2>
          <div class="legal-section__content">
            <p>4.1 使用 Cookie 来保持登录状态和记住偏好设置。</p>
            <p>4.2 可以清除浏览器 Cookie，但可能需要重新登录。</p>
            <p>4.3 不使用 Cookie 进行广告追踪。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">五、用户权利</h2>
          <div class="legal-section__content">
            <p>5.1 可以在个人主页修改或删除个人信息。</p>
            <p>5.2 可以删除自己发布的文章和评论。</p>
            <p>5.3 可以联系站长申请注销账号。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">六、政策更新</h2>
          <div class="legal-section__content">
            <p>6.1 本政策可能随时更新，更新后会在此页面显示。</p>
            <p>6.2 继续使用本站服务即表示接受更新后的政策。</p>
          </div>
        </div>

        <div class="legal-footer">
          <p class="legal-footer__text">本政策最后更新于：2026年2月1日</p>
          <p class="legal-footer__hint">如有疑问，请通过关于页面的联系方式与我联系。</p>
        </div>
      </SSurfacePanel>
    </PageShell>
  </div>
</template>

<script setup lang="ts">
import PageShell from '@/shared/components/layout/PageShell.vue'
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.legal-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.legal-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.legal-hero__main {
  display: grid;
  gap: var(--spacing-xxl);
  align-items: start;
}

.legal-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.legal-hero__title {
  margin: 0;
  font-size: clamp(2.4rem, 4vw, 3.2rem);
  line-height: 1.05;
  letter-spacing: 0.02em;
  color: var(--title-color);
}

.legal-hero__subtitle {
  max-width: 40rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.85;
}

.legal-content {
  display: grid;
  gap: var(--spacing-xl);
  padding: var(--spacing-xxl);
}

.legal-section {
  display: grid;
  gap: var(--spacing-md);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-panel-subtle);

  &:last-of-type {
    border-bottom: none;
  }
}

.legal-section__title {
  margin: 0;
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--title-color);
  letter-spacing: 0.02em;
}

.legal-section__content {
  display: grid;
  gap: var(--spacing-sm);

  p {
    margin: 0;
    color: var(--text-primary);
    font-size: 0.95rem;
    line-height: 1.85;
  }
}

.legal-footer {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-panel-subtle);
  text-align: center;

  p {
    margin: 0;
    color: var(--text-secondary);
    font-size: 0.88rem;
    line-height: 1.7;
  }
}

.legal-footer__hint {
  margin-top: var(--spacing-xs) !important;
  font-size: 0.82rem !important;
  color: var(--text-placeholder) !important;
}

@include sourcelin-down(md) {
  .legal-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .legal-content {
    padding: var(--spacing-lg);
  }

  .legal-section__title {
    font-size: 1.08rem;
  }
}
</style>
