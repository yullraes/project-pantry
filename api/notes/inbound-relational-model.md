# 인바운드 논리 데이터 모델

## 범위와 결정

이 문서는 `receipt-putaway-data-propositions.md`의 명제를 관계형 데이터베이스의 릴레이션으로 확정한다.

- 한 번의 입하는 `공급사 + 송장 번호`로 식별한다.
- 별도의 입고 릴레이션이나 입고 상태는 두지 않는다.
- 최초 인수 보고와 그 정정은 서로 다른 요청으로 접수한다.
- 최초 적재 완료 보고와 그 정정도 서로 다른 요청으로 접수한다.
- 접수된 보고 판과 그 항목은 추가 전용으로 저장한다.
- 각 정정 판은 전체 보고 내용의 스냅숏이다.
- 적재 완료 보고는 자신이 기준으로 삼은 정확한 인수 보고 판을 가리킨다.
- 한 인수 보고 항목을 여러 위치에 나누어 적재할 수 있다.

정정 요청의 API 형식과 사용 사례 흐름은 이 문서에서 다루지 않는다.

## 관계 개요

```text
suppliers
    1
    |
    N
receipt_reports
    1
    |
    N
receipt_report_revisions
    1
    |
    N
receipt_report_items
    1 <------------------------ N putaway_report_items
                                      N
                                      |
                                      1
                             putaway_report_revisions
                                      N
                                      |
                                      1
                               receipt_reports
```

`receipt_reports`는 물리적인 입고 과정을 나타내지 않는다. 공급사와 송장 번호로 식별되는 하나의 논리적 인수 보고를 나타내며, 중복 최초 보고를 막고 여러 정정 판을 묶는 역할만 한다.

적재 완료 보고는 하나의 논리적 인수 보고에 대해 최대 하나만 존재하므로 별도의 논리적 적재 보고 릴레이션을 두지 않는다. `receipt_report_id`가 적재 완료 보고 판들을 묶는 식별자가 된다.

## `receipt_reports`

술어:

> `id`로 식별되는 논리적 인수 보고는 공급사 `supplier_id`가 발행한 송장 번호 `invoice_number`에 대한 보고다.

| 열 | 형식 | NULL | 의미 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | 불가 | 논리적 인수 보고 식별자 |
| `supplier_id` | `BIGINT` | 불가 | 공급사 식별자 |
| `invoice_number` | `VARCHAR(100)` | 불가 | 해당 공급사가 발행한 송장 번호 |

키와 제약:

- 기본 키: `id`
- 후보 키: `(supplier_id, invoice_number)`
- `supplier_id`는 `suppliers(id)`를 참조한다.
- `invoice_number`는 앞뒤 공백을 제거한 정규화된 값으로 저장하며 빈 문자열을 허용하지 않는다.
- 행은 생성 후 수정하거나 삭제하지 않는다.

`(supplier_id, invoice_number)` 유일 제약이 같은 입하에 대한 중복 최초 인수 보고를 차단한다.

## `receipt_report_revisions`

술어:

> 담당자 `reported_by`가 시각 `reported_at`에 논리적 인수 보고 `receipt_report_id`의 `revision_no`번째 판 `id`를 보고했으며, 이 판은 `previous_revision_id` 판을 정정한다.

| 열 | 형식 | NULL | 의미 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | 불가 | 접수된 인수 보고 판 식별자 |
| `receipt_report_id` | `BIGINT` | 불가 | 논리적 인수 보고 식별자 |
| `revision_no` | `INTEGER` | 불가 | 1부터 시작하는 판 번호 |
| `previous_revision_id` | `BIGINT` | 가능 | 바로 이전 인수 보고 판 |
| `reported_by` | `VARCHAR(100)` | 불가 | 보고 담당자 식별값 |
| `reported_at` | `TIMESTAMPTZ` | 불가 | 보고가 접수된 시각 |

키와 제약:

- 기본 키: `id`
- 후보 키: `(receipt_report_id, revision_no)`
- `receipt_report_id`는 `receipt_reports(id)`를 참조한다.
- `previous_revision_id`는 `receipt_report_revisions(id)`를 참조하며 유일하다.
- `revision_no >= 1`이어야 한다.
- 1판은 `previous_revision_id`가 없어야 한다.
- 2판부터는 같은 논리적 보고의 바로 앞 판을 `previous_revision_id`로 가져야 한다.
- 행은 생성 후 수정하거나 삭제하지 않는다.

마지막 두 선후 관계는 다른 행의 값을 확인해야 하므로 정정 접수 트랜잭션에서 검증한다. 정정 시 `receipt_reports` 행을 잠근 뒤 현재 판을 확인하고 새 판을 추가하여 동시 정정으로 판이 갈라지는 것을 막는다. `previous_revision_id`의 유일 제약은 같은 이전 판을 두 번 정정하는 것도 차단한다.

## `receipt_report_items`

술어:

> 인수 보고 판 `receipt_report_revision_id`에는 발주 항목 `purchase_order_line_id`에 관하여 송장 수량, 도착 수량, 검사 수량, 인수 수량, 반송 결정 수량, 검사 결과와 판단 근거가 이러하다고 보고한 항목 `id`가 포함되어 있다.

| 열 | 형식 | NULL | 의미 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | 불가 | 해당 판에 포함된 인수 보고 항목 식별자 |
| `receipt_report_revision_id` | `BIGINT` | 불가 | 항목을 포함한 인수 보고 판 |
| `purchase_order_line_id` | `BIGINT` | 불가 | 항목 구분의 기준이 된 발주 항목 |
| `invoiced_quantity` | `NUMERIC(19,5)` | 불가 | 송장 기재 수량 |
| `arrived_quantity` | `NUMERIC(19,5)` | 불가 | 실제 도착했다고 보고한 수량 |
| `inspected_quantity` | `NUMERIC(19,5)` | 불가 | 검사했다고 보고한 수량 |
| `accepted_quantity` | `NUMERIC(19,5)` | 불가 | 인수하기로 결정했다고 보고한 수량 |
| `return_decided_quantity` | `NUMERIC(19,5)` | 불가 | 반송하기로 결정했다고 보고한 수량 |
| `uom` | `VARCHAR(20)` | 불가 | 이 항목의 모든 수량에 적용되는 비교 단위 |
| `inspection_result` | `VARCHAR(50)` | 불가 | 검사에서 확인했다고 보고한 품질 상태 |
| `inspection_note` | `VARCHAR(500)` | 가능 | 검사 결과에 대한 구체적인 근거 |
| `decision_reason` | `VARCHAR(500)` | 가능 | 인수·반송 결정의 사유 |

키와 제약:

- 기본 키: `id`
- 후보 키: `(receipt_report_revision_id, purchase_order_line_id)`
- `receipt_report_revision_id`는 `receipt_report_revisions(id)`를 참조한다.
- `purchase_order_line_id`는 `purchase_order_lines(id)`를 참조한다.
- 모든 수량은 0 이상이어야 한다.
- `arrived_quantity = accepted_quantity + return_decided_quantity`여야 한다.
- 현재의 전체 검수 정책에서는 `inspected_quantity = arrived_quantity`여야 한다.
- `uom`은 발주 항목의 단위와 비교 가능한 단위여야 한다.
- 행은 생성 후 수정하거나 삭제하지 않는다.

상품 식별자는 저장하지 않는다. 현재 모델에서 발주 항목이 상품을 결정하므로 `purchase_order_line_id`를 통해 알 수 있으며, 함께 저장하면 같은 사실을 중복해서 보관하게 된다.

상황에 따라 어떤 근거를 필수로 요구할지는 애플리케이션 검증에서 결정한다. 예를 들어 반송 결정 수량이 0보다 큰 경우 `decision_reason`을 필수로 요구할 수 있으나, 이 조건은 아직 확정하지 않는다.

## `putaway_report_revisions`

술어:

> 담당자 `reported_by`가 시각 `reported_at`에 논리적 인수 보고 `receipt_report_id`의 인수 물품 전체에 대한 `revision_no`번째 적재 완료 보고 판 `id`를 보고했으며, 이 판은 인수 보고 판 `basis_receipt_revision_id`를 기준으로 하고 `previous_revision_id` 판을 정정한다.

| 열 | 형식 | NULL | 의미 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | 불가 | 접수된 적재 완료 보고 판 식별자 |
| `receipt_report_id` | `BIGINT` | 불가 | 대상 논리적 인수 보고 식별자 |
| `revision_no` | `INTEGER` | 불가 | 1부터 시작하는 적재 완료 보고 판 번호 |
| `previous_revision_id` | `BIGINT` | 가능 | 바로 이전 적재 완료 보고 판 |
| `basis_receipt_revision_id` | `BIGINT` | 불가 | 접수 시 검증 기준이 된 정확한 인수 보고 판 |
| `reported_by` | `VARCHAR(100)` | 불가 | 보고 담당자 식별값 |
| `reported_at` | `TIMESTAMPTZ` | 불가 | 보고가 접수된 시각 |

키와 제약:

- 기본 키: `id`
- 후보 키: `(receipt_report_id, revision_no)`
- `receipt_report_id`는 `receipt_reports(id)`를 참조한다.
- `previous_revision_id`는 `putaway_report_revisions(id)`를 참조하며 유일하다.
- `basis_receipt_revision_id`는 `receipt_report_revisions(id)`를 참조한다.
- `revision_no >= 1`이어야 한다.
- 1판은 `previous_revision_id`가 없어야 한다.
- 2판부터는 같은 논리적 인수 보고에 속한 적재 완료 보고의 바로 앞 판을 가리켜야 한다.
- 기준 인수 보고 판은 `receipt_report_id`가 나타내는 논리적 인수 보고에 속해야 한다.
- 행은 생성 후 수정하거나 삭제하지 않는다.

마지막 세 관계는 적재 완료 보고 또는 정정 보고를 접수하는 트랜잭션에서 검증한다.

## `putaway_report_items`

술어:

> 적재 완료 보고 판 `putaway_report_revision_id`에서 인수 보고 항목 `receipt_report_item_id`의 물품을 위치 `location_id`에 수량 `quantity`, 단위 `uom`만큼 실제 적재했다고 보고한 항목 `id`가 포함되어 있다.

| 열 | 형식 | NULL | 의미 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | 불가 | 해당 판에 포함된 적재 완료 보고 항목 식별자 |
| `putaway_report_revision_id` | `BIGINT` | 불가 | 항목을 포함한 적재 완료 보고 판 |
| `receipt_report_item_id` | `BIGINT` | 불가 | 적재 대상 인수 보고 항목 |
| `location_id` | `BIGINT` | 불가 | 실제 적재했다고 보고한 위치 |
| `quantity` | `NUMERIC(19,5)` | 불가 | 해당 위치에 적재했다고 보고한 수량 |
| `uom` | `VARCHAR(20)` | 불가 | 적재 수량의 단위 |

키와 제약:

- 기본 키: `id`
- 후보 키: `(putaway_report_revision_id, receipt_report_item_id, location_id)`
- `putaway_report_revision_id`는 `putaway_report_revisions(id)`를 참조한다.
- `receipt_report_item_id`는 `receipt_report_items(id)`를 참조한다.
- `location_id`는 `locations(id)`를 참조한다.
- `quantity > 0`이어야 한다.
- 참조하는 인수 보고 항목은 적재 완료 보고 판의 `basis_receipt_revision_id`에 포함되어 있어야 한다.
- `uom`은 참조하는 인수 보고 항목의 단위와 같아야 한다.
- 행은 생성 후 수정하거나 삭제하지 않는다.

하나의 인수 보고 항목을 여러 위치에 나누어 적재하면 위치마다 한 행을 저장한다. 같은 보고 판에서 같은 인수 보고 항목과 같은 위치의 수량을 여러 행으로 나누지는 않는다.

## 보고 전체에 적용되는 무결성 규칙

다음 규칙은 여러 행을 집계해야 하므로 단일 행의 검사 제약만으로 보장할 수 없다. 보고 접수 트랜잭션에서 모두 검증한다.

### 인수 보고

- 보고에는 하나 이상의 인수 보고 항목이 있어야 한다.
- 모든 발주 항목은 보고의 공급사와 일치해야 한다.
- 한 판에는 같은 발주 항목이 한 번만 포함되어야 한다.
- 머리와 모든 항목은 한 트랜잭션에서 함께 추가한다.

### 적재 완료 보고

- 적재 완료 보고에는 하나 이상의 적재 완료 보고 항목이 있어야 한다.
- 기준 인수 보고 판에서 `accepted_quantity > 0`인 모든 항목이 빠짐없이 포함되어야 한다.
- 기준 인수 보고 판에서 `accepted_quantity = 0`인 항목은 포함하지 않는다.
- 각 인수 보고 항목별 적재 수량의 합이 해당 항목의 `accepted_quantity`와 같아야 한다.
- 모든 위치는 접수 시점에 유효해야 한다.
- 보고 판과 모든 항목의 추가, 재고 반영은 한 트랜잭션에서 함께 성공하거나 함께 실패한다.

모든 인수 보고 항목의 `accepted_quantity`가 0인 전량 반송 건에는 적재할 물품이 없으므로 적재 완료 보고를 만들지 않는다. 전량 반송 건의 최종 종료는 실제 반송 업무의 후속 범위로 남긴다.

적재 완료 후 인수 보고를 정정하여 인수 항목이나 수량이 바뀌는 경우에는 현재 적재 완료 보고와 재고가 정정 결과에 맞는지도 같은 트랜잭션 안에서 확인한다. 구체적인 정정 요청 형식은 후속 설계에서 정한다.

## 현재 유효한 판

현재 유효한 보고 판을 나타내는 별도 상태 열은 두지 않는다. 같은 논리적 보고에서 가장 큰 `revision_no`를 가진 행이 현재 판이다.

```sql
select r.*
from receipt_report_revisions r
where r.receipt_report_id = :receiptReportId
order by r.revision_no desc
limit 1;
```

적재 완료 보고도 같은 방식으로 현재 판을 찾는다. 조회가 빈번해지면 `(receipt_report_id, revision_no desc)` 인덱스를 사용한다. 현재 판 식별자를 별도 열로 중복 저장하지 않는다.

## 삭제와 갱신 방지

- 모든 외래 키의 삭제 동작은 `RESTRICT` 또는 `NO ACTION`으로 둔다.
- 애플리케이션에는 보고 판과 항목을 갱신하거나 삭제하는 저장소 기능을 제공하지 않는다.
- 운영 데이터베이스 권한을 분리할 수 있다면 보고 관련 릴레이션에는 `SELECT`, `INSERT`만 허용한다.
- 정정은 항상 새 보고 판과 새 항목을 `INSERT`한다.

이는 보고 기록에 한정된 추가 전용 정책이다. 상품, 공급사, 위치, 발주, 현재고 등 다른 릴레이션에 같은 정책을 강제하지 않는다.

## 인바운드가 재고 관리에 제공할 근거

적재 완료 보고의 접수와 재고 반영은 하나의 사용 사례에서 처리하지만 두 컨텍스트의 데이터 소유권은 구분한다.

인바운드가 재고 관리에 제공하는 값은 다음과 같다.

```text
적재 완료 보고 판 ID
적재 완료 보고 항목 ID
상품 ID
위치 ID
수량과 단위
```

상품 ID는 적재 완료 보고 항목이 참조하는 인수 보고 항목의 발주 항목에서 얻는다. 재고 관리가 생성한 재고 데이터의 구체적인 릴레이션은 인바운드 모델에 포함하지 않는다.

## 이 모델에서 만들지 않는 릴레이션

- 입고 또는 입하 상태
- 적재 중 상태
- 적재 작업과 작업 지시
- 추천 적재 위치
- 실제 반송 완료
- 재고 실사와 재고 조정

이 개념들은 현재 인바운드가 저장하기로 한 명제를 나타내지 않거나 다른 바운디드 컨텍스트의 책임이다.
