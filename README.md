# Quest 504

Java/Spring Boot 기본기를 직접 복습하기 위한 숫자야구 퀘스트 프로젝트다.
개발 기간은 2026.10.22~10.27이며, 기능 수보다 Java 객체와 요청 → 서비스 → DB 흐름의 이해를 우선한다.
현재는 초기 폴더, 빌드 선언, 진입점과 기본 테스트만 준비했다. 게임 기능은 없다.

## 구조

```text
quest-504/
├── AGENTS.md
├── README.md
├── .gitignore
├── backend/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── gradle/wrapper/
│   └── src/
│       ├── main/java/com/example/quest504/Quest504Application.java
│       ├── main/resources/application.properties
│       ├── test/java/com/example/quest504/Quest504ApplicationTests.java
│       └── test/resources/application-test.properties
├── frontend/README.md
└── docs/
    ├── ROADMAP.md
    └── WORKLOG.md
```

## 버전과 의존성

- Java toolchain: 21
- Spring Boot: 3.5.16 (요청한 3.5.x 유지)
- Gradle: 8.14.6 (Wrapper 설정). 사용자가 제공한 Initializr Wrapper 파일을 가져오고 배포 버전을 조정했다.
- Spring dependency-management Gradle plugin: 1.1.7
- Web, Data JPA, Validation, PostgreSQL Driver, Boot Test(JUnit 5/Mockito), JUnit Platform launcher
- 라이브러리 버전은 Spring Boot BOM에 맡긴다. 개발자가 Windows에서 의존성 해결, 기본 테스트 및 빌드 성공을 확인했다.
- Security, Docker Compose, React는 후속 단계다. PostgreSQL 서버 버전도 해당 단계에서 확정한다.

[Spring Boot 3.5 공식 요구사항](https://docs.spring.io/spring-boot/3.5/system-requirements.html)은 Java 21 및 Gradle 8.x 사용을 지원한다.
[Gradle 8.14.6 릴리스](https://docs.gradle.org/8.14.6/release-notes.html)를 참고했다.

## 준비 상태와 실행 방법

사용자가 Spring Initializr에서 생성한 Boot 4.0.8 프로젝트를 `_initializr/quest504/`에 제공했다.
해당 프로젝트의 공식 Wrapper 스크립트/JAR/설정 파일을 `backend/`로 가져왔다.
생성본의 Gradle 9.8.2 대신 Boot 3.5가 지원하는 8.x인 8.14.6으로 `distributionUrl`을 조정했다.
기존 AI 작성 Boot 3.5.16 빌드/진입점/테스트는 유지했다. Initializr의 4.x 의존성은 복사하지 않았다.
Initializr UI에는 현재 3.x가 제공되지 않으므로 3.5.x 선택 안내는 철회한다.
`_initializr/` 원본은 비교용으로 보존하고 Git 추적에서는 제외한다.

네트워크와 Java 21이 정상인 개발자 환경에서 다음 순서로 확인한다.

1. `java -version`과 `javac -version`이 모두 21인지 확인한다. Windows JDK는 Windows 터미널에서 사용한다. WSL Gradle에는 Linux용 JDK가 필요하다.
2. `backend/`에서 Windows PowerShell은 `.\gradlew.bat --version`, WSL/Linux는 `./gradlew --version`을 실행해 Gradle 8.14.6과 JVM 21을 확인한다.
3. Windows는 `.\gradlew.bat test`, WSL/Linux는 `./gradlew test`를 실행한다. 빌드는 각각 `.\gradlew.bat build`, `./gradlew build`다. 첫 실행에는 네트워크와 Gradle 캐시 쓰기 권한이 필요하다. 시스템 Gradle을 따로 설치할 필요는 없다.

`@SpringBootApplication`은 Spring 앱의 시작점이고, `main`이 서버 실행을 시작한다.
`@SpringBootTest`는 Spring 설정을 실제로 로드하는 기본 확인이다. 초기 테스트는 `test` 프로필에서 DB/JPA 자동 설정을 제외하므로 PostgreSQL 없이 실행할 수 있다. DB 통합 테스트가 아니며, 추후 DB 학습 시 별도 통합 테스트와 설정을 추가한다.
일반 실행(`bootRun`)은 JPA가 DB 연결을 요구하므로 PostgreSQL 설정 전에는 실패할 수 있다. 운영 설정에서 DB 자동 설정을 끄거나 임시 H2를 추가하지 않았다.

## 검증 결과 (2026-10-22)

개발자가 Windows PowerShell의 `backend/`에서 직접 실행한 출력으로 확인했다. AI가 재실행한 결과는 아니다.

| 명령 | 결과 |
| --- | --- |
| `java -version` / `javac -version` | Oracle JDK / javac 21.0.9 |
| `.\gradlew.bat --version` | Gradle 8.14.6, Launcher JVM 21.0.9 |
| `.\gradlew.bat test` | BUILD SUCCESSFUL (1분, 5 tasks executed) |
| `.\gradlew.bat build` | BUILD SUCCESSFUL (5초, 3 executed / 5 up-to-date) |

테스트 중 JVM 클래스 공유 경고가 출력됐지만 테스트와 빌드는 성공했다.
검증 범위는 DB를 제외한 기본 컨텍스트 테스트와 빌드다. 서버 실행 및 PostgreSQL 연결은 아직 확인하지 않았다.

## AI 실행 환경 제약

- 작업 위치: 저장소 루트 (WSL/bash)
- Git 2.43.0, Docker CLI 29.5.3, Compose v5.1.4 확인.
- Linux `java`, `javac`, `gradle` 없음. Windows JDK는 개발자 터미널에서 21.0.9로 확인됐다. AI 환경에서 Windows 실행 파일을 호출하면 WSL 소켓 오류가 발생한다.
- Docker 소켓 권한 오류로 데몬 접속 확인 실패. 컨테이너는 실행하지 않았다.
- `.git` 디렉터리는 있으나 `git status`는 Git 저장소로 인식하지 못했다. Git 초기화/커밋/원격 작업은 하지 않았다.
- 패키지 설치나 외부 폴더 수정은 하지 않았다.

## 학습 순서와 기록

[ROADMAP](docs/ROADMAP.md)의 첫 순수 Java 실습부터 개발자가 직접 진행한다.
[WORKLOG](docs/WORKLOG.md)에는 실제 명령과 검증 한계를 기록한다.
이번 초기 소스/문서는 AI가 작성했으며, 기존 개발자 기능 코드는 발견되지 않았다.
실제 비밀번호/토큰/DB 접속 정보는 저장하지 않는다.
