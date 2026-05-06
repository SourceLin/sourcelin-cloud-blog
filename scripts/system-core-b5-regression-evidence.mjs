#!/usr/bin/env node

import { mkdir, readFile, writeFile } from "node:fs/promises";
import { spawn } from "node:child_process";
import { join } from "node:path";

const baseUrl = process.env.BASE_URL ?? "http://localhost:8080";
const username = process.env.ADMIN_USERNAME ?? "admin";
const adminPassword = process.env.ADMIN_PASSWORD ?? "";
const cleanup = String(process.env.CLEANUP ?? "false").toLowerCase() === "true";
const passwordCandidates = process.env.ADMIN_PASSWORD_CANDIDATES ?? "admin123,123456";

const now = new Date();
const stamp = now.toISOString().replace(/[:.]/g, "-");
const outDir =
  process.env.OUT_DIR ??
  join(
    process.cwd(),
    "docs",
    "evidence",
    "system-core",
    `${now.toISOString().slice(0, 10)}-b5-${stamp}`
  );

function runWorker(scriptName, workerOutDir) {
  return new Promise((resolve) => {
    const stdout = [];
    const stderr = [];
    const child = spawn(process.execPath, [join(process.cwd(), "scripts", scriptName)], {
      cwd: process.cwd(),
      env: {
        ...process.env,
        BASE_URL: baseUrl,
        ADMIN_USERNAME: username,
        ADMIN_PASSWORD: adminPassword,
        ADMIN_PASSWORD_CANDIDATES: passwordCandidates,
        CLEANUP: String(cleanup),
        OUT_DIR: workerOutDir,
      },
      stdio: ["ignore", "pipe", "pipe"],
    });

    child.stdout.on("data", (chunk) => stdout.push(chunk.toString()));
    child.stderr.on("data", (chunk) => stderr.push(chunk.toString()));
    child.on("close", (code) => {
      resolve({
        scriptName,
        workerOutDir,
        exitCode: code ?? 1,
        ok: code === 0,
        stdout: stdout.join(""),
        stderr: stderr.join(""),
      });
    });
  });
}

async function readIfExists(filePath, fallback = "") {
  try {
    return await readFile(filePath, "utf8");
  } catch {
    return fallback;
  }
}

async function main() {
  await mkdir(outDir, { recursive: true });

  const b1b2OutDir = join(outDir, "b1-b2");
  const b3b4OutDir = join(outDir, "b3-b4");

  const b1b2Result = await runWorker("system-core-b1-b2-evidence.mjs", b1b2OutDir);
  const b3b4Result = await runWorker("system-core-b3-b4-evidence.mjs", b3b4OutDir);

  const combinedSql = [
    await readIfExists(join(b1b2OutDir, "verify.sql")),
    await readIfExists(join(b3b4OutDir, "verify.sql")),
  ]
    .filter(Boolean)
    .join("\n\n");

  const b1b2Summary = await readIfExists(join(b1b2OutDir, "summary.md"), "summary.md 不存在");
  const b3b4Summary = await readIfExists(join(b3b4OutDir, "summary.md"), "summary.md 不存在");
  const finalPass = b1b2Result.ok && b3b4Result.ok;

  await writeFile(
    join(outDir, "run-log.md"),
    `# B5 执行日志

## system-core-b1-b2-evidence.mjs

- exitCode: ${b1b2Result.exitCode}

### stdout

\`\`\`text
${b1b2Result.stdout || "(empty)"}
\`\`\`

### stderr

\`\`\`text
${b1b2Result.stderr || "(empty)"}
\`\`\`

## system-core-b3-b4-evidence.mjs

- exitCode: ${b3b4Result.exitCode}

### stdout

\`\`\`text
${b3b4Result.stdout || "(empty)"}
\`\`\`

### stderr

\`\`\`text
${b3b4Result.stderr || "(empty)"}
\`\`\`
`,
    "utf8"
  );

  if (combinedSql) {
    await writeFile(join(outDir, "verify.sql"), combinedSql, "utf8");
  }

  await writeFile(
    join(outDir, "summary.md"),
    `# B5 系统核心总回归结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 网关地址：${baseUrl}
- 管理员账号：${username}
- 输出目录：${outDir}
- CLEANUP：${cleanup ? "true" : "false"}
- 结果：${finalPass ? "PASS" : "FAIL"}

## 子批次结果

- B1/B2：${b1b2Result.ok ? "PASS" : "FAIL"}（目录：${b1b2OutDir}）
- B3/B4：${b3b4Result.ok ? "PASS" : "FAIL"}（目录：${b3b4OutDir}）

## 子批次摘要（摘录）

### B1/B2 summary

${b1b2Summary}

### B3/B4 summary

${b3b4Summary}

## 产物文件

- b1-b2/request-response-traces.json
- b1-b2/verify.sql
- b1-b2/summary.md
- b3-b4/request-response-traces.json
- b3-b4/verify.sql
- b3-b4/summary.md
- run-log.md
- verify.sql
`,
    "utf8"
  );

  if (!finalPass) {
    process.exitCode = 1;
  }
}

await main();
