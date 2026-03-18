# Off3D Studio Management API 🚀

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?style=for-the-badge&logo=postgresql)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

API robusta para o gerenciamento de estúdios de impressão 3D, focada na automação do fluxo de trabalho, desde o cadastro de clientes até a fila de impressão final.

## 🎯 Objetivos do Projeto
O sistema foi desenvolvido seguindo padrões de integridade de dados e experiência do desenvolvedor (DX):
- **Padronização de Objetos:** Todos os retornos de API (ResponseDTO) seguem o padrão de "Objeto Completo", garantindo que o front-end tenha todos os dados necessários sem múltiplas chamadas.
- **Flexibilidade de Atualização:** O sistema permite atualizações parciais (PATCH-style) via métodos PUT, preservando relacionamentos existentes caso IDs opcionais não sejam enviados.
- **Gestão de Tempo Precisa:** Uso da API `java.time.Duration` para o cálculo e armazenamento de tempos de impressão no padrão ISO-8601.

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java 21 (LTS).
- **Framework:** Spring Boot 3.4.1.
- **Persistência:** Spring Data JPA / Hibernate.
- **Banco de Dados:** PostgreSQL.
- **Produtividade:** Lombok.
- **Segurança:** Spring Security com autenticação via **JWT** (java-jwt 4.4.0).
- **Validação:** Bean Validation.
- **Qualidade:** Conformidade total com o **SonarQube Quality Gate**.
- **Logs:** SLF4J com Logback para rastreamento de operações críticas.

## 🧪 Qualidade e Testes
O projeto adota uma estratégia de testes em pirâmide para garantir a estabilidade das entregas:
- **Testes Unitários (JUnit 5 & Mockito):** Cobertura das regras de negócio na camada de `Service` e validação de segurança no `TokenService`.
- **Testes de Integração:**
  - `@WebMvcTest`: Validação dos endpoints, serialização JSON e tratamento global de exceções.
  - `@DataJpaTest`: Garantia da integridade dos relacionamentos JPA e queries customizadas.
- **Análise Estática:** Monitoramento constante via **SonarQube** para eliminação de *Code Smells*, vulnerabilidades e manutenção de baixa dívida técnica.

## 🏗️ Arquitetura e Domínios
### Sales (Vendas)
- **Customer:** Gestão de clientes com lógica de **Upsert** (cadastra ou atualiza automaticamente pelo e-mail).
- **Order:** Gestão de pedidos vinculados a clientes e modelos 3D.

### Manufacturing (Manufatura)
- **PrintJob:** O coração da operação. Gerencia a fila de impressão, status e tempos estimados.
- **Printer:** Controle de hardware (Impressoras FDM/SLA) e disponibilidade.
- **Material:** Gestão de filamentos e resinas.
- **Model3D:** Armazenamento de metadados dos arquivos STL/OBJ.

## 🚀 Como Executar
### Pré-requisitos
- Java 21 instalado.
- Instância do PostgreSQL rodando (Docker ou Local).

### Configuração
A aplicação utiliza variáveis de ambiente para segurança, com valores padrão para facilitar o desenvolvimento local:

```properties
# Exemplo de configuração no application.properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5433/off3d_db}
spring.datasource.username=${DB_USERNAME:seu_usuario}
spring.datasource.password=${DB_PASSWORD:sua_senha}
api.security.token.secret=${JWT_SECRET:off3d-studio-secret-key-2026}
```

3. Execute a aplicação:
```
./mvnw spring-boot:run
```

## 📖 Guia de API (Exemplos)

### PrintJobs
Atualizar Status e Tempo: `PUT /print-jobs/{id}`
```
{
"status": "PRINTING",
"estimatedTime": "PT3H45M"
}
```
*Nota: O tempo utiliza o padrão ISO-8601 (PT3H45M = 3 horas e 45 minutos). O retorno da API será formatado como 03:45:00 para facilitar a leitura*

### Customers
Cadastro Inteligente: `POST /customers`

- Se o e-mail já existir, o sistema atualiza o nome e telefone do cliente automaticamente, mantendo o histórico de pedidos íntegro.

## 🔐 Segurança e Autenticação
A API utiliza **Spring Security** com autenticação via **JWT (JSON Web Token)**.

- **Fluxo de Acesso:** O usuário deve se registrar em `/auth/register` e realizar o login em `/auth/login` para obter o Bearer Token.
- **Rastreamento Automático:** Cada `Order` ou `Customer` criado é automaticamente vinculado ao ID do usuário autenticado no banco de dados para fins de auditoria interna.
- **Níveis de Acesso (Roles):**
    - `ADMIN`: Acesso total ao sistema e gestão de sócios.
    - `PARTNER`: Gestão de vendas e visualização de manufatura.
    - `OPERATOR`: Foco exclusivo na fila de impressão (`PrintJobs`).

### Exemplo de Requisição Autenticada
Para acessar rotas protegidas, inclua o token no cabeçalho da requisição:
`Authorization: Bearer eyJhbGciOiJIUzI1Ni...`

## 🛡️ Tratamento de Erros
A API possui um `RestExceptionHandler` global que trata:
- 409 Conflict: Erros de duplicidade de e-mail ou violações de integridade.
- 400 Bad Request: Erros de validação ou IDs nulos em campos obrigatórios.
- 404 Not Found: Recursos não encontrados com mensagens personalizadas.

## 🤝 Colaboradores

Agradecemos às seguintes pessoas que contribuíram para este projeto:

|                                      Foto                                       | Colaborador           | Função             | GitHub                                         |
|:-------------------------------------------------------------------------------:|:----------------------|:-------------------|:-----------------------------------------------|
| <img src="https://avatars.githubusercontent.com/u/106124434?v=4" width="50px;"> | **Amanda Paiva Lino** | Backend Developer  | [@Amandapvln](https://github.com/Amandapvln)   |

---
*Este projeto é parte integrante das soluções Off3D Studio.*
