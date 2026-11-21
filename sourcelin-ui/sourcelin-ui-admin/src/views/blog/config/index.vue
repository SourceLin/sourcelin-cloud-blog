<template>
  <div class="app-container blog-config-page">
    <div class="config-card">
      <div class="settings-container">
        <el-tabs v-model="activeTab" tab-position="left" class="settings-sidebar-tabs">
          <!-- 1. 网站信息 -->
          <el-tab-pane label="网站信息" name="website">
            <div class="settings-pane-content">
              <el-form :model="formState" label-width="110px" class="settings-form">
                <!-- 子卡片 1：视觉识别 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">品牌与视觉识别</div>
                  <div class="upload-row">
                    <div class="upload-item-wrap">
                      <el-form-item label="网站头像">
                        <SingleImageUpload v-model:model-value="formState.logo" />
                      </el-form-item>
                      <div class="upload-desc" style="margin-left: 110px">
                        推荐使用 120×120px 透明背景 PNG 图标
                      </div>
                    </div>
                    <div class="upload-item-wrap">
                      <el-form-item>
                        <template #label>
                          <span>
                            游客头像
                            <el-tooltip
                              content="未登录用户在评论区留言时展示的默认占位头像。"
                              placement="top"
                            >
                              <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                            </el-tooltip>
                          </span>
                        </template>
                        <SingleImageUpload v-model:model-value="formState.touristAvatar" />
                      </el-form-item>
                      <div class="upload-desc" style="margin-left: 110px">
                        未登录访客发布留言评论时的默认占位头像
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 子卡片 2：站点基础配置 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">站点元数据配置</div>
                  <el-form-item label="网站名称">
                    <el-input v-model="formState.name" placeholder="请输入网站名称" />
                  </el-form-item>

                  <el-form-item>
                    <template #label>
                      <span>
                        网站地址
                        <el-tooltip
                          content="前台博客系统的完整主页网址，如 https://sourcelin.com"
                          placement="top"
                        >
                          <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-input v-model="formState.webUrl" placeholder="请输入网站地址" />
                  </el-form-item>

                  <el-form-item label="关键字">
                    <el-input v-model="formState.keyword" placeholder="请输入关键字" />
                  </el-form-item>

                  <el-form-item label="网站描述">
                    <el-input v-model="formState.summary" placeholder="请输入网站描述" />
                  </el-form-item>
                </div>

                <!-- 子卡片 3：备案与公告 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">公告与登记备案</div>
                  <el-form-item label="网站公告">
                    <el-input v-model="formState.notice" placeholder="请输入公告" />
                  </el-form-item>

                  <el-form-item>
                    <template #label>
                      <span>
                        备案号
                        <el-tooltip
                          content="ICP 备案登记号，将呈现在博客前台主页页脚处。"
                          placement="top"
                        >
                          <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-input v-model="formState.recordNum" placeholder="请输入备案号" />
                  </el-form-item>
                </div>

                <!-- 子卡片 4：登录认证 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">前台访客登录配置</div>
                  <el-form-item>
                    <template #label>
                      <span>
                        登录方式
                        <el-tooltip
                          content="设置博客前台系统所允许的访客登录认证方式，支持多选。"
                          placement="top"
                        >
                          <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-checkbox-group v-model="loginTypeSelections">
                      <el-checkbox
                        v-for="item in loginTypeOptions"
                        :key="item.value"
                        :label="item.value"
                        border
                      >
                        {{ item.label }}
                      </el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>
                </div>
              </el-form>

              <!-- 底部统一动作 -->
              <div class="form-actions" style="margin-left: 110px">
                <el-button
                  v-hasPerm="['blog:config:edit', 'blog:config:add']"
                  type="primary"
                  :loading="saving"
                  @click="handleSave"
                >
                  保存配置
                </el-button>
                <el-button :loading="loading" @click="loadConfig">重新加载</el-button>
              </div>
            </div>
          </el-tab-pane>

          <!-- 2. 作者信息 -->
          <el-tab-pane label="作者信息" name="author">
            <div class="settings-pane-content">
              <el-form :model="formState" label-width="110px" class="settings-form">
                <!-- 子卡片 1：个人名片 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">作者名片与简述</div>
                  <div class="upload-row">
                    <div class="upload-item-wrap">
                      <el-form-item label="作者头像">
                        <SingleImageUpload v-model:model-value="formState.authorAvatar" />
                      </el-form-item>
                      <div class="upload-desc" style="margin-left: 110px">
                        推荐使用正方形个人半身照，大小限制 2MB
                      </div>
                    </div>
                  </div>

                  <el-form-item label="作者" style="margin-top: 16px">
                    <el-input v-model="formState.author" placeholder="请输入作者名称" />
                  </el-form-item>

                  <el-form-item label="作者简介">
                    <el-input v-model="formState.authorInfo" placeholder="请输入作者简介" />
                  </el-form-item>
                </div>

                <!-- 子卡片 2：详细简历 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">详细履历（关于我）</div>
                  <el-form-item label="富文本介绍" label-width="90px">
                    <div class="editor-card-wrapper" style="width: 100%">
                      <WangEditor v-model:model-value="formState.aboutMe" height="360px" />
                    </div>
                  </el-form-item>
                </div>
              </el-form>

              <div class="form-actions" style="margin-left: 110px">
                <el-button
                  v-hasPerm="['blog:config:edit', 'blog:config:add']"
                  type="primary"
                  :loading="saving"
                  @click="handleSave"
                >
                  保存配置
                </el-button>
                <el-button :loading="loading" @click="loadConfig">重新加载</el-button>
              </div>
            </div>
          </el-tab-pane>

          <!-- 3. 评论与打赏 -->
          <el-tab-pane label="评论与打赏" name="comment">
            <div class="settings-pane-content">
              <el-form :model="formState" label-width="110px" class="settings-form">
                <!-- 子卡片 1：收款码 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">赞赏通道收款码</div>
                  <div class="upload-row">
                    <div class="upload-item-wrap">
                      <el-form-item>
                        <template #label>
                          <span>
                            支付宝收款
                            <el-tooltip
                              content="在前台打赏组件中呈现的支付宝收款二维码图片。"
                              placement="top"
                            >
                              <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                            </el-tooltip>
                          </span>
                        </template>
                        <SingleImageUpload v-model:model-value="formState.aliPay" />
                      </el-form-item>
                      <div class="upload-desc" style="margin-left: 110px">
                        上传支付宝个人收款二维码图片
                      </div>
                    </div>
                    <div class="upload-item-wrap">
                      <el-form-item>
                        <template #label>
                          <span>
                            微信收款
                            <el-tooltip
                              content="在前台打赏组件中呈现的微信支付收款二维码图片。"
                              placement="top"
                            >
                              <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                            </el-tooltip>
                          </span>
                        </template>
                        <SingleImageUpload v-model:model-value="formState.weixinPay" />
                      </el-form-item>
                      <div class="upload-desc" style="margin-left: 110px">
                        上传微信个人收款二维码图片
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 子卡片 2：互动策略 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">评论与赞赏开关</div>
                  <el-form-item label="开启评论">
                    <DictSelect v-model="formState.openComment" code="blog_yes_no" />
                  </el-form-item>
                  <el-form-item label="开启打赏">
                    <DictSelect v-model="formState.openAdmiration" code="blog_yes_no" />
                  </el-form-item>
                </div>
              </el-form>

              <div class="form-actions" style="margin-left: 110px">
                <el-button
                  v-hasPerm="['blog:config:edit', 'blog:config:add']"
                  type="primary"
                  :loading="saving"
                  @click="handleSave"
                >
                  保存配置
                </el-button>
                <el-button :loading="loading" @click="loadConfig">重新加载</el-button>
              </div>
            </div>
          </el-tab-pane>

          <!-- 4. 关注我们 -->
          <el-tab-pane label="关注我们" name="follow">
            <div class="settings-pane-content">
              <el-form :model="formState" label-width="110px" class="settings-form">
                <!-- 子卡片 1：首屏显示 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">首页展示社交项</div>
                  <el-form-item>
                    <template #label>
                      <span>
                        展示项目
                        <el-tooltip
                          content="设置允许在博客首页展示的社交联系小图标通道。"
                          placement="top"
                        >
                          <el-icon class="gui-item-tip"><QuestionFilled /></el-icon>
                        </el-tooltip>
                      </span>
                    </template>
                    <el-checkbox-group v-model="showSelections">
                      <el-checkbox
                        v-for="item in showFieldOptions"
                        :key="item.value"
                        :label="item.value"
                        border
                      >
                        {{ item.label }}
                      </el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>
                </div>

                <!-- 子卡片 2：社交链接 -->
                <div class="settings-sub-card">
                  <div class="settings-sub-card__title">社交网络联络地址</div>
                  <el-form-item label="邮箱">
                    <el-input v-model="formState.email" placeholder="请输入邮箱" />
                  </el-form-item>

                  <el-form-item label="QQ号">
                    <el-input v-model="formState.qqNumber" placeholder="请输入QQ号" />
                  </el-form-item>

                  <el-form-item label="QQ群">
                    <el-input v-model="formState.qqGroup" placeholder="请输入QQ群" />
                  </el-form-item>

                  <el-form-item label="微信号">
                    <el-input v-model="formState.wechat" placeholder="请输入微信号" />
                  </el-form-item>

                  <el-form-item label="GitHub">
                    <el-input v-model="formState.github" placeholder="请输入GitHub地址" />
                  </el-form-item>

                  <el-form-item label="Gitee">
                    <el-input v-model="formState.gitee" placeholder="请输入Gitee地址" />
                  </el-form-item>
                </div>
              </el-form>

              <div class="form-actions" style="margin-left: 110px">
                <el-button
                  v-hasPerm="['blog:config:edit', 'blog:config:add']"
                  type="primary"
                  :loading="saving"
                  @click="handleSave"
                >
                  保存配置
                </el-button>
                <el-button :loading="loading" @click="loadConfig">重新加载</el-button>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import type { WebConfigForm } from "@/types/api";
import BlogConfigAPI from "@/api/blog/config";
import SingleImageUpload from "@/components/Upload/SingleImageUpload.vue";
import WangEditor from "@/components/WangEditor/index.vue";
import { QuestionFilled } from "@element-plus/icons-vue";
import "@/styles/blog-config.scss";

defineOptions({
  name: "BlogConfig",
  inheritAttrs: false,
});

const activeTab = ref("website");
const loading = ref(false);
const saving = ref(false);

const loginTypeOptions = [
  { label: "账号密码登录", value: "1" },
  { label: "邮箱验证码登录", value: "2" },
];

const showFieldOptions = [
  { label: "邮箱", value: "1" },
  { label: "QQ号", value: "2" },
  { label: "GitHub", value: "3" },
  { label: "Gitee", value: "4" },
  { label: "微信号", value: "5" },
  { label: "QQ群", value: "6" },
];

const loginTypeSelections = ref<string[]>(["1"]);
const showSelections = ref<string[]>([]);

const formState = reactive<WebConfigForm>({
  openComment: "1",
  openAdmiration: "1",
});

function splitValues(raw?: string | null): string[] {
  if (!raw) return [];
  return String(raw)
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function joinValues(values: string[]): string {
  return values.filter(Boolean).join(",");
}

function applyConfig(data?: WebConfigForm | null) {
  const source = data || {};
  Object.keys(formState).forEach((key) => {
    delete (formState as any)[key];
  });
  Object.assign(formState, source);
  formState.openComment = source.openComment ?? "1";
  formState.openAdmiration = source.openAdmiration ?? "1";
  loginTypeSelections.value = splitValues(source.loginTypeList);
  if (!loginTypeSelections.value.includes("1")) {
    loginTypeSelections.value.unshift("1");
  }
  showSelections.value = splitValues(source.showList);
}

function buildPayload(): WebConfigForm {
  return {
    ...formState,
    loginTypeList: joinValues(loginTypeSelections.value),
    showList: joinValues(showSelections.value),
  };
}

function loadConfig() {
  loading.value = true;
  BlogConfigAPI.getWebConfig()
    .then((data) => {
      applyConfig(data);
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleSave() {
  saving.value = true;
  const payload = buildPayload();
  const request =
    payload.id == null ? BlogConfigAPI.create(payload) : BlogConfigAPI.update(payload);
  request
    .then(() => {
      ElMessage.success("网站配置保存成功");
      loadConfig();
    })
    .finally(() => {
      saving.value = false;
    });
}

onMounted(() => {
  loadConfig();
});
</script>
