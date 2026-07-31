# PhonePe Manager - System Architecture Diagram

## 0. Product Vision

Personal expense tracker. Two ways to get expenses in:

1. **Manual entry** — user logs a transaction directly.
2. **Bank statement upload** — user uploads a PDF; the system auto-detects
   the transactions inside it, categorizes them, and reflects them in the app.

Categorization (path 2) is handled by a separate NLP microservice (FastAPI +
Hugging Face), already built and working in its own repo. It accepts an
uploaded PDF directly and returns a categorized transaction list. This
Spring Boot backend does not call it yet — that's the next integration
milestone.

---

## 1. High-Level System Architecture

```mermaid
graph TB
    subgraph Client["📱 Client Layer"]
        SPA["React SPA<br/>(PLANNED)"]
    end

    subgraph Backend["🔧 Spring Boot Backend (PARTIAL)"]
        direction TB
        API["📡 REST API<br/>Controller Layer"]
        SEC["🔐 Security Filter Chain<br/>JWT Authentication"]
        SVC["⚙️ Service Layer<br/>Business Logic"]
        REPO["💾 Spring Data JPA<br/>Repositories"]
    end

    subgraph ML["🤖 NLP Microservice (DONE, external repo)"]
        FASTAPI["FastAPI Service<br/>accepts PDF upload"]
        HF["🧠 Hugging Face<br/>Category Model"]
    end

    subgraph Data["🗄️ Data Layer"]
        DB[("🗄️ MySQL<br/>phonepe_manager")]
    end

    Client -->|HTTPS + JWT Bearer Token| API
    API --> SEC
    SEC --> SVC
    SVC --> REPO
    REPO --> DB
    SVC -.->|forward PDF - NOT WIRED YET| FASTAPI
    FASTAPI --> HF
    HF -.->|categorized transactions| FASTAPI
    FASTAPI -.->|categorized transactions| SVC

    style Client fill:#e1f5ff
    style Backend fill:#f3e5f5
    style ML fill:#fff3e0
    style Data fill:#e8f5e9
```

---

## 2. Backend Layered Architecture

```mermaid
graph LR
    subgraph Layers["🏗️ Backend Layers"]
        direction TB

        subgraph Layer1["REST Endpoints<br/>(Request/Response)"]
            CTR["📡 Controller<br/>- AuthController<br/>- ExpenseController (full CRUD)<br/>- TransactionController<br/>- AccountController (create + list only)"]
        end

        subgraph Layer2["Business Logic<br/>(@Transactional)"]
            SRV["⚙️ Service<br/>- AuthService<br/>- ExpenseService<br/>- TransactionService<br/>- AccountService<br/>- ExpensePdfService (orphaned - no caller)"]
        end

        subgraph Layer3["Data Access"]
            REP["💿 Repository<br/>- UserRepository<br/>- AccountRepository<br/>- ExpenseRepository<br/>- TransactionRepository<br/>- CategoryRepository<br/>- DebtRecordRepository<br/>- UploadedStatementRepository"]
        end

        subgraph Layer4["Domain Models"]
            ENT["🔗 Entity<br/>- User<br/>- Account<br/>- Expense<br/>- Transaction<br/>- Category<br/>- DebtRecord<br/>- UploadedStatement (mismodeled)"]
        end

        subgraph Layer5["Transfer Objects"]
            DTO["📦 DTO<br/>- AuthRequest/Response<br/>- ExpenserequestDTO<br/>- TransactionDTO<br/>- AccountRequestDTO<br/>- BulkRequestDTO"]
        end
    end

    CTR --> SRV
    SRV --> REP
    REP --> ENT
    CTR -.->|validation| DTO
    DTO -.-> CTR

    style Layer1 fill:#bbdefb
    style Layer2 fill:#c8e6c9
    style Layer3 fill:#ffe0b2
    style Layer4 fill:#f8bbd0
    style Layer5 fill:#e1bee7
```

---

## 3. Data Model - Entity Relationships

```mermaid
erDiagram
    USER ||--o{ ACCOUNT : "owns"
    USER ||--o{ DEBT_RECORD : "owns"
    USER ||--o{ UPLOADED_STATEMENT : "owns"
    ACCOUNT ||--o{ EXPENSE : "spent from"
    ACCOUNT ||--o{ TRANSACTION : "sends (as sender)"
    ACCOUNT ||--o{ TRANSACTION : "receives (as receiver)"
    CATEGORY ||--o{ EXPENSE : "classifies"

    USER {
        Long id PK
        string name
        string email UK
        string passwordHash
        Role role
        LocalDateTime createdAt
    }

    ACCOUNT {
        Long id PK
        Long user_id FK
        BigDecimal balance
        AccountType accountType
        LocalDateTime createdAt
    }

    EXPENSE {
        Long id PK
        Long account_id FK
        Long category_id FK
        BigDecimal amount
        string description
        LocalDateTime timestamp
        ExpenseSource source
    }

    TRANSACTION {
        Long id PK
        Long sender_account_id FK
        Long receiver_account_id FK
        BigDecimal amount
        TransactionStatus status
        LocalDateTime createdAt
        string note
    }

    CATEGORY {
        Long id PK
        string name
        CategoryType type
    }

    DEBT_RECORD {
        Long id PK
        Long user_id FK
        string name
        BigDecimal amount
        DebtType type
        DebtStatus status
        LocalDateTime createdAt
        LocalDateTime dueDate
    }

    UPLOADED_STATEMENT {
        Long id PK
        Long user_id FK
        string personName "mismodeled - should be fileName"
        BigDecimal amount "mismodeled - not a single amount"
        DebtType type "mismodeled - wrong vocabulary"
        DebtStatus status "mismodeled - needs PENDING/PROCESSED/FAILED"
        LocalDateTime createdAt
        LocalDateTime dueDate "mismodeled - not applicable"
    }
```

---

## 4. Security Flow - JWT Authentication

```mermaid
sequenceDiagram
    participant Client as 🖥️ Client
    participant Controller as 📡 Controller
    participant Filter as 🔐 JWT Filter
    participant SecurityCtx as 🔑 Security Context
    participant Service as ⚙️ Service

    Note over Client,Service: Login Flow
    Client->>Controller: POST /api/auth/login<br/>{email, password}
    Controller->>Controller: Verify credentials<br/>(BCrypt)
    Controller->>Controller: Generate JWT<br/>(HS256, 10h expiry)
    Controller-->>Client: {token: JWT}

    Note over Client,Service: Authenticated Request
    Client->>Filter: GET /api/expenses/me<br/>Authorization: Bearer JWT
    Filter->>Filter: Extract username<br/>from JWT
    Filter->>Filter: Validate token<br/>(signature + expiry)
    Filter->>SecurityCtx: Set Authentication<br/>UsernamePasswordAuthenticationToken
    Filter->>Controller: Continue chain
    Controller->>Service: Get user expenses
    Service-->>Controller: expenses[]
    Controller-->>Client: {data: expenses}
```

---

## 5. Component Status Matrix

| Feature | Entity | Repository | Service | Controller | Status |
|---------|:------:|:----------:|:-------:|:----------:|:------:|
| **Auth** | ✅ | ✅ | ✅ | ✅ | DONE |
| **Account** | ✅ | ✅ | ✅ | ⚠️ create + list only | PARTIAL |
| **Expense** | ✅ | ✅ | ✅ | ✅ full CRUD | DONE |
| **Transaction** | ✅ | ✅ | ✅ | ✅ | DONE |
| **Category** | ✅ | ✅ | ❌ | ❌ | MISSING (blocks Expense creation) |
| **DebtRecord** | ✅ | ✅ | ❌ | ❌ | MISSING |
| **UploadedStatement** | ⚠️ mismodeled | ✅ | ❌ | ❌ | MISSING |
| **Bulk PDF Import (backend)** | - | - | ✅ | ❌ | PARTIAL - nothing calls the service |
| **NLP Categorization** | - | - | ✅ external repo | - | DONE, not yet integrated from this backend |
| **Analytics/KPI** | - | - | ❌ | ❌ | MISSING |

---

## 6. API Endpoints Overview

```mermaid
graph TD
    API["🌐 REST API<br/>/api/..."]

    AUTH["🔐 /api/auth<br/>- POST /register<br/>- POST /login"]

    EXPENSE["📊 /api/expenses<br/>- POST (create)<br/>- GET /me<br/>- GET (all, admin)<br/>- GET /{id}<br/>- GET /account/{accountId}<br/>- PUT /{id}<br/>- DELETE /{id}"]

    TRANSACTION["💸 /api/transactions<br/>- POST /transfer"]

    ACCOUNT["💰 /api/accounts<br/>- POST /create<br/>- GET /user/{userid}/accounts"]

    API --> AUTH
    API --> EXPENSE
    API --> TRANSACTION
    API --> ACCOUNT

    style API fill:#1976d2,color:#fff
    style AUTH fill:#388e3c,color:#fff
    style EXPENSE fill:#d32f2f,color:#fff
    style TRANSACTION fill:#f57c00,color:#fff
    style ACCOUNT fill:#7b1fa2,color:#fff
```

---

## 7. Enums Reference

```
🔤 Role:              USER | ADMIN
💳 AccountType:       WALLET | BANK | CASH | SAVINGS
📁 CategoryType:      EXPENSE | INCOME
📤 ExpenseSource:     MANUAL | PDF_IMPORT
✅ TransactionStatus: SUCCESS | FAILED | PENDING
💳 DebtType:          BORROWED | DEBT
⏳ DebtStatus:        PENDING | STATUS | OVERDUE  ⚠️ STATUS looks like a placeholder
```

---

## 8. Known Issues & Roadmap

### Current Issues
- ⚠️ Controllers return JPA entities instead of DTOs (over-serialization risk on lazy relations)
- ⚠️ Secrets-to-`.env` fix drafted but not committed yet
- ⚠️ `UploadedStatement` entity reuses debt vocabulary — needs remodeling before Phase 2
- ⚠️ `DebtStatus.STATUS` looks like a placeholder value

### Roadmap

| Phase | Features |
|-------|----------|
| **Phase 1 (current)** | Category service/controller (blocking), Account get/update/delete, DebtRecord service/controller, DTO responses, land secrets fix |
| **Phase 2 (next)** | Remodel `UploadedStatement`; `POST /api/statements/upload`; forward PDF to external NLP microservice; map returned categories to internal `Category` ids; call `ExpensePdfService.SaveBulkExpenses` |
| **Phase 3** | Analytics & KPI endpoints for dashboard (spend-by-category, monthly trend, income vs expense) |
| **Phase 4** | React SPA — auth, manual entry, PDF upload UI, Recharts dashboard |
| **Phase 5** | Consistent RBAC enforcement, Docker Compose (Spring Boot + MySQL + NLP microservice), secret rotation |

---

**Last Updated:** 2026-07-31
