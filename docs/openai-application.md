# Sourcelin Cloud Blog - OpenAI Open Source Application

## Project Overview

**Sourcelin Cloud Blog** is an AI-native open-source full-stack cloud platform built through a GPT-driven software engineering workflow.

Unlike traditional open-source projects that merely integrate AI capabilities, this repository demonstrates how large language models can participate in the entire software development lifecycle, including:

* Architecture design
* API contract definition
* Frontend generation
* Backend implementation
* Refactoring
* Documentation generation
* Engineering governance
* Continuous evolution

The project serves as a practical exploration of AI-assisted software engineering and Vibe Coding methodologies.

Repository:

[https://github.com/SourceLin/sourcelin-cloud-blog](https://github.com/SourceLin/sourcelin-cloud-blog)

---


---

## Planned AI Architecture

The project has a comprehensive AI integration blueprint designed across three phases:

### Three-Phase AI Roadmap

```
Phase 1: Lightweight AI Adapter
  Blog → sourcelin-ai (adapter) → Dify / DirectLLM
  - 5 core capabilities: title, summary, tags, polish, safety audit
  - Sync + SSE streaming + async task modes
  - Unified call logging and task management
  - AI is optional: blog core functions work without it

Phase 2: Enhanced AI Capabilities
  - RAG knowledge base Q&A (FastGPT integration)
  - AI content assistant (outline, cover image, batch SEO)
  - Comment reply suggestions
  - AI material library and usage statistics

Phase 3: Unified AI Service
  - Multi-tenant, multi-system AI service
  - Cross-system Agent and Workflow orchestration
  - Unified cost tracking, quotas, and auditing
```

### Planned AI Capabilities

| Capability | Mode | Description |
|---|---|---|
| Article title generation | Sync | Generate SEO-optimized titles from content |
| Article summary | Sync | Auto-generate concise summaries |
| Article tag recommendation | Sync | Suggest relevant tags from content |
| Article polish | SSE Stream | Real-time text refinement |
| SEO metadata generation | Sync | Generate title/description/keywords |
| Comment safety audit | Sync | AI-powered risk detection |
| Knowledge base Q&A | SSE Stream | RAG-based blog knowledge assistant |
| AI agent chat | SSE Stream | Multi-turn conversation assistant |

### Supporting Infrastructure (Planned)

| System | Technology | Purpose |
|---|---|---|
| AI Adapter Service | Spring Boot microservice | Unified protocol, provider routing, logging |
| AI Platform | Dify (self-hosted) | Prompt, Workflow, Knowledge Base, Model access |
| Direct LLM | OpenAI-compatible API | Direct model invocation for simple tasks |
| Message Center | Redis Pub/Sub + SSE | Distributed real-time push |
| SSE Auth | One-time ticket (Redis) | Secure EventSource connections |
| File Relay | URL-to-OSS upload | Archive temporary AI media to permanent storage |
| Config Hot-Reload | Nacos + @RefreshScope | Runtime provider switching |

### Design Principles

- **AI is optional**: `blog.ai.enabled=false` keeps all blog core functions fully operational
- **Non-destructive**: AI results saved as drafts; user must explicitly adopt before writing to production
- **Provider-agnostic**: business code references `abilityCode` only; provider differences encapsulated
- **Three config modes**: DirectLLM (zero dependency), Dify (full platform), or disabled (open-source default)

---
## Technical Architecture

### Frontend

* Vue 3
* TypeScript
* Naive UI
* Vite

### Backend

* Spring Boot
* Spring Cloud Alibaba
* MyBatis Plus
* Redis
* MySQL

### Infrastructure

* Nacos
* Gateway
* Docker
* OSS Storage

### System Modules

* Authentication Center
* User Management
* Article Management
* Comment System
* Category Management
* Tag Management
* File Management
* System Monitoring
* Administration Console

---

## AI-Assisted Development

This project is developed using a GPT-driven workflow.

Estimated AI participation:

| Area                | AI Participation |
| ------------------- | ---------------- |
| Architecture Design | 100%             |
| API Design          | 90%              |
| Backend Scaffolding | 85%              |
| Frontend Components | 80%              |
| Refactoring         | 90%              |
| Documentation       | 90%              |
| Testing Assistance  | 80%              |

Models used during development include:

* GPT-5
* GPT-5.5
* Claude
* Gemini

AI is not only used for code generation but also for architectural review, design discussions, contract validation, and project governance.

---

## Vibe Coding Workflow

The project follows a structured Vibe Coding methodology.

### Workflow

Requirement Analysis

↓

Prompt Engineering

↓

AI Architecture Proposal

↓

Code Generation

↓

Human Review

↓

Refactoring

↓

Automated Verification

↓

Commit & Documentation

---

### Engineering Principles

* Architecture first
* Contract first
* Documentation first
* AI-assisted implementation
* Automated validation
* Continuous refactoring

This workflow significantly improves development efficiency while maintaining engineering quality.

---

## AI Governance

One of the project's distinguishing features is the establishment of AI governance assets.

Repository includes:

### AGENTS.md

Defines AI collaboration rules.

### rules/

Project engineering standards.

Examples:

* API contract rules
* Component abstraction rules
* Architecture constraints
* Security standards

### skills/

Reusable AI execution skills.

Examples:

* Planning workflows
* Refactoring workflows
* Architecture review workflows
* Documentation generation workflows

These assets ensure AI-generated outputs remain aligned with project architecture and coding standards.

---

## AI Engineering Achievements

The project has successfully completed multiple large-scale AI-assisted refactoring efforts.

Examples include:

* API contract standardization
* Pagination contract unification
* Frontend component modularization
* Shared component extraction
* Architecture guard implementation
* Automated contract validation

The repository demonstrates that AI can participate in large-scale engineering evolution beyond simple code generation.

---

## Future AI Roadmap

The next phase of the project focuses on deeper AI integration.

### Content AI

* Article summarization
* SEO optimization
* Content enhancement
* Intelligent rewriting

### Community AI

* Comment moderation
* Spam detection
* Toxicity analysis

### Knowledge AI

* Knowledge base generation
* Semantic search
* RAG integration

### Admin AI

* Intelligent operation assistant
* AI diagnostics
* AI monitoring analysis

### Platform AI

* Multi-model management
* Prompt template management
* Workflow orchestration
* AI capability center

---

## Why OpenAI Support Is Important

This project is an active open-source initiative exploring how GPT can be integrated into real-world software engineering.

ChatGPT Pro support would directly contribute to:

* Ongoing architecture evolution
* Prompt engineering research
* AI feature development
* Open-source documentation
* Community knowledge sharing

The project will continue publishing engineering practices and Vibe Coding methodologies to help other developers build production-grade systems with AI assistance.

---

## Open Source Commitment

The project is actively maintained and continuously evolving.

Future plans include:

* More AI-native capabilities
* Expanded documentation
* Public engineering reports
* Open-source Vibe Coding best practices
* AI-assisted software engineering case studies

The goal is to become a representative example of AI-native open-source software development.
