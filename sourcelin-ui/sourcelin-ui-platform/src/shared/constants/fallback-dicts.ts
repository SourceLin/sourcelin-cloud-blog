export interface DictOption {
  label: string
  value: string
  tagType?: 'success' | 'warning' | 'error' | 'info' | 'default'
}

export const FALLBACK_DICTS: Record<string, DictOption[]> = {
  blog_status: [
    { label: '审核中', value: '1', tagType: 'warning' },
    { label: '已发布', value: '2', tagType: 'success' },
    { label: '草稿', value: '3', tagType: 'info' }
  ],
  blog_comment_audit_status: [
    { label: '审核中', value: '0', tagType: 'info' },
    { label: '通过', value: '1', tagType: 'success' },
    { label: '审核未通过', value: '2', tagType: 'warning' }
  ]
}
