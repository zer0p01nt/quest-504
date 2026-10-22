# 작업 기록

## 2026-10-22 — 초기 구조 준비 (AI 작성)

### 범위와 변경

- 현재 위치가 이미 프로젝트 루트임을 확인했다. 중첩 `quest-504` 폴더를 만들지 않았다.
- `backend/`, `frontend/`, `docs/`, README, AGENTS, .gitignore 생성.
- Java 21/Boot 3.5.16/Gradle Groovy 빌드 선언, Spring 진입점, 기본 컨텍스트 테스트 작성.
- 기본 테스트 전용 프로필에서 DB 자동 설정을 제외했다. 앱의 일반 DB 설정은 변경하지 않았다.
- 게임/Entity/API/보상/Security/React/Compose 구현 및 실행은 하지 않았다.
- 기존 개발자 소스는 발견되지 않았다. 이번 생성 파일은 모두 AI 작성이며 기존 파일 덮어쓰기는 없다.

### 실제 실행한 확인과 결과

| 명령 | 결과 |
| --- | --- |
| `pwd`, `ls -la`, `rg --files` | 프로젝트 루트 확인. 기존에는 숨김 환경 디렉터리만 존재 |
| `java -version`, `javac -version` | command not found |
| `gradle --version` | command not found |
| `git --version` | 2.43.0 |
| `docker --version` | 29.5.3 (build d1c06ef) |
| `docker compose version` | v5.1.4 |
| `docker info --format '{{.ServerVersion}}'` | Docker 소켓 permission denied. 서버 버전 확인 불가 |
| `git status --short --branch` | not a git repository. 기존 .git는 변경하지 않음 |
| `java.exe -version`, Windows JDK의 `javac.exe -version` | WSL UtilBindVsockAnyPort socket failed. Windows Java 버전 미확인 |
| PowerShell 경유 Initializr 조회 | 같은 WSL 소켓 오류로 실행 불가 |
| `curl -fsSL --connect-timeout 10 --max-time 30 https://start.spring.io/metadata/client -o /tmp/quest504-initializr-metadata.json` | exit 6, Could not resolve host |
| `gradle -p backend test` | command not found. 테스트 실행 전 실패 |
| `gradle -p backend build` | command not found. 컴파일/빌드 실행 전 실패 |
| 파일 구조, 패키지, 의존성 선언, 비밀값 유무 정적 점검 | 생성 파일 정적 확인만 수행. Java 컴파일/테스트를 대체하지 않음 |

웹 도구로 공식 Spring Boot 3.5 요구사항과 Gradle 릴리스를 확인했다. 웹 접근 성공은 셸의 의존성 다운로드 가능 여부를 보장하지 않는다.

### 미완료 및 다음 작업

1. Java 21을 실행할 개발 환경(Windows 또는 WSL)을 정한다. 설치/환경 수정은 이번에 수행하지 않았다.
2. 네트워크 정상 환경에서 Initializr 산출물을 생성해 비교하고 공식 Gradle Wrapper를 추가한다. 현재 Initializr 직접 생성 요구는 미완료다.
3. Wrapper를 통해 `test`, `build`를 실제 실행한다. 현재 컴파일/테스트 통과 여부와 라이브러리 해결은 검증하지 못했다.
4. 개발자가 ROADMAP의 순수 Java 숫자야구 판정 실습을 직접 작성한다.
5. 이후 요청에 따라 PostgreSQL Compose와 DB 연결, Git 저장소 상태를 정리한다.

## 2026-10-22 — 사용자 제공 Initializr Wrapper 반영

- 개발자 작업: Initializr에서 Boot 4.0.8/Java 21/Gradle 프로젝트를 생성해 `_initializr/quest504/`에 압축 해제했다.
- AI 작업: 생성본과 기존 backend를 비교하고 `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`를 backend에 추가했다.
- 생성본은 Gradle 9.8.2였으므로 Boot 3.5의 공식 Gradle 8.x 지원 범위에 맞춰 배포 URL을 8.14.6으로 변경했다. 스크립트/JAR는 Initializr 원본과 동일하며 Gradle 8.14.6으로 재생성한 산출물은 아니다. 실제 부트스트랩 실행은 아래 환경 제약으로 검증하지 못했다.
- 기존 Boot 3.5.16 빌드와 Java 코드/테스트를 유지했다. 4.x의 webmvc 및 개별 test starter는 가져오지 않았다.
- `.gitignore`에 `/_initializr/`를 추가하고 README의 잘못된 3.x UI 선택 안내와 Wrapper 미완료 상태를 수정했다. 비교용 원본은 삭제하지 않았다.
- `sh -n backend/gradlew`: 성공 (셸 문법 확인).
- Python 정적 검증: 스크립트/JAR의 원본 바이트 일치, JAR CRC 및 진입 클래스, Gradle URL과 Boot 선언 확인 성공.
- backend에서 `./gradlew --version`, `./gradlew test`, `./gradlew build`: 모두 exit 1. `JAVA_HOME is not set and no 'java' command could be found`로 JVM 실행 전에 실패했다. 컴파일/테스트 통과를 확인하지 못했다.
- 루트에서 처음 실행한 `./gradlew --version`은 파일 경로 오류였다. 이후 backend에서 다시 실행해 위 Java 오류를 확인했다.
- 앞선 미완료 항목 중 Initializr 생성본 비교와 Wrapper 추가는 완료했다. Java 환경과 실제 빌드 검증은 남아 있다.
- 다음 확인: 개발자의 Windows PowerShell에서 backend로 이동해 `java -version`, `javac -version`, `.\gradlew.bat --version`, `.\gradlew.bat test`, `.\gradlew.bat build` 실행. 이번 작업에서 설치/외부 폴더 수정/컨테이너 실행/게임 기능 구현은 하지 않았다.

## 2026-10-22 — 개발자 Windows 실행 검증 완료

- 근거: 개발자가 대화에 제공한 Windows PowerShell 실제 실행 출력. AI가 직접 실행하거나 재검증한 결과가 아니다.
- 실행 위치: 저장소의 `backend/` 디렉터리.
- `java -version`: Oracle Java 21.0.9 LTS. `javac -version`: 21.0.9.
- `.\gradlew.bat --version`: Gradle 8.14.6 다운로드 및 시작 성공, Launcher JVM 21.0.9, Daemon JVM은 Windows jdk-21.
- `.\gradlew.bat test`: BUILD SUCCESSFUL in 1m, 5 tasks executed.
- `.\gradlew.bat build`: BUILD SUCCESSFUL in 5s, 3 tasks executed / 5 up-to-date.
- 테스트 중 클래스 공유 관련 JVM warning이 출력됐으나 테스트/빌드는 성공했다.
- Java 환경, Wrapper 부트스트랩, 기본 테스트와 빌드 확인 항목은 Windows 환경에서 완료됐다. 이전 WSL 실패 기록은 해당 환경의 이력으로 남긴다.
- AI 변경 파일: README.md, docs/WORKLOG.md. 검증 상태만 갱신했으며 기능 코드는 변경하지 않았다. 이번 문서 변경을 위해 테스트를 재실행하지 않았다.
- 남은 작업: 순수 Java 판정 실습을 개발자가 직접 작성. PostgreSQL/서버 실행/DB 통합 테스트/Git 상태 정리는 후속 요청에서 진행한다.
