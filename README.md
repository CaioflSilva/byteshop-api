 ByteShop API

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?style=flat-square&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=flat-square&logo=jsonwebtokens)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.1-green?style=flat-square&logo=swagger)

API REST de E-commerce desenvolvida com Java 17 e Spring Boot 3, seguindo boas práticas de arquitetura em camadas, Git Flow e desenvolvimento orientado a testes.





 Sobre o Projeto

O  ByteShop API é uma API REST completa de e-commerce que permite o gerenciamento de produtos, categorias, carrinho de compras e pedidos. 
 O projeto foi desenvolvido com foco em boas práticas de arquitetura, segurança e organização de código.



 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.5.13 | Framework base |
| Spring Security | 6.x | Autenticação e autorização |
| JWT (jjwt) | 0.12.6 | Tokens de acesso |
| Spring Data JPA | 3.x | Persistência de dados |
| Flyway | 11.x | Migrations de banco |
| PostgreSQL | 17 | Banco de dados (dev) |
| MySQL | 8 | Banco de dados (prod) |
| H2 | - | Banco de dados (test) |
| Springdoc OpenAPI | 2.8.8 | Documentação Swagger |
| Lombok | 1.18.x | Redução de boilerplate |
| JUnit 5 | 5.x | Testes unitários |
| Mockito | 5.x | Mocks para testes |
| Maven | 3.x | Gerenciamento de dependências |



 Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

src/main/java/com/bytesoft/byteshop/
├── config/          → Configurações (Security, OpenAPI)
├── controller/      → Endpoints REST
├── dto/
│   ├── request/     → DTOs de entrada
│   └── response/    → DTOs de saída
├── exception/       → Exceções customizadas e handler global
├── model/           → Entidades JPA
├── repository/      → Interfaces de acesso ao banco
├── security/        → JWT e filtros de autenticação
└── service/         → Regras de negócio


Boas práticas aplicadas

 Separação clara de responsabilidades por camada
 DTOs separados em request e response
 Controller apenas delega para o service
 Service sem acesso direto ao banco (sempre via repository)
 Exceções semânticas com handler global
 Migrations versionadas com Flyway
 Profiles separados para dev, prod e test
 Validação de entrada com Bean Validation
 Soft delete em produtos
 Controle de estoque ao finalizar pedido



 Funcionalidades

 Autenticação
 Registro de novo usuário
 Login com geração de token JWT
 Controle de acesso por roles (USER e ADMIN)

  Categorias (ADMIN para escrita)
 Criar, listar, buscar, atualizar e deletar categorias
 Validação de nome duplicado

 Produtos (ADMIN para escrita)
 Criar, listar, buscar, atualizar e desativar produtos
 Filtro por categoria
 Paginação e ordenação
 Soft delete (produto desativado, não removido)
 Validação de estoque

     Carrinho
 Adicionar itens com validação de estoque
Atualizar quantidade de itens
Remover item específico
 Limpar carrinho completo
Cálculo automático de subtotal e total

     Pedidos
 Finalizar pedido a partir do carrinho
Dedução automática de estoque
 Histórico de pedidos do usuário
 Controle de status (PENDENTE → CONFIRMADO → EM_PREPARO → ENVIADO → ENTREGUE)
Cancelamento de pedidos (ADMIN)



    Pré-requisitos

Java 17+
 Maven 3.8+
 PostgreSQL 14+ (para ambiente dev)
 IntelliJ IDEA ou outra IDE de sua preferência



     Como Executar

    1  Clone o repositório

  bash
git clone https://github.com/CaioflSilva/byteshop-api.git
cd byteshop-api


    2 Crie o banco de dados

   sql
CREATE DATABASE byteshop;


    3 Configure o ambiente de desenvolvimento

Crie o arquivo src/main/resources/application-dev.properties :

    properties
spring.datasource.url=jdbc:postgresql://localhost:5432/byteshop
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration


    4 Execute a aplicação

    bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"


Ou pelo IntelliJ com VM options: -Dspring.profiles.active=dev

    5 Acesse o Swagger


http://localhost:8080/swagger-ui/index.html




     Endpoints

    Autenticação
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/registrar` | Registrar novo usuário | ❌ |
| POST | `/auth/login` | Login e obter token JWT | ❌ |

    Categorias

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/categorias` | Listar categorias | ❌ |
| GET | `/categorias/{id}` | Buscar por ID | ❌ |
| POST | `/categorias` | Criar categoria | ADMIN |
| PUT | `/categorias/{id}` | Atualizar categoria | ADMIN |
| DELETE | `/categorias/{id}` | Deletar categoria | ADMIN |

     Produtos

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/produtos` | Listar produtos ativos | ❌ |
| GET | `/produtos?categoriaId={id}` | Filtrar por categoria | ❌ |
| GET | `/produtos/{id}` | Buscar por ID | ❌ |
| POST | `/produtos` | Criar produto | ADMIN |
| PUT | `/produtos/{id}` | Atualizar produto | ADMIN |
| DELETE | `/produtos/{id}` | Desativar produto | ADMIN |

     Carrinho

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/carrinho` | Ver carrinho | USER |
| POST | `/carrinho/itens` | Adicionar item | USER |
| PUT | `/carrinho/itens/{itemId}` | Atualizar quantidade | USER |
| DELETE | `/carrinho/itens/{itemId}` | Remover item | USER |
| DELETE | `/carrinho` | Limpar carrinho | USER |

     Pedidos

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/pedidos` | Finalizar pedido | USER |
| GET | `/pedidos` | Listar meus pedidos | USER |
| GET | `/pedidos/{id}` | Buscar por ID | USER |
| PATCH | `/pedidos/{id}/status` | Atualizar status | ADMIN |



     Autenticação

A API utiliza autenticação via JWT Bearer Token

    Como usar

1 Registre um usuário via `POST /auth/registrar`
2 Faça login via `POST /auth/login` e copie o token retornado
3 No Swagger, clique em Authorize e insira: `Bearer {seu_token}`
4 Todos os endpoints protegidos aceitarão a requisição

    Exemplo de registro

   json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}


    Exemplo de login

  json
{
  "email": "joao@email.com",
  "senha": "senha123"
}



     Testes

O projeto possui testes unitários cobrindo os principais serviços.

 Executar os testes

     bash
./mvnw test -Dspring.profiles.active=test


    Cobertura atual

| Classe | Testes |
|---|---|
| AuthService | 4 testes |
| CategoriaService | 7 testes |
| ProdutoService | 7 testes |
| **Total** | **19 testes** |



     Git Flow

    O projeto foi desenvolvido seguindo o Git Flow:


master
└── develop
    ├── feature/auth       → JWT e Spring Security
    ├── feature/produto    → CRUD de categorias e produtos
    ├── feature/carrinho   → Carrinho de compras
    ├── feature/pedido     → Sistema de pedidos
    └── feature/testes     → Testes unitários


     Convenção de commits


feat:  nova funcionalidade
fix:   correção de bug
chore: configuração ou infraestrutura
test:  adição ou correção de testes
docs:  documentação






Desenvolvido por Caio Silva

[![GitHub](https://img.shields.io/badge/GitHub-CaioflSilva-black?style=flat-square&logo=github)](https://github.com/CaioflSilva)