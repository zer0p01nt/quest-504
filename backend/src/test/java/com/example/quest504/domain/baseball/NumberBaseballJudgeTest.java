package com.example.quest504.domain.baseball;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberBaseballJudgeTest {

    @Test
    void duplicateNumberTest() {
        NumberBaseballJudge judge = new NumberBaseballJudge();

        assertThrows(
                IllegalArgumentException.class,
                () -> judge.judge("427", "121")
        );
    }

    @Test
    void threeStrikesTest() {
        // 1. 테스트 객체 생성
        NumberBaseballJudge judge = new NumberBaseballJudge();

        // 2. 메서드 실행
        JudgeResult result = judge.judge("427", "427");

        // 3. 결과 검증
        assertEquals(3, result.strikes());
        assertEquals(0, result.balls());
    }

    @Test
    void outTest() {
        NumberBaseballJudge judge = new NumberBaseballJudge();

        JudgeResult result = judge.judge("427", "985");

        assertEquals(0, result.strikes());
        assertEquals(0, result.balls());
    }

    @Test
    void mixedResultTest() {
        NumberBaseballJudge judge = new NumberBaseballJudge();

        JudgeResult result = judge.judge("427", "472");

        assertEquals(1, result.strikes());
        assertEquals(2, result.balls());
    }

    @Test
    void threeBallsTest() {
        NumberBaseballJudge judge = new NumberBaseballJudge();

        JudgeResult result = judge.judge("427", "742");

        assertEquals(0, result.strikes());
        assertEquals(3, result.balls());
    }
}
