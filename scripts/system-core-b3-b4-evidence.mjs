#!/usr/bin/env node

import { mkdir, writeFile } from "node:fs/promises";
import { join } from "node:path";
import { constants, createPrivateKey, createPublicKey, publicEncrypt } from "node:crypto";

const baseUrl = process.env.BASE_URL ?? "http://localhost:8080";
const username = process.env.ADMIN_USERNAME ?? "admin";
const adminPassword = process.env.ADMIN_PASSWORD ?? "";
const fallbackPasswordCandidates = (process.env.ADMIN_PASSWORD_CANDIDATES ?? "admin123,123456")
  .split(",")
  .map((item) => item.trim())
  .filter(Boolean);
const passwordCandidates = [...new Set([adminPassword, ...fallbackPasswordCandidates].filter(Boolean))];
const captchaCode = process.env.CAPTCHA_CODE ?? "";
const captchaUuid = process.env.CAPTCHA_UUID ?? "";
const cleanup = String(process.env.CLEANUP ?? "false").toLowerCase() === "true";
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
    "system-core",
    `${now.toISOString().slice(0, 10)}-b3-b4-${stamp}`
  );

const traces = [];
const artifacts = {
  deptId: "",
  dictId: "",
  dictType: "",
  dictCode: "",
};

function ensureCodeOk(payload) {
  const code = payload?.code;
  return code === 0;
}

function maskPassword(value) {
  if (!value) return "(unknown)";
  if (value.length <= 2) return "*".repeat(value.length);
  return `${value.slice(0, 1)}${"*".repeat(Math.max(1, value.length - 2))}${value.slice(-1)}`;
}

function encryptPassword(rawPassword) {
  if (!rawPassword) return rawPassword;
  if (String(process.env.PASSWORD_ALREADY_ENCRYPTED ?? "false").toLowerCase() === "true") {
    return rawPassword;
  }
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
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") return;
    usp.append(key, String(value));
  });
  const qs = usp.toString();
  return qs ? `?${qs}` : "";
}

function toId(value) {
  if (value === null || value === undefined || value === "") return "";
  return String(value);
}

function pageItems(data) {
  if (!data) return [];
  return data.items ?? data.list ?? [];
}

function firstDeptId(deptTree) {
  const stack = Array.isArray(deptTree) ? [...deptTree] : [];
  while (stack.length > 0) {
    const node = stack.shift();
    const id = node?.deptId ?? node?.id ?? node?.value;
    if (id !== undefined && String(id) !== "0") {
      return String(id);
    }
    if (Array.isArray(node?.children)) {
      stack.unshift(...node.children);
    }
  }
  return "";
}

async function callApi({ name, method = "GET", path, token, query, body }) {
  const url = `${baseUrl}${path}${toQueryString(query)}`;
  const headers = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  const rawText = await response.text();
  let payload = null;
  try {
    payload = rawText ? JSON.parse(rawText) : null;
  } catch {
    payload = { rawText };
  }

  traces.push({
    name,
    request: {
      method,
      url,
      body: body ?? null,
    },
    response: {
      status: response.status,
      ok: response.ok,
      payload,
    },
  });

  if (!response.ok) {
    throw new Error(`${name} HTTP失败: ${response.status}`);
  }
  if (!ensureCodeOk(payload)) {
    throw new Error(`${name} 业务失败: code=${payload?.code}, message=${payload?.message}`);
  }
  return payload?.data;
}

async function loginWithPasswordCandidates({
  username: loginUserName,
  captchaCode: loginCaptchaCode,
  captchaUuid: loginCaptchaUuid,
}) {
  if (passwordCandidates.length === 0) {
    throw new Error("未提供可用管理员密码，请设置 ADMIN_PASSWORD 或 ADMIN_PASSWORD_CANDIDATES");
  }

  const errors = [];
  for (const candidate of passwordCandidates) {
    try {
      const loginData = await callApi({
        name: `A1 登录获取令牌（尝试 ${maskPassword(candidate)}）`,
        method: "POST",
        path: "/auth/login",
        body: {
          username: loginUserName,
          password: encryptPassword(candidate),
          captchaCode: loginCaptchaCode,
          captchaUuid: loginCaptchaUuid,
          loginType: "admin",
        },
      });
      const token = loginData?.token ?? loginData?.accessToken ?? "";
      if (!token) {
        errors.push(`${maskPassword(candidate)}: 登录成功但返回中未找到 token`);
        continue;
      }
      return {
        token,
        passwordHint: maskPassword(candidate),
      };
    } catch (error) {
      errors.push(`${maskPassword(candidate)}: ${error instanceof Error ? error.message : String(error)}`);
    }
  }
  throw new Error(`管理员登录失败，已尝试密码候选：${errors.join(" | ")}`);
}

function buildSqlVerify() {
  const deptId = artifacts.deptId || "{dept_id}";
  const dictId = artifacts.dictId || "{dict_id}";
  const dictType = artifacts.dictType || "{dict_type}";
  const dictCode = artifacts.dictCode || "{dict_code}";
  return `-- B3 部门写链路核验
SELECT dept_id, parent_id, dept_name, order_num, leader, phone, email, status, ancestors
FROM sys_dept
WHERE dept_id = ${deptId};

-- B4 字典类型与字典项写链路核验
SELECT dict_id, dict_name, dict_type, status, remark
FROM sys_dict_type
WHERE dict_id = ${dictId};

SELECT dict_code, dict_type, dict_label, dict_value, dict_sort, status, list_class, is_default, remark
FROM sys_dict_data
WHERE dict_code = ${dictCode};

SELECT dict_code, dict_type, dict_label, dict_value, dict_sort, status, list_class, is_default
FROM sys_dict_data
WHERE dict_type = '${dictType}'
ORDER BY dict_code;
`;
}

async function main() {
  await mkdir(outDir, { recursive: true });

  let token = "";
  let usedPasswordHint = "";
  const suffix = `${Date.now()}`.slice(-6);

  try {
    let loginCaptchaCode = captchaCode;
    let loginCaptchaUuid = captchaUuid;
    const captcha = await callApi({
      name: "A0 获取验证码状态",
      path: "/auth/captcha",
      query: { loginType: "admin" },
    });
    if (captcha?.captchaEnabled) {
      if (!loginCaptchaCode) {
        throw new Error(
          "管理员验证码已启用，请设置环境变量 CAPTCHA_CODE（若未传 CAPTCHA_UUID 将自动使用本次 uuid）"
        );
      }
      loginCaptchaUuid = loginCaptchaUuid || captcha?.uuid || "";
    } else {
      loginCaptchaCode = "";
      loginCaptchaUuid = "";
    }

    const loginResult = await loginWithPasswordCandidates({
      username,
      captchaCode: loginCaptchaCode,
      captchaUuid: loginCaptchaUuid,
    });
    token = loginResult.token;
    usedPasswordHint = loginResult.passwordHint;

    const deptName = `B3部门${suffix}`;
    const dictType = `b4_type_${suffix}`;
    const dictName = `B4字典${suffix}`;
    artifacts.dictType = dictType;

    const deptList = await callApi({
      name: "B3-1 查询部门树",
      path: "/system/dept/list",
      token,
    });
    const parentId = firstDeptId(deptList);
    if (!parentId) {
      throw new Error("未找到可用父部门，无法执行 B3");
    }

    await callApi({
      name: "B3-2 新增部门",
      method: "POST",
      path: "/system/dept",
      token,
      body: {
        parentId: Number(parentId),
        deptName,
        orderNum: 99,
        leader: "B3负责人",
        phone: `139${suffix.padStart(8, "0").slice(0, 8)}`,
        email: `b3_${suffix}@example.com`,
        status: "0",
      },
    });

    const newDeptList = await callApi({
      name: "B3-3 查询部门定位 deptId",
      path: "/system/dept/list",
      token,
      query: { deptName },
    });
    const createdDept = (newDeptList ?? []).find((item) => item.deptName === deptName);
    artifacts.deptId = toId(createdDept?.deptId);
    if (!artifacts.deptId) {
      throw new Error("未找到新建部门，无法继续 B3");
    }

    await callApi({
      name: "B3-4 编辑部门",
      method: "PUT",
      path: "/system/dept",
      token,
      body: {
        deptId: Number(artifacts.deptId),
        parentId: Number(parentId),
        deptName: `${deptName}-已编辑`,
        orderNum: 100,
        leader: "B3负责人-编辑",
        phone: `138${suffix.padStart(8, "0").slice(0, 8)}`,
        email: `b3_edit_${suffix}@example.com`,
        status: "0",
      },
    });

    await callApi({
      name: "B3-5 部门状态变更-停用",
      method: "PUT",
      path: "/system/dept",
      token,
      body: {
        deptId: Number(artifacts.deptId),
        parentId: Number(parentId),
        deptName: `${deptName}-已编辑`,
        orderNum: 100,
        leader: "B3负责人-编辑",
        phone: `138${suffix.padStart(8, "0").slice(0, 8)}`,
        email: `b3_edit_${suffix}@example.com`,
        status: "1",
      },
    });

    await callApi({
      name: "B3-6 部门状态变更-恢复",
      method: "PUT",
      path: "/system/dept",
      token,
      body: {
        deptId: Number(artifacts.deptId),
        parentId: Number(parentId),
        deptName: `${deptName}-已编辑`,
        orderNum: 100,
        leader: "B3负责人-编辑",
        phone: `138${suffix.padStart(8, "0").slice(0, 8)}`,
        email: `b3_edit_${suffix}@example.com`,
        status: "0",
      },
    });

    await callApi({
      name: "B4-1 新增字典类型",
      method: "POST",
      path: "/system/dict/type",
      token,
      body: {
        dictName,
        dictType,
        status: "0",
        remark: "B4证据脚本新增",
      },
    });

    const dictTypePage = await callApi({
      name: "B4-2 查询字典类型定位 dictId",
      path: "/system/dict/type/list",
      token,
      query: {
        page: 1,
        pageSize: 20,
        dictType,
      },
    });
    const createdDictType = pageItems(dictTypePage).find((item) => item.dictType === dictType);
    artifacts.dictId = toId(createdDictType?.dictId);
    if (!artifacts.dictId) {
      throw new Error("未找到新建字典类型，无法继续 B4");
    }

    await callApi({
      name: "B4-3 编辑字典类型",
      method: "PUT",
      path: "/system/dict/type",
      token,
      body: {
        dictId: Number(artifacts.dictId),
        dictName: `${dictName}-已编辑`,
        dictType,
        status: "0",
        remark: "B4证据脚本编辑",
      },
    });

    await callApi({
      name: "B4-4 新增字典项",
      method: "POST",
      path: "/system/dict/data",
      token,
      body: {
        dictType,
        dictLabel: `标签${suffix}`,
        dictValue: `值${suffix}`,
        dictSort: 1,
        status: "0",
        listClass: "P",
        isDefault: "N",
        remark: "B4证据脚本新增字典项",
      },
    });

    const dictDataPage = await callApi({
      name: "B4-5 查询字典项定位 dictCode",
      path: "/system/dict/data/list",
      token,
      query: {
        page: 1,
        pageSize: 20,
        dictType,
      },
    });
    const createdDictData = pageItems(dictDataPage).find((item) => item.dictType === dictType);
    artifacts.dictCode = toId(createdDictData?.dictCode);
    if (!artifacts.dictCode) {
      throw new Error("未找到新建字典项，无法继续 B4");
    }

    await callApi({
      name: "B4-6 编辑字典项",
      method: "PUT",
      path: "/system/dict/data",
      token,
      body: {
        dictCode: Number(artifacts.dictCode),
        dictType,
        dictLabel: `标签${suffix}-已编辑`,
        dictValue: `值${suffix}-已编辑`,
        dictSort: 2,
        status: "0",
        listClass: "S",
        isDefault: "N",
        remark: "B4证据脚本编辑字典项",
      },
    });

    if (cleanup) {
      if (artifacts.dictCode) {
        await callApi({
          name: "C1 清理字典项",
          method: "DELETE",
          path: `/system/dict/data/${artifacts.dictCode}`,
          token,
        });
      }
      if (artifacts.dictId) {
        await callApi({
          name: "C2 清理字典类型",
          method: "DELETE",
          path: `/system/dict/type/${artifacts.dictId}`,
          token,
        });
      }
      if (artifacts.deptId) {
        await callApi({
          name: "C3 清理部门",
          method: "DELETE",
          path: `/system/dept/${artifacts.deptId}`,
          token,
        });
      }
    }

    const verifySql = buildSqlVerify();
    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(join(outDir, "verify.sql"), verifySql, "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# B3/B4 证据执行结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- CLEANUP：${cleanup}
- 登录密码候选命中：${usedPasswordHint || "(未记录)"}

## 关键产物ID

- deptId: ${artifacts.deptId || "(未获取)"}
- dictId: ${artifacts.dictId || "(未获取)"}
- dictType: ${artifacts.dictType || "(未获取)"}
- dictCode: ${artifacts.dictCode || "(未获取)"}

## 产物文件

- request-response-traces.json
- verify.sql
`,
        "utf8"
      ),
    ]);

    console.log(`证据采集完成，输出目录：${outDir}`);
  } catch (error) {
    await Promise.all([
      writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8"),
      writeFile(join(outDir, "verify.sql"), buildSqlVerify(), "utf8"),
      writeFile(
        join(outDir, "summary.md"),
        `# B3/B4 证据执行失败

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- 错误：${error instanceof Error ? error.message : String(error)}

已输出当前阶段 traces 与 SQL 核验模板，请修复环境后重试。
`,
        "utf8"
      ),
    ]);
    console.error("\n# B3/B4 证据执行失败");
    console.error(`\n- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}`);
    console.error(`- 网关地址：${baseUrl}`);
    console.error(`- 输出目录：${outDir}`);
    console.error(`- 错误：${error instanceof Error ? error.message : String(error)}`);
    console.error("\n已输出当前阶段 traces 与 SQL 核验模板，请修复环境后重试。\n");
    process.exitCode = 1;
  }
}

main();

