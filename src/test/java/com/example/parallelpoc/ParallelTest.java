package com.example.parallelpoc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ParallelTest {
    @Test
    public void completableFutureTest() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<CompletableFuture<Integer>> futures = integers.stream().map(i -> CompletableFuture.supplyAsync(() -> {
            System.out.println("Integer : " + i);
            return i;
        }, executorService)).collect(toList());
        List<Integer> collect = futures.stream().map(CompletableFuture::join).collect(toList());
        long endTime = System.currentTimeMillis();
        System.out.println("실행 시간 :: " + (endTime - startTime) / 1000.0);
        Assertions.assertEquals(10, collect.size());
    }
}
