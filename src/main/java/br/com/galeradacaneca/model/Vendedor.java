package br.com.galeradacaneca.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entidade Vendedor.
 *
 * SOLID — SRP: modela exclusivamente os dados de um vendedor/funcionário.
 * O método isGerente() foi mantido aqui pois é uma regra de negócio
 * intrínseca ao modelo (consulta apenas o próprio estado do objeto).
 */
@Entity
@Table(name = "vendedores")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vendedores")
    private Integer id;

    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "nascimento", nullable = false)
    private LocalDate nascimento;

    @Column(name = "email", nullable = false, unique = true, length = 45)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "sexo", length = 1)
    private String sexo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;

    public Vendedor() {}

    public Integer getId()                     { return id; }
    public void setId(Integer id)              { this.id = id; }
    public String getNomeCompleto()            { return nomeCompleto; }
    public void setNomeCompleto(String nome)   { this.nomeCompleto = nome; }
    public String getCpf()                     { return cpf; }
    public void setCpf(String cpf)             { this.cpf = cpf; }
    public LocalDate getNascimento()           { return nascimento; }
    public void setNascimento(LocalDate nasc)  { this.nascimento = nasc; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getSenha()                   { return senha; }
    public void setSenha(String senha)         { this.senha = senha; }
    public String getSexo()                    { return sexo; }
    public void setSexo(String sexo)           { this.sexo = sexo; }
    public Cargo getCargo()                    { return cargo; }
    public void setCargo(Cargo cargo)          { this.cargo = cargo; }
    public boolean isGerente() {
        if (cargo == null) return false;
        String desc = cargo.getDescricao();
        return "Gerente".equalsIgnoreCase(desc) || "Supervisor".equalsIgnoreCase(desc);
    }

    @Override
    public String toString() { return nomeCompleto; }
}
