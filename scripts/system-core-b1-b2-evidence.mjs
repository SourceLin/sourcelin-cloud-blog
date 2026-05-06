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
    `${now.toISOString().slice(0, 10)}-b1-b2-${stamp}`
  );

const traces = [];
const artifacts = {
  userId: "",
  roleId: "",
  menuDirId: "",
  menuPageId: "",
  menuButtonId: "",
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

async function loginWithPasswordCandidates({ username: loginUserName, captchaCode: loginCaptchaCode, captchaUuid: loginCaptchaUuid }) {
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
      const token =
        loginData?.accessToken ??
        loginData?.token ??
        loginData?.authorization ??
        loginData?.access_token ??
        "";
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

function firstDeptId(deptTree) {
  const stack = Array.isArray(deptTree) ? [...deptTree] : [];
  while (stack.length > 0) {
    const node = stack.shift();
    const id = node?.id ?? node?.value;
    if (id !== undefined && String(id) !== "0") {
      return String(id);
    }
    if (Array.isArray(node?.children)) {
      stack.unshift(...node.children);
    }
  }
  return "";
}

function pageItems(data) {
  if (!data) return [];
  return data.items ?? data.list ?? [];
}

function toId(value) {
  if (value === null || value === undefined || value === "") return "";
  return String(value);
}

function buildSqlVerify() {
  const userId = artifacts.userId || "{user_id}";
  const roleId = artifacts.roleId || "{role_id}";
  const menuDirId = artifacts.menuDirId || "{menu_dir_id}";
  const menuPageId = artifacts.menuPageId || "{menu_page_id}";
  const menuButtonId = artifacts.menuButtonId || "{menu_button_id}";
  return `-- B1 用户写链路核验
SELECT user_id, user_name, nick_name, phonenumber, sex, status
FROM sys_user
WHERE user_id = ${userId};

SELECT user_id, role_id
FROM sys_user_role
WHERE user_id = ${userId};

SELECT user_id, post_id
FROM sys_user_post
WHERE user_id = ${userId};

-- B2 角色 + 菜单授权核验
SELECT role_id, role_name, role_key, role_sort, data_scope, status
FROM sys_role
WHERE role_id = ${roleId};

SELECT role_id, menu_id
FROM sys_role_menu
WHERE role_id = ${roleId}
ORDER BY menu_id;

SELECT role_id, dept_id
FROM sys_role_dept
WHERE role_id = ${roleId}
ORDER BY dept_id;

SELECT menu_id, menu_name, menu_type, order_num, perms, visible, status
FROM sys_menu
WHERE menu_id IN (${menuDirId}, ${menuPageId}, ${menuButtonId})
ORDER BY menu_id;
`;
}

async function main() {
  await mkdir(outDir, { recursive: true });

  let token = "";
  let usedPasswordHint = "";
  let deptId = "";
  let roleOptionId = "";
  let postOptionId = "";
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

    const [deptTree, roleOptions, postOptions] = await Promise.all([
      callApi({
        name: "A2 获取部门树",
        path: "/system/user/deptTree",
        token,
      }),
      callApi({
        name: "A3 获取角色选项",
        path: "/system/role/optionselect",
        token,
      }),
      callApi({
        name: "A4 获取岗位选项",
        path: "/system/post/optionselect",
        token,
      }),
    ]);

    deptId = firstDeptId(deptTree);
    roleOptionId = toId((roleOptions ?? [])[0]?.roleId);
    postOptionId = toId((postOptions ?? [])[0]?.postId);
    if (!deptId || !roleOptionId || !postOptionId) {
      throw new Error("基础选项不足，无法执行 B1/B2（dept/role/post 至少各 1 条）");
    }

    const userName = `b1_user_${suffix}`;
    const userPayload = {
      userName,
      nickName: `B1用户${suffix}`,
      password: `B1Pass#${suffix}`,
      deptId: Number(deptId),
      email: `b1_${suffix}@example.com`,
      phonenumber: `139${suffix.padStart(8, "0").slice(0, 8)}`,
      sex: "1",
      status: "0",
      roleIds: [Number(roleOptionId)],
      postIds: [Number(postOptionId)],
    };

    await callApi({
      name: "B1-1 新增用户",
      method: "POST",
      path: "/system/user",
      token,
      body: userPayload,
    });

    const userPage = await callApi({
      name: "B1-2 查询用户列表定位 userId",
      path: "/system/user/list",
      token,
      query: {
        page: 1,
        pageSize: 20,
        userName,
      },
    });
    const createdUser = pageItems(userPage).find((item) => item.userName === userName);
    artifacts.userId = toId(createdUser?.userId);
    if (!artifacts.userId) {
      throw new Error("未找到新建用户，无法继续 B1 证据链");
    }

    const userDetail = await callApi({
      name: "B1-3 获取用户详情（含角色/岗位关联）",
      path: `/system/user/${artifacts.userId}`,
      token,
    });

    await callApi({
      name: "B1-4 编辑用户（昵称/联系方式）",
      method: "PUT",
      path: "/system/user",
      token,
      body: {
        userId: Number(artifacts.userId),
        userName,
        nickName: `B1已编辑${suffix}`,
        deptId: Number(deptId),
        email: `b1_edit_${suffix}@example.com`,
        phonenumber: `138${suffix.padStart(8, "0").slice(0, 8)}`,
        sex: "2",
        status: "0",
        roleIds: userDetail?.roleIds ?? [Number(roleOptionId)],
        postIds: userDetail?.postIds ?? [Number(postOptionId)],
      },
    });

    await callApi({
      name: "B1-5 用户状态变更-停用",
      method: "PUT",
      path: "/system/user/changeStatus",
      token,
      body: {
        userId: Number(artifacts.userId),
        status: "1",
      },
    });

    await callApi({
      name: "B1-6 用户状态变更-恢复",
      method: "PUT",
      path: "/system/user/changeStatus",
      token,
      body: {
        userId: Number(artifacts.userId),
        status: "0",
      },
    });

    const menuDirName = `B2目录${suffix}`;
    const menuPageName = `B2菜单${suffix}`;
    const menuButtonName = `B2按钮${suffix}`;
    const roleName = `B2角色${suffix}`;
    const roleKey = `b2:role:${suffix}`;

    await callApi({
      name: "B2-1 新增目录菜单",
      method: "POST",
      path: "/system/menu",
      token,
      body: {
        parentId: 0,
        menuName: menuDirName,
        menuType: "M",
        orderNum: 99,
        path: `b2-dir-${suffix}`,
        routeName: `B2Dir${suffix}`,
        component: "",
        perms: "",
        visible: "0",
        status: "0",
        isFrame: "1",
        isCache: "1",
      },
    });

    const menuListAfterDir = await callApi({
      name: "B2-2 查询菜单列表定位目录ID",
      path: "/system/menu/list",
      token,
      query: { menuName: menuDirName },
    });
    const menuDir = (menuListAfterDir ?? []).find((item) => item.menuName === menuDirName);
    artifacts.menuDirId = toId(menuDir?.menuId);
    if (!artifacts.menuDirId) {
      throw new Error("未找到目录菜单ID");
    }

    await callApi({
      name: "B2-3 新增页面菜单",
      method: "POST",
      path: "/system/menu",
      token,
      body: {
        parentId: Number(artifacts.menuDirId),
        menuName: menuPageName,
        menuType: "C",
        orderNum: 1,
        path: `page-${suffix}`,
        routeName: `B2Page${suffix}`,
        component: "system/user/index",
        perms: "",
        visible: "0",
        status: "0",
        isFrame: "1",
        isCache: "0",
      },
    });

    const menuListAfterPage = await callApi({
      name: "B2-4 查询菜单列表定位页面ID",
      path: "/system/menu/list",
      token,
      query: { menuName: menuPageName },
    });
    const menuPage = (menuListAfterPage ?? []).find((item) => item.menuName === menuPageName);
    artifacts.menuPageId = toId(menuPage?.menuId);
    if (!artifacts.menuPageId) {
      throw new Error("未找到页面菜单ID");
    }

    await callApi({
      name: "B2-5 新增按钮菜单",
      method: "POST",
      path: "/system/menu",
      token,
      body: {
        parentId: Number(artifacts.menuPageId),
        menuName: menuButtonName,
        menuType: "F",
        orderNum: 1,
        path: "",
        routeName: "",
        component: "",
        perms: `system:b2:${suffix}:query`,
        visible: "0",
        status: "0",
        isFrame: "1",
        isCache: "1",
      },
    });

    const menuListAfterButton = await callApi({
      name: "B2-6 查询菜单列表定位按钮ID",
      path: "/system/menu/list",
      token,
      query: { menuName: menuButtonName },
    });
    const menuButton = (menuListAfterButton ?? []).find((item) => item.menuName === menuButtonName);
    artifacts.menuButtonId = toId(menuButton?.menuId);
    if (!artifacts.menuButtonId) {
      throw new Error("未找到按钮菜单ID");
    }

    await callApi({
      name: "B2-7 新增角色并授权菜单",
      method: "POST",
      path: "/system/role",
      token,
      body: {
        roleName,
        roleKey,
        roleSort: 99,
        status: "0",
        dataScope: "1",
        menuIds: [
          Number(artifacts.menuDirId),
          Number(artifacts.menuPageId),
          Number(artifacts.menuButtonId),
        ],
      },
    });

    const rolePage = await callApi({
      name: "B2-8 查询角色列表定位 roleId",
      path: "/system/role/list",
      token,
      query: {
        page: 1,
        pageSize: 20,
        roleName,
      },
    });
    const createdRole = pageItems(rolePage).find((item) => item.roleKey === roleKey);
    artifacts.roleId = toId(createdRole?.roleId);
    if (!artifacts.roleId) {
      throw new Error("未找到新建角色ID");
    }

    await callApi({
      name: "B2-9 角色数据权限改为自定义部门",
      method: "PUT",
      path: "/system/role/dataScope",
      token,
      body: {
        roleId: Number(artifacts.roleId),
        dataScope: "5",
        deptIds: [Number(deptId)],
      },
    });

    const roleMenuTree = await callApi({
      name: "B2-10 读取角色菜单树回显",
      path: `/system/menu/roleMenuTreeselect/${artifacts.roleId}`,
      token,
    });
    const roleDeptTree = await callApi({
      name: "B2-11 读取角色部门树回显",
      path: `/system/role/deptTree/${artifacts.roleId}`,
      token,
    });

    const menuChecked = (roleMenuTree?.checkedKeys ?? []).map((id) => String(id));
    const deptChecked = (roleDeptTree?.checkedKeys ?? []).map((id) => String(id));
    const b2Check = {
      roleMenuContainsAll: [
        artifacts.menuDirId,
        artifacts.menuPageId,
        artifacts.menuButtonId,
      ].every((id) => menuChecked.includes(id)),
      roleDeptContainsTarget: deptChecked.includes(deptId),
    };

    if (cleanup) {
      await callApi({
        name: "Z1 删除角色（清理）",
        method: "DELETE",
        path: `/system/role/${artifacts.roleId}`,
        token,
      });
      await callApi({
        name: "Z2 删除按钮菜单（清理）",
        method: "DELETE",
        path: `/system/menu/${artifacts.menuButtonId}`,
        token,
      });
      await callApi({
        name: "Z3 删除页面菜单（清理）",
        method: "DELETE",
        path: `/system/menu/${artifacts.menuPageId}`,
        token,
      });
      await callApi({
        name: "Z4 删除目录菜单（清理）",
        method: "DELETE",
        path: `/system/menu/${artifacts.menuDirId}`,
        token,
      });
      await callApi({
        name: "Z5 删除用户（清理）",
        method: "DELETE",
        path: `/system/user/${artifacts.userId}`,
        token,
      });
    }

    await writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8");
    await writeFile(join(outDir, "verify.sql"), buildSqlVerify(), "utf8");

    const summary = `# B1/B2 证据执行结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- CLEANUP：${cleanup ? "true" : "false"}
- 登录密码候选命中：${usedPasswordHint || "(未记录)"}

## 关键产物ID

- userId: ${artifacts.userId}
- roleId: ${artifacts.roleId}
- menuDirId: ${artifacts.menuDirId}
- menuPageId: ${artifacts.menuPageId}
- menuButtonId: ${artifacts.menuButtonId}

## B2 校验结果

- role_menu 包含目录/菜单/按钮：${b2Check.roleMenuContainsAll ? "PASS" : "FAIL"}
- role_dept 包含目标部门：${b2Check.roleDeptContainsTarget ? "PASS" : "FAIL"}

## 产物文件

- request-response-traces.json
- verify.sql
`;
    await writeFile(join(outDir, "summary.md"), summary, "utf8");
    process.stdout.write(`证据采集完成，输出目录：${outDir}\n`);
  } catch (error) {
    await writeFile(join(outDir, "request-response-traces.json"), JSON.stringify(traces, null, 2), "utf8");
    await writeFile(join(outDir, "verify.sql"), buildSqlVerify(), "utf8");
    const failSummary = `# B1/B2 证据执行失败

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 输出目录：${outDir}
- 错误：${error instanceof Error ? error.message : String(error)}

已输出当前阶段 traces 与 SQL 核验模板，请修复环境后重试。
`;
    await writeFile(join(outDir, "summary.md"), failSummary, "utf8");
    process.stderr.write(`${failSummary}\n`);
    process.exitCode = 1;
  }
}

await main();

