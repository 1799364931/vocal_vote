package com.example.vocal_vote.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class RandomList {
    private List<Integer> randomList;
    private Integer listSize;

    public RandomList(Integer listSize) {
        this.listSize = listSize;

        // 创建顺序列表
        randomList = new ArrayList<>();
        for (int i = 1; i <= listSize; i++) {
            randomList.add(i);
        }

        // 使用内置 API 打乱顺序
        Collections.shuffle(randomList);
    }
}
