package entity;

import java.io.Serializable;
import java.security.Key;

public class MensagemCriptografada implements Serializable {
	
	private static final long serialVersionUID = -3827545072264784658L;
	private byte[] mensagem;
	private Key aesKey;
	private byte[] cipherText;
	private int ctLength;
	
	public MensagemCriptografada (){}

	public byte[] getMensagem() {
		return mensagem;
	}

	public void setMensagem(byte[] mensagem) {
		this.mensagem = mensagem;
	}

	public Key getAesKey() {
		return aesKey;
	}

	public void setAesKey(Key aesKey) {
		this.aesKey = aesKey;
	}

	public byte[] getCipherText() {
		return cipherText;
	}

	public void setCipherText(byte[] cipherText) {
		this.cipherText = cipherText;
	}

	public int getCtLength() {
		return ctLength;
	}

	public void setCtLength(int ctLength) {
		this.ctLength = ctLength;
	}
	
	

}
