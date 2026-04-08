# Domain Architecture Roadmap (DDD)

This document defines the target bounded contexts, ownership, dependencies, and implementation order for the debt collection platform.

## 1) Bounded Contexts

### 1. Customer Context
**Purpose:** Manage customer identity, contact data, and communication consents.

**Core entities:**
- `Client`

**Commands/use cases:**
- Create customer
- Update contact
- Update channel consents
- Retrieve by ID/document

**Publishes events:**
- `CustomerCreated`
- `CustomerContactUpdated`
- `CustomerConsentsUpdated`

---

### 2. Obligation Context
**Purpose:** Represent debt obligations and delinquency lifecycle.

**Core entities:**
- `Obligation`

**Commands/use cases:**
- Register obligation
- Update delinquency
- Apply payment
- Retrieve by ID/number

**Publishes events:**
- `ObligationCreated`
- `DelinquencyUpdated`
- `ObligationPaid`

---

### 3. Scoring Context
**Purpose:** Calculate prioritization score and segment by risk/recovery potential.

**Core entities:**
- `ScoringSegmentation`

**Commands/use cases:**
- Calculate score
- Get latest score by obligation
- List score history by customer

**Inputs:**
- Customer profile
- Obligation delinquency and amount
- Contact attempts/effectiveness

---

### 4. Policy Context
**Purpose:** Strategy and rule management for debt collection actions.

**Core entities:**
- `Strategy`
- `Policy`

**Commands/use cases:**
- Define/update strategy
- Activate/deactivate policy
- Evaluate next action rules

---

### 5. Case Management Context
**Purpose:** Track human/manual collection process and case lifecycle.

**Core entities:**
- `Case`

**Commands/use cases:**
- Open case
- Assign advisor
- Schedule next action
- Close case

**Consumes:**
- Scoring output
- Policy decisions

---

### 6. Interaction Context
**Purpose:** Register omnichannel communication attempts and outcomes.

**Core entities:**
- `Interaction`

**Commands/use cases:**
- Create interaction
- Update delivery/read/failure status
- List interactions by case

---

### 7. Orchestration Context
**Purpose:** Execute channel actions based on scoring/policies/case state.

**Core entities:**
- `OrchestrationExecution`

**Commands/use cases:**
- Send orchestration command
- Query execution by ID/case

**Depends on:**
- Policy
- Case management
- Interaction adapters

---

### 8. Payment Context
**Purpose:** Payment link state and payment confirmation workflow.

**Core entities:**
- `Payment`

**Commands/use cases:**
- Create payment intent
- Confirm payment callback
- Reject payment
- Query by ID/reference

**Publishes events:**
- `PaymentConfirmedEvent`

---

### 9. Audit Context
**Purpose:** Immutable traceability of business/technical actions.

**Core entities:**
- `AuditEvent`

**Commands/use cases:**
- Register audit event
- Query/export traceability

---

### 10. Integration Context
**Purpose:** External gateways/adapters (core, channels, payment gateway, n8n).

**Responsibilities:**
- Contract translation
- Idempotency keys
- Retries and dead-letter handling
- Callback normalization

---

## 2) Dependency Direction (Target)

Preferred dependency flow:

`Customer` + `Obligation` -> `Scoring` -> `Policy` -> `CaseManagement` -> `Orchestration` -> `Interaction` -> `Payment` -> `Audit`

Rules:
1. Domain models never depend on infrastructure.
2. Context-to-context communication should happen through application services and events.
3. Shared kernel stays minimal (event interfaces, base exceptions, value object primitives).

## 3) 5-Delivery Implementation Order

### Delivery 1 (Foundations)
- Stabilize `Customer`, `Obligation`, `Payment` core domain APIs in English.
- Enforce common exception strategy.
- Ensure all contexts expose clear application services.

### Delivery 2 (Decisioning)
- Consolidate `Scoring` + `Policy`.
- Define next-best-action contract.
- Add deterministic fallback path when model is unavailable.

### Delivery 3 (Execution)
- Consolidate `CaseManagement`, `Orchestration`, `Interaction`.
- Implement channel adapter boundaries for n8n/WhatsApp/SMS/email.
- Add idempotent orchestration execution records.

### Delivery 4 (Payments + Compliance)
- Finalize `Payment` callback flow.
- Connect `Audit` end-to-end for critical events.
- Validate legal controls (windows/frequency/consents) via policy guardrails.

### Delivery 5 (Hardening + Integration)
- Harden `Integration` adapters (retries, dead-letter, observability).
- Add dashboards and KPI extraction.
- Prepare production runbooks and rollback strategy.

## 4) Current Refactor Focus

The current refactor focus should remain:
1. Remove mixed Spanish/English method names in each context.
2. Keep one public API language (English) per context.
3. Eliminate compatibility aliases only after usages are migrated.
4. Keep package moves for a dedicated phase to minimize churn.

