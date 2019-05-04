package entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.Key;

public class Funcionario implements Serializable {
	
	private static final long serialVersionUID = 5372908122736221744L;
	
	Integer id;
	String nome;
	String email;
	String chavePrivada;
	Key chaveCriptografica;
	byte[] IV;
	String chaveKeyStore;
	String mensagem;
	
	public Funcionario(Integer id, String nome, String email, String chavePrivada) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.chavePrivada = chavePrivada; 
	}
	
	public Funcionario() {}
	
	public Funcionario(Integer id) {
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

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getChaveKeyStore() {
		return chaveKeyStore;
	}

	public void setChaveKeyStore(String chaveKeyStore) {
		this.chaveKeyStore = chaveKeyStore;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getChavePrivada() {
		return chavePrivada;
	}

	public void setChavePrivada(String chavePrivada) {
		this.chavePrivada = chavePrivada;
	}
	
	

	public String getChaceKeytore() {
		return chaveKeyStore;
	}

	public void setChaceKeytore(String chaceKeytore) {
		this.chaveKeyStore = chaceKeytore;
	}

	public Key getChaveCriptografica() {
		return chaveCriptografica;
	}

	public void setChaveCriptografica(Key chaveCriptografica) {
		this.chaveCriptografica = chaveCriptografica;
	}

	public byte[] getIV() {
		return IV;
	}

	public void setIV(byte[] iV) {
		IV = iV;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Funcionario other = (Funcionario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	

}
