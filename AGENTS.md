# Quest 504 협업 지침

## 목적과 범위
- 2026.10.22~10.27 Java/Spring Boot 기본기를 복습하는 소규모 풀스택 숫자야구 게임.
- 개발자는 React/TypeScript 경험이 있지만 Java/Spring Boot는 재학습 중이다. 숙련자로 가정하지 않는다.
- 학습과 데이터 흐름 이해가 우선이다. 요청받은 한 단계에 집중하고 전체 게임을 자동 구현하지 않는다.

## AI 협업
- 구현 전에 설계 방향과 순서를 설명한다. 새 개념은 간단한 예제와 적용 위치를 설명한다.
- 개발자 코드를 먼저 검토하고 오류 원인과 수정 방법을 안내한다. 대신 작성한 코드에는 핵심 이유를 설명한다.
- 매 작업 후 변경 파일, 내용, 실제 테스트 결과, 남은 작업을 요약한다. 개발자 코드와 AI 코드를 구분한다.
- 실행하지 않은 테스트를 통과했다고 말하지 않는다. 불필요한 파일, 리팩토링, 라이브러리 도입을 피한다.
- 비밀번호/토큰/실제 비밀값을 코드와 Git에 포함하지 않는다.
- 외부 폴더 수정, 원격 저장소 생성/푸시, 불필요한 패키지 설치는 사전 확인한다.
- 사용자가 요청하기 전 컨테이너 실행, 배포, 다음 기능 구현을 진행하지 않는다.

## 기술스택
- Java 21, Spring Boot 3.5.x, Gradle, PostgreSQL, Spring Data JPA/Hibernate.
- Spring Web, Validation, JUnit 5/Mockito/Spring Boot Test. Spring Security는 후속 단계.
- Docker Compose는 후속 로컬 DB 단계. React/TypeScript/Vite는 후순위이며 아직 생성하지 않았다.
- 기본 패키지: com.example.quest504. 실제 버전과 환경 제약은 README 및 docs/WORKLOG.md 참고.
- 화면은 텍스트/숫자/버튼/기록 테이블 중심. 이미지/일러스트/복잡한 애니메이션 제외.

## 핵심 규칙과 설계
- 서버가 1~9 중 서로 다른 숫자 3개를 생성한다. 비밀 숫자를 API에 노출하지 않는다.
- 자리와 숫자가 같으면 S, 숫자만 같으면 B. 3S 0B이면 정답. 시도 횟수 제한 없음.
- GameSession: IN_PROGRESS → SOLVED → REWARDED. 새 게임에는 새 정답을 생성한다.
- 보상 요청 시 소유자/완료/중복 지급을 검증한다. 트랜잭션, 동시성 제어, DB 제약으로 게임당 1회 지급을 보장한다.
- Player, GameSession, GuessHistory, RewardHistory를 중심으로 설계한다. 완료와 보상 이력은 PostgreSQL에 저장한다.
- 판정 로직은 Spring과 분리한 순수 Java 클래스. DTO와 Entity를 분리하고 Controller → Service → Repository → DB 흐름을 학습한다.
- NPC는 설정이며 실제 계정이 아니다. 범용 Quest Entity/퍼즐 엔진/관리자 화면은 현재 범위 밖이다.
