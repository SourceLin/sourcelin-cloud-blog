<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const { params, query } = router.currentRoute.value
const { path } = params as { path: string }

router.replace({ path: '/' + path, query })
</script>

<template>
  <div />
</template>

