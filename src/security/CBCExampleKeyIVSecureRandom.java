package security;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security; // Incluido


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider; // Incluido
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import entity.MensagemCriptografada;

/**
 * Symmetric encryption example with padding and CBC using AES with the
 * initialization vector. Modificado para usar o AES.
 */
public class CBCExampleKeyIVSecureRandom {
	
	public MensagemCriptografada encryption(String mensagem,  Key aesKey, byte[] iv){
		
		MensagemCriptografada msCrip = new MensagemCriptografada();
		
		byte[] input = mensagem.getBytes(Charset.forName("UTF-8"));

		// Incluido: Instanciar um novo Security provider
		int addProvider = Security.addProvider(new BouncyCastleFipsProvider());
		if (Security.getProvider("BCFIPS") == null) {
			System.out.println("Bouncy Castle provider NAO disponivel");
		} else {
			System.out.println("Bouncy Castle provider esta disponivel");
		}
		
		try {
			//Security.addProvider(new BouncyCastleProvider());
			// Instanciando cipher
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BCFIPS");
			System.out.println("input : " + Utils.toHex(input));

			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			System.out.println("IV = " + Utils.toHex(iv));

			// encryption pass
			cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
			
			byte[] cipherText = new byte[cipher.getOutputSize(iv.length + input.length)];
			int ctLength = cipher.update(iv, 0, iv.length, cipherText, 0);
			ctLength += cipher.update(input, 0, input.length, cipherText, ctLength);
			ctLength += cipher.doFinal(cipherText, ctLength);
			System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
			
			msCrip.setAesKey(aesKey);
			msCrip.setCipherText(cipherText);
			msCrip.setCtLength(ctLength);
			msCrip.setMensagem(input);
			
			} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
				e.printStackTrace();
			}
			  catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			 catch (IllegalBlockSizeException | BadPaddingException | ShortBufferException e) {
				e.printStackTrace();
			}
		return msCrip;
	}
	
	public String decryption( MensagemCriptografada msCrip,  byte[] iv) {
		String texto = "";
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BCFIPS");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			
			//Decryption pass
			cipher.init(Cipher.DECRYPT_MODE, msCrip.getAesKey(), ivSpec);

			byte[] buf = new byte[cipher.getOutputSize(msCrip.getCtLength())];
			int bufLength = cipher.update(msCrip.getCipherText(), 0, msCrip.getCtLength(), buf, 0);
			bufLength += cipher.doFinal(buf, bufLength);

			// remove the iv from the start of the message
			byte[] plainText = new byte[bufLength - iv.length];
			System.arraycopy(buf, iv.length, plainText, 0, plainText.length);

			//System.out.println("plain : " + Utils.toHex(plainText, plainText.length) + " bytes: " + plainText.length);
			texto = new String(msCrip.getMensagem());
			//System.out.println(texto);
			

		} catch (NoSuchProviderException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException | BadPaddingException | ShortBufferException e) {
			e.printStackTrace();

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return texto;

	}

    public static void main( String[] args) throws Exception {
    	
    	String teste = "Vamooo";
        byte[] input = teste.getBytes(Charset.forName("UTF-8"));
        
        
        
        /*// Incluido: Instanciar um novo Security provider
        int addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        if (Security.getProvider("BCFIPS") == null) {
            System.out.println("Bouncy Castle provider NAO disponivel");
        } else {
            System.out.println("Bouncy Castle provider esta disponivel");
        }*/

        
  
        
        // Incluido: Gera uma chave AES
        System.out.print("Gerando chave AES -> ");
        KeyGenerator sKenGen = KeyGenerator.getInstance("AES");
        Key aesKey = sKenGen.generateKey();
        //System.out.println("Chave AES = " + Utils.toHex(aesKey.getEncoded()));

        // Incluido: Gerando o iv com SecureRandom
        //SecureRandom random = SecureRandom.getInstanceStrong();
        //System.out.println("Algoritmo no SecureRandom"+java.security.Security.getProperty( "securerandom.strongAlgorithms" ));
        //System.out.print("Gerando IV -> ");
        byte iv[] = new byte[16];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        //System.out.println("IV = " + Utils.toHex(iv));
        
        CBCExampleKeyIVSecureRandom cbc = new CBCExampleKeyIVSecureRandom();
        MensagemCriptografada msCripto = new MensagemCriptografada();
        
        msCripto = cbc.encryption("Vamooo", aesKey, iv);
        
        cbc.decryption(msCripto, iv);

        
        
        
        
        /*// Instanciando cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BCFIPS");
        System.out.println("input : " + Utils.toHex(input));

        
        
        // encryption pass
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] cipherText = new byte[cipher.getOutputSize(iv.length + input.length)];
        int ctLength = cipher.update(iv, 0, iv.length, cipherText, 0);
        ctLength += cipher.update(input, 0, input.length, cipherText, ctLength);
        ctLength += cipher.doFinal(cipherText, ctLength);
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);

        
        
        
        
        // recebe iv e AES por parametro
        
        
        // decryption pass
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        byte[] buf = new byte[cipher.getOutputSize(ctLength)];
        int bufLength = cipher.update(cipherText, 0, ctLength, buf, 0);
        bufLength += cipher.doFinal(buf, bufLength);

        // remove the iv from the start of the message
        byte[] plainText = new byte[bufLength - iv.length];
        System.arraycopy(buf, iv.length, plainText, 0, plainText.length);

        System.out.println("plain : " + Utils.toHex(plainText, plainText.length) + " bytes: " + plainText.length);
        String palavra = new String(input);
        System.out.println(palavra);*/
    
    }
}
