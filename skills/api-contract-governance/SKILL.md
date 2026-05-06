---
name: api-contract-governance
description: 用于治理统一响应体、分页协议、错误码、前后端 API 消费、静态扫描和 HTTP 冒烟测试。
---

# API 契约治理技能

## 使用场景

- 新增或改造后端 REST 接口。
- 前端接入或修改 API 消费。
- 修复 `api-contract-scan` 或 `blog-api-smoke` 失败。
- 更新 `docs/architecture/api-contract.md`。

## 输入

- 待治理接口路径、Controller、前端 API 文件或 smoke 失败列表。
- 当前统一响应体、分页、错误码和登录态验证要求。

## 输出

- API 契约一致的后端和前端改动。
- 更新后的接口文档或 smoke 覆盖。
- `api-contract-scan` 和必要 smoke 验证结果。

## 执行步骤

1. 读取 `rules/api-contract.md`、`rules/testing-and-validation.md` 和 `docs/architecture/api-contract.md`。
2. 扫描 Controller 是否存在手动 `ApiResponse`、对外 `R<T>`、`AjaxResult`、`TableDataInfo`、旧分页参数。
3. 扫描前端是否读取 `msg/rows/records/list`、判断 `code === 200` 或使用旧字段 fallback。
4. 后端按业务对象、`ListResult<T>`、`PageResult<T>`、`IdResponse`、`Void` 修复。
5. 前端按 request 解包后的业务数据和标准分页字段修复。
6. 更新 `scripts/blog-api-smoke.mjs` 时只增加真实严格断言，不降低标准。
7. 执行 `node scripts/api-contract-scan.mjs`；涉及运行态验证时执行 `node scripts/blog-api-smoke.mjs`。

## 依赖 rules

- `rules/api-contract.md`
- `rules/testing-and-validation.md`
- `rules/coding-conventions.md`

## 禁止行为

- 禁止保留旧字段兼容层。
- 禁止将 `code === 200` 作为成功判断。
- 禁止用 `items ?? list` 或同类 fallback 掩盖后端问题。
- 禁止把账号、密码、token、cookie 写入仓库。
- 禁止把 Feign `R<T>` 暴露给前端 REST。
