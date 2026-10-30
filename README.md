# 📚 DSM – Desenvolvimento de Software Multiplataforma – FATEC/SJC

Atividades práticas **ATV.I**, **ATV.II** e **ATV.III** - Desenvolvimento Web III · Prof. Dr. Eng. Gerson Penha.

# 🚗 AutoBots – Automanager

Microsserviço REST para gestão de lojas de manutenção veicular e autopeças.

- Microsserviço REST do **AutoBots**, startup de sistemas de gestão para lojas de manutenção veicular e venda de autopeças.
- O `automanager` é o primeiro módulo do back-end: cadastra e mantém **empresas** e, dentro delas, usuários (clientes, funcionários e fornecedores), veículos, mercadorias, serviços e vendas.

**O que o projeto entrega**
- CRUD de empresa, com usuários, veículos, mercadorias, serviços e vendas em rotas aninhadas na empresa (sem CRUD solto).
- Validação dos dados de cadastro e respostas de erro padronizadas em JSON.
- API REST no nível 3 de maturidade (RMM): as respostas trazem links (HATEOAS) para as próximas ações, o que facilita a integração com outros sistemas.

**Evolução do projeto**

| Atividade | Foco | Tag |
|---|---|---|
| ATV.I | CRUD completo, validação e tratamento de exceções | `atvi-entrega` |
| ATV.II | Maturidade REST: HATEOAS (RMM nível 3) | `atvii-entrega` |
| ATV.III | Empresa, usuários, veículos, mercadorias, serviços e vendas | `atviii-entrega` |

## 📑 Sumário

<table>
<tr>
<td valign="top">

**Visão geral**
- [Requisitos](#-requisitos)
- [Tecnologias](#-tecnologias)
- [Como executar](#-como-executar)

</td>
<td valign="top">

**Projeto**
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Estrutura de dependências das classes](#-estrutura-de-dependências-das-classes)
- [Modelo de dados](#-modelo-de-dados)
- [Decisão de arquitetura](#-decisão-de-arquitetura)
- [Arquitetura](#-arquitetura)

</td>
<td valign="top">

**API**
- [Estrutura de funcionamento da API](#-estrutura-de-funcionamento-da-api)
- [Maturidade REST (RMM) e HATEOAS](#-maturidade-rest-rmm-e-hateoas)
- [Endpoints](#-endpoints)
- [Validações (cadastro)](#-validações-cadastro)
- [Tratamento de erros](#-tratamento-de-erros)
- [Exemplos](#-exemplos)

</td>
<td valign="top">

**Qualidade e encerramento**
- [Testes](#-testes)
- [Compatibilidade](#-compatibilidade)
- [Persistência de dados](#-persistência-de-dados)
- [Conceitos aplicados](#-conceitos-aplicados)
- [Limitações conhecidas](#-limitações-conhecidas)

</td>
</tr>
</table>

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
- Na inicialização, `AutomanagerApplication.Runner` carrega uma empresa de exemplo (id 1) com 3 usuários (funcionário, fornecedor e cliente), 1 veículo, 1 mercadoria, 2 serviços e 2 vendas.

## 🗂 Estrutura do projeto

```
com.autobots.automanager
├── entidades      Empresa, Usuario, Documento, Email, Telefone, Endereco,
│                  Credencial (UsuarioSenha, CodigoBarra), Mercadoria,
│                  Servico, Veiculo, Venda
├── enumeracoes    PerfilUsuario, TipoDocumento, TipoVeiculo
├── repositorios   EmpresaRepositorio
├── controles      *Controle de cada recurso (Empresa, Usuario, Venda...)
├── modelo         BuscadorUsuario, *Atualizador, AdicionadorLink*,
│                  VendaDados, StringVerificadorNulo
└── excecoes       TratadorDeExcecoes e exceções de negócio
```

## 🌳 Estrutura de dependências das classes

``` bash
*Controle ──► EmpresaRepositorio ──► Empresa ─┬─► Usuario ─┬─► Documento, Email,
      │                                       │            │   Telefone, Endereco
      │                                       │            ├─► Credencial
      │                                       │            └─► Veiculo
      │                                       ├─► Mercadoria, Servico
      │                                       └─► Venda (usuários, veículo, itens)
      ├──► BuscadorUsuario   (empresa e usuário da rota)
      ├──► *Atualizador      (atualização parcial)
      └──► AdicionadorLink*  (HATEOAS)

TratadorDeExcecoes ──► captura as exceções lançadas por qualquer controle
```

## 🖥 Estrutura de funcionamento da API

``` bash
/empresa
├── GET /lista · GET /{id} · POST /cadastro · PUT /atualizar · DELETE /excluir
└── /{empresaId}
      ├── /telefone            → CRUD
      ├── /endereco            → GET · PUT
      ├── /mercadoria          → CRUD
      ├── /servico             → CRUD
      ├── /venda               → CRUD
      └── /usuario             → CRUD
            └── /{usuarioId}
                  ├── /telefone, /documento, /email → CRUD
                  ├── /credencial, /veiculo         → CRUD
                  ├── /endereco                     → GET · PUT
                  └── /mercadoria                   → GET · PUT /{id} · DELETE /{id}
```

CRUD = GET (lista), GET /{id}, POST, PUT /{id}, DELETE /{id}.

## 🗃 Modelo de dados

```
Empresa 1 ── * Usuario, Mercadoria, Servico, Venda, Telefone
        1 ── 1 Endereco
Usuario 1 ── * Documento, Email, Telefone, Credencial, Veiculo
        1 ── 1 Endereco
        1 ── * Mercadoria (fornecedor)
Venda   * ── 1 Usuario (cliente) e 1 Usuario (funcionário)
        * ── 0..1 Veiculo
        * ── * Mercadoria e * ── * Servico
```

Dados que pertencem a um dono usam `cascade = ALL` e `orphanRemoval = true`: remover da lista apaga do banco. `Venda` referencia usuários, veículo e itens sem cascade.

## 🏗 Decisão de arquitetura

- **Empresa é a raiz.** Todo recurso é acessado por rotas aninhadas. A rota valida a empresa e só depois procura o filho dentro dela: dados de outra empresa ou de outro usuário devolvem 404.
- **`Cliente` virou `Usuario`**, com os perfis CLIENTE, FUNCIONARIO e FORNECEDOR.
- **Listas no lugar de `Set`**, para manter os atualizadores (sincronização por `id`). `Usuario.vendas` e `Veiculo.vendas` foram removidos: as vendas já guardam cliente, funcionário e veículo.
- **Venda** usa `@ManyToMany` para mercadorias e serviços (o mesmo item aparece em várias vendas) e `@ManyToOne` para o veículo. O corpo do `POST` e do `PUT` recebe **ids**; a resposta mostra cliente e funcionário resumidos.
- **Credencial** é polimórfica: o campo `tipo` (`USUARIO_SENHA` ou `CODIGO_BARRA`) é obrigatório. A `senha` só entra, nunca sai.
- **Veículo:** `proprietario` fica fora do JSON, para evitar ciclo Usuario → veiculos → proprietario.

## 🏛 Arquitetura

O AutoBots adota **microsserviços** no back-end. O `automanager` cuida do cadastro de empresas e de tudo o que a operação de uma loja precisa. É um processo independente (`.jar` com Tomcat embutido), com banco próprio, sem estado de sessão, e se integra por HTTP/JSON com links HATEOAS.

**Camadas:** `controles` expõem as rotas e traduzem exceções em respostas HTTP; `modelo` concentra as regras (atualizadores, buscador, links); `repositorios` isolam o acesso a dados; `entidades` representam o domínio.

**Limites desta etapa:** banco em memória, sem autenticação, gateway ou comunicação com outros microsserviços.

## 🔗 Maturidade REST (RMM) e HATEOAS

| Nível | Característica | Situação |
|---|---|---|
| 0 | Um endpoint, HTTP só como transporte | Superado |
| 1 | Recursos com URIs próprias | Atendido |
| 2 | Verbos e status HTTP adequados | Atendido |
| 3 | HATEOAS: links para as próximas ações | Atendido |

As respostas de `GET` e `POST` com corpo trazem o array `links` (`rel` e `href`). `PUT` e `DELETE` respondem 200 sem corpo. Com `Accept: application/hal+json`, os links passam para `_links`.

| Recurso | Relações (`rel`) |
|---|---|
| Empresa | `self`, `empresas`, `atualizar`, `excluir`, `telefones`, `endereco`, `usuarios`, `mercadorias`, `servicos`, `vendas` |
| Usuário | `self`, `usuarios`, `empresa`, `telefones`, `documentos`, `emails`, `endereco`, `credenciais`, `veiculos`, `mercadorias` (fornecedor) |
| Telefone, documento, e-mail, credencial, veículo | `self`, plural, `usuario` (telefone da empresa: `empresa`) |
| Endereço | `self`, `usuario` ou `empresa` |
| Mercadoria, serviço | `self`, plural, `empresa` |
| Venda | `self`, `vendas`, `empresa`, `cliente`, `funcionario`, `veiculo` |

**Implementação:** as entidades estendem `RepresentationModel`. As classes `AdicionadorLink*` (pacote `modelo`) usam `linkTo(methodOn(...))`, então os links acompanham as rotas dos controllers.

## 🔌 Endpoints

### Empresa (`/empresa`)

| Método | Rota | Respostas |
|---|---|---|
| GET | `/lista` | 200 |
| GET | `/{id}` | 200, 404 |
| POST | `/cadastro` | 201, 400 |
| PUT | `/atualizar` (id no corpo) | 200, 400, 404 |
| DELETE | `/excluir` (`{"id": 1}`) | 200, 400, 404 |

- No PUT, campos nulos ou em branco são ignorados. Em `telefones`, item sem `id` é inserido, com `id` é atualizado e o `id` ausente é removido. Lista omitida ou vazia = sem alteração.
- Usuários, mercadorias, serviços e vendas são ignorados no cadastro e na atualização: têm rotas próprias.
- Excluir a empresa apaga tudo o que pertence a ela, inclusive as vendas.
- Telefones (CRUD) e endereço (GET e PUT) em `/empresa/{empresaId}/telefone` e `/endereco`.

### Usuário (base `/empresa/{empresaId}/usuario`)

| Método | Rota | Respostas |
|---|---|---|
| GET | `` e `/{id}` | 200, 404 |
| POST | `` | 201, 400, 404, 409 |
| PUT | `/{id}` | 200, 404 |
| DELETE | `/{id}` | 200, 404, 409 |

- O PUT altera `nome`, `nomeSocial`, `perfis` (substitui, se enviado) e `endereco` (parcial). Os demais dados usam as rotas abaixo.
- DELETE devolve 409 se o usuário aparece em alguma venda.

### Dados do usuário (base `/empresa/{empresaId}/usuario/{usuarioId}`)

| Recurso | Rotas | Observação |
|---|---|---|
| `/telefone` | CRUD | |
| `/documento` | CRUD | o último documento não pode ser removido (409) |
| `/email` | CRUD | |
| `/credencial` | CRUD | `tipo` obrigatório; PUT não troca o tipo (400) |
| `/veiculo` | CRUD | DELETE → 409 se consta em venda |
| `/endereco` | GET, PUT | |
| `/mercadoria` | GET; PUT e DELETE `/{id}` | vínculo do fornecedor |

- **Vínculo:** só usuário com perfil FORNECEDOR (409). Mercadoria já ligada a outro fornecedor → 409. Desvincular não apaga a mercadoria.
- **Credencial:** `criacao` e `ultimoAcesso` são controlados pelo servidor; `nomeUsuario` e `codigo` são únicos (409).

### Mercadoria e serviço (`/empresa/{empresaId}/mercadoria` e `/servico`)
CRUD completo. `DELETE` de item que consta em venda → 409. Excluir uma mercadoria remove antes o vínculo com o fornecedor.

### Venda (`/empresa/{empresaId}/venda`)
CRUD completo. O corpo recebe ids: `identificacao`, `clienteId`, `funcionarioId`, `veiculoId`, `mercadoriaIds`, `servicoIds`.
- Cliente com perfil CLIENTE e funcionário com perfil FUNCIONARIO (senão 409).
- Veículo do cliente e demais ids da própria empresa (senão 404).
- Ao menos uma mercadoria ou serviço (senão 409). `identificacao` única (409).
- No PUT, campo omitido = sem alteração; lista enviada substitui a anterior; `clienteId` é ignorado.

## ✅ Validações (cadastro)

| Entidade | Regra |
|---|---|
| Empresa | `razaoSocial` e `endereco` obrigatórios |
| Usuário | `nome`, `endereco`, ao menos 1 perfil e 1 documento |
| Documento | `tipo` (enum), `dataEmissao` e `numero` (único) |
| E-mail | `endereco` obrigatório e válido |
| Endereço | `rua`, `numero`, `cidade` |
| Telefone | `ddd`, `numero` |
| Credencial | usuário/senha: `nomeUsuario` (único) e `senha`; código de barra: `codigo` positivo (único) |
| Mercadoria | `nome`, `quantidade` ≥ 0, `valor` ≥ 0 |
| Serviço | `nome`, `valor` ≥ 0 |
| Veículo | `tipo` (enum), `modelo`, `placa` |
| Venda | `identificacao` (única), `clienteId`, `funcionarioId` |

O PUT não aplica `@Valid`: as validações de campos obrigatórios valem no cadastro.

## ⚠️ Tratamento de erros

Todas as respostas de erro são JSON, tratadas em `TratadorDeExcecoes`.

| Status | Situação | Corpo |
|---|---|---|
| 400 | Falha de validação | `{ "campo": "mensagem" }` |
| 400 | JSON malformado, enum ou `tipo` de credencial inválido | `{ "erro": "Corpo da requisição inválido ou malformado" }` |
| 400 | `id` ausente no PUT/DELETE de empresa | `{ "erro": "Id é obrigatório" }` |
| 400 | PUT trocando o tipo da credencial | `{ "erro": "O tipo da credencial não pode ser alterado" }` |
| 404 | Recurso não encontrado (ou de outro dono) | `{ "erro": "Empresa não encontrada: id 999" }` |
| 405 | Método HTTP não permitido | `{ "erro": "Método HTTP não permitido para esta rota" }` |
| 409 | Valor único repetido (documento, venda, credencial) | `{ "erro": "Dado duplicado ou inválido (...)" }` |
| 409 | Último documento, usuário com vendas, item em venda, perfil incorreto, venda vazia | `{ "erro": "<regra violada>" }` |
| 415 | Content-Type diferente de `application/json` | `{ "erro": "Tipo de conteúdo não suportado: use application/json" }` |
| 500 | Erro não previsto | `{ "erro": "Erro inesperado no servidor" }` |

## 📨 Exemplos

Cadastro de usuário:

```http
POST /empresa/1/usuario
Content-Type: application/json

{
  "nome": "Maria Souza",
  "perfis": ["CLIENTE"],
  "documentos": [{ "tipo": "CPF", "numero": "11122233344", "dataEmissao": "2020-01-10" }],
  "emails": [{ "endereco": "maria@teste.com" }],
  "endereco": { "rua": "Rua A", "numero": "10", "cidade": "São José dos Campos" }
}
```

Credencial de usuário e senha:

```http
POST /empresa/1/usuario/1/credencial

{ "tipo": "USUARIO_SENHA", "nomeUsuario": "maria", "senha": "abc123" }
```

Venda, por ids:

```http
POST /empresa/1/venda

{
  "identificacao": "V-100",
  "clienteId": 3,
  "funcionarioId": 1,
  "veiculoId": 1,
  "mercadoriaIds": [1],
  "servicoIds": [1, 2]
}
```

## 🧪 Testes

Testes manuais contra a API em execução, uma requisição por vez, conferindo status e corpo.

- **ATV.III:** T34 a T108b em [docs/testes.md](docs/testes.md). Todos aprovados.
- **ATV.I e ATV.II:** T01 a T33 (cliente) em [docs/testes-atvii.md](docs/testes-atvii.md), válidos para a tag `atvii-entrega`.

| Grupo | Cobertura |
|---|---|
| Empresa | CRUD, telefones, endereço e links |
| Usuário | CRUD, filhos, regras de exclusão e isolamento |
| Credencial | Polimorfismo, senha oculta, unicidade |
| Mercadoria, serviço, veículo | CRUD, vínculo do fornecedor, exclusão com vendas |
| Venda | Validação por perfil, itens, veículo e links |

## 🖥 Compatibilidade

| Sistema | Situação |
|---|---|
| Windows (desenvolvimento) | Testes T34 a T108b |
| Ubuntu 24.04 (WSL2) | Build com `./mvnw clean package` e amostra dos testes (T35, T54b, T76b, T104i) |

O `mvnw` é versionado com permissão de execução e quebra de linha LF (`.gitattributes`). No Linux basta ter o Java 17 e o Git.

## 💾 Persistência de dados

Os dados ficam no banco **H2 em memória**: são recriados a cada inicialização e populados pelo `Runner`. O `application.properties` desliga só a validação feita pelo JPA na gravação (`spring.jpa.properties.javax.persistence.validation.mode=none`). Motivo: no `merge` de entidade nova, o Hibernate grava primeiro uma cópia com as coleções vazias, e `@NotEmpty` falharia. A validação (`@Valid`) continua ativa nos controllers.

## 🚧 Limitações conhecidas

- **Senha** gravada em texto puro (não é devolvida nas respostas). Hash e autenticação ficam para a atividade de segurança (JWT).
- Sem autenticação, autorização ou gateway.
- Sem baixa de estoque ao vender e sem total da venda.
- Banco em memória.
- O erro 409 de valor único repetido usa a mesma mensagem para qualquer campo.
- `GET /empresa/{id}` devolve a empresa inteira, sem paginação.

## 🎯 Conceitos aplicados

- Java 17, POO, Spring Boot e API REST (verbos e status HTTP)
- JPA/Hibernate: `@OneToMany`, `@ManyToMany`, herança `JOINED`, `cascade` e `orphanRemoval`
- Bean Validation e tratamento centralizado de exceções (`@RestControllerAdvice`)
- HATEOAS (RMM nível 3) com `linkTo(methodOn(...))`
- Padrões de projeto e SOLID: regras em classes separadas (`*Atualizador`, `BuscadorUsuario`, `AdicionadorLink*`)
- Agregado: dados acessados apenas por meio do dono
- Arquitetura de microsserviços
