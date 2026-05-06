<template>
  <div class="glass-page legal-page">
    <PageShell>
      <SCard
        class="legal-hero"
        variant="lite"
      >
        <div class="legal-hero__main">
          <div class="legal-hero__copy">
            <SSurfaceChip class="article-card-prelude-label" tone="accent">协议</SSurfaceChip>
            <h1 class="legal-hero__title">用户协议</h1>
            <p class="legal-hero__subtitle">
              本协议是你与本站之间的简单约定。请仔细阅读，注册即表示同意。
            </p>
          </div>
        </div>
      </SCard>

      <SSurfacePanel tag="section" class="legal-content" variant="soft">
        <div class="legal-section">
          <h2 class="legal-section__title">一、站点性质</h2>
          <div class="legal-section__content">
            <p>1.1 本站为个人独立站点，非商业、非经营性网站。</p>
            <p>1.2 本站仅用于技术交流、个人学习和经验分享。</p>
            <p>1.3 本站由个人维护，不保证服务的持续性和稳定性。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">二、账号注册</h2>
          <div class="legal-section__content">
            <p>2.1 注册仅用于发表评论和管理个人发布内容。</p>
            <p>2.2 账号仅限本人使用，禁止转让、出售或共享。</p>
            <p>2.3 请妥善保管账号密码，因密码泄露造成的损失由用户自行承担。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">三、禁止内容</h2>
          <div class="legal-section__content">
            <p>3.1 禁止发布违反国家法律法规的内容。</p>
            <p>3.2 禁止发布色情、暴力、赌博、诈骗等违法信息。</p>
            <p>3.3 禁止发布侵犯他人知识产权的内容。</p>
            <p>3.4 禁止发布垃圾信息、恶意广告和骚扰内容。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">四、内容管理</h2>
          <div class="legal-section__content">
            <p>4.1 本站有权对发布的内容进行审核和管理。</p>
            <p>4.2 对于违反协议的内容，本站有权删除、屏蔽或修改。</p>
            <p>4.3 对于严重违规的用户，本站有权禁言或注销账号。</p>
            <p>4.4 用户对自己的发布内容负责，文责自负。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">五、免责声明</h2>
          <div class="legal-section__content">
            <p>5.1 本站不对用户发布内容的准确性、完整性负责。</p>
            <p>5.2 本站不对因服务中断、数据丢失等造成的损失负责。</p>
            <p>5.3 用户因使用本站服务而产生的任何纠纷，由用户自行解决。</p>
          </div>
        </div>

        <div class="legal-section">
          <h2 class="legal-section__title">六、协议修改</h2>
          <div class="legal-section__content">
            <p>6.1 本站有权随时修改本协议，修改后会在此页面更新。</p>
            <p>6.2 继续使用本站服务即表示接受修改后的协议。</p>
          </div>
        </div>

        <div class="legal-footer">
          <p class="legal-footer__text">本协议最后更新于：2026年2月1日</p>
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
