<template>
  <section class="dashboard-signal-grid">
    <article v-for="item in signalCards" :key="item.title" class="dashboard-signal-card">
      <div class="dashboard-signal-card__title">{{ item.title }}</div>
      <div class="dashboard-signal-card__value">{{ item.value }}</div>
      <div class="dashboard-signal-card__hint">{{ item.hint }}</div>
    </article>
  </section>
</template>

<script setup lang="ts">
interface SignalCard {
  title: string;
  value: string | number;
  hint: string;
}

defineProps<{
  signalCards: SignalCard[];
}>();
</script>
