package com.example.quest504.domain.baseball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberBaseballGenerator {
    public String generate() {
        List<Integer> numbers = new ArrayList<>();

        // 1. 리스트에 1~9 저장
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }

        // 2. 리스트 섞기
        Collections.shuffle(numbers);

        StringBuilder result = new StringBuilder();

        // 3. 앞의 3개 숫자만 StringBuilder에 추가
        for (int i = 0; i < 3; i++) {
            result.append(numbers.get(i));
        }

        return result.toString();
    }
}
