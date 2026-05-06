import fs from 'node:fs'
import { assertFile, printResult, repoPath } from './shared.mjs'

const findings = []

assertFile('sourcelin-common/sourcelin-common-webmvc/src/main/java/com/sourcelin/common/core/web/advice/GlobalMvcExceptionHandler.java', findings)

const oldSecurityHandler = repoPath('sourcelin-common/sourcelin-common-security/src/main/java/com/sourcelin/common/security/handler/GlobalExceptionHandler.java')
if (fs.existsSync(oldSecurityHandler)) {
  findings.push({ rule: 'security-webmvc-coupling', file: 'sourcelin-common/sourcelin-common-security/src/main/java/com/sourcelin/common/security/handler/GlobalExceptionHandler.java', message: '全局 Web 异常处理不应放在 common-security' })
}

printResult('backend-guard', findings)
