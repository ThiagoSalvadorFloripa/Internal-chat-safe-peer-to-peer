package security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class KeyStoreTEste {
	

	  private static KeyStore createKeyStore(String fileName, String pw) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
	    File file = new File(fileName);

	    final KeyStore keyStore = KeyStore.getInstance("JCEKS");
	    if (file.exists()) {
	      keyStore.load(new FileInputStream(file), pw.toCharArray());
	    } else {
	      keyStore.load(null, null);
	      keyStore.store(new FileOutputStream(fileName), pw.toCharArray());
	    }

	    return keyStore;
	  }

	  public static String getKey(String key, String keystoreLocation, String keyStorePassword) throws Exception{

	    KeyStore ks = KeyStore.getInstance("JCEKS");
	    ks.load(null, keyStorePassword.toCharArray());
	    KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(keyStorePassword.toCharArray());

	    FileInputStream fIn = new FileInputStream(keystoreLocation);

	    ks.load(fIn, keyStorePassword.toCharArray());

	    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

	    KeyStore.SecretKeyEntry ske =
	      (KeyStore.SecretKeyEntry)ks.getEntry(key, keyStorePP);

	    PBEKeySpec keySpec = (PBEKeySpec)factory.getKeySpec(
	        ske.getSecretKey(),
	        PBEKeySpec.class);

	    char[] password = keySpec.getPassword();

	    return new String(password);

	  }

	  public static void setKey(String key, String value, String keyStoreLocation, String keyStorePassword) throws Exception {

	    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	    SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(value.toCharArray()));

	    KeyStore ks = KeyStore.getInstance("JCEKS");
	    ks.load(null, keyStorePassword.toCharArray());
	    KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(keyStorePassword.toCharArray());

	    ks.setEntry(key, new KeyStore.SecretKeyEntry( generatedSecret), keyStorePP);

	    FileOutputStream fos = new java.io.FileOutputStream(keyStoreLocation);
	    ks.store(fos, keyStorePassword.toCharArray());
	  }
	  public static void main(String[] args) throws Exception {
		  
	    final String  keyStoreFile = "./codiceinsicuro.keystore";
	    KeyStore keyStore = createKeyStore(keyStoreFile, "test123");
	    setKey("test", "leggi questo blog ogni giorno", keyStoreFile,  "test123");
	    System.out.println("Found Key: " + getKey("test", keyStoreFile,  "test123" ));
	  }
	
}
