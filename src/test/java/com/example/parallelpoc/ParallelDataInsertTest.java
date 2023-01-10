package com.example.parallelpoc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
@Slf4j
public class ParallelDataInsertTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Test
    public void parallelDataInsertTest() {
        List<String> names = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        List<CompletableFuture<Integer>> futures = names.stream().map(name -> CompletableFuture.supplyAsync(() -> {
            int key = insert(name);
            log.info("Key : " + key + ", Name : " + name);
            return key;
        }, executorService)).collect(Collectors.toList());
        List<Integer> collectedKeys = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        Assertions.assertEquals(26, collectedKeys.size());
        Assertions.assertEquals(1, collectedKeys.stream().min(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException()));
        Assertions.assertEquals(26, collectedKeys.stream().max(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException()));
        long endTime = System.currentTimeMillis();
        log.info("실행 시간 :: " + (endTime - startTime) / 1000.0);
    }

    @Transactional
    public int insert(final String name) {
        String query = "INSERT INTO test_table(name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update((conn) -> {
            PreparedStatement preparedStatement = conn.prepareStatement(query, new String[] {"id"});
            preparedStatement.setString(1, name);
            return preparedStatement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }
}
