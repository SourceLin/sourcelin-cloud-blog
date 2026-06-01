#!/usr/bin/env node
/* global console, process */
import { spawnSync } from 'node:child_process';
import { existsSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const PLACEHOLDER_APP_ID = '__WECHAT_MINI_APP_ID__';
const ENV_KEY = 'VITE_WECHAT_MINI_APP_ID';

const currentFile = fileURLToPath(import.meta.url);
const projectRoot = resolve(dirname(currentFile), '..');
const manifestPath = resolve(projectRoot, 'src', 'manifest.json');
const envLocalPath = resolve(projectRoot, '.env.local');

function parseEnvFile(filePath) {
  if (!existsSync(filePath)) {
    return {};
  }

  return readFileSync(filePath, 'utf8')
    .split(/\r?\n/)
    .reduce((env, line) => {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) {
        return env;
      }

      const separatorIndex = trimmed.indexOf('=');
      if (separatorIndex <= 0) {
        return env;
      }

      const key = trimmed.slice(0, separatorIndex).trim();
      const rawValue = trimmed.slice(separatorIndex + 1).trim();
      env[key] = rawValue.replace(/^['"]|['"]$/g, '');
      return env;
    }, {});
}

function resolveWechatMiniAppId() {
  const localEnv = parseEnvFile(envLocalPath);
  return process.env[ENV_KEY] || localEnv[ENV_KEY] || PLACEHOLDER_APP_ID;
}

function writeManifestWithAppId(appId) {
  const source = readFileSync(manifestPath, 'utf8');
  const manifest = JSON.parse(source);
  manifest['mp-weixin'] = manifest['mp-weixin'] || {};
  manifest['mp-weixin'].appid = appId;
  writeFileSync(manifestPath, `${JSON.stringify(manifest, null, 2)}\n`, 'utf8');
  return source;
}

const commandIndex = process.argv.indexOf('--');
const commandArgs = commandIndex >= 0 ? process.argv.slice(commandIndex + 1) : [];
if (commandArgs.length === 0) {
  console.error('[with-local-wechat-appid] 缺少要执行的命令，例如：node build-tools/with-local-wechat-appid.mjs -- uni build -p mp-weixin');
  process.exit(1);
}

const appId = resolveWechatMiniAppId();
if (appId === PLACEHOLDER_APP_ID) {
  console.warn(`[with-local-wechat-appid] 未配置 ${ENV_KEY}，将使用占位 AppID 构建；该产物不能用于真实微信登录预览。`);
}

const originalManifest = writeManifestWithAppId(appId);
let restored = false;

function restoreManifest() {
  if (!restored) {
    writeFileSync(manifestPath, originalManifest, 'utf8');
    restored = true;
  }
}

process.on('exit', restoreManifest);
process.on('SIGINT', () => {
  restoreManifest();
  process.exit(130);
});
process.on('SIGTERM', () => {
  restoreManifest();
  process.exit(143);
});

const [command, ...args] = commandArgs;
const result = spawnSync(command, args, {
  cwd: projectRoot,
  env: process.env,
  shell: process.platform === 'win32',
  stdio: 'inherit'
});

restoreManifest();

if (result.error) {
  console.error(`[with-local-wechat-appid] 命令执行失败：${result.error.message}`);
  process.exit(1);
}

process.exit(result.status ?? 0);
