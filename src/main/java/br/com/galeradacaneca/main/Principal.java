package br.com.galeradacaneca.main;

import br.com.galeradacaneca.dao.ClienteDAO;
import br.com.galeradacaneca.dao.ProdutoDAO;
import br.com.galeradacaneca.dao.VendaDAO;
import br.com.galeradacaneca.dao.VendedorDAO;
import br.com.galeradacaneca.dao.impl.ClienteDAOImpl;
import br.com.galeradacaneca.dao.impl.ProdutoDAOImpl;
import br.com.galeradacaneca.dao.impl.VendaDAOImpl;
import br.com.galeradacaneca.dao.impl.VendedorDAOImpl;
import br.com.galeradacaneca.model.*;
import br.com.galeradacaneca.service.ClienteService;
import br.com.galeradacaneca.service.ProdutoService;
import br.com.galeradacaneca.service.VendaService;
import br.com.galeradacaneca.service.VendedorService;
import br.com.galeradacaneca.service.impl.ClienteServiceImpl;
import br.com.galeradacaneca.service.impl.ProdutoServiceImpl;
import br.com.galeradacaneca.service.impl.VendaServiceImpl;
import br.com.galeradacaneca.service.impl.VendedorServiceImpl;
import br.com.galeradacaneca.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Ponto de entrada do projeto refatorado — Galera da Caneca v2.
 *
 * O método main() contém testes funcionais que certificam que todas
 * as camadas (Model → DAO → Service) estão operando corretamente,
 * sem dependência de interface gráfica.
 *
 * Arquitetura em camadas aplicada:
 *   View (futura web) → Service → DAO → Banco de dados
 *
 * Injeção de Dependência:
 *   Cada Service recebe seu DAO via construtor, respeitando DIP.
 */
public class Principal {

    public static void main(String[] args) {

        System.out.println("=== Galera da Caneca — Projeto Refatorado v2.0 ===\n");
        try {
            JPAUtil.getFactory();
            System.out.println("[OK] Conexão com banco de dados estabelecida.");
        } catch (Exception e) {
            System.err.println("[ERRO] Não foi possível conectar ao banco de dados.");
            System.err.println("       Certifique-se de que o MySQL está rodando e o banco 'Galera_da_Caneca' existe.");
            System.err.println("       Detalhe: " + e.getMessage());
            System.exit(1);
        }
      
        ClienteDAO  clienteDAO  = new ClienteDAOImpl();
        ProdutoDAO  produtoDAO  = new ProdutoDAOImpl();
        VendedorDAO vendedorDAO = new VendedorDAOImpl();
        VendaDAO    vendaDAO    = new VendaDAOImpl();

        ClienteService  clienteService  = new ClienteServiceImpl(clienteDAO);
        ProdutoService  produtoService  = new ProdutoServiceImpl(produtoDAO);
        VendedorService vendedorService = new VendedorServiceImpl(vendedorDAO);
        VendaService    vendaService    = new VendaServiceImpl(vendaDAO);

        System.out.println("[OK] Camadas instanciadas via Injeção de Dependência.\n");
 
        int erros = 0;

        // Teste 1: Listar clientes
        try {
            var clientes = clienteService.listarTodos();
            System.out.println("[TESTE 1] listarTodos (Cliente): " + clientes.size() + " registro(s) encontrado(s). [OK]");
        } catch (Exception e) {
            System.err.println("[TESTE 1] FALHOU: " + e.getMessage());
            erros++;
        }

        // Teste 2: Listar produtos
        try {
            var produtos = produtoService.listarTodos();
            System.out.println("[TESTE 2] listarTodos (Produto): " + produtos.size() + " registro(s) encontrado(s). [OK]");
        } catch (Exception e) {
            System.err.println("[TESTE 2] FALHOU: " + e.getMessage());
            erros++;
        }

        // Teste 3: Listar vendedores
        try {
            var vendedores = vendedorService.listarTodos();
            System.out.println("[TESTE 3] listarTodos (Vendedor): " + vendedores.size() + " registro(s) encontrado(s). [OK]");
        } catch (Exception e) {
            System.err.println("[TESTE 3] FALHOU: " + e.getMessage());
            erros++;
        }

        // Teste 4: Listar vendas e total geral
        try {
            var vendas = vendaService.listarTodas();
            BigDecimal total = vendaService.totalGeral();
            System.out.println("[TESTE 4] listarTodas (Venda): " + vendas.size() + " venda(s). Total geral: R$ " + total + " [OK]");
        } catch (Exception e) {
            System.err.println("[TESTE 4] FALHOU: " + e.getMessage());
            erros++;
        }

        // Teste 5: Validação de negócio — cliente com CPF inválido deve lançar exceção
        try {
            Cliente clienteInvalido = new Cliente();
            clienteInvalido.setNomeCompleto("Teste Inválido");
            clienteInvalido.setCpf("123"); 
            clienteInvalido.setEmail("teste@email.com");
            clienteInvalido.setNascimento(LocalDate.of(1990, 1, 1));
            clienteInvalido.setTelefone("51999999999");
            clienteInvalido.setEndereco("Rua Teste, 1");
            clienteInvalido.setSenha("senha123");
            clienteInvalido.setSexo("M");
            clienteService.cadastrar(clienteInvalido);
            System.err.println("[TESTE 5] FALHOU: deveria ter lançado exceção para CPF inválido.");
            erros++;
        } catch (IllegalArgumentException e) {
            System.out.println("[TESTE 5] Validação de CPF inválido capturada corretamente: \"" + e.getMessage() + "\" [OK]");
        }
       
        try {
            Produto produtoInvalido = new Produto();
            produtoInvalido.setNomeProd("Caneca Teste");
            produtoInvalido.setPreco(new BigDecimal("-10.00"));
            produtoService.cadastrar(produtoInvalido);
            System.err.println("[TESTE 6] FALHOU: deveria ter lançado exceção para preço negativo.");
            erros++;
        } catch (IllegalArgumentException e) {
            System.out.println("[TESTE 6] Validação de preço negativo capturada corretamente: \"" + e.getMessage() + "\" [OK]");
        }

        // Teste 7: Autenticação com credenciais vazias retorna Optional.empty()
        try {
            Optional<Vendedor> resultado = vendedorService.autenticar("", "");
            if (resultado.isEmpty()) {
                System.out.println("[TESTE 7] autenticar com credenciais vazias retornou Optional.empty(). [OK]");
            } else {
                System.err.println("[TESTE 7] FALHOU: deveria retornar Optional.empty() para credenciais vazias.");
                erros++;
            }
        } catch (Exception e) {
            System.err.println("[TESTE 7] FALHOU com exceção inesperada: " + e.getMessage());
            erros++;
        }
       
        try {
            vendedorService.alterarSenha(1, "ab"); 
            System.err.println("[TESTE 8] FALHOU: deveria ter lançado exceção para senha curta.");
            erros++;
        } catch (IllegalArgumentException e) {
            System.out.println("[TESTE 8] Validação de senha curta capturada corretamente: \"" + e.getMessage() + "\" [OK]");
        }
      
        System.out.println("\n=== Resultado: " + (erros == 0 ? "TODOS OS TESTES PASSARAM" : erros + " TESTE(S) FALHARAM") + " ===");
        
        JPAUtil.close();
        System.out.println("\n[OK] EntityManagerFactory encerrado. Aplicação finalizada.");
    }
}
