<template>
  <div class="content-section">
    <SForm label-placement="left" label-width="80px" class="user-form">
      <SFormItem label="昵称">
        <SInput
          :model-value="modelValue.nickName"
          placeholder="请输入昵称"
          @update:model-value="updateField('nickName', String($event || ''))"
        />
      </SFormItem>

      <SFormItem label="手机号">
        <SInput
          :model-value="modelValue.phonenumber"
          placeholder="请输入手机号"
          maxlength="11"
          @update:model-value="updateField('phonenumber', String($event || ''))"
        />
      </SFormItem>

      <SFormItem label="邮箱">
        <SInput
          :model-value="modelValue.email"
          placeholder="请输入邮箱"
          maxlength="50"
          @update:model-value="updateField('email', String($event || ''))"
        />
      </SFormItem>

      <SFormItem label="性别">
        <SRadioGroup
          :model-value="modelValue.sex"
          @update:model-value="updateField('sex', String($event || '0'))"
        >
          <SRadio value="0">男</SRadio>
          <SRadio value="1">女</SRadio>
        </SRadioGroup>
      </SFormItem>

      <SFormItem label="个人简介">
        <STextarea
          :model-value="modelValue.introduction"
          placeholder="写一段话介绍自己..."
          :rows="4"
          maxlength="200"
          show-count
          @update:model-value="updateField('introduction', String($event || ''))"
        />
      </SFormItem>

      <SFormItem>
        <SButton variant="site" :loading="loading" @click="emit('submit')">保存修改</SButton>
      </SFormItem>
    </SForm>
  </div>
</template>

<script setup lang="ts">
import STextarea from '@/shared/components/ui/STextarea.vue'

interface UserFormData {
  nickName: string
  phonenumber: string
  email: string
  sex: string
  introduction: string
}

const props = defineProps<{ modelValue: UserFormData; loading: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: UserFormData]; submit: [] }>()

function updateField<K extends keyof UserFormData>(key: K, value: UserFormData[K]) {
  emit('update:modelValue', { ...props.modelValue, [key]: value })
}
</script>

<style scoped lang="scss">
.user-form {
  max-width: 540px;
}
</style>
