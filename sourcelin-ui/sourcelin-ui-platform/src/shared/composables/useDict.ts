import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useDictStore } from '@/stores/dict.store'

export function useDict(dictType: string) {
  const dictStore = useDictStore()
  const { dictMap } = storeToRefs(dictStore)

  const options = computed(() => dictStore.getDictItems(dictType))

  function labelOf(value: string | number | undefined) {
    return dictStore.resolveLabel(dictType, value)
  }

  function tagOf(value: string | number | undefined) {
    return dictStore.resolveTagType(dictType, value)
  }

  async function reload() {
    await dictStore.ensureDict(dictType)
  }

  onMounted(() => {
    dictStore.ensureDict(dictType)
  })

  return {
    options,
    labelOf,
    tagOf,
    dictMap,
    reload
  }
}
