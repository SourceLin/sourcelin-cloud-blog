<template>
  <div class="content-section">
    <SForm label-placement="left" label-width="80px" class="user-form">
      <SFormItem label="旧密码">
        <SInput
          :model-value="modelValue.oldPassword"
          type="password"
          placeholder="请输入旧密码"
          show-password-on="click"
          @update:model-value="updateField('oldPassword', String($event || ''))"
        />
      </SFormItem>

      <SFormItem label="新密码">
        <SInput
          :model-value="modelValue.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password-on="click"
          @update:model-value="updateField('newPassword', String($event || ''))"
        />
      </SFormItem>

      <SFormItem label="确认密码">
        <SInput
          :model-value="modelValue.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password-on="click"
          @update:model-value="updateField('confirmPassword', String($event || ''))"
        />
      </SFormItem>

      <SFormItem>
        <SButton variant="site" :loading="loading" @click="emit('submit')">修改密码</SButton>
      </SFormItem>
    </SForm>
  </div>
</template>

<script setup lang="ts">
interface PasswordFormData {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

const props = defineProps<{ modelValue: PasswordFormData; loading: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: PasswordFormData]; submit: [] }>()

function updateField<K extends keyof PasswordFormData>(key: K, value: PasswordFormData[K]) {
  emit('update:modelValue', { ...props.modelValue, [key]: value })
}
</script>

<style scoped lang="scss">
.user-form {
  max-width: 540px;
}
</style>
