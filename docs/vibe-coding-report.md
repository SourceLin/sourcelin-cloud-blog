# Vibe Coding Report

> **Sourcelin Cloud Blog** — How a full-stack microservice blog platform was built through Vibe Coding

This report documents the Vibe Coding methodology used to develop Sourcelin Cloud Blog — a practical case study of AI-assisted software engineering where natural language intent drives architecture, code, refactoring, and governance.

---

## 1. What is Vibe Coding?

Vibe Coding is a software development methodology where developers collaborate with large language models through natural language prompts, iterative refinement, and structured governance, rather than writing every line of code manually.

In this project, Vibe Coding means:

- **Intent-driven development:** Describe what you want, let AI propose how
- **Iterative refinement:** AI generates, human reviews, AI refines
- **Governed output:** Rules and skills constrain AI to project standards
- **Full-lifecycle participation:** AI is involved from architecture to deployment

---

## 2. The Vibe Coding Workflow

### Standard Development Loop

```
┌─────────────────────────────────────────────────────────┐
│                    Requirement Analysis                  │
│  (Human describes the goal in natural language)          │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                    Prompt Engineering                    │
│  (Craft context-rich prompts with rules & constraints)   │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                AI Architecture Proposal                  │
│  (AI suggests design, boundaries, and contracts)         │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                    Code Generation                       │
│  (AI generates implementation across layers)             │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                     Human Review                         │
│  (Review correctness, security, and rule compliance)     │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                     Refactoring                          │
│  (AI refactors based on review feedback)                 │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                Automated Verification                    │
│  (Maven compile, npm typecheck, lint, architecture test) │
└────────────────────────┬────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│               Commit & Documentation                     │
│  (AI generates commit messages, updates docs & rules)    │
└─────────────────────────────────────────────────────────┘
```

### Engineering Principles

| Principle | Practice |
|---|---|
| Architecture first | Design service boundaries and API contracts before coding |
| Contract first | Define `ApiResponse<T>` and `PageResult<T>` before implementing endpoints |
| Documentation first | Update `AGENTS.md`, `rules/`, and `docs/` alongside code changes |
| AI-assisted implementation | AI generates code; human reviews and approves |
| Automated validation | Every change triggers `mvn compile`, `npm run typecheck`, or `pnpm run lint` |
| Continuous refactoring | AI refactors across modules to maintain consistency |

---

## 3. Real Vibe Coding Sessions

### Session 1: Building the Uniapp Mini-Program from Scratch

**Intent:** "I need a WeChat mini-program version of my blog with 4 tabs: home, discover, community, mine."

**AI Workflow:**

1. **Architecture proposal:** AI analyzed existing Vue 3 blog frontend, proposed Uniapp project structure with conditional compilation (`#ifdef H5` / `#ifndef H5`)
2. **Scaffold generation:** AI created `pages.json`, `manifest.json`, `App.vue`, tab bar icons, and page skeletons
3. **Page implementation:** AI generated 4 tab pages + 10+ sub-pages (article list, search, detail, messages, user profile, settings, etc.)
4. **Cross-platform adaptation:** AI implemented `SseClientManager` with dual strategies — browser `EventSource` for H5, `uni.request` with `enableChunked` for mini-program
5. **Verification:** `npm run lint`, `npm run type-check`, `npm run build:mp-weixin` all passed

**Result:** A fully functional Uniapp mini-program with 593 frontend source files, delivered through iterative Vibe Coding sessions.

### Session 2: API Contract Standardization

**Intent:** "All our controllers return different response formats. Unify them to a single standard."

**AI Workflow:**

1. **Audit:** AI scanned all controllers and identified 4 incompatible response patterns (`AjaxResult`, `TableDataInfo`, `R<T>`, raw `Map`)
2. **Contract design:** AI proposed `ApiResponse<T>` with fixed fields `code/message/data/requestId/timestamp`, success code `0`
3. **Implementation:** AI created `ApiResponseBodyAdvice` for automatic wrapping, then refactored all controllers to return business objects
4. **Frontend alignment:** AI scanned frontend API consumers and removed all `code === 200` checks and old field references (`msg`, `rows`, `records`)
5. **Governance:** AI wrote `rules/api-contract.md` to prevent regression

**Result:** 100% API contract consistency. The rule file now prevents any developer (human or AI) from introducing incompatible response formats.

### Session 3: Large-Scale Refactoring — Module List Shell

**Intent:** "The admin panel has 8 list pages with duplicated table, search, pagination, and CRUD logic. Extract a reusable shell."

**AI Workflow:**

1. **Pattern analysis:** AI identified common patterns across all admin list pages
2. **Shell design:** AI proposed `ModuleListShell.vue` — a generic component accepting `columns`, `queryFields`, `rowActions`, and a `CrudApi` interface
3. **Migration:** AI refactored each list page to use the shell, reducing per-page code by ~60%
4. **Type safety:** AI defined TypeScript interfaces for column configs, query fields, and row actions
5. **Verification:** `pnpm run type-check` and `pnpm run lint` passed

**Result:** 673 lines of shared shell logic extracted. All admin list pages now share consistent UX and behavior.

### Session 4: Cross-Cutting Security — Internal API Path Isolation

**Intent:** "Some internal Feign endpoints are accidentally exposed on public paths. Move them behind `/inner/` and add `@InnerAuth`."

**AI Workflow:**

1. **Discovery:** AI scanned all controllers for endpoints that accept `FROM_SOURCE` header without `@InnerAuth`
2. **Path migration:** AI moved `/message/push` → `/inner/message/push`, `/blog/ai/records/create` → `/inner/blog/ai/records/create`
3. **Gateway whitelist audit:** AI verified that `/inner/**` paths are NOT in the gateway whitelist
4. **Feign sync:** AI updated all Feign interface `@PostMapping` paths to match

**Result:** Internal endpoints isolated from public access. Gateway whitelist correctly excludes `/inner/**`.

---

## 4. Prompt Patterns That Worked

### Pattern 1: Context-Rich Task Description

Instead of: "Add a user list page"

Use:
```
Task: Add admin user list page
Context: Project uses Vue 3 + Element Plus + TypeScript
Rules: Follow rules/frontend-admin.md
Existing pattern: See src/views/blog/shared/ModuleListShell.vue
API: GET /admin/system/user/list returns PageResult<User>
Columns: id, username, nickname, email, status, createTime
Actions: edit, delete, reset password
```

### Pattern 2: Rule-Referencing Prompts

```
Read AGENTS.md, rules/api-contract.md, and rules/backend.md first.
Then implement the following controller following the established patterns...
```

### Pattern 3: Iterative Refinement

```
Round 1: "Generate the basic controller structure"
Round 2: "Add pagination support following PageResult<T> pattern"
Round 3: "Add @SaCheckPermission annotations"
Round 4: "Add input validation"
Round 5: "Generate the corresponding frontend API layer"
```

### Pattern 4: Verification-Driven Prompts

```
After implementing, run:
- mvn compile -pl sourcelin-modules/sourcelin-blog -am
- cd sourcelin-ui/sourcelin-ui-admin && pnpm run type-check
Fix any errors before considering the task complete.
```

---

## 5. Governance: How AI Stays on Track

The project uses a layered governance system to keep AI output consistent:

### Layer 1: AGENTS.md (Entry Point)

Every AI session starts by reading `AGENTS.md`, which defines:
- Project scope and module boundaries
- Open-source branch rules
- API contract hard rules
- Development workflows per module
- Forbidden practices

### Layer 2: rules/ (Engineering Standards)

| Rule File | What It Governs |
|---|---|
| `api-contract.md` | Response format, pagination, error codes |
| `backend.md` | Module boundaries, Controller/Service/Mapper patterns |
| `frontend-platform.md` | Blog frontend component and state patterns |
| `frontend-uniapp.md` | Uniapp cross-platform rules |
| `frontend-admin.md` | Admin panel patterns |
| `coding-conventions.md` | Naming, encoding, security, artifacts |
| `testing-and-validation.md` | Required verification per change type |

### Layer 3: skills/ (Reusable Workflows)

| Skill | When Used |
|---|---|
| `backend-dev` | Any backend Controller/Service/Mapper change |
| `frontend-platform-dev` | Blog frontend changes |
| `frontend-uniapp-dev` | Uniapp mini-program changes |
| `frontend-admin-dev` | Admin panel changes |
| `api-contract-governance` | Any API response/pagination/error code change |
| `architecture-refactor` | Cross-module refactoring |

### Layer 4: Automated Verification

| Module | Verification Command |
|---|---|
| Backend | `mvn compile -pl <module> -am` |
| Blog frontend | `npm run typecheck`, `npm run style:guard`, `npm run test:architecture` |
| Uniapp | `npm run lint`, `npm run type-check`, `npm run build:mp-weixin` |
| Admin panel | `pnpm run type-check`, `pnpm run lint` |

---

## 6. Lessons Learned

### What Worked Well

- **Rule-first approach:** Writing `rules/` before large-scale development prevented AI from drifting
- **Iterative refinement:** Breaking tasks into small, verifiable steps produced higher quality than single-shot generation
- **Verification gates:** Requiring `mvn compile` or `npm run typecheck` after every change caught errors early
- **Context-rich prompts:** Including file paths, existing patterns, and rule references dramatically improved output quality
- **Governance assets:** `AGENTS.md` + `rules/` + `skills/` created a self-reinforcing system where each AI session improves the next

### What Could Be Better

- **Prompt versioning:** Prompts are not currently versioned alongside code
- **Multi-model comparison:** No systematic comparison of output quality across GPT, Claude, and Gemini
- **AI code review:** Pre-commit AI review against project rules is not yet automated
- **Context window management:** Large refactoring sessions required careful chunking

---

## 7. Vibe Coding vs. Traditional Development

| Aspect | Traditional | Vibe Coding (This Project) |
|---|---|---|
| Architecture design | Human-only whiteboarding | AI proposes, human reviews |
| Code generation | Manual typing | AI generates, human refines |
| Documentation | Often deferred or skipped | AI generates alongside code |
| Refactoring | Time-consuming manual work | AI performs cross-module refactoring |
| Code review | Human-only | AI assists with rule compliance checks |
| Onboarding | Read docs, study code | AI reads `AGENTS.md` + `rules/` and aligns output |
| Consistency | Depends on developer discipline | Enforced by governance rules |

---

## 8. Conclusion

Sourcelin Cloud Blog demonstrates that Vibe Coding is not just about generating code faster — it is about building a **governed, repeatable, AI-assisted engineering system** that produces consistent, maintainable software.

The key insight: **AI output quality is proportional to the quality of your rules, prompts, and verification gates.** The `AGENTS.md` → `rules/` → `skills/` → verification pipeline is the real product of this project, as much as the blog system itself.

---

*This report is part of the Sourcelin Cloud Blog AI documentation suite. See also: [`openai-application.md`](./openai-application.md), [`ai-development-report.md`](./ai-development-report.md).*
