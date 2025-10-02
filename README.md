# API Produtos

Esta é uma aplicação Quarkus básica, desenvolvida com o propósito de explorar ferramentas comumente usadas em aplicações backend de alto-nível.

Nela, foram configurados:

 - Banco de dados MySQL
 - Gerenciador de acesso KeyCloak
 - Redis client para cache
 - Swagger UI para acesso dos endpoints

Procurou-se desenvolver a aplicação inteiramente no ambiente Docker, nativo ao Quarkus.

## Rotina de execução

Para criar os containeres, pode-se utilizar o comando:

```shell script
docker-compose up -d
```

Caso não possua o docker-compose, os mesmos podem ser implementados manualmente: 
```shell script
docker run -d -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=produtos \
    --name mysql_test mysql:8.0 

docker run -d -p 8088:8080 \
    -e KEYCLOAK_URL=http://keycloak:8080/auth/realms/quarkus \
    -e KEYCLOAK_ADMIN=admin \
    -e KEYCLOAK_ADMIN_PASSWORD=admin \
    --name keycloak_test quay.io/keycloak/keycloak:21.0.1 start-dev

docker run -d -p 6379:6379 --name redis_test redis:7.0
```

Certifique-se de que não há nenhum conflito de porta com outras aplicações. Caso algumas dessas dependências já estejam instaladas, o projeto pode ser adaptado às configurações locais sem maiores prejuízos.

Por fim, é possível iniciar a aplicação Quarkus

```shell script
./mvnw quarkus:dev
```

que pode ser acessada em `http://localhost:8080`.

## Referência API

A API simula uma base de dados simplificada de produtos, contendo nome, descrição e preço. Foram definidos os seguintes endpoints:

|           |                |
|:---------:|:---------------|
| **GET**   | /produtos      |
| **GET**   | /produtos/{id} |
| **POST**  | /produtos      |
| **PUT**   | /produtos/{id} |
| **DELETE**| /produtos/{id} |

Também foi definido um endpoint especial:

|        |                 |
|:------:|:----------------|
|**GET** | /produtos/cache |

 para retornar o estado do cache da aplicação.

## KeyCloak

O KeyCloak foi configurado com os seguintes parâmetros:

 - Realm: quarkus
 - Client: quarkus-cli

que podem ser importadas do arquivo `realm-export.json` presente na pasta `src/main/resources`. Ao iniciá-lo, encontram-se os dois perfis _user_ e _admin_, onde o primeiro possui somente acesso de leitura aos dados (métodos **GET**).

Os usuários com estes perfis devem ser criados no painel de [usuários](http://localhost:8088/admin/master/console/#/quarkus/users) do realm quarkus.

Acesso:

* Usuario: admin
* Senha: admin


## Agradecimentos

Agradeço ao prof. Matheus Cruz e aos colegas de turma pelo curso que participamos neste último mês, e à ADATech e à CAIXA pela oportunidade oferecida. Minha mais completa gratidão a todos.

<p style="text-align: right">At. te, <br> Matheus Benedito Mendes</p>