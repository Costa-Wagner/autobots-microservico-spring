# 🧪 Roteiro de testes — ATV.IV (AutoBots Automanager)

Testes manuais com a aplicação em execução (`http://localhost:8080`), por `curl` no Git Bash, conferindo o status (e o corpo, onde indicado). Os testes da ATV.III (T34 a T108b) estão em [testes-atviii.md](testes-atviii.md) e os da ATV.I e ATV.II (T01 a T33), em [testes-atvii.md](testes-atvii.md). A numeração da ATV.IV continua em T109.

## Ambiente
- Windows, VS Code, Git Bash, `./mvnw clean spring-boot:run`.
- Cada teste usa o token do perfil indicado (login com a senha `123456`).
- Ubuntu 24.04 (WSL2): amostra da ATV.IV ainda não executada.
- Onde consta **reiniciar**, os dados voltam ao estado do Runner. Ids de filhos podem variar entre execuções.

## Dados iniciais (Runner)
- Empresa 1: Car service toyota ltda.
- Usuários: 1 vendedor (`dompedrofuncionario`), 2 fornecedor (`dompedrofornecedor`), 3 cliente (`dompedrocliente`), 4 admin (`admin`), 5 gerente (`gerente`).
- Uma credencial por usuário, todas com senha `123456` (BCrypt no banco); veículo 1 (do usuário 3).
- Mercadoria 1; serviços 1 e 2; vendas 1 e 2 (cliente 3, vendedor 1).

## Resumo
- T109–T115: Autenticação (login, token, senha com hash) — Aprovado
- T116–T138: Autorização por perfil e por empresa — Aprovado
- T139–T177: Autorização por dado (hierarquia de usuários, filhos do usuário, vendas) — Aprovado
- T178: Venda do vendedor sem `funcionarioId` — Aprovado

## Autenticação (T109–T115)
- T109: `POST /login` com `admin`/`123456` → 200, header `Authorization: Bearer ...` e corpo `{"token": "..."}`.
- T110: `POST /login` com senha errada → 401.
- T111: `GET /empresa/lista` sem token → 401 `{"erro": "Autenticação necessária ou token inválido"}`.
- T112: `GET /empresa/lista` com o token do T109 → 200.
- T113: o mesmo GET com o token alterado (um caractere a mais) → 401.
- T114: `POST /empresa/1/usuario/3/credencial` (`USUARIO_SENHA`, nome único) → 201, sem `senha` na resposta; o login com essa credencial → 200.
- T115: `PUT` da credencial com `senha` nova → 200; login com a nova → 200 e com a antiga → 401.

## Autorização por perfil e por empresa (T116–T138)
Esperado, por teste:
- T116: admin `GET /empresa/lista` → 200.
- T117: gerente `GET /empresa/lista` → 403.
- T118: gerente `GET /empresa/1` → 200.
- T119: vendedor `GET /empresa/1` → 403.
- T120: gerente `PUT /empresa/atualizar` → 403.
- T121: cliente `GET /empresa/1/endereco` → 200.
- T122: cliente `PUT /empresa/1/endereco` → 403.
- T123: gerente `PUT /empresa/1/endereco` → 403.
- T124: vendedor `GET /empresa/1/servico` → 200.
- T125: vendedor `POST /empresa/1/servico` → 403.
- T126: gerente `POST /empresa/1/servico` → 201.
- T127: cliente `GET /empresa/1/servico` → 403.
- T128: vendedor `GET /empresa/1/mercadoria` → 200.
- T129: vendedor `DELETE /empresa/1/mercadoria/1` → 403.
- T130: gerente `GET /empresa/1/venda` → 200.
- T131: vendedor `GET /empresa/1/venda` → 200 (só as suas).
- T132: cliente `GET /empresa/1/venda` → 200 (só as suas).
- T133: fornecedor `GET /empresa/1/servico` → 403 (token válido, sem permissão).
- T134: gerente `GET /empresa/1/usuario/3/telefone` → 200.
- T135: vendedor `GET /empresa/1/usuario/3/telefone` → 200 (cliente é gerenciável pelo vendedor).
- T136: admin `POST /empresa/cadastro` → 201 (empresa 2).
- T137: gerente da empresa 1 `GET /empresa/2/servico` → 403 (outra empresa).
- T138: admin `GET /empresa/2/servico` → 200 (admin é global).

## Autorização por dado (T139–T177)

**Hierarquia de usuários (T139–T157)**
- T139: gerente `GET /empresa/1/usuario` → lista sem o admin.
- T140: admin `GET /empresa/1/usuario` → lista com o admin.
- T141: gerente `GET /empresa/1/usuario/4` (admin) → 403.
- T142: admin `GET /empresa/1/usuario/4` → 200.
- T143: gerente `PUT /empresa/1/usuario/3` (cliente) → 200.
- T144: gerente `PUT /empresa/1/usuario/4` (admin) → 403.
- T145: gerente `PUT /empresa/1/usuario/3` com `"perfis":["ADMIN"]` → 403 (sem escalada de privilégio).
- T146: gerente `DELETE /empresa/1/usuario/2` (fornecedor) → 403.
- T147: gerente `POST /empresa/1/usuario` com perfil ADMIN → 403.
- T148: vendedor `GET /empresa/1/usuario/3` (cliente) → 200.
- T149: vendedor `GET /empresa/1/usuario/1` (ele mesmo) → 200.
- T150: vendedor `GET /empresa/1/usuario/5` (gerente) → 403.
- T151: vendedor `PUT /empresa/1/usuario/5` → 403.
- T152: vendedor `POST /empresa/1/usuario` com perfil GERENTE → 403.
- T153: vendedor `POST /empresa/1/usuario` com perfil CLIENTE → 201.
- T154: cliente `GET /empresa/1/usuario` (lista) → 403.
- T155: cliente `GET /empresa/1/usuario/3` (ele mesmo) → 200.
- T156: cliente `GET /empresa/1/usuario/1` → 403.
- T157: cliente `PUT /empresa/1/usuario/3` → 403.

**Filhos do usuário (T158–T166)**
- T158: cliente `GET /empresa/1/usuario/3/telefone` → 200.
- T159: cliente `GET /empresa/1/usuario/1/telefone` → 403.
- T160: cliente `POST /empresa/1/usuario/3/telefone` → 403.
- T161: vendedor `POST /empresa/1/usuario/3/telefone` → 201.
- T162: vendedor `GET /empresa/1/usuario/5/telefone` (gerente) → 403.
- T163: gerente `POST /empresa/1/usuario/4/credencial` (admin) → 403.
- T164: vendedor `POST` de credencial para o cliente criado no T153 → 201.
- T165: gerente `GET /empresa/1/usuario/2/mercadoria` → 200.
- T166: vendedor `GET /empresa/1/usuario/2/mercadoria` → 403.

**Vendas (T167–T177)**
- T167: cliente `GET /empresa/1/venda` → 200.
- T168: cliente `GET /empresa/1/venda/1` (venda dele) → 200.
- T169: cliente `POST /empresa/1/venda` → 403.
- T170: cliente novo (T153/T164) `GET /empresa/1/venda` → nenhuma venda listada.
- T171: cliente novo `GET /empresa/1/venda/1` (venda de outro) → 403.
- T172: vendedor `GET /empresa/1/venda/1` (venda dele) → 200.
- T173: vendedor `PUT /empresa/1/venda/1` → 403.
- T174: vendedor `DELETE /empresa/1/venda/1` → 403.
- T175: vendedor `POST /empresa/1/venda` com `funcionarioId: 5` → 201; a venda sai no nome do vendedor (o id do corpo é ignorado).
- T176: gerente `POST /empresa/1/venda` sem `funcionarioId` → 409.
- T177: gerente `POST /empresa/1/venda` com `funcionarioId: 1` → 201.

## Venda do vendedor sem funcionário (T178)
- T178: vendedor `POST /empresa/1/venda` sem `funcionarioId` → 201. Antes do ajuste do `VendaDados` (remoção do `@NotNull` do funcionário), este teste e o T176 davam 400.