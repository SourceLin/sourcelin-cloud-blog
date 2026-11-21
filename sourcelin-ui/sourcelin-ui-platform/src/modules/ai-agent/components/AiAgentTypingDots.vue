<script setup lang="ts">
</script>

<template>
  <div class="ai-agent-typing">
    <span class="ai-agent-typing__dot" />
    <span class="ai-agent-typing__dot" />
    <span class="ai-agent-typing__dot" />
  </div>
</template>

<style scoped lang="scss">
.ai-agent-typing {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}

.ai-agent-typing__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary-color);
  opacity: 0.4;
  animation: typing-dot 1.4s ease-in-out infinite;

  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.4s; }
}

@keyframes typing-dot {
  0%, 60%, 100% { opacity: 0.2; transform: scale(0.8); }
  30% { opacity: 1; transform: scale(1.2); }
}
</style>
