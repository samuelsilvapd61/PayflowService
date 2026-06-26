package com.samuel.payment.resource;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientResource {

    private final DataSource dataSource;
    private final StringRedisTemplate redisTemplate;

    @PostMapping
    public String post() {
        return "Funcionou!";
    }


    @GetMapping("/health")
    public String health() throws Exception {

        dataSource.getConnection().close();

        redisTemplate.opsForValue()
                .set("test", "ok");

        return "UP";
    }
}
