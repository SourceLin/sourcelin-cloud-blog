import { assertFile, assertPascalCaseVueFiles, printResult } from './shared.mjs'

const findings = []

for (const file of [
  'sourcelin-ui/sourcelin-ui-platform/src/modules/say/composables/useSayCardPresentation.ts',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/say/composables/useSayCardInteractions.ts',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/say/composables/useSayCardComments.ts',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/article/composables/useTagPageQuery.ts',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/about/composables/useAboutPage.ts',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/navigation/composables/useLinksPage.ts',
]) {
  assertFile(file, findings)
}

for (const dir of [
  'sourcelin-ui/sourcelin-ui-platform/src/modules/say/components',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/article/components',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/about/components',
  'sourcelin-ui/sourcelin-ui-platform/src/modules/navigation/components',
]) {
  assertPascalCaseVueFiles(dir, findings)
}

printResult('frontend-platform-guard', findings)
