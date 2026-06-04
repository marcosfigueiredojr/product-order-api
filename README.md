# Product Order API

Sistema de gerenciamento de pedidos e produtos para e-commerce desenvolvido com Java + Spring Boot, utilizando arquitetura moderna baseada em microsserviços e boas práticas de desenvolvimento.

---

# Tecnologias Utilizadas

* Java 17+
* Spring Boot
* Spring Security + JWT
* Spring Data JPA
* MySQL
* Elasticsearch
* Apache Kafka
* Redis
* Micrometer + Actuator
* Docker
* AWS ECS
* AWS ALB
* AWS CloudWatch
* JUnit 5
* Mockito

---

# Objetivo do Projeto

Construir uma API robusta para gerenciamento de:

* Produtos
* Pedidos
* Pagamentos
* Relatórios
* Controle de estoque

Garantindo:

* Segurança
* Escalabilidade
* Consistência de dados
* Observabilidade
* Alta performance

---

# Arquitetura

```text
                Internet
                    │
                 AWS ALB
                    │
        ┌───────────┴───────────┐
        │                       │
    ECS Task               ECS Task
   (Spring Boot)          (Spring Boot)
        │
        │
    Redis Cache
        │
        │
      MySQL
        │
        │
 Elasticsearch
        │
        │
      Kafka
        │
        │
  CloudWatch Logs/Metrics
```

---

# Funcionalidades

# Autenticação e Autorização

Autenticação baseada em JWT.

Perfis disponíveis:

## ADMIN

Permissões:

* Criar produtos
* Atualizar produtos
* Remover produtos
* Acessar relatórios

## USER

Permissões:

* Visualizar produtos
* Criar pedidos
* Realizar pagamento de pedidos

---

# Produtos

CRUD completo de produtos.

Campos:

* id (UUID)
* nome
* descrição
* preço
* categoria
* quantidade em estoque
* data de criação
* data de atualização

---

# Busca de Produtos

Integração com Elasticsearch.

Filtros disponíveis:

* Nome com tolerância a erro de digitação
* Categoria
* Faixa de preço

Somente produtos ativos e com estoque disponível são retornados.

---

# Pedidos

Fluxo completo de pedidos:

## Criação

* Pedido inicia com status `PENDENTE`
* Validação de estoque
* Snapshot de preço dos produtos
* Cálculo automático do valor total

## Pagamento

Após pagamento:

* Status alterado para `PAGO`
* Evento `order.paid` enviado para Kafka

---

# Atualização de Estoque

Consumer Kafka processa:

* Evento `order.paid`
* Atualização do estoque
* Controle de idempotência
* Proteção contra processamento duplicado

---

# Relatórios

Relatórios disponíveis:

## Top 5 usuários que mais compraram

Filtro por período:

* data inicial
* data final

## Ticket médio por usuário

Filtro por período.

## Faturamento mensal

Retorna:

* valor total faturado no mês atual

---

# Banco de Dados

Persistência principal em MySQL.

Ferramenta de versionamento:

* Flyway

---

# Elasticsearch

Produtos são indexados automaticamente no Elasticsearch.

Sincronização ocorre em:

* criação
* atualização
* exclusão

---

# Redis Cache

Implementado para ganho de performance.

Caches disponíveis:

* product-by-id
* product-list
* product-category
* reports

Recursos:

* TTL customizado
* Cache warming
* Cache synchronization
* Cache invalidation
* Stampede protection (`sync = true`)

---

# Kafka + Outbox Pattern

Implementação de consistência eventual utilizando:

* Kafka
* Outbox Pattern

Fluxo:

```text
Pagamento do pedido
        ↓
Outbox Event
        ↓
Kafka Producer
        ↓
Kafka Consumer
        ↓
Atualização de estoque
```

---

# Observabilidade

Implementado com:

* Spring Actuator
* Micrometer
* CloudWatch

Métricas disponíveis:

* cache hits
* cache misses
* cache evictions
* latência
* métricas de pedidos
* métricas JVM

Endpoints:

```text
/actuator/health
/actuator/metrics
/actuator/prometheus
```

---

# Segurança

Implementações:

* JWT Authentication
* Role-based authorization
* Stateless session
* Password encryption com BCrypt

---

# Testes

Testes implementados:

## Unitários

* Regras de domínio
* Entidades
* Serviços

## Integração

Fluxos testados:

* criação de pedido
* pagamento
* atualização de estoque
* rollback transacional
* idempotência
* validação de estoque

---

# Estrutura do Projeto

```text
src/main/java
│
├── application
│   ├── dto
│   ├── service
│
├── domain
│   ├── entity
│   ├── enums
│   ├── exceptions
│
├── infrastructure
│   ├── repository
│   ├── security
│   ├── kafka
│   ├── elasticsearch
│   ├── config
│   ├── metrics
│
├── report
│   ├── dto
│   ├── repository
│   ├── service
```

---

# Como Executar Localmente

# Requisitos

* Java 17+
* Maven
* Docker

---

# Subir dependências

## MySQL

```bash
docker run --name mysql \
-e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_DATABASE=ecommerce \
-p 3306:3306 \
-d mysql:8
```

## Redis

```bash
docker run -d -p 6379:6379 redis
```

## Elasticsearch

```bash
docker run -d \
-p 9200:9200 \
-e "discovery.type=single-node" \
docker.elastic.co/elasticsearch/elasticsearch:8.13.4
```

## Kafka

Utilizar docker-compose ou ambiente local.

---

# Build

```bash
mvn clean install
```

---

# Executar

```bash
mvn spring-boot:run
```

---

# Docker

Build da imagem:

```bash
docker build -t product-order-api .
```

Executar:

```bash
docker run -p 8080:8080 product-order-api
```

---

# Deploy AWS

Infraestrutura preparada para:

* ECS
* ALB
* CloudWatch
* ElastiCache Redis
* RDS MySQL

---

# Endpoints Principais

## Auth

```text
POST /auth/login
POST /auth/register
```

## Produtos

```text
GET    /products
GET    /products/{id}
POST   /products
PUT    /products/{id}
DELETE /products/{id}
```

## Pedidos

```text
POST /orders
POST /orders/{id}/pay
```

## Relatórios

```text
GET /reports/top-users
GET /reports/ticket-average
GET /reports/monthly-revenue
```

---

# Status do Projeto

Projeto implementado com:

* arquitetura escalável
* segurança
* mensageria
* observabilidade
* cache distribuído
* busca full-text
* testes automatizados
* integração cloud-ready

---

# Autor

Marcos Tadeu Figueiredo Junior
