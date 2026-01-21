# 식자재 유통을 위한 물류 관리 백엔드 시스템

## 1. 프로젝트 개요

이 프로젝트는 식자재 유통 도메인에 초점을 맞춘 WMS(Warehouse Management System)의 핵심 기능을 구현한 REST API 서버입니다. 품목(SKU), 공급사, 로케이션 등 기준 정보를 관리하고, 이를 바탕으로 구매 발주(PO), 입고(Goods Receipt), 재고 관리 등 물류의 핵심 프로세스를 처리합니다.

객체 지향 도메인 모델링, 계층형 아키텍처, 영속성 분리 등 소프트웨어 공학 원칙을 실제 코드에 적용하며 학습하는 것을 목표로 합니다. Spring Boot를 기반으로 REST API를 제공하며, 복잡한 물류 규칙을 도메인 객체의 책임과 협력으로 풀어내는 데 중점을 두었습니다.

---

## 2. 핵심 도메인 모델

본 시스템은 크게 `기준정보`, `구매`, `입고`, `재고` 네 가지 핵심 도메인으로 구성됩니다.

### 가. 기준정보 (Master Data)

물류 활동의 기반이 되는 데이터입니다.

- **`Item` (품목)**: 관리의 기본 단위(SKU)입니다. 이름, 코드와 같은 기본 정보 외에, 식자재 특성을 반영한 **온도대(`TemperatureZone`)**, **품목 카테고리(`ItemCategory`)**를 가집니다. 특히 **단위 변환 프로필(`UomConversionProfile`)**을 통해 `BOX`를 `EA`로, `L`를 `kg`으로 변환하는 등 품목별 단위 변환 규칙을 정의할 수 있습니다.
- **`Location` (로케이션)**: 재고가 보관되는 물리적 또는 논리적 위치입니다. `WH(창고)`, `BIN(보관랙)`, `STAGE(임시구역)` 등 타입을 가지며, 특정 온도대나 품목 카테고리의 상품만 보관하도록 제약 조건을 설정할 수 있습니다.
- **`Supplier` (공급사)**: 품목을 공급하는 주체입니다. 리드 타임, 연락처 등의 정보를 관리합니다.

### 나. 구매 (Purchasing)

공급사로부터 품목을 조달하는 과정을 관리합니다.

- **`PO` (Purchase Order, 구매 발주)**: 특정 공급사에 품목을 주문하는 문서입니다. 내부에 하나 이상의 발주 라인(`POLine`)을 포함합니다.
- **발주 라이프사이클**: `DRAFT` (초안) → `APPROVED` (승인) → `SENT` (발송) → `PARTIALLY_RECEIVED` (부분 입고) / `CLOSED` (완료) 순으로 상태가 변경되며, 각 단계에 맞는 비즈니스 규칙이 적용됩니다.

### 다. 입고 (Inbound)

주문한 품목이 물류 센터에 도착했을 때 처리하는 과정입니다.

- **`GoodsReceipt` (입고)**: 입고 활동을 기록하는 문서입니다.
  - **`PO_BASED`**: 구매 발주(`PO`)를 기반으로 생성되는 표준 입고입니다.
  - **`FREE`**: 발주 없이 비정규적으로 발생하는 입고(예: 증정품)입니다.
- **`GoodsReceiptLine` (입고 라인)**: 실제 입고된 품목, 수량, 적재 위치(`Location`), 유통기한(`LotInfo`) 등의 상세 정보를 가집니다.

### 라. 재고 (Stock)

물류 센터 내의 모든 재고를 추적하고 관리합니다.

- **`Lot`**: 유통기한을 기준으로 묶인 동일 품목의 집합입니다. 모든 재고는 `Lot` 단위로 관리됩니다.
- **`StockQuant`**: **가장 핵심적인 재고 객체**로, 특정 `Lot`이 특정 `Location`에 얼마만큼 있는지를 나타냅니다. `onHandQty`(현재고)와 `reservedQty`(예약/출고 대기 수량)를 분리하여 가용 재고(`availableQty`)를 정확히 계산합니다.
- **`StockMove`**: 재고의 모든 움직임(`입고`, `출고`, `이동`, `조정`)을 기록하는 트랜잭션 로그입니다. 모든 재고 변경은 반드시 `StockMove`를 생성하여 추적 가능성을 보장합니다.

---

## 3. 아키텍처 및 설계 원칙

### 가. 계층형 아키텍처 (Layered Architecture)

프로젝트는 역할에 따라 4개의 계층으로 명확히 분리되어 있습니다.

1.  **`web` (표현 계층)**: HTTP 요청을 받고 응답을 보냅니다. `Controller`, `DTO`, `DtoMapper`가 위치하며, 외부 세계와의 소통을 전담합니다.
2.  **`app` (애플리케이션 계층)**: 유스케이스를 구현합니다. 여러 도메인 객체와 리포지토리를 조율하여 비즈니스 흐름을 완성하며, 트랜잭션 경계 역할을 합니다.
3.  **`domain` (도메인 계층)**: 시스템의 핵심 비즈니스 규칙과 상태를 포함합니다. 순수 Java/Kotlin 객체로 구성된 `Aggregate`, `Value Object`, `Repository Interface`가 위치하며, 다른 계층에 대한 의존성이 없습니다.
4.  **`infra` (인프라 계층)**: 데이터베이스 연동, 외부 시스템 호출 등 기술적인 구현을 담당합니다. `Repository`의 JPA 구현체, `JPA Entity`, `QueryDSL` 쿼리 등이 위치합니다.

이러한 구조는 도메인 계층을 특정 기술(예: JPA)로부터 보호하고, 각 계층이 자신의 책임에만 집중할 수 있도록 하여 유연하고 유지보수하기 쉬운 시스템을 지향합니다.

### 나. 도메인 모델과 영속성 모델의 분리

- **도메인 모델**: 비즈니스 로직을 수행하는 순수한 객체입니다. (`PO`, `Item`, `Quantity` 등)
- **영속성 모델**: 데이터베이스 테이블과 1:1로 매핑되는 `JPA Entity`입니다. (`POEntity`, `ItemEntity` 등)

두 모델을 분리하고 `EntityMapper`를 통해 상호 변환합니다. 이로 인해 도메인 모델은 영속성 기술의 제약(ex: 기본 생성자, getter/setter)에서 자유로워지며, 더 풍부한 비즈니스 표현이 가능해집니다.

### 다. 값 객체(Value Object)의 적극적 활용

`POId`(발주 ID), `Quantity`(수량), `SKU` 등 단순 값처럼 보일 수 있는 개념들을 별도의 `record` 또는 `class`로 모델링했습니다. 이를 통해 아래와 같은 이점을 얻습니다.

- **타입 안정성**: `Long` 타입의 ID 대신 `POId`, `ItemId`를 사용함으로써, 메서드 시그니처만으로 의도를 명확히 하고 컴파일 시점에 타입 오류를 방지합니다.
- **불변성 및 유효성 검증**: 생성 시점에서 데이터의 유효성(ex: 수량은 음수일 수 없음)을 검증하고, 불변 객체로 만들어 시스템 전반에서 데이터의 일관성을 유지합니다.
- **핵심 로직 캡슐화**: `Quantity` 객체는 `add`, `subtract` 등 수량 관련 연산과 단위 변환 로직을 내부에 포함하여, 관련 로직을 한곳에서 응집력 있게 관리합니다.

---

## 4. API 엔드포인트

| Feature         | Method     | URL                                    | Description                              |
| --------------- | ---------- | -------------------------------------- | ---------------------------------------- |
| **품목 (Item)**     | `POST`     | `/items`                               | 새 품목 생성                             |
|                 | `GET`      | `/items`                               | 품목 목록 조회 (페이징)                  |
|                 | `GET`      | `/items/{id}`                          | 특정 품목 상세 조회                      |
|                 | `PUT`      | `/items/{id}`                          | 품목 정보 수정                           |
| **공급사 (Supplier)** | `POST`     | `/suppliers`                           | 새 공급사 생성                           |
|                 | `GET`      | `/suppliers`                           | 공급사 목록 조회 (페이징)                |
|                 | `GET`      | `/suppliers/{id}`                      | 특정 공급사 상세 조회                    |
|                 | `PUT`      | `/suppliers/{id}`                      | 공급사 정보 수정                         |
| **로케이션 (Location)** | `POST`     | `/locations`                           | 새 로케이션 생성                         |
|                 | `GET`      | `/locations`                           | 로케이션 목록 조회 (페이징)              |
|                 | `GET`      | `/locations/{id}`                      | 특정 로케이션 상세 조회                  |
|                 | `PUT`      | `/locations/{id}`                      | 로케이션 정보 수정                       |
| **발주 (PO)**       | `POST`     | `/purchase-orders`                     | 새 발주 생성 (초안)                      |
|                 | `GET`      | `/purchase-orders`                     | 발주 목록 조회 (페이징)                  |
|                 | `GET`      | `/purchase-orders/open`                | 미완료 발주 목록 조회                    |
|                 | `GET`      | `/purchase-orders/overdue`             | 납기 지연 발주 목록 조회                 |
|                 | `GET`      | `/purchase-orders/{id}`                | 특정 발주 상세 조회                      |
|                 | `POST`     | `/purchase-orders/{id}/approve`        | 발주 승인                                |
|                 | `POST`     | `/purchase-orders/{id}/send`           | 발주서 공급사 전송                       |
|                 | `DELETE`   | `/purchase-orders/{id}`                | 발주 삭제                                |
| **입고 (GRN)**      | `POST`     | `/receiving/grns`                      | 입고 생성 (구현 예정)                    |

---

## 5. 시작하기

### 가. 요구사항

- `Java 21`
- `Gradle 8.x`
- `PostgreSQL` 데이터베이스

### 나. 설정

1.  `src/main/resources/application.yml` 파일에서 자신의 데이터베이스 환경에 맞게 `spring.datasource` 설정을 수정합니다.

    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/your_database # DB 주소 및 이름
        username: your_username                            # DB 사용자 이름
        password: your_password                            # DB 비밀번호
    ```

### 다. 빌드 및 실행

1.  **프로젝트 빌드**

    ```bash
    ./gradlew build
    ```

2.  **애플리케이션 실행**

    ```bash
    ./gradlew bootRun
    ```

애플리케이션이 시작되면 `http://localhost:8080`에서 API를 사용할 수 있습니다.
API 명세는 `http://localhost:8080/swagger-ui.html`에서 확인할 수 있습니다.

---

## 6. 기술 스택

- **Language**: `Java 21`
- **Framework**: `Spring Boot 3.5`
  - `Spring Web`, `Spring Data JPA`, `Spring Validation`, `Spring Security`
- **Persistence**:
  - `JPA / Hibernate`
  - `QueryDSL`: 동적 쿼리 및 복잡한 조회 쿼리 작성을 위해 사용
  - `PostgreSQL`: 운영 데이터베이스
  - `H2`: 테스트용 인메모리 데이터베이스
- **Build Tool**: `Gradle`
- **Utilities**:
  - `Lombok`: 보일러플레이트 코드 감소
  - `MapStruct`: DTO와 도메인 객체 간의 매핑 자동화
- **API Docs**: `SpringDoc OpenAPI` (Swagger UI)