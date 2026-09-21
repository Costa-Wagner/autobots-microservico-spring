# 📚 DSM – Desenvolvimento de Software Multiplataforma – FATEC/SJC

Atividade prática **ATVI** – Prof. Dr. Eng. Gerson Penha.

# 🚗 AutoBots – Automanager

Microsserviço REST para cadastro de clientes de lojas de manutenção veicular e autopeças.

## 📋 Requisitos

- Java 17


## 📋 Requisitos

- Java 17
- Maven: opcional, pois o projeto inclui o Maven Wrapper (`mvnw`)

## 🛠 Tecnologias

### ⚙️ Back-end (Linguagem e Frameworks)

<p align="left">
<a href="https://openjdk.org" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/Java_17-ED8B00?style=flat&logo=openjdk&logoColor=white" alt="Java 17" height="25px">
</a>
<a href="https://spring.io/projects/spring-boot" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white" alt="Spring Boot" height="25px">
</a>
<a href="https://hibernate.org" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/Hibernate-59666C?style=flat&logo=hibernate&logoColor=white" alt="Hibernate" height="25px">
</a>
<a href="https://projectlombok.org" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/Lombok-BC4521?style=flat" alt="Lombok" height="25px">
</a>
</p>

### 🗄 Banco de dados

<p align="left">
<a href="https://www.h2database.com" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/H2_Database-09476B?style=flat" alt="H2 Database" height="25px">
</a>
</p>

### 🧰 Build

<p align="left">
<a href="https://maven.apache.org" target="_blank" rel="noopener noreferrer">
<img src="https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white" alt="Maven" height="25px">
</a>
</p>

Versões: Spring Boot 2.6.3 · Java 17 · Bean Validation (`spring-boot-starter-validation`).

## 🚀 Como executar

```bash
./mvnw spring-boot:run        # Linux, macOS e Git Bash
.\mvnw.cmd spring-boot:run    # PowerShell (Windows)
```
Execute na raiz do projeto, a pasta que contém o `pom.xml`. Na primeira execução o wrapper baixa o Maven, então é preciso ter internet. Com o Maven instalado, `mvn spring-boot:run` também funciona.

- Porta padrão: `8080`.
- O banco H2 é em memória: os dados são recriados a cada inicialização.
- Na inicialização, `AutomanagerApplication.Runner` carrega um cliente de exemplo (id 1) com 2 documentos (RG e CPF), 1 telefone e 1 endereço.

## 🗂 Estrutura do projeto

```
com.autobots.automanager
├── entidades      Cliente, Documento, Endereco, Telefone
├── repositorios   ClienteRepositorio
├── controles      ClienteControle, TelefoneControle, DocumentoControle, EnderecoControle
├── modelo         ClienteSelecionador, *Atualizador, StringVerificadorNulo
└── excecoes       TratadorDeExcecoes e exceções de negócio
```

## 🌳 Estrutura de dependências das classes

``` bash
ClienteControle ──► ClienteRepositorio ──► Cliente ─┬─► Documento
      │                                              ├─► Endereco
      ├──► ClienteSelecionador                       └─► Telefone
      └──► ClienteAtualizador ─┬─► DocumentoAtualizador
                               ├─► EnderecoAtualizador
                               └─► TelefoneAtualizador
                                   (todos usam StringVerificadorNulo)

TelefoneControle / DocumentoControle / EnderecoControle
      └──► ClienteRepositorio + Atualizador correspondente

TratadorDeExcecoes ──► captura as exceções lançadas por qualquer controle
```

## 🖥 Estrutura de funcionamento da API

``` bash
/cliente
├── GET     /lista
├── GET     /{id}
├── POST    /cadastro
├── PUT     /atualizar
├── DELETE  /excluir
└── /{clienteId}
      ├── /telefone   → GET (lista) · GET /{id} · POST · PUT /{id} · DELETE /{id}
      ├── /documento  → GET (lista) · GET /{id} · POST · PUT /{id} · DELETE /{id}
      └── /endereco   → GET · PUT
```

## 🗃 Modelo de dados

```
Cliente 1 ── * Documento
        1 ── 1 Endereco
        1 ── * Telefone
```

`Documento`, `Endereco` e `Telefone` pertencem sempre a um `Cliente` (agregado). O relacionamento usa `cascade = ALL` e `orphanRemoval = true`: salvar, atualizar ou remover o cliente propaga para as entidades filhas, e um item removido da lista é apagado do banco.

## 🏗 Decisão de arquitetura

As entidades filhas não têm CRUD solto (por exemplo, `POST /telefone`), porque não têm referência ao cliente e um registro criado sozinho ficaria sem dono. O CRUD é **aninhado no cliente**, em rotas `/cliente/{clienteId}/...`. Toda rota valida primeiro o cliente e só depois procura a entidade filha dentro dele: um telefone ou documento de outro cliente devolve 404.

## 🔌 Endpoints

### Cliente

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/cliente/lista` | Lista todos os clientes | 200 |
| GET | `/cliente/{id}` | Busca um cliente | 200, 404 |
| POST | `/cliente/cadastro` | Cadastra cliente com documentos, endereço e telefones | 201, 400, 409 |
| PUT | `/cliente/atualizar` | Atualização parcial (id no corpo) | 200, 400, 404, 409 |
| DELETE | `/cliente/excluir` | Exclui cliente e filhos (id no corpo: `{"id": 1}`) | 200, 400, 404 |

**Regras do `PUT /cliente/atualizar`:**
- Campos nulos ou em branco são ignorados (não sobrescrevem o valor atual).
- Em `documentos` e `telefones`: item sem `id` é inserido; item com `id` é atualizado; item cujo `id` **não** aparece na lista enviada é removido.
- Lista omitida ou vazia significa "sem alteração".
- Um `id` que não pertence ao cliente é ignorado.
- Não aplica `@Valid`; as validações de campos obrigatórios valem no cadastro.

### Telefone (`/cliente/{clienteId}/telefone`)

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/cliente/{clienteId}/telefone` | Lista telefones do cliente | 200, 404 |
| GET | `/cliente/{clienteId}/telefone/{telefoneId}` | Busca um telefone | 200, 404 |
| POST | `/cliente/{clienteId}/telefone` | Cadastra telefone | 201, 400, 404 |
| PUT | `/cliente/{clienteId}/telefone/{telefoneId}` | Atualização parcial | 200, 404 |
| DELETE | `/cliente/{clienteId}/telefone/{telefoneId}` | Remove telefone (pode remover o último) | 200, 404 |

### Documento (`/cliente/{clienteId}/documento`)

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/cliente/{clienteId}/documento` | Lista documentos do cliente | 200, 404 |
| GET | `/cliente/{clienteId}/documento/{documentoId}` | Busca um documento | 200, 404 |
| POST | `/cliente/{clienteId}/documento` | Cadastra documento | 201, 400, 404, 409 |
| PUT | `/cliente/{clienteId}/documento/{documentoId}` | Atualização parcial | 200, 404, 409 |
| DELETE | `/cliente/{clienteId}/documento/{documentoId}` | Remove documento | 200, 404, 409 |

O `DELETE` do último documento devolve 409: todo cliente precisa ter ao menos um documento.

### Endereço (`/cliente/{clienteId}/endereco`)

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/cliente/{clienteId}/endereco` | Busca o endereço do cliente | 200, 404 |
| PUT | `/cliente/{clienteId}/endereco` | Atualização parcial | 200, 404 |

O endereço é obrigatório e 1:1 com o cliente. Por isso não há `POST` (ele é criado no cadastro do cliente) nem `DELETE` isolado (deixaria o cliente inválido; ele é removido junto com o cliente).

## ✅ Validações (cadastro)

| Entidade | Campo | Regra |
|---|---|---|
| Cliente | `nome` | obrigatório |
| Cliente | `dataNascimento` | obrigatória (formato `yyyy-MM-dd`) |
| Cliente | `documentos` | ao menos 1 |
| Cliente | `endereco` | obrigatório |
| Documento | `tipo`, `numero` | obrigatórios; `numero` único |
| Endereço | `rua`, `numero`, `cidade` | obrigatórios |
| Telefone | `ddd`, `numero` | obrigatórios |

## ⚠️ Tratamento de erros

Todas as respostas de erro são JSON, tratadas em `TratadorDeExcecoes`.

| Status | Situação | Corpo |
|---|---|---|
| 400 | Falha de validação | `{ "campo": "mensagem" }` |
| 400 | JSON malformado | `{ "erro": "Corpo da requisição inválido ou malformado" }` |
| 400 | `id` ausente no PUT/DELETE de cliente | `{ "erro": "Id do cliente é obrigatório" }` |
| 404 | Cliente, telefone ou documento não encontrado | `{ "erro": "Cliente não encontrado: id 999" }` |
| 409 | Número de documento duplicado | `{ "erro": "Dado duplicado ou inválido (ex: número de documento já cadastrado)" }` |
| 409 | Remoção do último documento | `{ "erro": "Cliente precisa ter ao menos um documento" }` |
| 500 | Erro não previsto | `{ "erro": "Erro inesperado no servidor" }` |

## 📨 Exemplos

Cadastro de cliente:

```http
POST /cliente/cadastro
Content-Type: application/json

{
  "nome": "Maria Souza",
  "dataNascimento": "1990-05-20",
  "documentos": [{ "tipo": "CPF", "numero": "11122233344" }],
  "telefones": [{ "ddd": "12", "numero": "911112222" }],
  "endereco": { "rua": "Rua A", "numero": "10", "cidade": "São José dos Campos" }
}
```

Remoção de um telefone específico:

```http
DELETE /cliente/1/telefone/2
```

## 🧪 Testes

Os testes foram executados manualmente contra a API em execução, uma requisição por vez, conferindo status e corpo da resposta. O roteiro completo (passos, corpo de cada requisição, resultado esperado e obtido) está em [docs/testes.md](docs/testes.md).

| Grupo | Cobertura |
|---|---|
| Cliente | Cadastro e validações, atualização parcial com sincronização de listas, exclusão com cascade |
| Telefone | POST, GET, PUT, DELETE e isolamento entre clientes |
| Documento | POST, GET, PUT, DELETE, número duplicado e bloqueio do último documento |
| Endereço | GET e PUT parcial |

## 🖥 Compatibilidade

| Sistema | Situação |
|---|---|
| Windows (desenvolvimento) | Roteiro completo, testes 1 a 25 |
| Ubuntu 24.04 (WSL2) | Build com `./mvnw clean package` e amostra dos testes (T01, T08, T13, T18 e regra do último documento). Detalhes em [docs/testes.md](docs/testes.md) |

O `mvnw` é versionado com permissão de execução e quebra de linha LF (`.gitattributes`), o que permite rodar no Linux logo após o clone. No Ubuntu basta ter o Java 17 e o Git.

## 💾 Persistência de dados

Os dados ficam no banco **H2 em memória**, com a configuração padrão do Spring Boot (não há `application.properties` customizado). Eles são recriados a cada inicialização e populados pelo `Runner`. Não há arquivos de dados.

## 🎯 Conceitos aplicados

- Java 17 e Programação Orientada a Objetos
- Spring Boot e API REST (verbos HTTP e códigos de status)
- JPA/Hibernate: `@OneToMany`, `@OneToOne`, `cascade` e `orphanRemoval`
- Bean Validation com mensagens personalizadas
- Tratamento centralizado de exceções (`@RestControllerAdvice`)
- Padrões de projeto e SOLID: regras de negócio em classes separadas (`*Atualizador`, `ClienteSelecionador`)
- Agregado: entidades filhas acessadas apenas por meio do cliente
- Arquitetura de microsserviços