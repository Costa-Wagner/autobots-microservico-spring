package com.autobots.automanager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.enumeracoes.TipoDocumento;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@SpringBootApplication
public class AutomanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomanagerApplication.class, args);
	}

	@Component
	public static class Runner implements ApplicationRunner {
		@Autowired
		public EmpresaRepositorio repositorio;

		private Endereco endereco(String estado, String cidade, String bairro, String rua, String numero, String cep) {
			Endereco e = new Endereco();
			e.setEstado(estado);
			e.setCidade(cidade);
			e.setBairro(bairro);
			e.setRua(rua);
			e.setNumero(numero);
			e.setCodigoPostal(cep);
			return e;
		}

		private Telefone telefone(String ddd, String numero) {
			Telefone t = new Telefone();
			t.setDdd(ddd);
			t.setNumero(numero);
			return t;
		}

		private Documento documento(TipoDocumento tipo, String numero) {
			Documento d = new Documento();
			d.setTipo(tipo);
			d.setNumero(numero);
			d.setDataEmissao(new Date());
			return d;
		}

		private Email email(String endereco) {
			Email e = new Email();
			e.setEndereco(endereco);
			return e;
		}

		private CredencialUsuarioSenha credencial(String nomeUsuario) {
			CredencialUsuarioSenha c = new CredencialUsuarioSenha();
			c.setNomeUsuario(nomeUsuario);
			c.setSenha("123456");
			c.setCriacao(new Date());
			c.setUltimoAcesso(new Date());
			c.setInativo(false);
			return c;
		}

		@Override
		public void run(ApplicationArguments args) throws Exception {
			Empresa empresa = new Empresa();
			empresa.setRazaoSocial("Car service toyota ltda");
			empresa.setNomeFantasia("Car service manutenção veicular");
			empresa.setCadastro(new Date());
			empresa.setEndereco(endereco("São Paulo", "São Paulo", "Centro", "Av. São João", "00", "01035-000"));
			empresa.getTelefones().add(telefone("11", "986454527"));

			Usuario funcionario = new Usuario();
			funcionario.setNome("Pedro Alcântara de Bragança e Bourbon");
			funcionario.setNomeSocial("Dom Pedro");
			funcionario.getPerfis().add(PerfilUsuario.FUNCIONARIO);
			funcionario.setEndereco(endereco("São Paulo", "São Paulo", "Jardins", "Av. São Gabriel", "00", "01435-001"));
			funcionario.getTelefones().add(telefone("11", "9854633728"));
			funcionario.getDocumentos().add(documento(TipoDocumento.CPF, "85647381922"));
			funcionario.getEmails().add(email("a@a.com"));
			funcionario.getCredenciais().add(credencial("dompedrofuncionario"));
			empresa.getUsuarios().add(funcionario);

			Usuario fornecedor = new Usuario();
			fornecedor.setNome("Componentes varejo de partes automotivas ltda");
			fornecedor.setNomeSocial("Loja do carro, vendas de componentes automotivos");
			fornecedor.getPerfis().add(PerfilUsuario.FORNECEDOR);
			fornecedor.setEndereco(endereco("Rio de Janeiro", "Rio de Janeiro", "Centro", "Av. República do Chile", "00", "20031-170"));
			fornecedor.getDocumentos().add(documento(TipoDocumento.CNPJ, "00014556000100"));
			fornecedor.getEmails().add(email("f@f.com"));
			fornecedor.getCredenciais().add(credencial("dompedrofornecedor"));
			empresa.getUsuarios().add(fornecedor);

			Usuario cliente = new Usuario();
			cliente.setNome("Pedro Alcântara de Bragança e Bourbon");
			cliente.setNomeSocial("Dom Pedro cliente");
			cliente.getPerfis().add(PerfilUsuario.CLIENTE);
			cliente.setEndereco(endereco("São Paulo", "São José dos Campos", "Centro", "Av. Dr. Nelson D'Ávila", "00", "12245-070"));
			cliente.getDocumentos().add(documento(TipoDocumento.CPF, "12584698533"));
			cliente.getEmails().add(email("c@c.com"));
			cliente.getCredenciais().add(credencial("dompedrocliente"));
			empresa.getUsuarios().add(cliente);

			Veiculo veiculo = new Veiculo();
			veiculo.setPlaca("ABC-0000");
			veiculo.setModelo("corolla-cross");
			veiculo.setTipo(TipoVeiculo.SUV);
			veiculo.setProprietario(cliente);
			cliente.getVeiculos().add(veiculo);

			Mercadoria roda = new Mercadoria();
			roda.setNome("Roda de liga leve modelo toyota etios");
			roda.setDescricao("Roda de liga leve original de fábrica da toyota para modelos do tipo hatch");
			roda.setCadastro(new Date());
			roda.setFabricacao(new Date());
			roda.setValidade(new Date());
			roda.setQuantidade(30);
			roda.setValor(300.0);
			empresa.getMercadorias().add(roda);
			fornecedor.getMercadorias().add(roda);

			Servico trocaRodas = new Servico();
			trocaRodas.setNome("Troca de rodas");
			trocaRodas.setDescricao("Troca das rodas do carro por novas");
			trocaRodas.setValor(50);
			Servico alinhamento = new Servico();
			alinhamento.setNome("Alinhamento de rodas");
			alinhamento.setDescricao("Alinhamento das rodas do carro");
			alinhamento.setValor(50);
			empresa.getServicos().add(trocaRodas);
			empresa.getServicos().add(alinhamento);

			Venda venda1 = new Venda();
			venda1.setCadastro(new Date());
			venda1.setIdentificacao("1234698745");
			venda1.setCliente(cliente);
			venda1.setFuncionario(funcionario);
			venda1.setVeiculo(veiculo);
			venda1.getMercadorias().add(roda);
			venda1.getServicos().add(trocaRodas);
			venda1.getServicos().add(alinhamento);
			empresa.getVendas().add(venda1);

			Venda venda2 = new Venda();   // reaproveita veículo e serviço: valida o @ManyToMany
			venda2.setCadastro(new Date());
			venda2.setIdentificacao("1234698749");
			venda2.setCliente(cliente);
			venda2.setFuncionario(funcionario);
			venda2.setVeiculo(veiculo);
			venda2.getServicos().add(alinhamento);
			empresa.getVendas().add(venda2);

			repositorio.save(empresa);
		}
	}
}