# PhonePe Manager - System Architecture Diagram

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

    subgraph ML["🤖 NLP Microservice (PLANNED)"]
        FASTAPI["FastAPI Service"]
        HF["🧠 Hugging Face<br/>Category Model"]
    end

    subgraph Data["��� Data Layer"]
        DB[("🗄️ MySQL<br/>phonepe_manager")]
    end

    Client -->|HTTPS + JWT Bearer Token| API
    API --> SEC
    SEC --> SVC
    SVC --> REPO
    REPO --> DB
    SVC -.->|async call| FASTAPI
    FASTAPI --> HF
    HF -.->|result| SVC

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
            CTR["📡 Controller<br/>- AuthController<br/>- ExpenseController<br/>- TransactionController<br/>- AccountController"]
        end
        
        subgraph Layer2["Business Logic<br/>(@Transactional)"]
            SRV["⚙️ Service<br/>- AuthService<br/>- ExpenseService<br/>- TransactionService<br/>- AccountService<br/>- ExpensePdfService"]
        end
        
        subgraph Layer3["Data Access"]
            REP["💿 Repository<br/>- UserRepository<br/>- AccountRepository<br/>- ExpenseRepository<br/>- TransactionRepository<br/>- CategoryRepository<br/>- DebtRecordRepository"]
        end
        
        subgraph Layer4["Domain Models"]
            ENT["🔗 Entity<br/>- User<br/>- Account<br/>- Expense<br/>- Transaction<br/>- Category<br/>- DebtRecord"]
        end
        
        subgraph Layer5["Transfer Objects"]
            DTO["📦 DTO<br/>- AuthRequest/Response<br/>- ExpenseRequestDTO<br/>- TransactionDTO<br/>- AccountRequestDTO"]
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
        string personName
        BigDecimal amount
        DebtType type
        DebtStatus status
        LocalDateTime createdAt
        LocalDateTime dueDate
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
    Client->>Filter: GET /api/expenses<br/>Authorization: Bearer JWT
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
| **Account** | ✅ | ⚠️ | ✅ | ✅ | PARTIAL |
| **Expense** | ✅ | ✅ | ✅ | ⚠️ | PARTIAL |
| **Transaction** | ✅ | ✅ | ✅ | ✅ | DONE |
| **Category** | ✅ | ✅ | ❌ | ❌ | MISSING |
| **DebtRecord** | ✅ | ✅ | ❌ | ❌ | MISSING |
| **UploadedStatement** | ✅ | ✅ | ❌ | ❌ | MISSING |
| **Bulk PDF Import** | - | - | ✅ | ❌ | PARTIAL |
| **Analytics/KPI** | - | - | ❌ | ❌ | MISSING |
| **NLP Categorization** | - | - | ❌ (Python) | ❌ | MISSING |

---

## 6. API Endpoints Overview

```mermaid
graph TD
    API["🌐 REST API<br/>/api/..."]
    
    AUTH["🔐 /api/auth<br/>- POST /register<br/>- POST /login"]
    
    EXPENSE["📊 /api/expenses<br/>- GET /all<br/>- GET /{id}<br/>- POST /create<br/>- GET /account/{id}"]
    
    TRANSACTION["💸 /api/transactions<br/>- POST /send<br/>- GET /{id}<br/>- GET /all"]
    
    ACCOUNT["💰 /accounts<br/>- POST /create<br/>(⚠️ No /api prefix)"]
    
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
⏳ DebtStatus:        PENDING | STATUS | OVERDUE
```

---

## 8. Known Issues & Roadmap

### Current Issues (§5)
- ❌ Inconsistent routing (`/api/**` vs `/accounts`)
- ❌ No ownership scoping on Expense reads
- ❌ Controllers return JPA entities instead of DTOs
- ❌ Secrets hardcoded in `application.properties`

### Roadmap

| Phase | Features | Timeline |
|-------|----------|----------|
| **Phase 1** | CRUD gaps (Category, Account, DebtRecord), wire bulk import, fix inconsistencies | Current |
| **Phase 2** | PDF statement upload/parsing, UploadedStatement service | Next |
| **Phase 3** | Analytics & KPI endpoints for dashboard | Q3 2026 |
| **Phase 4** | FastAPI + Hugging Face NLP service | Q4 2026 |
| **Phase 5** | React SPA frontend | Q4 2026 |
| **Phase 6** | RBAC, Docker Compose, secret rotation | Q1 2027 |

---

**Last Updated:** July 2026  
**Status:** Architecture Phase 1 (In Progress)
