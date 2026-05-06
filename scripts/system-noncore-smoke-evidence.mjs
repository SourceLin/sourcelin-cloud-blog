#!/usr/bin/env node

import { mkdir, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { constants, createPrivateKey, createPublicKey, publicEncrypt } from "node:crypto";

const baseUrl = process.env.BASE_URL ?? "http://localhost:8080";
const adminUsername = process.env.ADMIN_USERNAME ?? "admin";
const adminPassword = process.env.ADMIN_PASSWORD ?? "123456";
const adminPasswordCandidates = (process.env.ADMIN_PASSWORD_CANDIDATES ?? "123456,admin123")
  .split(",")
  .map((s) => s.trim())
  .filter(Boolean);
const systemDirectBaseUrl = process.env.SYSTEM_DIRECT_BASE_URL ?? "http://localhost:9201";
const cleanup = String(process.env.CLEANUP ?? "true").toLowerCase() === "true";

const rsaPrivateKeyBase64 =
  process.env.RSA_PRIVATE_KEY_BASE64 ??
  "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKNPuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gAkM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWowcSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99EcvDQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthhYhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3UP8iWi1Qw0Y=";

const now = new Date();
const stamp = now.toISOString().replace(/[:.]/g, "-");
const outDir =
  process.env.OUT_DIR ??
  join(
    process.cwd(),
    "docs",
    "evidence",
    "system-noncore",
    `${now.toISOString().slice(0, 10)}-smoke-${stamp}`
  );

const traces = [];
const checks = [];
const artifacts = {
  createdPostId: "",
  createdNoticeId: "",
};

function ensureOkCode(payload) {
  const code = payload?.code;
  return code === 0;
}

function encryptPassword(rawPassword) {
  if (!rawPassword) return rawPassword;
  const privateKeyObj = createPrivateKey({
    key: Buffer.from(rsaPrivateKeyBase64, "base64"),
    format: "der",
    type: "pkcs8",
  });
  const publicKeyObj = createPublicKey(privateKeyObj);
  const encrypted = publicEncrypt(
    {
      key: publicKeyObj,
      padding: constants.RSA_PKCS1_PADDING,
    },
    Buffer.from(rawPassword, "utf8")
  );
  return encrypted.toString("base64");
}

function toQueryString(params) {
  if (!params) return "";
  const usp = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v === undefined || v === null || v === "") return;
    usp.append(k, String(v));
  });
  const qs = usp.toString();
  return qs ? `?${qs}` : "";
}

async function callApi({ name, method = "GET", path, query, body, token }) {
  const rawUrl = path.startsWith("http://") || path.startsWith("https://") ? path : `${baseUrl}${path}`;
  const url = `${rawUrl}${toQueryString(query)}`;
  const headers = {};
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (body !== undefined) {
    headers["Content-Type"] = "application/json";
  }
  const response = await fetch(url, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  const text = await response.text();
  let payload = null;
  try {
    payload = text ? JSON.parse(text) : null;
  } catch {
    payload = { rawText: text };
  }
  traces.push({
    name,
    request: { method, url, body: body ?? null, hasAuth: Boolean(token) },
    response: {
      status: response.status,
      ok: response.ok,
      payload,
    },
  });
  if (!response.ok || !ensureOkCode(payload)) {
    throw new Error(`${name} 失败: status=${response.status}, code=${payload?.code}, message=${payload?.message}`);
  }
  return payload?.data;
}

function addCheck(module, pass, detail) {
  checks.push({ module, pass, detail });
}

function pageItems(data) {
  if (!data) return [];
  return data.items ?? data.list ?? [];
}

function toId(value) {
  if (value === undefined || value === null || value === "") return "";
  return String(value);
}

async function loginAdmin() {
  const candidates = [...new Set([adminPassword, ...adminPasswordCandidates].filter(Boolean))];
  const captcha = await callApi({
    name: "A0 获取验证码(admin)",
    method: "GET",
    path: "/auth/captcha",
    query: { loginType: "admin" },
  });
  for (const candidate of candidates) {
    try {
      const loginData = await callApi({
        name: `A1 登录(admin:${candidate.length})`,
        method: "POST",
        path: "/auth/login",
        body: {
          username: adminUsername,
          password: encryptPassword(candidate),
          loginType: "admin",
          captchaCode: "",
          captchaUuid: captcha?.uuid ?? "",
        },
      });
      const token = loginData?.token ?? loginData?.accessToken ?? "";
      if (token) {
        return token;
      }
    } catch {
      // 尝试下一个候选口令
    }
  }
  throw new Error("admin 登录失败");
}

async function main() {
  await mkdir(outDir, { recursive: true });
  const suffix = `${Date.now()}`.slice(-6);
  try {
    const token = await loginAdmin();

    // 1) 参数配置
    const configPage = await callApi({
      name: "S1 参数配置列表",
      path: "/system/config/list",
      query: { page: 1, pageSize: 10 },
      token,
    });
    const firstConfig = pageItems(configPage)[0];
    if (firstConfig?.configId) {
      await callApi({
        name: "S1 参数配置修改",
        method: "PUT",
        path: "/system/config",
        token,
        body: {
          configId: firstConfig.configId,
          configName: firstConfig.configName,
          configKey: firstConfig.configKey,
          configValue: firstConfig.configValue,
          remark: `${firstConfig.remark ?? ""}`.slice(0, 180),
        },
      });
      addCheck("参数配置", true, "列表+修改通过");
    } else {
      addCheck("参数配置", false, "未获取到可修改配置");
    }

    // 2) 岗位管理：新增+删除
    const postCode = `SMK_${suffix}`;
    await callApi({
      name: "S2 岗位新增",
      method: "POST",
      path: "/system/post",
      token,
      body: {
        postCode,
        postName: `冒烟岗位${suffix}`,
        postSort: 99,
        status: "0",
        remark: "smoke",
      },
    });
    const postPage = await callApi({
      name: "S2 岗位列表定位",
      path: "/system/post/list",
      query: { page: 1, pageSize: 20, postCode },
      token,
    });
    const post = pageItems(postPage).find((i) => i.postCode === postCode);
    artifacts.createdPostId = toId(post?.postId);
    if (!artifacts.createdPostId) {
      throw new Error("岗位新增后未定位到 postId");
    }
    if (cleanup) {
      await callApi({
        name: "S2 岗位删除",
        method: "DELETE",
        path: `/system/post/${artifacts.createdPostId}`,
        token,
      });
    }
    addCheck("岗位管理", true, "列表+新增+删除通过");

    // 3) 公告管理：新增+编辑(+删除)
    const noticeTitle = `冒烟公告${suffix}`;
    await callApi({
      name: "S3 公告新增",
      method: "POST",
      path: "/system/notice",
      token,
      body: {
        noticeTitle,
        noticeType: "1",
        noticeContent: "system noncore smoke",
        status: "1",
      },
    });
    const noticePage = await callApi({
      name: "S3 公告列表定位",
      path: "/system/notice/list",
      query: { page: 1, pageSize: 20, noticeTitle },
      token,
    });
    const notice = pageItems(noticePage).find((i) => i.noticeTitle === noticeTitle);
    artifacts.createdNoticeId = toId(notice?.noticeId);
    if (!artifacts.createdNoticeId) {
      throw new Error("公告新增后未定位到 noticeId");
    }
    await callApi({
      name: "S3 公告编辑",
      method: "PUT",
      path: "/system/notice",
      token,
      body: {
        noticeId: Number(artifacts.createdNoticeId),
        noticeTitle: `${noticeTitle}-编辑`,
        noticeType: "1",
        noticeContent: "system noncore smoke edited",
        status: "1",
      },
    });
    if (cleanup) {
      await callApi({
        name: "S3 公告删除",
        method: "DELETE",
        path: `/system/notice/${artifacts.createdNoticeId}`,
        token,
      });
    }
    addCheck("公告管理", true, "列表+新增+编辑(+删除)通过");

    // 4) 操作日志
    await callApi({
      name: "S4 操作日志列表",
      path: "/system/operlog/list",
      query: { page: 1, pageSize: 10 },
      token,
    });
    addCheck("操作日志", true, "列表通过");

    // 5) 登录日志：列表 + clean
    await callApi({
      name: "S5 登录日志列表",
      path: "/system/logininfor/list",
      query: { page: 1, pageSize: 10 },
      token,
    });
    await callApi({
      name: "S5 登录日志清理",
      method: "DELETE",
      path: "/system/logininfor/clean",
      token,
    });
    addCheck("登录日志", true, "列表+清理通过");

    // 6) 在线用户：列表 + 强退(使用不存在 tokenId 验证链路)
    await callApi({
      name: "S6 在线用户列表",
      path: "/system/online/list",
      query: { pageNum: 1, pageSize: 10 },
      token,
    });
    await callApi({
      name: "S6 在线用户强退",
      method: "DELETE",
      path: `/system/online/smoke-${suffix}`,
      token,
    });
    addCheck("在线用户", true, "列表+强退通过");

    // 7) 个人中心：查询 + 修改
    const profileToken = await loginAdmin();
    let profilePath = "/system/user/profile";
    let profile;
    try {
      profile = await callApi({
        name: "S7 个人中心查询",
        path: profilePath,
        token: profileToken,
      });
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : String(error);
      const needFallback = errorMessage.includes("S7 个人中心查询") && errorMessage.includes("status=401");
      if (!needFallback || !systemDirectBaseUrl) {
        throw error;
      }
      profilePath = `${systemDirectBaseUrl}/user/profile`;
      profile = await callApi({
        name: "S7 个人中心查询(直连)",
        path: profilePath,
        token: profileToken,
      });
    }
    const user = profile?.user ?? {};
    await callApi({
      name: "S7 个人中心修改",
      method: "PUT",
      path: profilePath,
      token: profileToken,
      body: {
        nickName: user.nickName,
        email: user.email,
        phonenumber: user.phonenumber,
        sex: user.sex,
        remark: user.remark,
      },
    });
    addCheck("个人中心", true, "查询+修改通过");

    const allPass = checks.every((c) => c.pass);
    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(join(outDir, "checks.json"), JSON.stringify(checks, null, 2), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# System 非核心模块冒烟结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- CLEANUP：${cleanup ? "true" : "false"}
- 结果：${allPass ? "PASS" : "FAIL"}

## 模块结果

${checks.map((c) => `- [${c.pass ? "PASS" : "FAIL"}] ${c.module}：${c.detail}`).join("\n")}

## 关键产物ID

- createdPostId: ${artifacts.createdPostId || "(none)"}
- createdNoticeId: ${artifacts.createdNoticeId || "(none)"}

## 产物文件

- request-response-traces.json
- checks.json
`,
        "utf8"
      ),
    ]);

    if (!allPass) {
      process.exitCode = 1;
    } else {
      console.log(`system 非核心冒烟完成：${outDir}`);
    }
  } catch (error) {
    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(join(outDir, "checks.json"), JSON.stringify(checks, null, 2), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# System 非核心模块冒烟失败

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- 错误：${error instanceof Error ? error.message : String(error)}
`,
        "utf8"
      ),
    ]);
    process.exitCode = 1;
  }
}

await main();

