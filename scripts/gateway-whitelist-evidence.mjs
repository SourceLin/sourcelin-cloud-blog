#!/usr/bin/env node

import { mkdir, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { constants, createPrivateKey, createPublicKey, publicEncrypt } from "node:crypto";
import { spawnSync } from "node:child_process";

const baseUrl = process.env.BASE_URL ?? "http://localhost:8080";
const adminUsername = process.env.ADMIN_USERNAME ?? "admin";
const adminPassword = process.env.ADMIN_PASSWORD ?? "123456";
const adminPasswordCandidates = (process.env.ADMIN_PASSWORD_CANDIDATES ?? "123456,admin123")
  .split(",")
  .map((s) => s.trim())
  .filter(Boolean);
const blogUsername = process.env.BLOG_USERNAME ?? "";
const blogPassword = process.env.BLOG_PASSWORD ?? "";
const redisHost = process.env.REDIS_HOST ?? "localhost";
const redisPort = process.env.REDIS_PORT ?? "6379";
const redisPassword = process.env.REDIS_PASSWORD ?? "";

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
    "gateway",
    `${now.toISOString().slice(0, 10)}-whitelist-${stamp}`
  );

const traces = [];

function ensureOkCode(payload) {
  const code = payload?.code;
  return code === undefined || code === null || code === 0;
}

function isUnauthorizedStatus(status, payload) {
  const code = Number(payload?.code);
  return status === 401 || status === 403 || code === 401 || code === 403 || code === 40100 || code === 40300;
}

function isHardUnauthorizedForWhitelist(status, payload) {
  const code = Number(payload?.code);
  return status === 401 || code === 401 || code === 40100;
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

function readCaptchaCodeFromRedis(uuid) {
  if (!uuid) return "";
  const args = ["-h", redisHost, "-p", String(redisPort)];
  if (redisPassword) {
    args.push("-a", redisPassword);
  }
  args.push("GET", `captcha:shear:${uuid}`);
  const result = spawnSync("redis-cli", args, { encoding: "utf8" });
  if (result.status !== 0) {
    return "";
  }
  const lines = `${result.stdout ?? ""}`
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean)
    .filter((line) => !line.startsWith("Warning:"));
  const value = lines.length ? lines[lines.length - 1] : "";
  if (!value || value === "(nil)") {
    return "";
  }
  return value;
}

function toQueryString(params) {
  if (!params) return "";
  const usp = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") return;
    usp.append(key, String(value));
  });
  const qs = usp.toString();
  return qs ? `?${qs}` : "";
}

async function callApi({ name, method = "GET", path, query, body, token, headers, isFormData = false }) {
  const url = `${baseUrl}${path}${toQueryString(query)}`;
  const requestHeaders = {
    ...(headers ?? {}),
  };
  if (token) {
    requestHeaders.Authorization = `Bearer ${token}`;
  }
  if (!isFormData && !requestHeaders["Content-Type"] && body !== undefined) {
    requestHeaders["Content-Type"] = "application/json";
  }

  const response = await fetch(url, {
    method,
    headers: requestHeaders,
    body: body === undefined ? undefined : isFormData ? body : JSON.stringify(body),
  });

  const text = await response.text();
  let payload = null;
  try {
    payload = text ? JSON.parse(text) : null;
  } catch {
    payload = { rawText: text };
  }

  const record = {
    name,
    request: {
      method,
      url,
      body: isFormData ? "[FormData]" : body ?? null,
      hasAuth: Boolean(token),
    },
    response: {
      status: response.status,
      ok: response.ok,
      payload,
    },
  };
  traces.push(record);
  return record;
}

async function getCaptcha(loginType) {
  const result = await callApi({
    name: `A0 获取验证码(${loginType})`,
    path: "/auth/captcha",
    query: { loginType },
  });
  if (!result.response.ok || !ensureOkCode(result.response.payload)) {
    throw new Error(`获取验证码失败(${loginType})`);
  }
  return result.response.payload?.data ?? {};
}

async function tryLogin({ username, password, loginType, tag }) {
  const captcha = await getCaptcha(loginType);
  const captchaCode = captcha?.captchaEnabled ? readCaptchaCodeFromRedis(captcha?.uuid ?? "") : "";
  const result = await callApi({
    name: `${tag} 登录(${loginType})`,
    method: "POST",
    path: "/auth/login",
    body: {
      username,
      password: encryptPassword(password),
      loginType,
      captchaCode,
      captchaUuid: captcha?.uuid ?? "",
    },
  });
  if (!result.response.ok || !ensureOkCode(result.response.payload)) {
    return null;
  }
  const token =
    result.response.payload?.data?.token ??
    result.response.payload?.data?.accessToken ??
    result.response.payload?.data?.authorization ??
    "";
  return token || null;
}

async function loginAdmin() {
  const candidates = [...new Set([adminPassword, ...adminPasswordCandidates].filter(Boolean))];
  for (const candidate of candidates) {
    const token = await tryLogin({
      username: adminUsername,
      password: candidate,
      loginType: "admin",
      tag: "A1",
    });
    if (token) {
      return token;
    }
  }
  throw new Error("admin 登录失败");
}

async function ensureBlogToken() {
  if (blogUsername && blogPassword) {
    const token = await tryLogin({
      username: blogUsername,
      password: blogPassword,
      loginType: "blog",
      tag: "A2",
    });
    if (token) return { token };
  }

  const suffix = `${Date.now()}`.slice(-6);
  const username = `gw_blog_${suffix}`;
  const password = `Blog#${suffix}`;
  const captcha = await getCaptcha("blog");
  const captchaCode = captcha?.captchaEnabled ? readCaptchaCodeFromRedis(captcha?.uuid ?? "") : "";
  const register = await callApi({
    name: "A2 博客用户注册",
    method: "POST",
    path: "/auth/register",
    body: {
      username,
      password: encryptPassword(password),
      loginType: "blog",
      captchaCode,
      captchaUuid: captcha?.uuid ?? "",
      registerRequestId: `gw_${Date.now()}`,
    },
  });
  if (!register.response.ok || !ensureOkCode(register.response.payload)) {
    return {
      token: "",
      reason: `博客用户注册失败: code=${register.response.payload?.code}, message=${register.response.payload?.message}`,
    };
  }

  const token = await tryLogin({
    username,
    password,
    loginType: "blog",
    tag: "A2",
  });
  if (!token) {
    return {
      token: "",
      reason: "博客用户登录失败",
    };
  }
  return { token };
}

async function main() {
  await mkdir(outDir, { recursive: true });
  const checks = [];

  try {
    const adminToken = await loginAdmin();
    const blogLoginResult = await ensureBlogToken();
    const blogToken = blogLoginResult.token;

    const publicCases = [
      { name: "W1 captcha", method: "GET", path: "/auth/captcha", query: { loginType: "admin" } },
      { name: "W2 login path open", method: "POST", path: "/auth/login", body: {} },
      { name: "W3 register path open", method: "POST", path: "/auth/register", body: {} },
      { name: "W4 logout path open", method: "DELETE", path: "/auth/logout" },
      { name: "W5 refresh path open", method: "POST", path: "/auth/refresh" },
      { name: "W6 email code path open", method: "POST", path: "/auth/email/code", body: {} },
      { name: "W7 email login path open", method: "POST", path: "/auth/email/login", body: {} },
      { name: "W8 file download open", method: "GET", path: "/file/download/1" },
      { name: "W9 file statics open", method: "GET", path: "/file/statics/smoke.txt" },
      { name: "W10 blog home open", method: "GET", path: "/blog/front/home" },
      { name: "W11 blog articles open", method: "GET", path: "/blog/front/articles" },
      { name: "W12 blog says open", method: "GET", path: "/blog/front/says" },
      { name: "W13 blog search hot open", method: "GET", path: "/blog/front/search/hot" },
      { name: "W14 blog config open", method: "GET", path: "/blog/front/config/siteInfo" },
    ];

    for (const item of publicCases) {
      const result = await callApi(item);
      checks.push({
        group: "public_allow",
        name: item.name,
        pass: !isHardUnauthorizedForWhitelist(result.response.status, result.response.payload),
        status: result.response.status,
        code: result.response.payload?.code,
      });
    }

    const protectedCases = [
      { name: "P1 system list block", method: "GET", path: "/system/user/list", query: { page: 1, pageSize: 1 } },
      { name: "P2 blog admin block", method: "GET", path: "/blog/admin/article/list", query: { page: 1, pageSize: 1 } },
      { name: "P3 schedule block", method: "GET", path: "/schedule/job/list", query: { page: 1, pageSize: 1 } },
      { name: "P4 codegen block", method: "GET", path: "/code/gen/list", query: { page: 1, pageSize: 1 } },
      { name: "P5 file upload block", method: "POST", path: "/file/upload" },
      { name: "P6 file confirm block", method: "POST", path: "/file/confirm/1", body: { bizType: "smoke", bizId: "0" } },
    ];
    for (const item of protectedCases) {
      const result = await callApi(item);
      checks.push({
        group: "protected_block",
        name: item.name,
        pass: isUnauthorizedStatus(result.response.status, result.response.payload),
        status: result.response.status,
        code: result.response.payload?.code,
      });
    }

    const fileData = new FormData();
    fileData.append("file", new Blob(["smoke"], { type: "text/plain" }), "smoke.txt");
    const adminFileUpload = await callApi({
      name: "T1 admin file upload",
      method: "POST",
      path: "/file/upload",
      token: adminToken,
      body: fileData,
      isFormData: true,
    });
    checks.push({
      group: "token_allow",
      name: "T1 admin file upload",
      pass: !isUnauthorizedStatus(adminFileUpload.response.status, adminFileUpload.response.payload),
      status: adminFileUpload.response.status,
      code: adminFileUpload.response.payload?.code,
    });

    const adminSystem = await callApi({
      name: "T2 admin system access",
      method: "GET",
      path: "/system/user/list",
      query: { page: 1, pageSize: 1 },
      token: adminToken,
    });
    checks.push({
      group: "token_allow",
      name: "T2 admin system access",
      pass:
        !isUnauthorizedStatus(adminSystem.response.status, adminSystem.response.payload) &&
        ensureOkCode(adminSystem.response.payload),
      status: adminSystem.response.status,
      code: adminSystem.response.payload?.code,
    });

    if (blogToken) {
      const blogFileUploadData = new FormData();
      blogFileUploadData.append("file", new Blob(["smoke-blog"], { type: "text/plain" }), "smoke-blog.txt");
      const blogFileUpload = await callApi({
        name: "T3 blog file upload",
        method: "POST",
        path: "/file/upload",
        token: blogToken,
        body: blogFileUploadData,
        isFormData: true,
      });
      checks.push({
        group: "token_allow",
        name: "T3 blog file upload",
        pass: !isUnauthorizedStatus(blogFileUpload.response.status, blogFileUpload.response.payload),
        status: blogFileUpload.response.status,
        code: blogFileUpload.response.payload?.code,
      });

      const blogFileConfirm = await callApi({
        name: "T4 blog file confirm",
        method: "POST",
        path: "/file/confirm/1",
        token: blogToken,
        body: { bizType: "smoke", bizId: "0" },
      });
      checks.push({
        group: "token_allow",
        name: "T4 blog file confirm",
        pass: !isUnauthorizedStatus(blogFileConfirm.response.status, blogFileConfirm.response.payload),
        status: blogFileConfirm.response.status,
        code: blogFileConfirm.response.payload?.code,
      });
    } else {
      checks.push({
        group: "token_allow",
        name: "T3 blog file upload",
        pass: false,
        status: -1,
        code: -1,
        note: blogLoginResult.reason ?? "未获取到 blog token",
      });
      checks.push({
        group: "token_allow",
        name: "T4 blog file confirm",
        pass: false,
        status: -1,
        code: -1,
        note: blogLoginResult.reason ?? "未获取到 blog token",
      });
    }

    const allPass = checks.every((c) => c.pass);

    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(join(outDir, "checks.json"), JSON.stringify(checks, null, 2), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# 网关白名单与鉴权核验结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- 结果：${allPass ? "PASS" : "FAIL"}

## 检查项

${checks
  .map(
    (c) =>
      `- [${c.pass ? "PASS" : "FAIL"}] ${c.group} / ${c.name} (status=${c.status}, code=${c.code ?? "-"}${c.note ? `, note=${c.note}` : ""})`
  )
  .join("\n")}

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
      console.log(`网关白名单核验完成：${outDir}`);
    }
  } catch (error) {
    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# 网关白名单核验失败

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

