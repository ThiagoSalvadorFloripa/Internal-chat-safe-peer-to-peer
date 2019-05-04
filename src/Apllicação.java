import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import business.ComunicacaoSegura;
import entity.KeyChat;
import entity.MensagemCriptografada;
import entity.Empresa;
import entity.Funcionario;
import security.KeyStoreCompany;
import security.Utils;

public class Apllicação {

	public static void main(String[] args){
		
		ComunicacaoSegura comunicacaoSegura = new ComunicacaoSegura();
		
		//cria empresa
		Empresa empresa = new Empresa(1, "Tecnologia", "tec@@tecnologia.com");
		empresa.setChave(comunicacaoSegura.geraHash(empresa.getNome()+empresa.getEmail()));
		
		
		
		
		//cria funcionario Alice e sua chave privada
		Funcionario alice = new Funcionario(1, "Alice", "alice@tecnologia.com","");
		alice.setChavePrivada(comunicacaoSegura.geraChaveFuncionario(alice.getNome(), alice.getEmail()));
		
		
		
		
		// cria funcionario Bob e sua chave privada
		Funcionario bob = new Funcionario(2, "Bob", "bob@tecnologia.com", "");
		bob.setChavePrivada(comunicacaoSegura.geraChaveFuncionario(bob.getNome(),bob.getEmail()));
		
		
		
		// add funcionarios na empresa Tecnologia
		Set<Funcionario> funcionarios = new HashSet<Funcionario>();
		funcionarios.add(alice);
		funcionarios.add(bob);
		empresa.setFuncionarios(funcionarios);
		
		
		
		
		/*Notifica conversa
		 * ID do KeyChat é a concatenação da chave privada dos funcuinarios
		 * A mensagem inicia vazia
		 * É enviado também a empresa
		 * */
		
		//teste funcionario malicioso
		Funcionario maliciador = new Funcionario(3, "maliciador", "maliciador@tecnologia.com","");
		// Inicia uma 
		KeyChat keyChat = new KeyChat(comunicacaoSegura.geraHash(alice.getChavePrivada()+bob.getChavePrivada()), "", empresa, alice, bob,"");
		

		
		
		/*Envia keyChat para validação dos dados
		 Verifica se os funcionarios existem na empresa
		 Se existir entra no processo de geração de chaves
		 e recebe a chave da empresa que abre o keystore
		*/
		if(comunicacaoSegura.pertenceAempresa(keyChat)){
			
			
			//libera chave do keystore para Alice;
			alice.setChaceKeytore(empresa.getChave());
			
			
			
			
			
			//gera chave criptográfica
			alice.setChaveCriptografica(comunicacaoSegura.geraChavecriptograficaAES());
			
			
			
			
			//gera gerar IV em PBKDF2
			alice.setIV(comunicacaoSegura.geraIVComPBKDF2(alice.getChavePrivada(), alice.getId().toString()));
			
			
			
			
			//gera KeyStore e guarda todos os tokens principais do chat, inclusivi o IV
			comunicacaoSegura.salvaChavesNoKeyStore(keyChat);
			
			
			
			
			//mensagem a ser cifrada.
			alice.setMensagem("“With the grace of God, "
					+ "we are always better at "
					+ "everything with joy, love "
					+ "and happiness, contributing "
					+ "in this world");
			
			
			
			
			
			//Imprimi mensagem original
			System.out.println("Mensagem Origial: \n"+alice.getMensagem());
			
			
			
			
			
			//cifra a mensagem usando a chaveCriptografica e o IV
			MensagemCriptografada mensagemCriptografada = new MensagemCriptografada();
			mensagemCriptografada = comunicacaoSegura.encryptionMensagem(alice.getMensagem(), alice.getChaveCriptografica(), alice.getIV()); 
				
			
			
			
			//Imprimi mensagem cifrada
			System.out.println("Mensagem criptografada com algoritmo 'AES/CBC/PKCS7Padding': \n"+ Utils.toHex(mensagemCriptografada.getMensagem()));
	
			
			
			//valida novamente se os funcionarios pertencem a empresa
			if(comunicacaoSegura.pertenceAempresa(keyChat)){
				
				
				// libera chave do KeyStore para Bob
				bob.setChaceKeytore(empresa.getChave());
				
				
				// Envia dados para o receptor
				comunicacaoSegura.enviaDadosReceptor(ComunicacaoSegura.nomeKeystore, alice.getChaveCriptografica(), mensagemCriptografada, keyChat.getReceptor());
			}
			
			
		}
		
	
		
	}

}
