package entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class KeyChat implements Serializable {
	private static final long serialVersionUID = -4014600767194016112L;

	private String id;
	private Empresa empresa;
	private Funcionario trasmissor;
	private Funcionario receptor;

	
	public KeyChat(String id, String mensagem, Empresa empresa, Funcionario trasmissor, Funcionario receptor,
			String ivTrasmissor) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.trasmissor = trasmissor;
		this.receptor = receptor;
	}

	public KeyChat() {
	}
	
	public KeyChat(String id) {
		this.id = id;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Funcionario getTrasmissor() {
		return trasmissor;
	}

	public void setTrasmissor(Funcionario trasmissor) {
		this.trasmissor = trasmissor;
	}

	public Funcionario getReceptor() {
		return receptor;
	}

	public void setReceptor(Funcionario receptor) {
		this.receptor = receptor;
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
		KeyChat other = (KeyChat) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
