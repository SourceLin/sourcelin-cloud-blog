# Sourcelin 文件中心 + Nacos 配置基线

> 最后更新：2026-04-05  
> 版本：1.1  
> 适用范围：`sourcelin-gateway`、`sourcelin-file`、`sourcelin-api-file`、`sourcelin-blog`、`sourcelin-system`、`sourcelin-ui-admin`、`sourcelin-ui-platform`  
> 定位：文件上传、确认、展示、下载、删除链路的**当前权威配置与联调基线**。

---

## 0. 2026-04-05 更新摘要

### 0.1 已完成变更

1. 文件中心对外路由统一为：
   - `POST /file/upload`
   - `POST /file/confirm/{fileId}`
   - `GET /file/{fileId}`
   - `GET /file/download/{fileId}`
   - `DELETE /file/{fileId}`
2. 网关文件公开放行规则统一为：
   - `/file/statics/**`
   - `/file/download/**`
3. 前台平台图片展示统一走下载路由，不再把 `GET /file/{id}` 的 JSON 元数据当图片地址。
4. 管理后台补齐封面 `fileId` 上送与业务保存（`avatarFileId`）闭环。
5. 文章/说说保存后接入 `confirm` 闭环，富文本 `fileId` 提取规则支持新旧路由与 query 参数。
6. 下载链路增加安全整改：
   - 下载前增加服务端权限与状态闸门（匿名仅可访问 `ACTIVE + PUBLIC`）。
   - 下载响应按“安全内联白名单”控制 `inline/attachment`，并统一返回 `X-Content-Type-Options: nosniff`。
   - 白名单兼容后续视频预览（`video/mp4`、`video/webm`、`video/ogg`）。

### 0.2 本次基线的关键结论

1. `/file/{fileId}` 是元数据接口，不能直接作为 `<img src>`。
2. 图片展示/预览必须使用 `/file/download/{fileId}` 或 `/file/statics/**`。
3. 网关白名单中旧规则 `- /file/file/*/download` 已不再适用，必须替换为 `- /file/download/**`。
4. `file_info.storage_path` 应保存本地真实路径，不应保存完整 HTTP URL。
5. `/file/download/{id}` 虽可公开访问，但实际下载权限最终以文件服务内校验为准，不再仅依赖网关白名单。

---

## 1. 文件中心架构与职责

### 1.1 请求链路

```text
浏览器/前端
  -> 网关 /file/**
     - Path=/file/**
     - StripPrefix=1
     - Sa-Token 规则：仅 /file/statics/**、/file/download/** 公开
  -> sourcelin-file
     - FileCenterController
     - FileCenterServiceImpl
     - Local/FastDFS/MinIO 适配
  -> file_info（文件元数据）
```

### 1.2 组件分工

1. `sourcelin-file`：上传、确认、元数据、下载、删除。
2. `sourcelin-api-file`：Feign 契约（供 blog/system 等模块调用）。
3. `sourcelin-gateway`：统一入口与公开文件路由放行。
4. `sourcelin-blog` / `sourcelin-system`：业务保存后触发 `confirm`。
5. `sourcelin-ui-platform` / `sourcelin-ui-admin`：上传后提交 `fileId`，展示走下载地址。

---

## 2. 接口契约基线（当前有效）

## 2.1 `sourcelin-file` 控制器接口

来源：`sourcelin-modules/sourcelin-file/.../FileCenterController.java`

1. `POST /upload`
   - 表单字段：`file`
   - 返回：`R<SysFile>`，包含 `url`、`fileId`、`contentType` 等。
2. `POST /confirm/{fileId}`
   - Body：`FileConfirmRequest`
   - 说明：path `fileId` 与 body `fileId` 不一致会失败。
3. `GET /{fileId}`
   - 说明：返回元数据 JSON，不是文件流。
4. `GET /download/{fileId}`
   - 说明：返回二进制文件流；是否 `inline` 由安全白名单决定（非白名单强制 `attachment`）。
5. `DELETE /{fileId}`
   - 说明：逻辑删除（状态置为 `PENDING_DELETE`）。

## 2.2 网关对外真实地址

网关有 `Path=/file/**` + `StripPrefix=1`，因此对外地址为：

1. `POST /file/upload`
2. `POST /file/confirm/{fileId}`
3. `GET /file/{fileId}`
4. `GET /file/download/{fileId}`
5. `DELETE /file/{fileId}`

## 2.3 下载安全策略（v1.1）

`GET /file/download/{fileId}` 的服务端规则如下：

1. 权限与状态闸门（`FileCenterServiceImpl#getFileInfo`）：
   - `admin` 登录：允许访问任意文件。
   - 匿名访问：仅允许 `status=ACTIVE` 且 `accessScope=PUBLIC`。
   - `blog` 登录：允许访问 `ACTIVE + PUBLIC`，以及本人上传文件（含 `TEMP`）。
2. 响应头安全策略（`FileCenterController#download`）：
   - 永远设置 `X-Content-Type-Options: nosniff`。
   - 仅当 `contentType + 扩展名` 同时命中白名单时使用 `inline`：
     - 图片：`image/png|jpeg|jpg|gif|bmp|webp`
     - 视频：`video/mp4|webm|ogg`
   - 其他类型统一使用 `attachment`，并降级 `Content-Type=application/octet-stream`。
3. 状态码语义：
   - 文件不存在：`404`
   - 无下载权限：`403`

---

## 3. Nacos 配置基线

## 3.1 `sourcelin-gateway-dev.yml`（文件相关关键片段）

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 文件服务
        - id: sourcelin-file
          uri: lb://sourcelin-file
          predicates:
            - Path=/file/**
          filters:
            - StripPrefix=1

security:
  ignore:
    whites:
      # ==================== 文件相关 ====================
      - /file/statics/**
      - /file/download/**

sa-token:
  is-check: true
```

> 注意：若仍保留 `- /file/file/*/download`，会与当前实现不一致，需删除。
> 说明：本次安全整改不需要新增 Nacos 配置项；如需匿名图片/视频展示，继续保留 `/file/download/**` 即可。

## 3.2 `sourcelin-file-dev.yml`（建议关键片段）

```yaml
# 本地文件上传
file:
  # 推荐统一走网关对外域名，避免前端直连 9300
  domain: http://127.0.0.1:8080/file
  path: D:/sourcelin/uploadPath
  prefix: /statics

# 防盗链配置
referer:
  enabled: false
  allowed-domains: localhost,127.0.0.1
```

> 若你当前环境必须直连文件服务，也可临时使用 `http://127.0.0.1:9300`，但不建议长期保留。

## 3.3 bootstrap 共享配置加载

`sourcelin-gateway` 与 `sourcelin-file` 都应显式绑定共享配置：

```yaml
spring:
  cloud:
    nacos:
      config:
        group: BLOG_GROUP
        file-extension: yml
        shared-configs:
          - data-id: application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
            group: ${spring.cloud.nacos.config.group}
```

---

## 4. 数据模型与状态语义

核心表：`file_info`

关键字段语义：

1. `file_id`：文件主键 ID（业务侧应优先存此字段）。
2. `access_url`：访问地址（上传返回 URL）。
3. `storage_path`：本地真实磁盘路径（供下载流读取）。
4. `status`：
   - `TEMP`：上传完成但未确认。
   - `ACTIVE`：业务确认后可长期引用。
   - `PENDING_DELETE`：逻辑删除待清理。
5. `biz_type` / `biz_id`：业务归属信息（confirm 时写入）。
6. `ref_count`：当前引用计数（当前实现确认时置 1）。

---

## 5. 前后端对齐基线

## 5.1 平台前端（Vue3）

1. `VITE_FILE_BASE_API=/file`
2. `useFileUrl.fileIdToDownloadUrl()` 统一输出 `/file/download/{id}`。
3. 上传返回的 `url` 若为 `/statics/**`，展示时统一拼成网关路径。

## 5.2 管理后台（Vue2）

1. 上传入口统一走：`process.env.VUE_APP_BASE_API + '/file/upload'`
2. `ImageUpload` 会回传 `file-ids-change` 事件。
3. 文章管理页接收回调并写入 `form.avatarFileId`。
4. 富文本上传会把 `fileId` 附加到图片 URL query（`?fileId=...`），便于后端提取确认。

---

## 6. 业务确认（confirm）闭环基线

## 6.1 文章

`ArticleServiceImpl` 在新增/更新成功后执行：

1. 若有 `avatarFileId`：确认封面文件。
2. 从正文 HTML 提取 `fileId` 并逐个确认。
3. `bizType=article`，`bizId=articleId`。

## 6.2 说说

`SayServiceImpl` 在新增/更新成功后执行：

1. 读取 `imageFileIds`（逗号分隔）批量确认。
2. `bizType=say`，`bizId=sayId`。

## 6.3 树洞

当前 `Treehole` 仍以 `images` URL 字段为主，未引入 `imageFileIds` 字段；如需完整文件中心闭环，建议后续补齐该字段并接入确认逻辑。

---

## 7. 富文本 `fileId` 提取规则基线

`BlogRichTextSanitizer.collectFileIds()` 支持：

1. `/file/download/{id}`
2. `/file/{id}/download`
3. `/file/{id}`
4. `/file-api/file/{id}`（历史路径兼容）
5. query 参数：`?fileId={id}`、`?fid={id}`

---

## 8. 验证清单（发布前必做）

1. 上传图片后，接口返回包含 `fileId`。
2. 业务保存后，`file_info.status` 从 `TEMP` 变为 `ACTIVE`。
3. `GET /file/{id}` 返回 JSON 元数据。
4. `GET /file/download/{id}`：
   - 图片/视频白名单类型：`Content-Disposition=inline`
   - 非白名单（如 `text/html`）：`Content-Disposition=attachment` 且 `X-Content-Type-Options=nosniff`
5. 匿名下载 `TEMP` 文件返回 `403`；匿名下载 `ACTIVE + PUBLIC` 返回 `200`。
6. 未登录访问 `/file/download/{id}` 与 `/file/statics/**` 可成功（前提：文件符合公开规则）。
7. 未登录访问 `/file/upload` 仍受保护（按当前网关策略）。
8. 前端页面中不再出现 `/file/file/{id}/download` 老路径。

---

## 9. 常见问题与排障

### 9.1 浏览器中 `data-error="true"`，图片不显示

优先检查：

1. 图片地址是否误用了 `/file/{id}` 元数据接口。
2. 网关是否仍配置旧白名单 `/file/file/*/download`。
3. `storage_path` 是否写成了完整 HTTP URL（应为本地路径）。
4. `file.path` 与磁盘实际目录是否一致。

### 9.2 上传成功但下载 404

排查顺序：

1. 查 `file_info` 中 `storage_path`、`access_url`。
2. 检查 `FileCenterServiceImpl.resolvePhysicalFileForDownload` 是否能回退解析。
3. 检查 `file.prefix` 与 `access_url` 中静态前缀是否一致。

### 9.4 下载返回 403（新增）

排查顺序：

1. 校验文件状态是否为 `ACTIVE` 且 `access_scope=PUBLIC`（匿名访问场景）。
2. 校验当前登录域：
   - `admin` 域应可访问全部；
   - `blog` 域仅本人文件放行（非公开文件）。
3. 确认是否是未确认文件（`TEMP`）被匿名下载，属于预期拒绝。

### 9.3 配置已改但未生效

1. 确认配置发布在正确 `group`（`BLOG_GROUP`）。
2. 重启 `sourcelin-gateway` 与 `sourcelin-file`。
3. 用 `/actuator/env` 验证实际加载的配置源。

---

## 10. 变更记录

### v1.1（2026-04-05）

1. 增加下载权限与状态闸门基线（匿名仅 `ACTIVE + PUBLIC`）。
2. 增加下载安全响应基线（安全内联白名单 + `nosniff` + 非白名单强制附件）。
3. 明确视频预览支持边界（`mp4/webm/ogg`）与状态码语义（`403/404`）。
4. 补充整改后的验证清单与 `403` 排障流程。

### v1.0（2026-04-05）

1. 首次形成文件中心配置与链路基线文档。
2. 固化 5 个对外接口契约。
3. 固化网关公开文件路由白名单规则。
4. 固化 admin/platform 与 blog/file 的闭环对齐策略。
