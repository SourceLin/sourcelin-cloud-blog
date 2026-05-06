---
name: api-contract-governance
description: 用于治理统一响应体、分页协议、错误码、前后端 API 消费和运行态接口验证。
---

# API 契约治理技能

## 使用场景

- 新增或改造后端 REST 接口。
- 前端接入或修改 API 消费。
- 修复响应体、分页、错误码或登录态接口验证失败。
- 对齐 Controller、前端 API 类型和页面消费字段。

## 输入

- 待治理接口路径、Controller、前端 API 文件或接口失败列表。
- 当前统一响应体、分页、错误码和登录态验证要求。

## 输出

- API 契约一致的后端和前端改动。
- 更新后的接口说明或验证记录。
- 后端编译、单测、前端类型检查或 HTTP 验证结果。

## 执行步骤

1. 读取 `rules/api-contract.md`、`rules/testing-and-validation.md`。
2. 扫描 Controller 是否存在手动 `ApiResponse`、对外 `R<T>`、`AjaxResult`、`TableDataInfo`、旧分页参数。
3. 扫描前端是否读取 `msg/rows/records/list`、判断 `code === 200` 或使用旧字段 fallback。
4. 后端按业务对象、`ListResult<T>`、`PageResult<T>`、`IdResponse`、`Void` 修复。
5. 前端按 request 解包后的业务数据和标准分页字段修复。
6. 涉及运行态验证时，使用本地临时登录态执行真实 HTTP 请求，不把账号、密码、token、cookie 写入仓库。
7. 按影响范围执行 Maven、npm 或 pnpm 验证；失败先修复再继续。

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
