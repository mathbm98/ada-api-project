package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.smallrye.mutiny.Uni;

// Configurações adaptadas da documentação do Quarkus Redis:
//   * https://quarkus.io/guides/redis
//   * https://quarkus.io/guides/redis-reference
@ApplicationScoped
public class RedisService { 
    private ReactiveKeyCommands<Long> keyCommands;
    private HashCommands<Long, String, String> hashCommands;

    public RedisService(RedisDataSource ds, ReactiveRedisDataSource rds) { 
        this.keyCommands = rds.key(Long.class);
        this.hashCommands = ds.hash(Long.class, String.class, String.class);
    }

    public String hget(Long id, String field) {
        String value = hashCommands.hget(id, field); 
        return value;
    }

    public void hset(Long id, String field, String value) {
        hashCommands.hset(id, field, value);
    }

    public Uni<Void> del(Long id) {
        return keyCommands.del(id).replaceWithVoid();
    }

    public Uni<List<Long>> keys() {
        return keyCommands.keys("*"); 
    }
}
