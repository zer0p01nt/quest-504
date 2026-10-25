package com.example.quest504.domain.baseball;

public class NumberBaseballJudge {

    private void validateGuess(String guess) {
        if (guess == null) {
            throw new IllegalArgumentException("잘못된 입력값입니다.");
        }

        if (guess.length() != 3) {
            throw new IllegalArgumentException("3자리 숫자를 입력해야 합니다.");
        }

        for (int i = 0; i < 3; i++) {
            char digit = guess.charAt(i);

            if (digit < '1' || digit > '9') {
                throw new IllegalArgumentException("1~9까지의 숫자만 포함되어야 합니다.");
            }

            for (int j = i + 1; j < 3; j++) {
                char digit2 = guess.charAt(j);

                if (digit == digit2) {
                    throw new IllegalArgumentException("중복 숫자를 포함하고 있습니다.");
                }
            }
        }
    }

    public JudgeResult judge(String secret, String guess) {
        // 1. 입력값 검증
        validateGuess(guess);

        // 2. 스트라이크, 볼 계산
        int strikes = 0;
        int balls = 0;

        // 3. 판정 로직
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                strikes++;
            } else if (secret.contains(String.valueOf(guess.charAt(i)))) {
                balls++;
            }
        }

        // 4. 결과 반환
        return new JudgeResult(strikes, balls);
    }
}
