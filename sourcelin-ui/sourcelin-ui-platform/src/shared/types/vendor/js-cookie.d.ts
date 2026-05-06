declare module 'js-cookie' {
  export function get(name?: string): string | undefined
  export function set(name: string, value: string, options?: unknown): void
  export function remove(name: string, options?: unknown): void
}

