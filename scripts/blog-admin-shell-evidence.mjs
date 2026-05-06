#!/usr/bin/env node

import { access, mkdir, writeFile } from "node:fs/promises";
import { constants } from "node:fs";
import { join } from "node:path";

const now = new Date();
const stamp = now.toISOString().replace(/[:.]/g, "-");
const projectRoot = process.cwd();
const outDir = join(
  projectRoot,
  "docs",
  "evidence",
  "admin-blog-shell",
  `${now.toISOString().slice(0, 10)}-shell-${stamp}`
);

const modules = [
  "article",
  "category",
  "tag",
  "comment",
  "moderation",
  "say",
  "treehole",
  "collect",
  "follow",
  "link",
  "navigation",
  "navbar",
  "config",
  "stats",
  "user",
];

async function fileExists(path) {
  try {
    await access(path, constants.F_OK);
    return true;
  } catch {
    return false;
  }
}

async function main() {
  await mkdir(outDir, { recursive: true });
  const checks = [];
  const baseDir = join(projectRoot, "sourcelin-ui", "sourcelin-ui-admin", "src");

  for (const moduleName of modules) {
    const apiPath = join(baseDir, "api", "blog", `${moduleName}.ts`);
    const viewPath = join(baseDir, "views", "blog", moduleName, "index.vue");
    const apiOk = await fileExists(apiPath);
    const viewOk = await fileExists(viewPath);
    checks.push({
      module: moduleName,
      apiPath,
      viewPath,
      apiOk,
      viewOk,
      pass: apiOk && viewOk,
    });
  }

  const allPass = checks.every((item) => item.pass);

  await Promise.all([
    writeFile(join(outDir, "checks.json"), JSON.stringify(checks, null, 2), "utf8"),
    writeFile(
      join(outDir, "summary.md"),
      `# Admin Blog 模块壳层核验结果

- 执行时间：${new Date().toLocaleString("zh-CN", { hour12: false })}
- 输出目录：${outDir}
- 核验范围：blog 15 模块 API 文件 + 页面壳文件
- 结果：${allPass ? "PASS" : "FAIL"}

## 模块清单

${checks
  .map(
    (item) =>
      `- [${item.pass ? "PASS" : "FAIL"}] ${item.module}: api=${item.apiOk ? "ok" : "missing"}, view=${item.viewOk ? "ok" : "missing"}`
  )
  .join("\n")}

## 产物文件

- checks.json
`,
      "utf8"
    ),
  ]);

  if (allPass) {
    console.log(`admin blog 模块壳层核验完成：${outDir}`);
  } else {
    process.exitCode = 1;
  }
}

await main();
