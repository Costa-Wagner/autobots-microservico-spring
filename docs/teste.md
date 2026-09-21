# 🧪 Roteiro de testes — AutoBots Automanager

Testes manuais da API com a aplicação em execução (`http://localhost:8080`), um passo por vez, conferindo status e corpo da resposta. Todos os testes executados no ambiente de desenvolvimento foram aprovados.

## Ambiente

| Ambiente | Ferramenta | Situação |
|---|---|---|
| Desenvolvimento | Cliente HTTP (Thunder Client ou similar) | Testes 1 a 25 aprovados |
| Ubuntu 24.04 | A definir | Pendente |

A coleção de requisições está em [`autobots-automanager.postman_collection.json`](autobots-automanager.postman_collection.json) no formato Postman v2.1, importável no Thunder Client, Postman ou Insomnia.

## Dados iniciais (Runner)

Ao subir a aplicação, o cliente 1 é criado com:

| Item | Valores |
|---|---|
| Cliente | id 1, Pedro Alcântara de Bragança e Bourbon, nome social Dom Pedro I |
| Documentos | id 1 (RG 123456789) e id 2 (CPF 12345678900) |
| Telefone | id 1 (21 987654321) |
| Endereço | id 1 (Av. Atlântica, 1702, Copacabana, Rio de Janeiro, CEP 22021001) |

Os testes dependem desses ids e da ordem. Onde consta **Reiniciar**, os dados voltam ao estado acima.

## Resumo

| Teste | Grupo | Descrição | Resultado |
|---|---|---|---|
| 1 | Cliente | PUT: atualizar, inserir e preservar | Aprovado |
| 2 | Cliente | PUT: remover documento omitindo o id | Aprovado |
| 3 | Cliente | PUT: número de documento duplicado | Aprovado |
| 4 | Cliente | PUT: campo omitido não apaga listas | Aprovado |
| 5 | Cliente | PUT: telefone com id que não pertence ao cliente | Aprovado |
| 6 | Cliente | PUT: cliente inexistente | Aprovado |
| 7 | Cliente | PUT: endereço parcial pelo cliente | Aprovado |
| 8 | Cliente | DELETE: excluir cliente e conferir cascade | Aprovado |
| 9 | Cliente | DELETE: cliente inexistente | Aprovado |
| 10 | Cliente | DELETE: corpo sem id (antes do ajuste) | Aprovado |
| 11 | Cliente | DELETE: corpo sem id (após o ajuste) | Aprovado |
| 12 | Cliente | PUT: corpo sem id | Aprovado |
| 13 | Telefone | POST de telefone | Aprovado |
| 14 | Telefone | GET por id e 404 | Aprovado |
| 15 | Telefone | PUT de telefone (parcial) | Aprovado |
| 16 | Telefone | DELETE de telefone | Aprovado |
| 17 | Telefone | Isolamento entre clientes | Aprovado |
| 18 | Documento | POST de documento | Aprovado |
| 19 | Documento | GET por id e 404 | Aprovado |
| 20 | Documento | PUT de documento (parcial) | Aprovado |
| 21 | Documento | DELETE de documento e bloqueio do último | Aprovado |
| 22 | Endereço | GET de endereço | Aprovado |
| 23 | Endereço | PUT de endereço (parcial) | Aprovado |
| 24 | Cliente | POST: validação aninhada (sem cidade) | Aprovado |
| 25 | Cliente | POST: cadastro completo e dataCadastro | Aprovado |

## Teste 1 — PUT: atualizar, inserir e preservar

**Grupo:** Cliente · **Pré-condição:** Reiniciar a aplicação (volta ao estado do Runner).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /cliente/1` | — | 200. Documentos 1 e 2 e telefone 1 existentes. |
| 2 | `PUT /cliente/atualizar` | `{"id": 1, "nome": "Teste", "telefones": [{"id": 1, "numero": "111"}, {"ddd": "11", "numero": "222"}], "documentos": [{"id": 1}, {"id": 2}]}` | 200 sem corpo. |
| 3 | `GET /cliente/1` | — | nome "Teste"; telefone 1 com ddd "21" e numero "111"; novo telefone id 2 (11/222); documentos 1 e 2 inalterados. |

**Resultado:** Aprovado

## Teste 2 — PUT: remover documento omitindo o id

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/atualizar` | `{"id": 1, "telefones": [{"id": 1}, {"id": 2}], "documentos": [{"id": 1}]}` | 200 sem corpo. |
| 2 | `GET /cliente/1` | — | Documento 2 removido; documento 1 e telefones 1 e 2 intactos. |

**Resultado:** Aprovado

## Teste 3 — PUT: número de documento duplicado

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/atualizar` | `{"id": 1, "telefones": [{"id": 1}, {"id": 2}], "documentos": [{"id": 1}, {"tipo": "CPF", "numero": "123456789"}]}` | 409 {"erro": "Dado duplicado ou inválido (ex: número de documento já cadastrado)"}. |
| 2 | `GET /cliente/1` | — | Documentos continuam só com o 1 (CPF novo não gravado). |

**Resultado:** Aprovado

## Teste 4 — PUT: campo omitido não apaga listas

**Grupo:** Cliente · **Pré-condição:** Reiniciar a aplicação (volta ao estado do Runner). Exige o ajuste de lista vazia nos atualizadores.

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /cliente/1` | — | Anotar documentos e telefones. |
| 2 | `PUT /cliente/atualizar` | `{"id": 1, "nome": "Teste 4"}` | 200 sem corpo. |
| 3 | `GET /cliente/1` | — | nome "Teste 4"; documentos e telefones idênticos ao primeiro GET. |

**Resultado:** Aprovado

## Teste 5 — PUT: telefone com id que não pertence ao cliente

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/atualizar` | `{"id": 1, "telefones": [{"id": 1}, {"id": 999, "numero": "000"}]}` | 200 sem erro. |
| 2 | `GET /cliente/1` | — | Telefone 1 intacto; nenhum telefone com id 999 ou número 000. |

**Resultado:** Aprovado

## Teste 6 — PUT: cliente inexistente

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/atualizar` | `{"id": 999, "nome": "Inexistente"}` | 404 {"erro": "Cliente não encontrado: id 999"} |

**Resultado:** Aprovado

## Teste 7 — PUT: endereço parcial pelo cliente

**Grupo:** Cliente · **Pré-condição:** Reiniciar a aplicação (volta ao estado do Runner). Exige o ajuste do codigoPostal no EnderecoAtualizador.

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /cliente/1` | — | Documento 2 com tipo "CPF"; anotar o endereço. |
| 2 | `PUT /cliente/atualizar` | `{"id": 1, "endereco": {"rua": "Rua Nova", "codigoPostal": "11111000"}}` | 200 sem corpo. |
| 3 | `GET /cliente/1` | — | rua "Rua Nova" e codigoPostal "11111000"; demais campos do endereço, documentos e telefones inalterados. |

**Resultado:** Aprovado

## Teste 8 — DELETE: excluir cliente e conferir cascade

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /cliente/cadastro` | `{"nome": "Cliente Delete", "dataNascimento": "2000-01-01", "documentos": [{"tipo": "CPF", "numero": "999888777"}], "telefones": [{"ddd": "12", "numero": "911112222"}], "endereco": {"rua": "Rua A", "numero": "10", "cidade": "Sao Jose dos Campos"}}` | 201 com o cliente no corpo. Anotar o id (usado no passo 2; aqui 2). |
| 2 | `DELETE /cliente/excluir` | `{"id": 2}` | 200 sem corpo. |
| 3 | `GET /cliente/2` | — | 404 {"erro": "Cliente não encontrado: id 2"}. |
| 4 | `POST /cliente/cadastro` | `{"nome": "Cliente Delete", "dataNascimento": "2000-01-01", "documentos": [{"tipo": "CPF", "numero": "999888777"}], "telefones": [{"ddd": "12", "numero": "911112222"}], "endereco": {"rua": "Rua A", "numero": "10", "cidade": "Sao Jose dos Campos"}}` | 201. O número 999888777 é único: o 201 prova que o documento foi apagado do banco. |
| 5 | `GET /cliente/lista` | — | Cliente 1 e o novo cliente, sem o excluído repetido. |

**Resultado:** Aprovado

## Teste 9 — DELETE: cliente inexistente

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `DELETE /cliente/excluir` | `{"id": 999}` | 404 {"erro": "Cliente não encontrado: id 999"} |
| 2 | `GET /cliente/lista` | — | Lista igual à anterior; nada apagado. |

**Resultado:** Aprovado

## Teste 10 — DELETE: corpo sem id (antes do ajuste)

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior). Executado antes da criação de IdNaoInformadoException.

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `DELETE /cliente/excluir` | `{}` | 500 {"erro": "Erro inesperado no servidor"} (findById(null)). |
| 2 | `DELETE /cliente/excluir` | `{"nome": "qualquer"}` | 500, mesmo resultado. |
| 3 | `GET /cliente/lista` | — | Nenhum cliente apagado. Este resultado motivou o ajuste dos testes 11 e 12. |

**Resultado:** Aprovado

## Teste 11 — DELETE: corpo sem id (após o ajuste)

**Grupo:** Cliente · **Pré-condição:** Reiniciar a aplicação com IdNaoInformadoException e o handler aplicados.

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `DELETE /cliente/excluir` | `{}` | 400 {"erro": "Id do cliente é obrigatório"}. |
| 2 | `GET /cliente/lista` | — | Nenhum cliente apagado. |

**Resultado:** Aprovado

## Teste 12 — PUT: corpo sem id

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/atualizar` | `{"nome": "Sem id"}` | 400 {"erro": "Id do cliente é obrigatório"}. |
| 2 | `GET /cliente/1` | — | Nome continua "Pedro Alcântara de Bragança e Bourbon". |

**Resultado:** Aprovado

## Teste 13 — POST de telefone

**Grupo:** Telefone · **Pré-condição:** Reiniciar a aplicação (volta ao estado do Runner).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /cliente/1/telefone` | `{}` | 400 {"ddd": "DDD é obrigatório", "numero": "Número é obrigatório"} (ordem dos campos livre). |
| 2 | `POST /cliente/1/telefone` | `{"ddd": "11", "numero": "222"}` | 201 com o telefone no corpo, id 2. |
| 3 | `GET /cliente/1/telefone` | — | Telefones 1 e 2, nenhum vazio. |

**Resultado:** Aprovado

## Teste 14 — GET por id e 404

**Grupo:** Telefone · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /cliente/1/telefone/2` | — | 200 {"id": 2, "ddd": "11", "numero": "222"}. |
| 2 | `GET /cliente/1/telefone/999` | — | 404 {"erro": "Telefone não encontrado: id 999"}. |
| 3 | `GET /cliente/999/telefone/1` | — | 404 {"erro": "Cliente não encontrado: id 999"} |

**Resultado:** Aprovado

## Teste 15 — PUT de telefone (parcial)

**Grupo:** Telefone · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/1/telefone/2` | `{"numero": "333"}` | 200 sem corpo. |
| 2 | `GET /cliente/1/telefone/2` | — | {"id": 2, "ddd": "11", "numero": "333"}. |
| 3 | `PUT /cliente/1/telefone/2` | `{}` | 200; um GET seguinte mostra o telefone inalterado. |
| 4 | `PUT /cliente/1/telefone/999` | `{"numero": "1"}` | 404 {"erro": "Telefone não encontrado: id 999"}. |

**Resultado:** Aprovado

## Teste 16 — DELETE de telefone

**Grupo:** Telefone · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `DELETE /cliente/1/telefone/2` | — | 200 sem corpo. |
| 2 | `GET /cliente/1/telefone` | — | Só o telefone 1. |
| 3 | `DELETE /cliente/1/telefone/2` | — | 404 {"erro": "Telefone não encontrado: id 2"}. |
| 4 | `DELETE /cliente/1/telefone/1` | — | 200 (remover o último é permitido). |
| 5 | `GET /cliente/1/telefone` | — | [] |
| 6 | `GET /cliente/1` | — | Cliente existe com telefones []; documentos e endereço intactos. |

**Resultado:** Aprovado

## Teste 17 — Isolamento entre clientes

**Grupo:** Telefone · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /cliente/cadastro` | `{"nome": "Cliente Isolamento", "dataNascimento": "2000-01-01", "documentos": [{"tipo": "CPF", "numero": "555444333"}], "telefones": [{"ddd": "12", "numero": "911112222"}], "endereco": {"rua": "Rua B", "numero": "20", "cidade": "Sao Jose dos Campos"}}` | 201. Cliente 2 com telefone 3 (anotar os ids). |
| 2 | `GET /cliente/1/telefone/3` | — | 404 {"erro": "Telefone não encontrado: id 3"}. |
| 3 | `PUT /cliente/1/telefone/3` | `{"numero": "000"}` | 404. |
| 4 | `DELETE /cliente/1/telefone/3` | — | 404. |
| 5 | `GET /cliente/2/telefone/3` | — | 200 {"id": 3, "ddd": "12", "numero": "911112222"}; não alterado nem apagado. |

**Resultado:** Aprovado

## Teste 18 — POST de documento

**Grupo:** Documento · **Pré-condição:** Reiniciar a aplicação (volta ao estado do Runner).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /cliente/1/documento` | `{}` | 400 {"tipo": "Tipo é obrigatório", "numero": "Número é obrigatório"} (ordem livre). |
| 2 | `POST /cliente/1/documento` | `{"tipo": "CNH", "numero": "123456789"}` | 409 (número já existente). |
| 3 | `POST /cliente/1/documento` | `{"tipo": "CNH", "numero": "111222333"}` | 201 com o documento no corpo. Anotar o id (3 ou 4). |
| 4 | `GET /cliente/1/documento` | — | Documentos 1, 2 e o novo, sem vazios nem duplicados. |

**Resultado:** Aprovado

## Teste 19 — GET por id e 404

**Grupo:** Documento · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /cliente/1/documento/2` | — | 200 {"id": 2, "tipo": "CPF", "numero": "12345678900"}. |
| 2 | `GET /cliente/1/documento/999` | — | 404 {"erro": "Documento não encontrado: id 999"}. |
| 3 | `GET /cliente/999/documento/1` | — | 404 {"erro": "Cliente não encontrado: id 999"} |

**Resultado:** Aprovado

## Teste 20 — PUT de documento (parcial)

**Grupo:** Documento · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/1/documento/2` | `{"numero": "98765432100"}` | 200 sem corpo. |
| 2 | `GET /cliente/1/documento/2` | — | {"id": 2, "tipo": "CPF", "numero": "98765432100"}. |
| 3 | `PUT /cliente/1/documento/2` | `{}` | 200; documento inalterado. |
| 4 | `PUT /cliente/1/documento/2` | `{"numero": "123456789"}` | 409 (número do documento 1). Um GET seguinte mantém 98765432100. |
| 5 | `PUT /cliente/1/documento/999` | `{"numero": "1"}` | 404 {"erro": "Documento não encontrado: id 999"}. |

**Resultado:** Aprovado

## Teste 21 — DELETE de documento e bloqueio do último

**Grupo:** Documento · **Pré-condição:** Não reiniciar (usa o estado do teste anterior). Passo 4 usa o id do CNH criado no teste 18 (aqui 3).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `DELETE /cliente/1/documento/2` | — | 200 sem corpo. |
| 2 | `GET /cliente/1/documento` | — | Só o documento 1 e o CNH. |
| 3 | `DELETE /cliente/1/documento/2` | — | 404 {"erro": "Documento não encontrado: id 2"}. |
| 4 | `DELETE /cliente/1/documento/3` | — | 200. Restam só o documento 1. |
| 5 | `DELETE /cliente/1/documento/1` | — | 409 {"erro": "Cliente precisa ter ao menos um documento"}. |
| 6 | `GET /cliente/1/documento` | — | Documento 1 continua. |
| 7 | `POST /cliente/1/documento` | `{"tipo": "CPF", "numero": "98765432100"}` | 201. O número era do documento 2 apagado: prova que a linha saiu do banco. |

**Resultado:** Aprovado

## Teste 22 — GET de endereço

**Grupo:** Endereço · **Pré-condição:** Reiniciar a aplicação (volta ao estado do Runner).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `GET /cliente/1/endereco` | — | 200 com o endereço do Runner, id 1. |
| 2 | `GET /cliente/999/endereco` | — | 404 {"erro": "Cliente não encontrado: id 999"} |

**Resultado:** Aprovado

## Teste 23 — PUT de endereço (parcial)

**Grupo:** Endereço · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `PUT /cliente/1/endereco` | `{"rua": "Rua Nova", "codigoPostal": "11111000"}` | 200 sem corpo. |
| 2 | `GET /cliente/1/endereco` | — | rua "Rua Nova" e codigoPostal "11111000"; demais campos como no Runner. |
| 3 | `PUT /cliente/1/endereco` | `{}` | 200; endereço inalterado. |
| 4 | `PUT /cliente/1/endereco` | `{"cidade": "   "}` | 200; cidade continua "Rio de Janeiro". |
| 5 | `PUT /cliente/1/endereco` | `{"id": 50, "rua": "Rua Id"}` | 200; rua "Rua Id" e id do endereço continua 1. |
| 6 | `PUT /cliente/999/endereco` | `{"rua": "X"}` | 404 {"erro": "Cliente não encontrado: id 999"} |
| 7 | `GET /cliente/1` | — | Endereço igual ao do passo 5; documentos e telefones intactos. |

**Resultado:** Aprovado

## Teste 24 — POST: validação aninhada (sem cidade)

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior).

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /cliente/cadastro` | `{"nome": "Teste", "nomeSocial": "", "dataNascimento": "1980-11-11", "documentos": [{"tipo": "RG", "numero": "987321"}], "endereco": {"rua": "Rua 1", "numero": "100"}, "telefones": [{"ddd": "21", "numero": "987654321"}]}` | 400 {"endereco.cidade": "Cidade é obrigatória"}. |

**Resultado:** Aprovado

## Teste 25 — POST: cadastro completo e dataCadastro

**Grupo:** Cliente · **Pré-condição:** Não reiniciar (usa o estado do teste anterior). Exige setDataCadastro no ClienteControle.

| # | Requisição | Corpo | Esperado |
|---|---|---|---|
| 1 | `POST /cliente/cadastro` | `{"nome": "Teste", "nomeSocial": "", "dataNascimento": "1980-11-11", "documentos": [{"tipo": "RG", "numero": "987321"}], "endereco": {"rua": "Rua 1", "numero": "100", "cidade": "Rio de Janeiro"}, "telefones": [{"ddd": "21", "numero": "987654321"}]}` | 201 com dataCadastro preenchida (data atual). |
| 2 | `GET /cliente/lista` | — | O novo cliente aparece com dataCadastro diferente de null. |

**Resultado:** Aprovado