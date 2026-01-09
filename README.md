# Off3D Studio Management API
API robusta para o gerenciamento de estúdios de impressão 3D, focada na automação do fluxo de trabalho, desde o cadastro de clientes até a fila de impressão final.

## 🎯 Objetivos do Projeto
O sistema foi desenvolvido seguindo padrões de integridade de dados e experiência do desenvolvedor (DX):
- **Padronização de Objetos:** Todos os retornos de API (ResponseDTO) seguem o padrão de "Objeto Completo", garantindo que o front-end tenha todos os dados necessários sem múltiplas chamadas.
- **Flexibilidade de Atualização:** O sistema permite atualizações parciais (PATCH-style) via métodos PUT, preservando relacionamentos existentes caso IDs opcionais não sejam enviados.
- **Gestão de Tempo Precisa:** Uso da API java.time.Duration para o cálculo e armazenamento de tempos de impressão.

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java 21.
- **Framework:** Spring Boot 3.4.1.
- **Persistência:** Spring Data JPA / Hibernate.
- **Banco de Dados:** PostgreSQL.
- **Produtividade:** Lombok & MapStruct (ou mapeamento manual via Record).
- **Logs:** SLF4J com Logback para rastreamento de operações críticas.

## 🏗️ Arquitetura e Domínios
### Sales (Vendas)
- **Customer:** Gestão de clientes com lógica de Upsert (cadastra ou atualiza automaticamente pelo e-mail) [cite: 2025-12-30].
- **Order:** Gestão de pedidos vinculados a clientes e modelos 3D.

### Manufacturing (Manufatura)
- **PrintJob:** O coração da operação. Gerencia a fila de impressão, status e tempos estimados.
- **Printer:** Controle de hardware (Impressoras FDM/SLA) e disponibilidade.
- **Material:** Gestão de filamentos e resinas.
- **Model3D:** Armazenamento de metadados dos arquivos STL/OBJ.

## 🚀 Como Executar
### Pré-requisitos
- Java 21 instalado.
- Instância do PostgreSQL rodando.

### Configuração
1. Clone o repositório.
2. Configure o arquivo `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/off3d_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
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

- Se o e-mail já existir, o sistema atualiza o nome e telefone do cliente automaticamente, mantendo o histórico de pedidos íntegro [cite: 2025-12-30].

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