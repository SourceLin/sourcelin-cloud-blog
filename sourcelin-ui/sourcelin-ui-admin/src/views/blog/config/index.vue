<template>
  <div class="app-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>网站配置</span>
          <div class="header-actions">
            <el-button :loading="loading" @click="loadConfig">重新加载</el-button>
            <el-button
              v-hasPerm="['blog:config:edit', 'blog:config:add']"
              type="primary"
              :loading="saving"
              @click="handleSave"
            >
              保存配置
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="网站信息" name="website">
          <el-form :model="formState" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="网站头像">
                  <SingleImageUpload v-model:model-value="formState.logo" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="游客头像">
                  <SingleImageUpload v-model:model-value="formState.touristAvatar" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="网站名称">
                  <el-input v-model="formState.name" placeholder="请输入网站名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="网站地址">
                  <el-input v-model="formState.webUrl" placeholder="请输入网站地址" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="关键字">
                  <el-input v-model="formState.keyword" placeholder="请输入关键字" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="网站描述">
                  <el-input v-model="formState.summary" placeholder="请输入网站描述" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="公告">
                  <el-input v-model="formState.notice" placeholder="请输入公告" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="备案号">
                  <el-input v-model="formState.recordNum" placeholder="请输入备案号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="登录方式">
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
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="作者信息" name="author">
          <el-form :model="formState" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="作者头像">
                  <SingleImageUpload v-model:model-value="formState.authorAvatar" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="作者">
                  <el-input v-model="formState.author" placeholder="请输入作者名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="作者简介">
                  <el-input v-model="formState.authorInfo" placeholder="请输入作者简介" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="关于我">
              <WangEditor v-model:model-value="formState.aboutMe" height="360px" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="评论与打赏" name="comment">
          <el-form :model="formState" label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="支付宝收款码">
                  <SingleImageUpload v-model:model-value="formState.aliPay" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="微信收款码">
                  <SingleImageUpload v-model:model-value="formState.weixinPay" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="开启评论">
                  <DictSelect v-model="formState.openComment" code="blog_yes_no" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="开启打赏">
                  <DictSelect v-model="formState.openAdmiration" code="blog_yes_no" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="关注我们" name="follow">
          <el-form :model="formState" label-width="110px">
            <el-form-item label="首页展示项">
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
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="邮箱">
                  <el-input v-model="formState.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="QQ号">
                  <el-input v-model="formState.qqNumber" placeholder="请输入QQ号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="QQ群">
                  <el-input v-model="formState.qqGroup" placeholder="请输入QQ群" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="微信号">
                  <el-input v-model="formState.wechat" placeholder="请输入微信号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="GitHub">
                  <el-input v-model="formState.github" placeholder="请输入GitHub地址" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Gitee">
                  <el-input v-model="formState.gitee" placeholder="请输入Gitee地址" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import type { WebConfigForm } from "@/types/api";
import BlogConfigAPI from "@/api/blog/config";
import SingleImageUpload from "@/components/Upload/SingleImageUpload.vue";
import WangEditor from "@/components/WangEditor/index.vue";

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

<style scoped lang="scss">
.card-header {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>
