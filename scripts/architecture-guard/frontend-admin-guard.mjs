import { assertFile, assertPascalCaseVueFiles, printResult } from './shared.mjs'

const findings = []

for (const file of [
  'sourcelin-ui/sourcelin-ui-admin/src/components/CURD/parts/PageToolbar.vue',
  'sourcelin-ui/sourcelin-ui-admin/src/components/CURD/parts/PageTableContainer.vue',
  'sourcelin-ui/sourcelin-ui-admin/src/components/CURD/parts/PagePagination.vue',
  'sourcelin-ui/sourcelin-ui-admin/src/views/blog/shared/components/ModuleQueryForm.vue',
  'sourcelin-ui/sourcelin-ui-admin/src/views/blog/shared/components/ModuleTableSection.vue',
  'sourcelin-ui/sourcelin-ui-admin/src/views/blog/shared/components/ModuleActionColumn.vue',
  'sourcelin-ui/sourcelin-ui-admin/src/views/blog/shared/types/module-list-shell.ts',
  'sourcelin-ui/sourcelin-ui-admin/src/views/dashboard/composables/useDashboard.ts',
]) {
  assertFile(file, findings)
}

for (const dir of [
  'sourcelin-ui/sourcelin-ui-admin/src/components/CURD/parts',
  'sourcelin-ui/sourcelin-ui-admin/src/views/blog/shared/components',
  'sourcelin-ui/sourcelin-ui-admin/src/views/dashboard/components',
]) {
  assertPascalCaseVueFiles(dir, findings)
}

printResult('frontend-admin-guard', findings)
