package entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Empresa implements Serializable {
	private static final long serialVersionUID = -614193307993269602L;

	private Integer id;
	private String nome;
	private String email;
	private Set<Funcionario> funcionarios = new HashSet<Funcionario>();
	private String chave;

	public Empresa(Integer id, String nome, String chave) {
		super();
		this.id = id;
		this.nome = nome;
		this.chave = chave;
	}

	public Empresa() {
	}

	public Empresa(Integer id) {
		this.id = id;
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public Set<Funcionario> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(Set<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
	}

}
