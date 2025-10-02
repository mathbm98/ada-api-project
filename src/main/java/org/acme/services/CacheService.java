package org.acme.services;

import java.time.Duration;

import org.acme.Produto;
import org.acme.dto.ProdutoDTO;

public class CacheService {
    RedisService redisService;

    public CacheService(RedisService redisService) {
        this.redisService = redisService;
    }

     public boolean isCached(Long id) {
        return redisService.hget(id, "nome") != null 
            || redisService.hget(id, "descricao") != null
            || redisService.hget(id, "preco") != null;
    }

    public void redisCaching(Long id, Produto produto) {
        if (isCached(id)) {
            return; // Já está em cache
        }

        redisService.hset(id, "nome", produto.nome);
        redisService.hset(id, "descricao", produto.descricao);
        redisService.hset(id, "preco", String.valueOf(produto.preco));
    }

    public ProdutoDTO redisGet(Long id) {
        ProdutoDTO produtoDTO = new ProdutoDTO(
            redisService.hget(id, "nome"),
            redisService.hget(id, "descricao"),
            Double.valueOf(redisService.hget(id, "preco"))
        );

        return produtoDTO;
    }

    public void redisDelete(Long id) {
        redisService.del(id).await().atMost(Duration.ofSeconds(5));
    }
}
