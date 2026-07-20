package br.com.galeradacaneca.web;

import br.com.galeradacaneca.dao.*;
import br.com.galeradacaneca.dao.impl.*;
import br.com.galeradacaneca.service.*;
import br.com.galeradacaneca.service.impl.*;

/**
 * Ponto único de composição das dependências (DAO -> Service) usadas pelos
 * servlets. Evita repetir a fiação (wiring) em cada endpoint.
 *
 * SOLID — DIP: os servlets dependem das interfaces de Service, não das
 * implementações concretas.
 */
public final class AppContext {

    public static final VendedorDAO VENDEDOR_DAO   = new VendedorDAOImpl();
    public static final ProdutoDAO  PRODUTO_DAO    = new ProdutoDAOImpl();
    public static final ClienteDAO  CLIENTE_DAO    = new ClienteDAOImpl();
    public static final VendaDAO    VENDA_DAO      = new VendaDAOImpl();
    public static final CargoDAO    CARGO_DAO      = new CargoDAOImpl();
    public static final PagamentoDAO PAGAMENTO_DAO = new PagamentoDAOImpl();

    public static final VendedorService VENDEDOR_SERVICE   = new VendedorServiceImpl(VENDEDOR_DAO);
    public static final ProdutoService  PRODUTO_SERVICE    = new ProdutoServiceImpl(PRODUTO_DAO);
    public static final ClienteService  CLIENTE_SERVICE    = new ClienteServiceImpl(CLIENTE_DAO);
    public static final VendaService    VENDA_SERVICE      = new VendaServiceImpl(VENDA_DAO);
    public static final PagamentoService PAGAMENTO_SERVICE = new PagamentoServiceImpl(PAGAMENTO_DAO);

    private AppContext() {}
}
