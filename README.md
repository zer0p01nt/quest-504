# ⚾ Quest 504

숫자야구를 풀고 100 GOLD를 받는 작은 퀘스트 게임.
Java와 Spring Boot를 복습하며 회원가입부터 게임, 보상 지급, 배포까지 구현한 개인 프로젝트

## 🎮 주요 기능

- **회원가입 · 로그인** — Spring Security 세션 인증, BCrypt 비밀번호 암호화
- **숫자야구** — 1~9 중 중복 없는 세 자리 숫자 맞히기, 시도 횟수 제한 없음
- **판정 · 기록** — 숫자와 자리가 같으면 S, 숫자만 같으면 B, 모두 없으면 OUT. 현재 라운드의 시도 기록 표시
- **보상 수령** — 정답을 맞히면 게임당 100 GOLD 지급, 누적 골드 확인
- **중복 지급 방지** — 트랜잭션, 비관적 잠금, DB 유니크 제약 적용

## 🛠 기술 스택

| 역할 | 기술 | 사용 목적 |
| --- | --- | --- |
| Frontend | ![React](https://img.shields.io/badge/React-20232A?logo=react&logoColor=61DAFB) ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white) | 게임 화면과 사용자 입력 처리 |
| Styling | ![CSS](https://img.shields.io/badge/CSS-663399?logo=css&logoColor=white) | 반응형 화면 구성 |
| HTTP Client | ![Axios](https://img.shields.io/badge/Axios-5A29E4?logo=axios&logoColor=white) | API 요청, 세션 쿠키와 CSRF 토큰 처리 |
| Backend | ![Java](https://img.shields.io/badge/Java_21-ED8B00) ![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?logo=springboot&logoColor=white) | REST API와 게임·보상 로직 |
| Auth | Spring Security | 세션 인증과 접근 제어 |
| ORM | Spring Data JPA · Hibernate | 엔티티 매핑과 데이터 저장 |
| Database | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL_17-4169E1?logo=postgresql&logoColor=white) | 플레이어, 게임, 보상 이력 저장 |
| Build | Vite · npm · Gradle Wrapper | 프론트엔드·백엔드 빌드 |
| Test | JUnit 5 · Mockito · Spring Boot Test | 판정 로직, 서비스, DB 연동과 보상 동시성 테스트 |
| CI · Container | GitHub Actions · Docker · Docker Compose | 자동 검증, 배포 이미지 빌드, 로컬 DB 실행 |

## 🚀 로컬 실행

Java 21, Node.js 22, Docker가 필요합니다. 프론트엔드는 **npm**, 백엔드는 **Gradle Wrapper**를 사용합니다.
아래 명령은 Windows PowerShell 기준입니다.

### 1. 환경변수 · DB

프로젝트 루트에서 `.env.example`을 `.env`로 복사하고 `POSTGRES_PASSWORD`를 채웁니다. 기존 `.env`가 있다면 그대로 사용합니다.

```powershell
Copy-Item .env.example .env
docker compose up -d --wait postgres
docker compose ps
```

기본 DB 주소는 `localhost:5432`입니다. 다른 프로젝트와 포트가 겹치면 `.env`의 `DB_PORT`를 변경합니다.
`.env`는 Git에 올리지 않습니다.

### 2. 백엔드

```powershell
cd backend
.\gradlew.bat bootRun
```

- 주소: `http://localhost:8080`
- 루트 `.env`를 읽으므로 IDE 실행 시에도 작업 디렉터리를 `backend/`로 지정합니다.
- macOS/Linux에서는 `./gradlew bootRun`을 사용합니다.

### 3. 프론트엔드

새 터미널에서 프로젝트 루트 기준으로 실행합니다.

```powershell
cd frontend
npm ci
npm run dev
```

- 접속: `http://localhost:5173`
- `/api` 요청은 Vite 프록시를 통해 백엔드의 `8080` 포트로 전달됩니다.

### 테스트 · 빌드

```powershell
# frontend/에서
npm run lint
npm run build

# backend/에서
.\gradlew.bat test
.\gradlew.bat build
```

DB 연동 테스트에는 별도의 `quest504_test` 데이터베이스가 필요합니다. 로컬 PostgreSQL에 테스트 DB를 준비하고, 접속 계정에 접근 권한을 부여한 뒤 실행합니다.

### Docker로 전체 실행

루트 `.env` 설정 후 프로젝트 루트에서 실행합니다.

```powershell
docker compose up -d --build --wait
```

`http://localhost:8081`로 접속합니다. React 빌드 결과를 Spring Boot 정적 리소스에 포함해 하나의 컨테이너에서 제공합니다.
DB 데이터는 `postgres_data` 볼륨에 보관됩니다.

## ✅ CI 워크플로우

[`Quest 504 CI`](.github/workflows/ci.yml)는 `main` 브랜치 push와 `main` 대상 PR에서 실행됩니다.

```text
코드 체크아웃
  → Node.js 22 · Java 21 설정
  → Frontend: npm ci → lint → build
  → Backend: Gradle clean test
  → Docker 이미지 빌드
```

테스트용 PostgreSQL 17을 별도로 띄워 DB 연동 테스트를 수행합니다.
Docker 이미지는 빌드까지만 검증하며, 이미지 업로드나 배포 단계는 포함하지 않습니다.

## 🔗 커밋 컨벤션

**형식:** `Type: 작업 내용`

| 유형 | 내용 |
| --- | --- |
| `Feat` | 새로운 기능 |
| `Fix` | 버그 수정 |
| `Design` | UI · 스타일 수정 |
| `Refactor` | 동작 변경 없는 코드 정리 |
| `Test` | 테스트 추가 · 수정 |
| `Docs` | 문서 작성 · 수정 |
| `Deps` | 의존성 추가 · 변경 |
| `CI` | CI 워크플로우 수정 |
| `Chore` | 개발 환경 · 빌드 설정 등 기타 작업 |

```text
Feat: 회원가입 구현 및 테스트 추가
Fix: 내 정보 응답에 현재 골드 추가
Design: 아웃 표시 UI 수정
CI: 자동 테스트 워크플로우 구성
```

## 📂 프로젝트 구조

```text
quest-504/
├── .github/workflows/
│   └── ci.yml                  # 프론트·백엔드·Docker 검증
├── backend/
│   ├── src/main/
│   │   ├── java/com/example/quest504/
│   │   │   ├── controller/     # 인증·게임·보상 API
│   │   │   ├── domain/baseball/ # 순수 Java 숫자 생성·판정
│   │   │   ├── dto/            # 요청·응답 데이터
│   │   │   ├── entity/         # Player, Game, RewardHistory
│   │   │   ├── exception/      # 공통 예외 처리
│   │   │   ├── repository/     # DB 조회·저장
│   │   │   ├── security/       # 인증·접근 제어 설정
│   │   │   └── service/        # 회원가입·게임·보상 로직
│   │   └── resources/          # 기본·test·prod 설정
│   ├── src/test/               # 단위·DB 연동·동시성 테스트
│   ├── build.gradle
│   └── gradlew / gradlew.bat
├── frontend/
│   ├── public/                 # 정적 파일
│   ├── src/
│   │   ├── api/                # Axios 클라이언트·인증·게임 API
│   │   ├── App.tsx             # 인증 화면·게임 화면
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.tsx
│   ├── package.json
│   └── vite.config.ts          # 개발 서버·API 프록시
├── .env.example
├── compose.yaml                # 로컬 PostgreSQL·앱 컨테이너
├── Dockerfile                  # React + Spring Boot 멀티 스테이지 빌드
└── README.md
```

요청은 `Controller → Service → Repository → PostgreSQL` 순서로 처리합니다.
숫자야구 생성·판정은 Spring과 분리한 순수 Java 클래스로 구성했습니다.
