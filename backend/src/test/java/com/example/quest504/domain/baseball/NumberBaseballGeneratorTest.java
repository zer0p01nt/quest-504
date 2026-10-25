package com.example.quest504.domain.baseball;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberBaseballGeneratorTest {

    @Test
    void generateTest() {
        NumberBaseballGenerator generator = new NumberBaseballGenerator();

        // 100번 생성 및 실행
        for (int i = 0; i < 100; i++) {
            String result = generator.generate();

            // 길이가 3인지 검증
            assertEquals(3, result.length());

            // 1~9의 숫자로만 구성됐는지 검증
            assertTrue(result.matches("[1-9]{3}"));

            // 숫자가 중복되지 않았는지 검증
            assertNotEquals(result.charAt(0), result.charAt(1));
            assertNotEquals(result.charAt(0), result.charAt(2));
            assertNotEquals(result.charAt(1), result.charAt(2));
        }
    }
}
