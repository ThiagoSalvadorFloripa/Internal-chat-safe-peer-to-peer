package business;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.fips.FipsDRBG;
import org.bouncycastle.crypto.util.BasicEntropySourceProvider;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

import entity.KeyChat;
import entity.MensagemCriptografada;
import entity.Empresa;
import entity.Funcionario;
import security.CBCExampleKeyIVSecureRandom;
import security.ExValues;
import security.KeyStoreCompany;
import security.PBKDF2Util;
import security.Utils;

public class ComunicacaoSegura implements Serializable {

	private static final long serialVersionUID = -553370458218515491L;
	
	public static String nomeKeystore ="";
	
	public Key geraChavecriptograficaAES() {
		Key aesKey = null;
		String chave = "";
		try {
			KeyGenerator sKenGen = KeyGenerator.getInstance("AES");
			aesKey = sKenGen.generateKey();
			//chave = Utils.toHex(aesKey.getEncoded());

		} catch (Exception e) {
		}
		System.out.println( "Chave criptografada com o algoritmo 'AES': \n"+Utils.toHex(aesKey.getEncoded()));
		System.out.println("\n");
		System.out.println("\n");
		return aesKey;
	}
	
	public boolean pertenceAempresa(KeyChat keyChat){
		boolean liberado = false;
		Set<Funcionario> funcionarios = keyChat.getEmpresa().getFuncionarios();
		
		if(funcionarios.contains(keyChat.getTrasmissor())&& funcionarios.contains(keyChat.getReceptor())){
			liberado = true;
		}
		return liberado;
	}
	
	@SuppressWarnings("static-access")
	public byte[] geraIVComPBKDF2(String senha, String saltInput) {
		/*• senha é a senha fornecida
		• salt deve ser ALEATÓRIO e ser gerado com um gerador de bits aleatórios
		• Usar salt = CTX ll valor aleatório. O tamanho do salt é importante.
		• c (count) número de vezes que a função PRF é executada
		• Mínimo 1000 (100.000?). Para sistemas críticos 10.000.000 pode ser apropriado.
		• dkLen é o comprimento da chave derivada k (no mínimo 112)
		*/
		String ivFinal="";
		PBKDF2Util obj = new PBKDF2Util();
		String salt = this.geraHash(saltInput);
		int c = 30000;
		byte[] chaveDerivada = null;

		try {
			salt = obj.getSalt();
			chaveDerivada = obj.generateDerivedKey(senha, salt, c);
		} catch (Exception e) {
			e.getMessage();
		}
		
		byte iv[] = chaveDerivada;
		try {
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
		
			System.out.println("IV gerado com o algoritmo PBKDF2: \n" + Utils.toHex(iv));
			System.out.println("\n");
			System.out.println("\n");
			ivFinal = Utils.toHex(iv);

		} catch (Exception e) {
			e.getMessage();
		}
		return iv;
	}
	
	public String geraChaveFuncionario(String senha, String saltInput) {
		String ivFinal="";
		PBKDF2Util obj = new PBKDF2Util();
		String salt = this.geraHash(saltInput);
		int c = 30000;
		byte[] chaveDerivada = null;

		try {
			salt = obj.getSalt();
			chaveDerivada = obj.generateDerivedKey(senha, salt, c);
		} catch (Exception e) {
			e.getMessage();
		}
		 return ivFinal = chaveDerivada.toString();
		
	}
	
	public String geraHash(String salt) {
		String hashFinal = salt;
		try {
			MessageDigest m = MessageDigest.getInstance("SHA-512");
			m.update(hashFinal.getBytes(), 0, hashFinal.length());
			byte[] digest = m.digest();
			String hexa = new BigInteger(1, digest).toString(16);
			hashFinal = hexa;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashFinal;
	}
	
	public void salvaChavesNoKeyStore(KeyChat keyChat) {
		KeyStoreCompany obj = new KeyStoreCompany();
		Security.addProvider(new BouncyCastleFipsProvider());

		// Resolve lentidão linux
		CryptoServicesRegistrar.setSecureRandom(FipsDRBG.SHA512_HMAC
				.fromEntropySource(new BasicEntropySourceProvider(new SecureRandom(), true)).build(null, false));

		try {
			// Criar o keystore no diretorio atual
			KeyStore ks = KeyStore.getInstance("BCFKS", "BCFIPS");
			// Cria do zero o keystore
			ks.load(null, null);
			
			// gera nome do arquivo e a senha
			String extensao = "keystore.bcfks";
			
			ks.store(new FileOutputStream(keyChat.getEmpresa().getNome()+extensao), keyChat.getTrasmissor().getChaceKeytore().toCharArray());

			// Armazena IV no keystore
			char[] storePass = keyChat.getTrasmissor().getChaceKeytore().toCharArray(); // Da empresa
			String keystoreFilename = keyChat.getEmpresa().getNome()+extensao;// nome arquivoEmpresa
			String criptIndividual = new String(keyChat.getTrasmissor().getChaveCriptografica().toString());
			char[] keyPass1 = criptIndividual.toCharArray();// chave criptografica alice
			String IV = new String(keyChat.getTrasmissor().getIV()); // pega IV do ALICE
			obj.storeSecretKey(keystoreFilename, storePass, IV, keyPass1, ExValues.SampleAesKey);
			
			ComunicacaoSegura.nomeKeystore = keyChat.getEmpresa().getNome()+extensao;

			//printa na tela o IV
			//KeyStoreCompany.printKeyStore(keystoreFilename, storePass);

		} catch (IOException | NoSuchProviderException | KeyStoreException | NoSuchAlgorithmException
				| CertificateException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getIVdoKeyStore(String keystoreFilename, char[] storePass){
		String IV="";
		try {
			IV =KeyStoreCompany.printKeyStore(keystoreFilename, storePass);
		} catch (NoSuchProviderException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			e.printStackTrace();
		}
		return IV;
	}
	
	public MensagemCriptografada encryptionMensagem(String mensagem, Key aesKey, byte[] iv){
		CBCExampleKeyIVSecureRandom cbf = new CBCExampleKeyIVSecureRandom();
		MensagemCriptografada mscrip = new MensagemCriptografada();
		mscrip=cbf.encryption(mensagem, aesKey, iv);
		return mscrip; 
	}
	
	public String decryptionMensagem(MensagemCriptografada mensagemCriptografadaEmissor, String ivEmissor){
		CBCExampleKeyIVSecureRandom cbf = new CBCExampleKeyIVSecureRandom();
		byte[] iv = ivEmissor.getBytes();
		return cbf.decryption(mensagemCriptografadaEmissor, iv);
		
	}
	
	public void enviaDadosReceptor(String nomeKeystore, Key chaveCriptograficaEmissor,
			MensagemCriptografada mensagemCriptografadaEmissor, Funcionario receptor) {
		
		// chave que foi criptrograda a mensage
		System.out.println("Chave usada para cifragem da mensagem: \n"+chaveCriptograficaEmissor);
		System.out.println("\n");
		System.out.println("\n");
		//abrindo o keyTore para pegar o IV do trasmissor Salvado e printando na tela
		System.out.println("IV do emissor recuperado do KeyStore: \n"+ this.getIVdoKeyStore(nomeKeystore, receptor.getChaceKeytore().toCharArray()));
		System.out.println("\n");
		System.out.println("\n");
		
		//printando mensagem cifrada
		System.out.println("Mensagem cifrada: \n"+Utils.toHex(mensagemCriptografadaEmissor.getMensagem()));
		System.out.println("\n");
		System.out.println("\n");
		//printando mensagem descifrada
		System.out.println("Mensagem real descifrada: \n"+this.decryptionMensagem(mensagemCriptografadaEmissor, this.getIVdoKeyStore(nomeKeystore, receptor.getChaceKeytore().toCharArray())));
		
		
		System.out.println("\n");
		System.out.println("\n");
	
		System.out.println("███████▓█████▓▓╬╬╬╬╬╬╬╬▓███▓╬╬╬╬╬╬╬▓╬╬▓█ \r\n" + 
				"████▓▓▓▓╬╬▓█████╬╬╬╬╬╬███▓╬╬╬╬╬╬╬╬╬╬╬╬╬█ \r\n" + 
				"███▓▓▓▓╬╬╬╬╬╬▓██╬╬╬╬╬╬▓▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█ \r\n" + 
				"████▓▓▓╬╬╬╬╬╬╬▓█▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█ \r\n" + 
				"███▓█▓███████▓▓███▓╬╬╬╬╬╬▓███████▓╬╬╬╬▓█ \r\n" + 
				"████████████████▓█▓╬╬╬╬╬▓▓▓▓▓▓▓▓╬╬╬╬╬╬╬█ \r\n" + 
				"███▓▓▓▓▓▓▓╬╬▓▓▓▓▓█▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█ \r\n" + 
				"████▓▓▓╬╬╬╬▓▓▓▓▓▓█▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█ \r\n" + 
				"███▓█▓▓▓▓▓▓▓▓▓▓▓▓▓▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█ \r\n" + 
				"█████▓▓▓▓▓▓▓▓█▓▓▓█▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█ \r\n" + 
				"█████▓▓▓▓▓▓▓██▓▓▓█▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬██ \r\n" + 
				"█████▓▓▓▓▓████▓▓▓█▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬██ \r\n" + 
				"████▓█▓▓▓▓██▓▓▓▓██╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬██ \r\n" + 
				"████▓▓███▓▓▓▓▓▓▓██▓╬╬╬╬╬╬╬╬╬╬╬╬█▓╬▓╬╬▓██ \r\n" + 
				"█████▓███▓▓▓▓▓▓▓▓████▓▓╬╬╬╬╬╬╬█▓╬╬╬╬╬▓██ \r\n" + 
				"█████▓▓█▓███▓▓▓████╬▓█▓▓╬╬╬▓▓█▓╬╬╬╬╬╬███ \r\n" + 
				"██████▓██▓███████▓╬╬╬▓▓╬▓▓██▓╬╬╬╬╬╬╬▓███ \r\n" + 
				"███████▓██▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓╬╬╬╬╬╬╬╬╬╬╬████ \r\n" + 
				"███████▓▓██▓▓▓▓▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓████ \r\n" + 
				"████████▓▓▓█████▓▓╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬▓█████ \r\n" + 
				"█████████▓▓▓█▓▓▓▓▓███▓╬╬╬╬╬╬╬╬╬╬╬▓██████ \r\n" + 
				"██████████▓▓▓█▓▓▓╬▓██╬╬╬╬╬╬╬╬╬╬╬▓███████ \r\n" + 
				"███████████▓▓█▓▓▓▓███▓╬╬╬╬╬╬╬╬╬▓████████ \r\n" + 
				"██████████████▓▓▓███▓▓╬╬╬╬╬╬╬╬██████████ \r\n" + 
				"███████████████▓▓▓██▓▓╬╬╬╬╬╬▓███████████");
	}
}
