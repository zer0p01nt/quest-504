# Quest 504 작업 지침

## 프로젝트

- React/TypeScript + Spring Boot 기반 숫자야구 게임.
- 프론트엔드: `frontend/`, React, TypeScript, Vite, Axios, CSS.
- 백엔드: `backend/`, Java 21, Spring Boot 3.5.x, Spring Security, Spring Data JPA, PostgreSQL 17.
- 기본 패키지: `com.example.quest504`. 의존성 버전은 각 빌드 파일을 기준으로 한다.
- 실행 방법과 전체 구조는 `README.md`, CI 검증 절차는 `.github/workflows/ci.yml`을 참고한다.

## 코드 수정

- 기존 코드와 작업 중인 변경을 먼저 확인한다. 요청과 무관한 수정이나 되돌리기를 하지 않는다.
- 변경 방향을 간단히 설명하고 필요한 범위만 수정한다. 새 의존성은 꼭 필요한 경우에만 추가한다.
- 백엔드는 Controller → Service → Repository 구조와 DTO/Entity 분리를 유지한다.
- 숫자 생성·판정은 `domain/baseball/`의 순수 Java 로직으로 유지한다.
- 프론트엔드 API 호출은 `frontend/src/api/`에 두고 기존 Axios 클라이언트를 사용한다.
- 세션 인증과 CSRF 처리를 유지한다. 오류를 피하려고 인증이나 검증을 비활성화하지 않는다.

## 게임·보상 규칙

- 정답은 서버에서 생성한 1~9의 중복 없는 세 자리 숫자이며 API에 노출하지 않는다.
- 숫자와 자리가 같으면 S, 숫자만 같으면 B. 3S이면 정답이며 시도 횟수 제한은 없다.
- 게임 상태는 `PLAYING → SOLVED`. 보상 수령 여부는 `RewardHistory`로 관리한다.
- 새 게임은 새 정답을 생성하며, 게임 접근 시 로그인한 사용자의 소유권을 확인한다.
- 완료한 게임마다 100 GOLD를 한 번 지급한다. 소유자·완료·중복 수령 검증을 유지한다.
- 보상의 트랜잭션, Player 비관적 잠금, 게임별 보상 유니크 제약을 함께 유지한다.
- 현재 엔티티는 `Player`, `Game`, `RewardHistory`다. 시도 기록은 프론트엔드의 현재 라운드 상태로 관리한다.

## 검증

- 프론트엔드 변경: `frontend/`에서 `npm run lint`, `npm run build`.
- 백엔드 변경: `backend/`에서 `./gradlew test` 또는 PowerShell의 `.\gradlew.bat test`.
- DB 연동 테스트는 운영 DB와 분리한 `quest504_test`를 사용한다. 테스트 설정을 확인하고 실행한다.
- 문서만 수정했다면 링크·경로·명령과 `git diff --check`를 확인한다.
- 완료 시 변경 파일과 내용, 실제 검증 결과, 남은 작업을 짧게 알린다. 실행하지 않은 검증을 통과했다고 쓰지 않는다.

## 환경·데이터

- 비밀번호, 토큰, 실제 DB 접속 정보는 코드·문서·Git에 포함하지 않는다. 설정 예시는 `.env.example`을 사용한다.
- `.env` 내용이나 비밀값이 포함된 명령 출력을 노출하지 않는다.
- 요청 없이 배포, 원격 푸시, 컨테이너 실행, DB 초기화·볼륨 삭제를 진행하지 않는다.
- 운영 프로필의 HTTPS 세션 쿠키와 스키마 검증 설정을 유지한다. 운영 DB 변경은 별도로 검토한다.
