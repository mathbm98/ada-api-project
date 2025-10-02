package org.acme;

import org.acme.dto.ProdutoDTO;
import org.acme.services.CacheService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/produtos")
@Produces("application/json")
@Consumes("application/json")
public class ProdutoController {
    CacheService cacheService;

    public ProdutoController(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    @GET
    @RolesAllowed({"user", "admin"})
    public Response listarProdutos() {
        return Response.ok()
            .entity(Produto.listAll())
            .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    public Response obterProduto(Long id) {
        if (cacheService.isCached(id)) {
            ProdutoDTO produtoDTO = cacheService.redisGet(id);
            return Response.ok()
                    .entity(produtoDTO)
                    .build();
        }
        else {
            Produto produto = Produto.findById(id);
            if (produto == null) {
                return Response.status(404)
                        .entity("Produto não encontrado.")
                        .build();
            }
        
            cacheService.redisCaching(id, produto);

            return Response.ok()
                    .entity(produto)
                    .build();    
        }
        
    }

    @POST
    @Transactional
    @RolesAllowed("admin")
    public Response criarProduto(Produto produto) {
        try {
            produto.persist();
            return Response.ok()
                .entity(produto)
                .build();
        }
        catch (Exception e) {
            return Response.status(400)
                    .entity("Dados inválidos, não foi possível cadastrar o produto.")
                    .build();
        }
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response atualizarProduto(Long id, Produto dadosAtualizados) {
        Produto produto = Produto.findById(id);
        if (produto == null) { 
            return Response.status(404)
                    .entity("Produto não encontrado.")
                    .build();
        }
        try {
            if (dadosAtualizados.nome != null && !dadosAtualizados.nome.isBlank()) { produto.nome = dadosAtualizados.nome; }
            if (dadosAtualizados.descricao != null && !dadosAtualizados.descricao.isBlank()) { produto.descricao = dadosAtualizados.descricao; }
            if (dadosAtualizados.preco != null && dadosAtualizados.preco > 0) { produto.preco = dadosAtualizados.preco; }
            produto.persist();

            return Response.ok()
                .entity(produto)
                .build();
        }
        catch (Exception e) {
            return Response.status(400)
                    .entity("Dados inválidos, não foi possível atualizar o produto.")
                    .build();
        }
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deletarProduto(Long id) {
        Produto produto = Produto.findById(id);
        if (produto == null) { 
            return Response.status(404)
                    .entity("Produto não encontrado.")
                    .build();
        }

        cacheService.redisDelete(id); // Remove do cache
        produto.delete();

        return Response.status(204)
            .entity("Produto id %d deletado com sucesso.".formatted(id))
            .build();

    }
}
