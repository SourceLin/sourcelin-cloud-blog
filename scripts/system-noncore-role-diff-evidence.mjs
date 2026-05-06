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
    `${now.toISOString().slice(0, 10)}-role-diff-${stamp}`
  );

const traces = [];
const artifacts = {
  roleId: "",
  userId: "",
  userName: "",
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

async function callApi({ name, method = "GET", path, query, body, token, allowAnyStatus = false }) {
  const url = `${baseUrl}${path}${toQueryString(query)}`;
  const headers = {};
  if (token) headers.Authorization = `Bearer ${token}`;
  if (body !== undefined) headers["Content-Type"] = "application/json";
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
  const record = {
    name,
    request: { method, url, body: body ?? null, hasAuth: Boolean(token) },
    response: { status: response.status, ok: response.ok, payload },
  };
  traces.push(record);
  if (!allowAnyStatus) {
    if (!response.ok || !ensureOkCode(payload)) {
      throw new Error(`${name} 失败: status=${response.status}, code=${payload?.code}, message=${payload?.message}`);
    }
  }
  return record;
}

function pageItems(data) {
  if (!data) return [];
  return data.items ?? data.list ?? [];
}

function toId(value) {
  if (value === undefined || value === null || value === "") return "";
  return String(value);
}

async function login(username, password, loginType, tag) {
  const captcha = await callApi({
    name: `${tag} 获取验证码`,
    path: "/auth/captcha",
    query: { loginType },
  });
  const data = await callApi({
    name: `${tag} 登录`,
    method: "POST",
    path: "/auth/login",
    body: {
      username,
      password: encryptPassword(password),
      loginType,
      captchaCode: "",
      captchaUuid: captcha.response.payload?.data?.uuid ?? "",
    },
  });
  const token = data.response.payload?.data?.token ?? data.response.payload?.data?.accessToken ?? "";
  if (!token) {
    throw new Error(`${tag} 未获取到 token`);
  }
  return token;
}

async function loginAdmin() {
  const candidates = [...new Set([adminPassword, ...adminPasswordCandidates].filter(Boolean))];
  for (const candidate of candidates) {
    try {
      return await login(adminUsername, candidate, "admin", "A1");
    } catch {
      // try next
    }
  }
  throw new Error("admin 登录失败");
}

function firstDeptId(deptTree) {
  const stack = Array.isArray(deptTree) ? [...deptTree] : [];
  while (stack.length) {
    const node = stack.shift();
    const id = node?.deptId ?? node?.id ?? node?.value;
    if (id && String(id) !== "0") return String(id);
    if (Array.isArray(node?.children)) stack.unshift(...node.children);
  }
  return "";
}

async function main() {
  await mkdir(outDir, { recursive: true });
  const suffix = `${Date.now()}`.slice(-6);
  const limitedPassword = `Role#${suffix}`;
  let adminToken = "";
  try {
    adminToken = await loginAdmin();
    const roleName = `冒烟受限角色${suffix}`;
    const roleKey = `smoke:limited:${suffix}`;
    const userName = `smoke_limited_${suffix}`;
    artifacts.userName = userName;

    await callApi({
      name: "B1 新增受限角色",
      method: "POST",
      path: "/system/role",
      token: adminToken,
      body: {
        roleName,
        roleKey,
        roleSort: 99,
        status: "0",
        dataScope: "1",
        menuIds: [],
      },
    });

    const rolePage = await callApi({
      name: "B2 查询受限角色",
      path: "/system/role/list",
      token: adminToken,
      query: { page: 1, pageSize: 20, roleName },
    });
    const role = pageItems(rolePage.response.payload?.data).find((r) => r.roleKey === roleKey);
    artifacts.roleId = toId(role?.roleId);
    if (!artifacts.roleId) throw new Error("未定位到 roleId");

    const deptTree = await callApi({
      name: "B3 查询部门树",
      path: "/system/user/deptTree",
      token: adminToken,
    });
    const deptId = firstDeptId(deptTree.response.payload?.data);
    if (!deptId) throw new Error("未定位到 deptId");

    const postOptions = await callApi({
      name: "B4 查询岗位选项",
      path: "/system/post/optionselect",
      token: adminToken,
    });
    const postId = toId((postOptions.response.payload?.data ?? [])[0]?.postId);
    if (!postId) throw new Error("未定位到 postId");

    await callApi({
      name: "B5 新增受限用户",
      method: "POST",
      path: "/system/user",
      token: adminToken,
      body: {
        userName,
        nickName: `受限用户${suffix}`,
        password: limitedPassword,
        deptId: Number(deptId),
        email: `limited_${suffix}@example.com`,
        phonenumber: `139${suffix.padStart(8, "0").slice(0, 8)}`,
        sex: "1",
        status: "0",
        roleIds: [Number(artifacts.roleId)],
        postIds: [Number(postId)],
      },
    });

    const userPage = await callApi({
      name: "B6 查询受限用户",
      path: "/system/user/list",
      token: adminToken,
      query: { page: 1, pageSize: 20, userName },
    });
    const user = pageItems(userPage.response.payload?.data).find((u) => u.userName === userName);
    artifacts.userId = toId(user?.userId);
    if (!artifacts.userId) throw new Error("未定位到 userId");

    const limitedToken = await login(userName, limitedPassword, "admin", "B7");
    const denied = await callApi({
      name: "B8 受限用户访问 system/config/list",
      path: "/system/config/list",
      token: limitedToken,
      query: { page: 1, pageSize: 10 },
      allowAnyStatus: true,
    });
    const code = Number(denied.response.payload?.code);
    const deniedByPermission =
      denied.response.status === 403 || denied.response.status === 401 || code === 403 || code === 40300 || code === 40100;

    if (!deniedByPermission) {
      throw new Error(
        `受限用户未被正确拒绝: status=${denied.response.status}, code=${denied.response.payload?.code}`
      );
    }

    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# System 非核心角色差异冒烟结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- CLEANUP：${cleanup ? "true" : "false"}
- 结果：PASS

## 关键产物ID

- roleId: ${artifacts.roleId}
- userId: ${artifacts.userId}
- userName: ${artifacts.userName}

## 结论

- 已验证“管理员与受限角色”权限差异：
- 受限用户访问 \`/system/config/list\` 被网关/鉴权层拒绝（401/403）。
`,
        "utf8"
      ),
    ]);
    console.log(`system 角色差异冒烟完成：${outDir}`);
  } catch (error) {
    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# System 非核心角色差异冒烟失败

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- 错误：${error instanceof Error ? error.message : String(error)}

## 关键产物ID

- roleId: ${artifacts.roleId || "(none)"}
- userId: ${artifacts.userId || "(none)"}
- userName: ${artifacts.userName || "(none)"}
`,
        "utf8"
      ),
    ]);
    process.exitCode = 1;
  } finally {
    if (cleanup && adminToken) {
      if (artifacts.userId) {
        await callApi({
          name: "C1 清理受限用户",
          method: "DELETE",
          path: `/system/user/${artifacts.userId}`,
          token: adminToken,
          allowAnyStatus: true,
        });
      }
      if (artifacts.roleId) {
        await callApi({
          name: "C2 清理受限角色",
          method: "DELETE",
          path: `/system/role/${artifacts.roleId}`,
          token: adminToken,
          allowAnyStatus: true,
        });
      }
      await writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8");
    }
  }
}

await main();

