package com.project.JobPortalSystem.Servies;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
@Service
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public <T> T get(String key, Class<T> clazz) {
//        String str = redisTemplate.opsForValue().get(key).toString();
        try {
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(o.toString(), clazz);
        }catch (Exception e){
            log.error("Redis get failed for key '{}': {}", key, e.getMessage(), e);
            return null;
        }

    }

    public void set(String key,Object o,Long ttl) {
//        String str = redisTemplate.opsForValue().get(key).toString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,json,ttl, TimeUnit.SECONDS);

        }catch (Exception e){
            log.error("Redis set failed for key '{}': {}", key, e.getMessage(), e);
        }

    }
}
