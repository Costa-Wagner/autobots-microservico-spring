# 🧪 Roteiro de testes — ATV.III (AutoBots Automanager)

Testes manuais com a aplicação em execução (`http://localhost:8080`), uma requisição por vez, conferindo status e corpo. Os testes da ATV.I e ATV.II (T01 a T33, sobre `Cliente`) estão em [testes-atvii.md](testes-atvii.md). A numeração da ATV.III continua em T34.

## Ambiente
- Windows, VS Code, Thunder Client (Body → JSON), `./mvnw clean spring-boot:run`.
- Ubuntu 24.04 (WSL2): build e amostra aprovados (T35, T54b, T76b, T104i). Os demais foram executados apenas no Windows.
- Onde consta **reiniciar**, os dados voltam ao estado do Runner. Ids de filhos podem variar entre execuções: conferir pelo GET.

## Dados iniciais (Runner)
- Empresa 1: Car service toyota ltda.
- Usuários: 1 funcionário, 2 fornecedor, 3 cliente.
- Credenciais 1, 2 e 3 (uma por usuário); veículo 1 (do usuário 3).
- Mercadoria 1; serviços 1 e 2; vendas 1 e 2.

## Resumo
- T34–T41: Empresa (CRUD) — Aprovado
- T42–T51: Empresa, telefones e endereço — Aprovado
- T52–T59: Usuário (CRUD) — Aprovado
- T60–T75: Usuário, telefones, documentos, e-mails e endereço — Aprovado
- T76–T83: Credenciais — Aprovado
- T84–T95c: Mercadoria, serviço e vínculo fornecedor — Aprovado
- T96–T99b: Veículo — Aprovado
- T100–T101: Links e isolamento entre empresas — Aprovado
- T102–T108b: Venda — Aprovado

## Empresa (T34–T51)
- T34, T35: GET lista e por id, com `links`.
- T35b: GET inexistente → 404 `Empresa não encontrada: id 999`.
- T36: POST `{}` → 400 (`razaoSocial`, `endereco`). T36b: POST completo → 201, `cadastro` preenchido, `usuarios: []`.
- T37: PUT parcial: campo em branco não sobrescreve; usuários e vendas intactos.
- T38: PUT sem id → 400 `Id é obrigatório`. T38b: id inexistente → 404.
- T39: PUT com lista de telefones: insere, atualiza e remove os ausentes.
- T40, T40b: DELETE → 200 e 404 depois; sem id → 400.
- T41: DELETE da empresa 1 apaga usuários e vendas (lista vazia).
- T42–T47: telefones da empresa: GET, POST (400 e 201), PUT parcial, DELETE e isolamento (telefone de usuário → 404).
- T48–T49c: endereço: GET e PUT parcial (branco ignorado, id imutável). T50: links navegáveis. T51: DELETE → 405.

## Usuário (T52–T75)
- T52, T53: GET lista e por id (404 para usuário ou empresa inexistente).
- T54–T54e: POST `{}` → 400; completo → 201; e-mail inválido → 400; documento repetido → 409; perfil inexistente → 400.
- T55–T55c: PUT parcial de nome, nomeSocial, perfis e endereço; `nome` em branco ignorado; inexistente → 404.
- T56: isolamento entre empresas → 404.
- T57: DELETE libera o número de documento. T58: usuário com vendas → 409. T58b: fornecedor sem vendas → 200 e mercadoria preservada.
- T60–T65: telefones do usuário (CRUD, 404 e isolamento entre usuários).
- T66–T70c: documentos (CRUD, duplicado → 409, último documento → 409, número liberado após DELETE).
- T71–T72c: e-mails (validação, CRUD, remover o último é permitido).
- T73–T75: endereço (GET, PUT parcial) e links; DELETE → 405.

## Credenciais (T76–T83)
- T76, T76b, T82: a `senha` nunca aparece nas respostas.
- T77–T77h: POST sem `tipo` ou com tipo inválido → 400; campos obrigatórios; `nomeUsuario` e `codigo` repetidos → 409; 201 com `criacao` e `inativo=false`.
- T78–T79f: GET; PUT parcial; troca de tipo → 400; conflito → 409.
- T80, T81, T83: DELETE; isolamento entre usuários; remover a última é permitido.

## Mercadoria, serviço, vínculo e veículo (T84–T101)
- T84–T90b: CRUD de mercadoria e serviço (validação, PUT parcial com valor negativo ignorado, 404).
- T91–T94: vínculo fornecedor ↔ mercadoria: só FORNECEDOR (409); associar e desassociar sem apagar a mercadoria.
- T95–T95c: excluir item que consta em venda → 409; excluir mercadoria remove o vínculo.
- T96–T99b: veículo por usuário (CRUD, tipo inválido → 400, isolamento, em venda → 409).
- T100: links da empresa sem repetição. T101: isolamento entre empresas.

## Venda (T102–T108b)
- T102, T103: GET lista e por id; `cliente` e `funcionario` resumidos; links.
- T104–T104i: POST `{}` → 400; venda vazia, perfil errado e identificação duplicada → 409; ids inexistentes → 404; completo → 201.
- T105–T105e: PUT parcial; venda vazia → 409; `clienteId` ignorado.
- T106: isolamento entre empresas. T107: links na empresa.
- T108, T108b: DELETE; sem vendas, itens e veículo podem ser excluídos.
