# 소개 페이지 데이터 전환 가이드

## 목적

프론트엔드에 하드코딩된 소개 페이지 데이터를 DB로 옮기고, 이후 기수 변경 시 어드민 API로 안전하게 갱신하기 위한 절차를 정의한다.

소개 정보는 여러 기수를 누적 보관하는 구조가 아니라 **현재 노출할 데이터 한 건을 관리하는 구조**다. 따라서 34기 전환 시 기존 33기 소개 데이터를 백업한 뒤 현재 데이터를 갱신한다.

## 역할과 입력 방식

| 역할 | 담당 작업 |
| --- | --- |
| 기획/PM | 노출 문구, 집계 수치, 링크, 운영진·강연·활동 목록과 이미지 최종본 확정 |
| MANAGER 이상 어드민 | 확정된 데이터를 어드민의 소개 수정 화면에서 입력 |
| 백엔드 | 배포 및 DB 백업, 입력 후 공개·어드민 API와 `current_cardinal` 검증 |
| 프론트엔드 | 기존 constants를 공개 소개 API 응답으로 교체하고 화면을 확인 |

초기 데이터도 일회성 SQL이나 애플리케이션 시작 스크립트 대신 `PUT /admin/introductions`로 입력한다. 소개 데이터에는 이미지 업로드가 많고 운영 중에도 같은 화면에서 수정해야 하므로, 어드민 입력 경로를 단일 진입점으로 유지한다.

## API 계약

### 조회

| 용도 | Method | Endpoint | 인증 | 데이터가 없을 때 |
| --- | --- | --- | --- | --- |
| 공개 사이트 | `GET` | `/api/introductions` | 불필요 | HTTP `404`, code `404` |
| 어드민 | `GET` | `/admin/introductions` | MANAGER 이상 | HTTP `404`, code `404` |

공개 응답에는 `banner_cardinal`이 없고, 기수가 반영된 `banner_content`가 내려간다. 어드민 응답에는 편집을 위해 `banner_cardinal`이 포함되고, `banner_status`는 코드값(`CLOSE` 등)으로 내려간다.

이미지 필드는 모두 S3 전체 URL로 내려간다. DB에는 S3 키(`intro/<uuid>파일명`)만 저장하고 응답에서 `aws.s3.prefix`를 붙인다. 업로드하지 않은 이미지는 `null`이다.

목록 항목에는 수정용 `id`가 포함된다. 이미지 목록은 공개 응답에서 URL 배열(`partner_logo_urls`, `meetup_image_urls`, `sponsor_image_urls`)로, 어드민 응답에서 `{id, image_url}` 항목 배열(`partner_logos`, `meetup_images`, `sponsors`)로 내려간다.

메인 배너(문구·배경)는 프론트 컴포넌트로 구현하므로 슬로건과 배너 이미지는 API에서 관리하지 않는다. (`slogan`, `banner_image_url` 컬럼은 남아 있지만 매핑하지 않는다)

### 등록 및 수정

- Method: `PUT`
- Endpoint: `/admin/introductions`
- Content-Type: `multipart/form-data`
- 권한: MANAGER 이상
- 소개 데이터가 없으면 생성하고, 있으면 기존 한 건을 수정한다.
- 별도의 `POST /admin/introductions`는 사용하지 않는다.

필수 필드는 다음과 같다.

| 필드 | 형식 | 설명 |
| --- | --- | --- |
| `bannerCardinal` | number | 소개 배너에 표시할 기수 |
| `bannerStatus` | enum | `CLOSE`(모집 마감), `UPCOMING_RECRUIT`(모집 예정), `MANAGE_RECRUIT`(운영진 모집), `MEMBER_RECRUIT`(학회원 모집) |
| `memberCount` | number | 누적 학회원 수 |
| `projectCount` | number | 누적 프로젝트 수 |
| `universityCount` | number | 참여 대학 수 |
| `introYoutubeLink` | string | 소개 영상 URL |

이미지와 하위 목록은 선택 필드다.

| 구분 | 필드 |
| --- | --- |
| 단일 이미지 | `planningImage`, `designImage`, `frontendImage`, `backendImage` |
| 항목 목록 | `teams`, `expertLecture`, `obLecture`, `activities` |
| 이미지 목록 | `partnerLogos`, `meetupImages`, `sponsors` |

목록 항목은 어드민 조회 응답의 `id`로 기존 항목을 가리킨다.

| 목록 | 항목 필드 | 이미지 |
| --- | --- | --- |
| `teams[i]` | `id`, `name`, `description` | `imageFile`, `removeImage` |
| `expertLecture[i]` | `id`, `name`, `corporation`, `description` | `imageFile`, `removeImage` |
| `obLecture[i]` | `id`, `name`, `topic` | `imageFile`, `removeImage` |
| `activities[i]` | `id`, `name`, `description` | `imageFile1`, `imageFile2`, `removeImage1`, `removeImage2` |
| `partnerLogos[i]`, `meetupImages[i]`, `sponsors[i]` | `id` | `imageFile` |

수정 요청의 적용 규칙은 다음과 같다.

- 단일 이미지 파일을 생략하면 기존 URL을 유지한다.
- 하위 목록을 생략하면 기존 목록을 유지한다.
- 하위 목록을 전달하면 `id` 기준으로 맞춘다.
  - `id`가 있는 항목: 해당 항목을 수정한다.
  - `id`가 없는 항목: 새로 추가한다.
  - 요청에 없는 기존 항목: 삭제한다.
  - 노출 순서는 요청 순서를 따른다.
- 기존 항목의 이미지는 새 파일 → `removeImage=true`면 삭제 → 그 외에는 기존 이미지 유지 순으로 적용한다. 이미지를 바꾸지 않으면 파일을 다시 보낼 필요가 없다.
- 이미지 목록(`partnerLogos` 등)은 `id`가 있거나 새 파일이 있는 항목만 저장한다. 새 파일을 함께 보내면 해당 이미지를 교체한다.
- 완전히 빈 항목은 저장하지 않는다. (인덱스를 건너뛰면 중간 항목이 빈 값으로 채워지기 때문) 따라서 빈 항목 하나만 보내면(예: `sponsors[0].id=`) 해당 목록을 비울 수 있다.
- 내용(`id`, 설명, 파일 등)은 있는데 `name`이 없는 항목은 HTTP `400`을 반환한다.
- 이 소개의 해당 목록에 없는 `id`나 한 요청 안의 중복 `id`는 HTTP `400`을 반환한다. 소개를 처음 등록할 때는 `id`를 보낼 수 없다.
- `id`와 이름 검증은 업로드 전에 하므로, 이 검증으로 실패하면 S3에 파일이 남지 않는다. (업로드 이후 단계에서 실패하면 이미 올라간 파일은 남는다)
- 제거된 `slogan`, `bannerImageFile`을 보내도 무시한다.
- 최초 등록에서 생략한 선택 필드는 빈 값 또는 빈 목록으로 조회된다.
- 필수 필드가 누락되거나 enum 값이 잘못되면 HTTP `400`을 반환한다.

전체 34기 데이터 최초 입력에서는 33기 하위 데이터가 섞이지 않도록 모든 하위 목록을 함께 전달한다.

## 34기 전환 절차

### 1. 입력 자료 확정

기획/PM에게 다음 자료를 한 번에 전달받는다.

- 배너 기수·모집 상태 (배너 문구·디자인은 프론트에서 구현)
- 누적 학회원·프로젝트·대학 수
- 소개 영상 URL
- 파트별 이미지
- 운영진, 전문가 강연, OB 강연, 활동 목록
- 파트너사 로고, 밋업 이미지, 후원사 이미지
- 연락처 및 외부 채널 링크(현재 API 미지원, 아래 "현재 범위에서 남은 사항" 참고)

원본 파일명에는 항목과 순서를 표시해 어드민 입력 순서를 확인할 수 있게 한다.

### 2. 운영 데이터 백업

입력 직전 운영 DB를 백업한다. 최소한 다음 테이블은 복구 가능해야 한다.

- `introduction`
- `manage_team`
- `expert_lecture`
- `oblecture`
- `partner_logo`
- `intro_meetup_image`
- `activity`
- `sponsor`
- `contact`
- `current_cardinal`

실제 테이블명은 운영 DB 스키마에서 다시 확인한다. 자동 마이그레이션 도구가 아직 없으므로, 데이터 수정 시각과 작업자를 배포 기록에 남긴다.

### 3. 소개 데이터 입력

MANAGER 이상 계정으로 어드민에 로그인한 뒤 `PUT /admin/introductions`를 호출한다.

1. 먼저 `GET /admin/introductions`로 현재 데이터 존재 여부와 33기 데이터를 확인한다.
2. 확정된 필수 필드와 34기 이미지·목록 전체를 입력한다.
3. 성공 응답의 `code`가 `200`인지 확인한다.
4. 다시 어드민 조회를 호출하여 입력값과 이미지 URL을 대조한다.

### 4. 현재 활동 기수 변경

`bannerCardinal`과 `current_cardinal`은 서로 다른 값이다.

- `bannerCardinal`: 소개 배너 문구에만 사용한다.
- `current_cardinal`: 회원가입과 로그인 시 YB/OB 판별에 사용한다.

현재 기수는 OWNER 전용 API로 변경한다. DB를 직접 수정하지 않는다.

```http
PUT /admin/owner/current-cardinal
Content-Type: application/json

{"cardinal": 34}
```

- 다음 기수(+1) 또는 직전 기수(−1, 되돌리기)로만 변경할 수 있고, 같은 값은 변경 없이 성공한다.
- 변경 즉시 탈퇴 회원을 제외한 전체 회원의 YB/OB를 다시 계산한다. (가입 기수 >= 현재 기수: YB, 그 외: OB)
- 응답의 `changedToOb`, `changedToYb`로 바뀐 인원을 확인한다.
- 관리자 권한인 `authority`에는 영향을 주지 않는다.

변경 후 `GET /api/auth/current-cardinal`의 `data.currentCardinal`이 `34`인지 확인한다.

### 5. 공개 API와 화면 검증

`GET /api/introductions` 응답과 실제 소개 페이지를 함께 확인한다.

- 배너 상태와 문구
- 누적 수치 세 항목
- 소개 영상 링크
- 모든 이미지의 로딩 여부와 순서
- 운영진·강연·활동 목록의 내용과 순서
- 모바일·데스크톱 화면
- S3 이미지 공개 접근 여부

문제가 있으면 프론트 constants로 조용히 되돌리지 않고, 백업 데이터 복원 또는 어드민 API 재입력으로 DB 데이터를 바로잡는다.

## 프론트엔드 전환 체크리스트

- [ ] 소개 화면 진입 시 `GET /api/introductions` 호출
- [ ] 기존 배너 문구·수치·영상 URL constants 제거
- [ ] 운영진·강연·활동·파트너·밋업·후원사 constants 제거
- [ ] 응답의 snake_case 필드명 반영
- [ ] 로딩·오류·빈 데이터 상태 처리
- [ ] `404`일 때 임시 하드코딩 데이터를 노출하지 않고 준비 중 화면 또는 명시적인 오류 상태 표시
- [ ] 어드민 수정 화면은 `GET /admin/introductions`로 초기화
- [ ] 어드민 저장은 `POST`가 아닌 `PUT /admin/introductions` 사용
- [ ] 수정하지 않은 단일 이미지와 목록은 요청에서 생략
- [ ] 목록을 수정할 때는 남길 항목을 모두 전송하고, 기존 항목은 어드민 조회 응답의 `id`를 함께 전송 (이미지는 바꿀 때만 파일 전송)
- [ ] 이미지 URL은 응답 값을 그대로 `src`에 사용 (전체 URL)
- [ ] 메인 배너는 프론트 컴포넌트로 구현하고, 모집 상태 문구만 `banner_content` 사용
- [ ] `banner_cardinal`은 어드민 응답에만 있고 공개 응답에는 없음을 반영

## 현재 범위에서 남은 사항

`Contact` 엔티티는 존재하지만 repository, service, API 연결이 없다. 따라서 정관·이메일·Instagram·YouTube·카페·GitHub 링크는 현재 소개 API로 입력하거나 조회할 수 없다.

연락처 constants 제거는 다음 중 하나를 기획·프론트와 결정한 뒤 별도 작업한다.

1. 연락처 필드를 `IntroRequest`, `IntroResponse`에 포함해 소개 API에서 함께 관리한다.
2. 연락처 전용 어드민·공개 API를 만든다.

이 결정 전까지 연락처 영역은 프론트 하드코딩 대상으로 명시하고, 소개 API 전환 완료 범위에서 제외한다.

## 완료 기준

- [ ] 34기 확정 자료 수령 및 입력 담당자 지정 (2026-11-09 1차 수령, 학회 수치·운영팀 설명·주요 활동은 33기 값으로 임시 입력)
- [x] 운영 DB 백업 완료 (2026-11-09, Heroku 수동 백업)
- [x] 어드민 API로 34기 소개 데이터 입력 완료 (2026-11-09)
- [x] `current_cardinal`을 34로 설정 (2026-11-09, YB→OB 1명)
- [x] 공개·어드민·현재 기수 API 검증 완료 (2026-11-09, 이미지 61개 접근 확인)
- [ ] 프론트 소개 constants 교체 완료
- [ ] 연락처 데이터 관리 방식 결정
- [ ] 작업자·작업 시각·검증 결과 기록
