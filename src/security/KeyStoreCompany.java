package security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.fips.FipsDRBG;
import org.bouncycastle.crypto.util.BasicEntropySourceProvider;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.operator.OperatorCreationException;


public class KeyStoreCompany {
	
	public static void storeSecretKey(String storeFilename, char[] storePassword, String alias, char[] keyPass, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("BCFKS", "BCFIPS");
        keyStore.load(new FileInputStream(storeFilename), storePassword);
        keyStore.load(null, null);

        keyStore.setKeyEntry(alias, secretKey, keyPass, null);
        keyStore.store(new FileOutputStream(storeFilename), storePassword);
    }

    public static void storeCertificate(String storeFilename, char[] storePassword, String alias, X509Certificate trustedCert)
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("BCFKS", "BCFIPS");
        keyStore.load(new FileInputStream(storeFilename), storePassword);
        keyStore.setCertificateEntry(alias, trustedCert);
        keyStore.store(new FileOutputStream(storeFilename), storePassword);

    }

    public static void storePrivateKey(String storeFilename, char[] storePassword, String alias, char[] keyPass, PrivateKey eeKey, X509Certificate[] eeCertChain)
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("BCFKS", "BCFIPS");
        keyStore.load(new FileInputStream(storeFilename), storePassword);
        keyStore.setKeyEntry(alias, eeKey, keyPass, eeCertChain);
        keyStore.store(new FileOutputStream(storeFilename), storePassword);
    }

    public static String printKeyStore(String storeFilename, char[] storePassword) throws NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    	String iv="";
    	KeyStore keyStore = KeyStore.getInstance("BCFKS", "BCFIPS");
        keyStore.load(new FileInputStream(storeFilename), storePassword);
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String elem = aliases.nextElement();
            if (keyStore.isKeyEntry(elem)) 
            iv=elem;
         
          
        }
        return iv;
    }

    public static void main(String[] args)
            throws GeneralSecurityException, IOException, OperatorCreationException {

        // Install Provider FIPS
        Security.addProvider(new BouncyCastleFipsProvider());

        // Adicionado para resolver problema da lentidao no Linux - Sugerido por Marcio Sagaz
        CryptoServicesRegistrar.setSecureRandom(FipsDRBG.SHA512_HMAC
                .fromEntropySource(new BasicEntropySourceProvider(new SecureRandom(), true)).build(null, false));

        // Escrevendo os providers do ambiente
        // Conferir se aparece o provider BCFIPS
        /**
         * Provider[] providers = Security.getProviders(); for (Provider p :
         * providers) { System.out.println("Provider Name : " + p.getName());
         * System.out.println("Provider Information : " + p.getInfo());
         * System.out.println("Provider Version : " + p.getVersion());
         *
         * System.out.println("-------------------------------"); }
         */
        
        // Criar o keystore no diretorio atual
       // KeyStore ksd = KeyStore.getInstance(KeyStore.getDefaultType());
        KeyStore ks = KeyStore.getInstance("BCFKS", "BCFIPS");
        // Cria do zero o keystore
        ks.load(null, null);
        // Armazena a senha mestre do keystore 
        // Senha do cofre no cÃ³digo - soluÃ§Ã£o HORRÃ�VEL!!
        // JAMAIS FAÃ‡A IGUAL!
        ks.store(new FileOutputStream("meukeystore.bcfks"), "password".toCharArray());

        // Armazena duas chaves secretas
        // HORRÃ�VEL. NÃ£o faÃ§a igual: password1 e password2 estÃ£o escritos no cÃ³digo!
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        Key key1 = keyGenerator.generateKey();
        Key key2 = keyGenerator.generateKey();
        ks.load(new FileInputStream("meukeystore.bcfks"), "password".toCharArray());
        ks.setKeyEntry("aeskey1", key1, "password1".toCharArray(), null);
        ks.setKeyEntry("aeskey2", key2, "password2".toCharArray(), null);
        ks.store(new FileOutputStream("meukeystore.bcfks"), "password".toCharArray());

        // Recupera chaves do keystore e imprime as chaves na tela 
        SecretKey sk1 = (SecretKey) ks.getKey("aeskey1", "password1".toCharArray());
        System.out.println("Chave aeskey1 = " + Hex.encodeHexString(sk1.getEncoded()));

        SecretKey sk2 = (SecretKey) ks.getKey("aeskey2", "password2".toCharArray());
        //System.out.println(new BigInteger(1, sk2.getEncoded()).toString(16));
        System.out.println("Chave aeskey2 = " + Hex.encodeHexString(sk2.getEncoded()));

        // Armazena mais duas chaves no keystore via metodo
        char[] storePass = "password".toCharArray();
        String keystoreFilename = "meukeystore.bcfks";
        char[] keyPass1 = "keyPassword1".toCharArray();
        char[] keyPass2 = "keyPassword2".toCharArray();
        char[] keyPass = "keyPassword".toCharArray();

        storeSecretKey(keystoreFilename, storePass, "aeskey3", keyPass1, ExValues.SampleAesKey);
        storeSecretKey(keystoreFilename, storePass, "aeskey4", keyPass2, ExValues.SampleAesKey);

//        // Criar chaves assimÃ©tricas
//        // Chaves da CA
//        KeyPair caKeyPair = EC.generateKeyPair();
//        // Chaves de uma entidade assinada pela CA
//        KeyPair eeKeyPair = EC.generateKeyPair();
//
//        // Faz certificado da CA
//        X509Certificate caCert = Cert.makeV1Certificate(caKeyPair.getPrivate(), caKeyPair.getPublic());
//        // Faz certificado da entidade
//        X509Certificate eeCert = Cert.makeV3Certificate(caCert, caKeyPair.getPrivate(), eeKeyPair.getPublic());
//
//        // Armazena certificado da CA no keystore
//        storeCertificate(keystoreFilename, storePass, "trustedca certificate", caCert);
//        
//        // Armazena certificado da entidade no keystore
//        storeCertificate(keystoreFilename, storePass, "certificate", eeCert);
//
//        // Armazena chave privada da entidade no keystore
//        storePrivateKey(keystoreFilename, storePass, "private key", keyPass, eeKeyPair.getPrivate(), new X509Certificate[]{eeCert});;

        printKeyStore(keystoreFilename, storePass);

    }

}
