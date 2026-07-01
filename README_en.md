<p align="center">
  <a href="./README.md">中文</a> | <span>English</span>
</p>

<p align="center">
  <img alt="Sourcelin Blog Logo" src="https://pic1.imgdb.cn/item/69faac182356c9fc9194b076.png" width="120">
</p>

<h1 align="center">Sourcelin Blog</h1>

<p align="center">
  A full-stack microservice blog system built with Spring Cloud Alibaba + Vue 3, designed for personal writing, graduation project demos, and small-team content operations, with reusable AI-assisted development assets baked in.
</p>
<p align="center">
  <a href="https://github.com/SourceLin/sourcelin-cloud-blog"><img src="https://img.shields.io/github/stars/SourceLin/sourcelin-cloud-blog?style=flat-square&logo=github&logoColor=white" alt="GitHub Stars"></a>
  <a href="https://gitee.com/my_lyq/sourcelin-cloud-blog"><img src="https://gitee.com/my_lyq/sourcelin-cloud-blog/badge/star.svg?theme=dark" alt="Gitee Stars"></a>
  <img src="https://img.shields.io/badge/Java-1.8+-blue.svg" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021.0.9-brightgreen.svg" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883.svg" alt="Vue">
  <a href="./LICENSE"><img src="https://img.shields.io/badge/License-MIT-green.svg" alt="MIT License"></a>
</p>

<p align="center">
  <a href="https://sourcelin.cn/">Live Demo</a> |
  <a href="./docs/DOCS_INDEX.md">Docs</a> |
  <a href="./docs/guides/QUICK_START.md">Quick Start</a> |
  <a href="./docker-compose.example.yml">Compose Example</a> |
  <a href="./docs/guides/CONTRIBUTING.md">Contributing</a> |
  <a href="https://github.com/SourceLin/sourcelin-cloud-blog/issues">Issues</a>
</p>

## What is Sourcelin Blog?

Sourcelin Blog is a modern blogging platform for personal writing, life journaling, content showcase, and small-team content operations. It adopts a front-end/back-end separated microservice architecture, delivering a blog frontend, admin panel, unified API contract, permission system, and complete engineering conventions.

It is not just a blog demo. It is a full-stack project baseline ready for further customization, production deployment, graduation project presentation, and AI-assisted development practice.

This repository treats AI not as a sidekick plugin but as a core part of the development workflow — coding, documentation, rule curation, and release governance all happen within the same AI Coding pipeline. The current public release was primarily built with Codex and retains strong vibe-coding collaboration characteristics.

## Why It Matters

- Full-stack microservice architecture: built on Spring Cloud Alibaba with gateway, auth, system, blog, file, and monitoring services.
- Complete frontend + backend delivery: ships both a Vue 3 blog frontend and a Vue 3 admin panel, not just backend APIs or a single-page frontend.
- AI Coding friendly: the codebase, documentation, and rule curation were primarily done with Codex. The repo includes `AGENTS.md`, `rules/`, and `skills/` ready for continued AI Coding and vibe-coding collaboration.
- Clear engineering conventions: unified API response envelope, pagination protocol, module boundaries, and verification requirements for long-term maintenance and multi-contributor collaboration.
- Broad scenario coverage: suitable as a personal blog, content site, course project, graduation project, or secondary development baseline.


## AI Native Development

Sourcelin Blog is not just a blog system — it is an AI-native open-source project. The entire codebase, documentation, engineering rules, and release governance were built through a GPT-driven software engineering workflow.

### AI Participation

| Area                | AI Participation |
| ------------------- | ---------------- |
| Architecture Design | 100%             |
| API Design          | 90%              |
| Backend Scaffolding | 85%              |
| Frontend Components | 80%              |
| Refactoring         | 90%              |
| Documentation       | 90%              |
| Testing Assistance  | 80%              |

Models used: GPT-5, GPT-5.5, Claude, Gemini.

### Vibe Coding Workflow

```
Requirement Analysis → Prompt Engineering → AI Architecture Proposal
→ Code Generation → Human Review → Refactoring
→ Automated Verification → Commit & Documentation
```

Engineering principles: Architecture first, Contract first, Documentation first, AI-assisted implementation, Automated validation, Continuous refactoring.

### AI Governance Assets

The repository includes structured AI collaboration assets that ensure AI-generated outputs stay aligned with project architecture and coding standards:

- **`AGENTS.md`** — AI collaboration entry point and rules
- **`rules/`** — Engineering standards (API contract, component abstraction, architecture constraints, security)
- **`skills/`** — Reusable AI execution skills (planning, refactoring, architecture review, documentation generation)

### AI Engineering Achievements

- API contract standardization
- Pagination contract unification
- Frontend component modularization
- Shared component extraction
- Architecture guard implementation
- Automated contract validation

### Future AI Roadmap

- **Content AI**: article summarization, SEO optimization, content enhancement
- **Community AI**: comment moderation, spam detection, toxicity analysis
- **Knowledge AI**: knowledge base generation, semantic search, RAG integration
- **Admin AI**: intelligent operation assistant, AI diagnostics
- **Platform AI**: multi-model management, prompt template management, workflow orchestration

Read more: [docs/openai-application.md](./docs/openai-application.md) | [docs/ai-development-report.md](./docs/ai-development-report.md) | [docs/vibe-coding-report.md](./docs/vibe-coding-report.md)

## Screenshots

### Blog Frontend

#### Homepage

![Homepage](./docs/screenshot/首页.png)

#### Homepage Content

| Light Theme | Dark Theme |
|---|---|
| ![Homepage Content](./docs/screenshot/首页内容.png) | ![Homepage Content Dark](./docs/screenshot/首页内容-暗色主题.png) |

#### Article Detail

![Article Content](./docs/screenshot/文章内容.png)

#### User Center

![User Center](./docs/screenshot/个人中心.png)

<details>
<summary>More blog frontend screenshots</summary>

#### Categories & Tags

| Categories | Tags |
|---|---|
| ![Categories](./docs/screenshot/分类.png) | ![Tags](./docs/screenshot/标签.png) |

#### Archives & Trending

| Archives | Trending |
|---|---|
| ![Archives](./docs/screenshot/归档.png) | ![Trending](./docs/screenshot/热门.png) |

#### Moments & Treehole

| Moments | Treehole |
|---|---|
| ![Moments](./docs/screenshot/说说.png) | ![Treehole](./docs/screenshot/树洞.png) |

#### About & Login

| About | Login |
|---|---|
| ![About](./docs/screenshot/关于本站.png) | ![Login](./docs/screenshot/登录.png) |

</details>

### Admin Panel

#### Dashboard & Login

| Dashboard | Login |
|---|---|
| ![Admin Dashboard](./docs/screenshot/后台管理-首页.png) | ![Admin Login](./docs/screenshot/后台管理-登录页.png) |

#### Core Management

| System Management | Blog Management |
|---|---|
| ![System Management](./docs/screenshot/后台管理-系统管理.png) | ![Blog Management](./docs/screenshot/后台管理-博客管理.png) |

| Site Management | System Monitor |
|---|---|
| ![Site Management](./docs/screenshot/后台管理-网站管理.png) | ![System Monitor](./docs/screenshot/后台管理-系统监控.png) |

#### Content Editor & Profile

| Article Editor | Profile |
|---|---|
| ![Article Editor](./docs/screenshot/发布文章.png) | ![Profile](./docs/screenshot/后台管理-个人中心.png) |

</details>

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 2.7.18, Spring Cloud 2021.0.9, Spring Cloud Alibaba 2021.0.6.0 |
| Registry & Config | Nacos |
| Gateway | Spring Cloud Gateway |
| Auth | Sa-Token |
| ORM | MyBatis, MyBatis-Plus, PageHelper |
| Database | MySQL 8.0 |
| Cache | Redis |
| Frontend | Vue 3, TypeScript, Naive UI (blog frontend), Element Plus (admin panel) |
| Build | Maven, pnpm, Vite |
| Deployment | Docker, Docker Compose, Nginx |

## Service Architecture

```text
┌─────────────────────────────────────────────────────────┐
│                     Nginx (Port 80)                      │
└──────────┬──────────────────────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│              Gateway (Port 8080)                         │
│         Spring Cloud Gateway + Auth Filter               │
└──┬───────┬────────┬──────────┬──────────┬───────────────┘
   │       │        │          │          │
┌──▼──┐ ┌──▼──┐ ┌───▼───┐ ┌───▼───┐ ┌───▼───┐
│Auth │ │System│ │ Blog  │ │ File  │ │Monitor│
│9200 │ │9201  │ │ 9204  │ │ 9300  │ │ 9100  │
└──┬──┘ └──┬───┘ └───┬───┘ └───┬───┘ └───────┘
   │       │         │         │
┌──▼───────▼─────────▼─────────▼──┐
│         MySQL + Redis           │
└─────────────────────────────────┘
```

## Quick Start

### Prerequisites

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0
- Redis 6.0+
- Nacos 2.x
- Node.js 18+ & pnpm

### Local Development

1. **Initialize the database**

```bash
mysql -u root -p < docs/sql/sourcelin-cloud.sql
```

2. **Initialize Nacos config**

Import `docs/sql/sourcelin-config.sql` into Nacos.

3. **Start backend services** (in order)

```bash
# sourcelin-gateway (port 8080)
# sourcelin-auth (port 9200)
# sourcelin-system (port 9201)
# sourcelin-blog (port 9204)
# sourcelin-file (port 9300, optional)
```

4. **Start blog frontend**

```bash
cd sourcelin-ui/sourcelin-ui-platform
npm install
npm run dev
```

5. **Start admin panel**

```bash
cd sourcelin-ui/sourcelin-ui-admin
pnpm install
pnpm run dev
```

### Docker Deployment

For users who want to:

- Quickly deploy to a cloud server
- Run frontend and backend under a single domain
- Reference public Compose, Nginx, gateway entry, and upgrade guides

Minimal usage:

```bash
cp .env.example .env
# Edit .env to match your environment
docker compose -f docker-compose.example.yml up -d
```

## Newcomer Tips

- Make sure MySQL, Redis, and Nacos are running before starting microservices; most startup failures are just missing infrastructure dependencies.
- `docs/sql/sourcelin-config.sql` is not a business schema — it initializes Nacos configuration. Without it, services may start but with incomplete config.
- When deploying the admin panel under `/admin/`, the frontend build needs `--base=/admin/`.
- For local blog debugging, starting `gateway`, `auth`, `system`, and `blog` is sufficient.
- Before Docker deployment, read [`docs/guides/QUICK_START.md`](./docs/guides/QUICK_START.md) and [`docs/deployment/NGINX_CONFIG.md`](./docs/deployment/NGINX_CONFIG.md) to confirm directories, ports, and gateway entry match your environment.

## Project Structure

```text
sourcelin-cloud-blog/
├── sourcelin-api/                         # Feign interfaces & cross-service DTOs
├── sourcelin-common/                      # Shared utilities
├── sourcelin-gateway/                     # API Gateway
├── sourcelin-auth/                        # Auth service
├── sourcelin-modules/
│   ├── sourcelin-system/                  # System management
│   ├── sourcelin-blog/                    # Blog business logic
│   ├── sourcelin-file/                    # File service
│   └── sourcelin-job/                     # Scheduled tasks
├── sourcelin-visual/                      # Monitoring & visualization
├── sourcelin-ui/
│   ├── sourcelin-ui-platform/             # Blog frontend
│   ├── sourcelin-ui-admin/                # Admin panel
├── docs/                                  # Documentation
├── rules/                                 # Repository rules
├── skills/                                # AI collaboration skills
└── AGENTS.md                              # Repository entry point
```

## API Contract

All public HTTP JSON APIs follow a unified contract:

- `ApiResponse<T>` top-level envelope
- Success code fixed at `0`
- Top-level fields: `code / message / data / requestId / timestamp`
- Pagination uses `PageResult<T>`
- Pagination fields: `items / total / page / pageSize / totalPages`

See [`rules/api-contract.md`](./rules/api-contract.md) and [`docs/architecture/api-contract.md`](./docs/architecture/api-contract.md) for details.

## Documentation

- [Docs Index](./docs/DOCS_INDEX.md)
- [Quick Start](./docs/guides/QUICK_START.md)
- [Compose Example](./docker-compose.example.yml)
- [Showcase](./docs/guides/SHOWCASE.md)
- [Contributing](./docs/guides/CONTRIBUTING.md)
- [Changelog](./docs/guides/CHANGELOG.md)
- [Support](./docs/guides/SUPPORT.md)
- [Nginx Config](./docs/deployment/NGINX_CONFIG.md)
- [Upgrade Guide](./docs/deployment/UPGRADE.md)
- [SQL Scripts](./docs/sql/README.md)

## Roadmap

- App / Mini-program capabilities
- AI content assistant
- AI moderation upgrade
- Enhanced message center
- SSE real-time push
- Python content republishing
- SEO / traffic growth
- Operations dashboard

## Contributing

We welcome the following types of contributions:

- Bug reports and issue feedback
- Feature suggestions
- Documentation and screenshot improvements
- Frontend and backend fixes
- Deployment guide and example config improvements
- Secondary development case studies

Please read [`docs/guides/CONTRIBUTING.md`](./docs/guides/CONTRIBUTING.md) before getting started.

## Showcase

If you have successfully deployed Sourcelin Blog, we welcome you to submit your site URL, use case, and deployment method via Issue or PR.

We plan to feature:

- Personal blog cases
- Graduation / course project cases
- Small-team content site cases

See [`docs/guides/SHOWCASE.md`](./docs/guides/SHOWCASE.md) for details.

## FAQ

### Can I use it commercially?

Yes. This project is licensed under the [MIT License](./LICENSE). Please retain the license and copyright notice.

### Is it suitable for a graduation project?

Yes. It includes a frontend, admin panel, microservices, permissions, deployment, and engineering conventions — a well-rounded showcase.

### Is it suitable for secondary development?

Yes. The repository structure, API contract, and AI collaboration rules have clear boundary constraints, making it a solid baseline for business customization.

### Does it support Docker?

Yes. Refer to the deployment baseline documentation for server deployment. See the Docker-related doc entries above.

## Support

- Free support: environment setup, deployment Q&A, common issue troubleshooting
- Enterprise services: private deployment, custom development, training, security hardening

See [`docs/guides/SUPPORT.md`](./docs/guides/SUPPORT.md) for details.

## Star History

If this project helps you, a Star would be much appreciated.

Your support directly drives feature iteration, documentation improvements, deployment optimization, and continued AI collaboration rule curation.
