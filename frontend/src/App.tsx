import { useEffect, useRef, useState, type FormEvent } from "react";
import { isAxiosError } from "axios";
import { getMe, login, logout, signup, type MeResponse } from "./api/auth";
import { claimReward, startGame, submitGuess } from "./api/game";
import "./App.css";

type AuthMode = "login" | "signup";
type Guess = { value: string; strikes: number; balls: number; solved: boolean };

function errorMessage(error: unknown, fallback: string): string {
  if (isAxiosError(error)) {
    const responseData: unknown = error.response?.data;
    if (
      responseData &&
      typeof responseData === "object" &&
      "message" in responseData
    ) {
      const message = (responseData as { message?: unknown }).message;
      // Display the backend ErrorResponse.message exactly as received.
      if (typeof message === "string" && message.trim()) return message;
    }
    if (!error.response)
      return "서버에 연결할 수 없습니다. 백엔드 실행 상태를 확인해 주세요.";
    if (error.response.status === 403)
      return "요청이 거부되었습니다. 페이지를 새로고침한 후 다시 시도해 주세요.";
    if (error.response.status === 401) return fallback;
    if (error.response.status >= 500)
      return "서버에서 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
  }
  return fallback;
}

function Brand({ compact = false }: { compact?: boolean }) {
  return (
    <div className={`brand ${compact ? "brand--compact" : ""}`}>
      <div className='brand__text'>
        <strong>QUEST 504</strong>
        <small>NUMBER BASEBALL CLUB</small>
      </div>
    </div>
  );
}

function AuthScreen({
  onAuthenticated,
}: {
  onAuthenticated: (user: MeResponse) => void;
}) {
  const [mode, setMode] = useState<AuthMode>("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const changeMode = (next: AuthMode) => {
    setMode(next);
    setPassword("");
    setConfirmPassword("");
    setError("");
    setNotice("");
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");
    setNotice("");

    if (mode === "signup" && password !== confirmPassword) {
      setError("비밀번호가 일치하지 않습니다.");
      return;
    }

    setBusy(true);
    try {
      if (mode === "signup") {
        await signup(username, password);
        setMode("login");
        setPassword("");
        setConfirmPassword("");
        setNotice("회원가입이 완료되었습니다. 새 계정으로 로그인해 주세요.");
      } else {
        await login(username, password);
        // Trust the session only after the authenticated /me endpoint succeeds.
        const user = await getMe();
        setPassword("");
        onAuthenticated(user);
      }
    } catch (err) {
      setError(
        errorMessage(
          err,
          mode === "login"
            ? "아이디 또는 비밀번호가 올바르지 않습니다."
            : "회원가입에 실패했습니다.",
        ),
      );
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className='auth-page'>
      <section className='auth-story' aria-label='Quest 504 소개'>
        <div className='auth-story__content'>
          <Brand />
          <span className='eyebrow'>
            <span className='eyebrow__dot' /> THE LITTLE GAME LAB
          </span>
          <h1>
            세 자리 숫자,
            <br />
            <em>단 하나의 정답.</em>
          </h1>
          <p className='auth-story__description'>
            1부터 9까지 중복되지 않는 숫자 세 개를 맞혀 보세요.
            <br />
            스트라이크와 볼을 단서로 정답을 찾으면 100 GOLD!
          </p>

          <div className='demo-board' aria-hidden='true'>
            <div className='demo-board__top'>
              <span>LIVE ROUND</span>
              <span className='demo-board__light'>● ● ●</span>
            </div>
            <div className='demo-board__digits'>
              <span>4</span>
              <span>2</span>
              <span>7</span>
            </div>
            <div className='demo-board__divider' />
            <div className='demo-board__row'>
              <span>GUESS 01</span>
              <strong>1 STRIKE</strong>
              <span className='demo-board__balls'>2 BALLS</span>
            </div>
            <div className='demo-board__row'>
              <span>GUESS 02</span>
              <strong>2 STRIKES</strong>
              <span className='demo-board__balls'>0 BALLS</span>
            </div>
            <div className='demo-board__row demo-board__row--win'>
              <span>GUESS 03</span>
              <strong>HOME RUN!</strong>
              <span>+100 GOLD</span>
            </div>
          </div>
          <div className='auth-story__bottom'>
            <span>THINK. GUESS. WIN.</span>
            <span>EST. 2026</span>
          </div>
        </div>
      </section>

      <section className='auth-panel' aria-label='계정 인증'>
        <div className='auth-panel__inner'>
          <div className='auth-panel__mobile-brand'>
            <Brand compact />
          </div>
          <div className='panel-kicker'>
            WELCOME TO THE CLUB <span>✦</span>
          </div>
          <h2>
            {mode === "login" ? "다시 만나서 반갑습니다!" : "플레이어 등록하기"}
          </h2>
          <p className='auth-panel__subtitle'>
            {mode === "login"
              ? "로그인하고 숫자야구를 시작해 보세요."
              : "아이디와 비밀번호를 설정하면 바로 시작할 수 있습니다."}
          </p>

          <div className='auth-switch' role='group' aria-label='계정 화면 선택'>
            <button
              type='button'
              className={mode === "login" ? "is-active" : ""}
              onClick={() => changeMode("login")}
              disabled={busy}
            >
              로그인
            </button>
            <button
              type='button'
              className={mode === "signup" ? "is-active" : ""}
              onClick={() => changeMode("signup")}
              disabled={busy}
            >
              회원가입
            </button>
          </div>

          <form onSubmit={handleSubmit} className='auth-form' noValidate>
            <label htmlFor='username'>아이디</label>
            <input
              id='username'
              name='username'
              type='text'
              autoComplete='username'
              placeholder='아이디를 입력해 주세요'
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={busy}
            />

            <label htmlFor='password'>비밀번호</label>
            <input
              id='password'
              name='password'
              type='password'
              autoComplete={
                mode === "login" ? "current-password" : "new-password"
              }
              placeholder={
                mode === "signup"
                  ? "8자 이상 입력해 주세요"
                  : "비밀번호를 입력해 주세요"
              }
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={busy}
            />

            {mode === "signup" && (
              <>
                <label htmlFor='confirm-password'>비밀번호 확인</label>
                <input
                  id='confirm-password'
                  name='confirm-password'
                  type='password'
                  autoComplete='new-password'
                  placeholder='비밀번호를 한 번 더 입력해 주세요'
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  disabled={busy}
                />
                <p className='form-hint'>아이디 3~20자 · 비밀번호 8자 이상</p>
              </>
            )}

            {notice && (
              <p className='form-notice' role='status'>
                ✓ {notice}
              </p>
            )}
            {error && (
              <p className='form-error' role='alert'>
                {error}
              </p>
            )}
            <button
              className='button button--primary auth-submit'
              type='submit'
              disabled={busy}
            >
              {busy
                ? "처리 중..."
                : mode === "login"
                  ? "게임 시작하기"
                  : "회원가입 완료하기"}
              <span aria-hidden='true'>↗</span>
            </button>
          </form>
          <p className='auth-footer'>
            3개의 숫자로 시작하는 작은 도전 · QUEST 504
          </p>
        </div>
      </section>
    </main>
  );
}

function GameScreen({
  user,
  onUserChange,
  onLogout,
}: {
  user: MeResponse;
  onUserChange: (user: MeResponse) => void;
  onLogout: () => Promise<void>;
}) {
  const [gameId, setGameId] = useState<number | null>(null);
  const [guess, setGuess] = useState("");
  const [history, setHistory] = useState<Guess[]>([]);
  const [solved, setSolved] = useState(false);
  const [rewardClaimed, setRewardClaimed] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const guessInput = useRef<HTMLInputElement>(null);

  const start = async () => {
    setBusy(true);
    setError("");
    setNotice("");
    try {
      const result = await startGame();
      setGameId(result.gameId);
      setHistory([]);
      setGuess("");
      setSolved(false);
      setRewardClaimed(false);
      setNotice("새 게임이 시작되었습니다. 세 자리 숫자를 추측해 보세요.");
      setTimeout(() => guessInput.current?.focus(), 0);
    } catch (err) {
      setError(
        errorMessage(
          err,
          "게임을 시작하지 못했습니다. 로그인 상태를 확인해 주세요.",
        ),
      );
    } finally {
      setBusy(false);
    }
  };

  const onGuess = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (gameId === null || solved || busy) return;
    setBusy(true);
    setError("");
    setNotice("");
    try {
      const result = await submitGuess(gameId, guess);
      setHistory((previous) => [{ value: guess, ...result }, ...previous]);
      setGuess("");
      if (result.solved) {
        setSolved(true);
        setNotice("정답입니다! 보상 100 GOLD를 수령해 주세요.");
      } else {
        setNotice(
          result.strikes === 0 && result.balls === 0
            ? "아웃입니다. 다른 숫자로 다시 도전해 보세요."
            : "새로운 단서를 얻었습니다. 다음 숫자를 입력해 보세요.",
        );
      }
    } catch (err) {
      setError(
        errorMessage(
          err,
          "숫자를 제출하지 못했습니다. 로그인 상태를 확인해 주세요.",
        ),
      );
    } finally {
      setBusy(false);
    }
  };

  const onReward = async () => {
    if (gameId === null || rewardClaimed || busy) return;
    setBusy(true);
    setError("");
    try {
      const result = await claimReward(gameId);
      setRewardClaimed(true);
      const updatedUser = await getMe();
      onUserChange(updatedUser);
      setNotice(
        `${result.rewardAmount} GOLD를 획득했습니다! 다음 게임에도 도전해 보세요.`,
      );
    } catch (err) {
      setError(
        errorMessage(
          err,
          "보상 수령에 실패했습니다. 로그인 상태를 확인해 주세요.",
        ),
      );
    } finally {
      setBusy(false);
    }
  };

  const doLogout = async () => {
    setBusy(true);
    setError("");
    try {
      await onLogout();
    } catch (err) {
      setError(
        errorMessage(err, "로그아웃에 실패했습니다. 다시 시도해 주세요."),
      );
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className='game-page'>
      <header className='game-header'>
        <Brand compact />
        <div className='game-header__right'>
          <div className='gold-pill'>
            <span aria-hidden='true'>✦</span>
            <strong>{user.gold.toLocaleString()}</strong>
            <span>GOLD</span>
          </div>
          <div className='user-pill'>
            <span aria-hidden='true'>●</span>
            <span title={user.username}>{user.username}</span>
          </div>
          <button
            className='text-button'
            type='button'
            onClick={doLogout}
            disabled={busy}
          >
            로그아웃
          </button>
        </div>
      </header>

      <div className='game-container'>
        <div className='game-hero'>
          <div>
            <span className='eyebrow eyebrow--dark'>THE DAILY CHALLENGE</span>
            <h1>
              세 자리의 비밀을
              <br />
              <em>풀어볼 시간.</em>
            </h1>
            <p>1~9 · 중복 없는 숫자 · 정확한 자리라면 스트라이크!</p>
          </div>
          <div className='game-hero__graphic' aria-hidden='true'>
            <span>?</span>
            <span>?</span>
            <span>?</span>
          </div>
        </div>

        <div className='game-grid'>
          <section className='play-card'>
            <div className='section-heading'>
              <span className='section-number'>01 / PLAY</span>
              <span
                className={`round-status ${gameId !== null && !solved ? "is-live" : ""}`}
              >
                {gameId === null ? "READY" : solved ? "SOLVED" : "PLAYING"}
              </span>
            </div>
            {gameId === null ? (
              <div className='play-card__start'>
                <div className='play-card__emoji' aria-hidden='true'>
                  ⚾
                </div>
                <h2>첫 번째 게임을 시작해 보세요.</h2>
                <p>
                  게임을 시작하면 정답 숫자 세 개가 무작위로 생성됩니다.
                  <br />
                  위치와 숫자를 모두 맞히면 홈런!
                </p>
                <button
                  className='button button--primary'
                  type='button'
                  onClick={start}
                  disabled={busy}
                >
                  {busy ? "시작 중..." : "새 게임 시작하기"}
                  <span aria-hidden='true'>↗</span>
                </button>
              </div>
            ) : (
              <div className='play-card__active'>
                <div className='game-meta'>
                  <span>ROUND #{gameId}</span>
                  <span>시도 {history.length}회</span>
                </div>
                <h2>
                  {solved ? "정답입니다! 홈런! 🎉" : "정답을 추측해 보세요."}
                </h2>
                <p className='play-card__description'>
                  {solved
                    ? "정답을 맞혔습니다. 보상을 받고 다음 게임에 도전해 보세요."
                    : "서로 다른 1~9 숫자를 세 자리로 입력해 주세요."}
                </p>
                {!solved && (
                  <form className='guess-form' onSubmit={onGuess}>
                    <label htmlFor='guess-input'>YOUR GUESS</label>
                    <div className='guess-form__row'>
                      <input
                        ref={guessInput}
                        id='guess-input'
                        inputMode='numeric'
                        autoComplete='off'
                        maxLength={3}
                        placeholder='예: 427'
                        value={guess}
                        onChange={(event) =>
                          setGuess(
                            event.target.value
                              .replace(/[^0-9]/g, "")
                              .slice(0, 3),
                          )
                        }
                        disabled={busy}
                        aria-describedby='guess-rule'
                      />
                      <button
                        className='button button--primary'
                        type='submit'
                        disabled={busy}
                      >
                        {busy ? "확인 중..." : "제출하기"}
                        <span aria-hidden='true'>→</span>
                      </button>
                    </div>
                    <p id='guess-rule' className='form-hint'>
                      0은 사용할 수 없으며, 같은 숫자를 중복해서 입력할 수
                      없습니다.
                    </p>
                  </form>
                )}
                {solved && !rewardClaimed && (
                  <button
                    type='button'
                    className='button button--gold'
                    disabled={busy}
                    onClick={onReward}
                  >
                    {busy ? "처리 중..." : "100 GOLD 보상 받기"}
                    <span aria-hidden='true'>✦</span>
                  </button>
                )}
                {solved && rewardClaimed && (
                  <div className='reward-done'>✓ 보상 수령 완료</div>
                )}
                <button
                  type='button'
                  className='button button--outline new-round'
                  onClick={start}
                  disabled={busy}
                >
                  {solved ? "다음 게임 시작" : "새 게임으로 다시 시작"}
                </button>
              </div>
            )}
            {notice && (
              <p className='game-notice' role='status'>
                ✓ {notice}
              </p>
            )}
            {error && (
              <p className='game-error' role='alert'>
                {error}
              </p>
            )}
          </section>

          <section className='history-card'>
            <div className='section-heading'>
              <span className='section-number'>02 / RECORD</span>
              <span className='history-count'>{history.length} GUESSES</span>
            </div>
            <h2>나의 추측 기록</h2>
            {history.length === 0 ? (
              <div className='history-empty'>
                <div className='history-empty__icon' aria-hidden='true'>
                  ⌁
                </div>
                <strong>아직 기록이 없습니다.</strong>
                <span>게임을 시작하고 첫 숫자를 입력해 보세요.</span>
              </div>
            ) : (
              <ol className='history-list'>
                {history.map((item, index) => (
                  <li
                    key={`${history.length - index}-${item.value}`}
                    className={item.solved ? "history-list__win" : ""}
                  >
                    <span className='history-list__index'>
                      #{String(history.length - index).padStart(2, "0")}
                    </span>
                    <strong className='history-list__guess'>
                      {item.value}
                    </strong>
                    <div className='history-list__scores'>
                      <span>
                        <b>{item.strikes}</b> S
                      </span>
                      <span>
                        <b>{item.balls}</b> B
                      </span>
                    </div>
                    {item.solved && (
                      <span className='history-list__home-run'>HOME RUN</span>
                    )}
                  </li>
                ))}
              </ol>
            )}
            <div className='rules-card'>
              <span className='rules-card__title'>HOW TO PLAY</span>
              <p>
                <strong>STRIKE</strong> 같은 숫자 + 같은 자리
              </p>
              <p>
                <strong>BALL</strong> 같은 숫자 + 다른 자리
              </p>
              <p>
                <strong>OUT</strong> 세 숫자 모두 없음
              </p>
            </div>
          </section>
        </div>
        <footer className='game-footer'>
          <span>QUEST 504 / A NUMBER BASEBALL EXPERIMENT</span>
          <span>PLAY SMART, WIN GOLD ✦</span>
        </footer>
      </div>
    </main>
  );
}

export default function App() {
  const [user, setUser] = useState<MeResponse | null>(null);
  const [checking, setChecking] = useState(true);
  const [startupError, setStartupError] = useState("");

  useEffect(() => {
    let active = true;
    getMe()
      .then((profile) => {
        if (active) setUser(profile);
      })
      .catch((error: unknown) => {
        if (!active) return;
        if (!isAxiosError(error) || error.response?.status !== 401) {
          setStartupError(
            errorMessage(error, "로그인 상태를 확인하지 못했습니다."),
          );
        }
      })
      .finally(() => {
        if (active) setChecking(false);
      });
    return () => {
      active = false;
    };
  }, []);

  if (checking) {
    return (
      <div className='loading-screen'>
        <Brand compact />
        <div className='loading-screen__spinner' />
        <p>플레이어 정보를 확인하는 중...</p>
      </div>
    );
  }

  return (
    <>
      {startupError && (
        <div className='startup-warning' role='alert'>
          {startupError}
        </div>
      )}
      {user ? (
        <GameScreen
          user={user}
          onUserChange={setUser}
          onLogout={async () => {
            await logout();
            setUser(null);
            setStartupError("");
          }}
        />
      ) : (
        <AuthScreen
          onAuthenticated={(profile) => {
            setUser(profile);
            setStartupError("");
          }}
        />
      )}
    </>
  );
}
