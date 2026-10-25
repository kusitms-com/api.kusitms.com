# Development Convention

프로젝트의 일관된 코드 작성과 원활한 협업을 위한 개발 컨벤션을 정의한다.
기존 코드와 컨벤션이 다른 경우 기능 변경과 분리하여 점진적으로 개선한다.

## 1. 디렉토리 구조

도메인 단위로 패키지를 나누고, 각 도메인 내부에서 역할별 하위 패키지를 사용한다.

```text
domain/{도메인명}/
├── controller/
├── service/
├── entity/
├── repository/
└── dto/
    ├── request/
    └── response/
```

- 도메인에서 사용하지 않는 역할의 디렉토리는 생략할 수 있다.
- Controller와 Service는 각각 `controller/`, `service/`에 배치한다.
- Entity와 Repository는 각각 `entity/`, `repository/`에 배치한다.
- Request와 Response DTO는 각각 `dto/request/`, `dto/response/`에 배치한다.
- 여러 도메인에서 공유하는 기능만 `global/`에 배치한다.

```text
global/
├── auth/
├── common/
├── config/
├── exception/
└── util/
```

## 2. 네이밍 규칙

| 역할 | 네이밍 패턴 | 예시 |
| --- | --- | --- |
| Controller | `{도메인명}Controller` | `AdminController` |
| Service | `{도메인명}Service` | `MeetupService` |
| 복합 도메인 Service | `{세부기능}{도메인명}Service` | `MemberAdminService` |
| Repository | `{엔티티명}Repository` | `MemberRepository` |
| Entity | 접두사·접미사가 없는 명사형 | `MeetupTeam` |
| Request DTO | `{기능}Request` | `MeetupRequest` |
| Response DTO | `{기능}Response` | `MeetupDetailResponse` |

- DTO 클래스명에는 `Dto` 접미사를 사용하지 않는다.
- 새로운 임시 Entity 또는 Repository에는 `TMP` 접두사를 사용하지 않는다.
- 임시 구현이 필요하면 `@Deprecated`와 제거 예정 시점을 주석으로 명시한다.
- 기존 `TMP*` 코드는 실제 엔티티 전환 작업에서 제거한다.

## 3. 코드 작성 규칙

### API 응답

모든 API는 `BaseResponse<T>`를 공통 응답 래퍼로 사용한다.

```json
{
  "code": 200,
  "message": "성공 메시지",
  "data": {}
}
```

### Controller

- `@RestController`를 사용한다.
- 생성자 주입은 `@RequiredArgsConstructor`를 사용한다.
- 공통 경로는 클래스의 `@RequestMapping`에 정의한다.
- Controller는 요청 처리와 응답 반환만 담당한다.
- 비즈니스 로직은 Service에 위임한다.
- 응답 타입은 가능한 경우 `BaseResponse<T>`의 제네릭을 명시한다.

### Swagger

| 어노테이션 | 작성 기준 |
| --- | --- |
| `@Tag` | Controller에 `name`, `description` 명시 |
| `@Operation` | API별 `summary`, `description` 명시 |
| `@ApiResponses` | 발생 가능한 400, 401, 403, 404 등 명시 |
| `@Hidden` | FE 연동 전 또는 테스트용 임시 API에만 사용 |

- 정식 API에는 `@Hidden`을 사용하지 않는다.
- API 동작 또는 요청·응답이 변경되면 Swagger도 함께 수정한다.

## 4. Git 규칙

### 브랜치

```text
타입/#이슈번호-작업내용
```

작업 내용은 영문 소문자와 하이픈을 사용한다.

```text
feature/#2-login
docs/#3-readme
refactor/#4-cleanup
fix/#5-bugfix
```

### 커밋

```text
<type>: <description>
```

| 타입 | 용도 |
| --- | --- |
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `docs` | 문서 변경 |
| `test` | 테스트 변경 |
| `style` | 코드 형식 변경 |
| `refactor` | 동작을 바꾸지 않는 리팩토링 |
| `perf` | 성능 개선 |
| `build` | 빌드 변경 |
| `ci` | CI/CD 변경 |
| `chore` | 기타 변경 |

## 5. PR 및 리뷰

- 하나의 PR은 하나의 이슈 또는 명확한 작업 목적을 중심으로 작성한다.
- 기능 변경과 무관한 리팩토링을 혼합하지 않는다.
- 작업 배경, 주요 변경 사항, 완료 조건과 테스트 결과를 PR에 기록한다.
- API, DB 또는 설정 변경이 있으면 영향 범위를 명시한다.
- 리뷰 요청 전에 작성자가 전체 diff를 확인한다.
- 최소 한 명 이상의 승인을 받고 CI가 통과한 뒤 병합한다.
- `main` 브랜치에 직접 커밋하지 않는다.

리뷰 코멘트는 다음 접두사로 중요도를 구분한다.

| 접두사 | 의미 |
| --- | --- |
| `[MUST]` | 기능 오류, 보안, 데이터 정합성 등 필수 수정 |
| `[SUGGEST]` | 구조, 가독성, 유지보수성 개선 제안 |
| `[QUESTION]` | 구현 의도나 설계 판단에 관한 질문 |
| `[NIT]` | 선택적으로 반영할 사소한 의견 |
