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
}
