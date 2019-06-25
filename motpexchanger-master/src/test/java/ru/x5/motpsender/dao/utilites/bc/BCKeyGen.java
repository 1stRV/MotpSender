package ru.x5.motpsender.dao.utilites.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;

import javax.security.cert.CertificateException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BCKeyGen {

    /**
     * @param args Создает тестовый ключ и сертификат основанный на корневом сертификате тестового удостоверяющего
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     * @throws CertificateException
     * @throws OperatorCreationException
     * @throws java.security.cert.CertificateException
     */
    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException, CertificateException, OperatorCreationException, java.security.cert.CertificateException, URISyntaxException {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECGOST3410", "BC");
        keyPairGenerator.initialize(new ECGenParameterSpec("Tc26-Gost-3410-12-256-paramSetA"));
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        InputStream is = BCKeyGen.class.getClassLoader().getResourceAsStream("keys/root_cert.cer");
        X509Certificate rootCA = (X509Certificate) factory.generateCertificate(is);

        X509Certificate certificate = selfSign(keyPair, "CN=Me", rootCA);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());

        FileOutputStream fos = new FileOutputStream("template.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos = new FileOutputStream("template.cer");
        fos.write(certificate.getEncoded());

        PKCS8EncryptedPrivateKeyInfoBuilder builder = new JcaPKCS8EncryptedPrivateKeyInfoBuilder(keyPair.getPrivate());
        ASN1ObjectIdentifier m = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC;
        JcePKCSPBEOutputEncryptorBuilder encryptorBuilder = new JcePKCSPBEOutputEncryptorBuilder(m);
        OutputEncryptor outputBuilder = encryptorBuilder.build("123".toCharArray());
        PKCS8EncryptedPrivateKeyInfo privKeyObj = builder.build(outputBuilder);
        fos = new FileOutputStream("template.pk8");

        fos.write(privKeyObj.getEncoded());
        fos.flush();
        fos.close();
    }

    public static X509Certificate selfSign(KeyPair keyPair, String subjectDN, X509Certificate rootCA) throws OperatorCreationException, CertificateException, IOException, java.security.cert.CertificateException {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name(subjectDN);
        BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number

        X500Name x500issuerName = new JcaX509CertificateHolder(rootCA).getSubject();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1); // <-- 1 Yr validity

        Date endDate = calendar.getTime();

        String signatureAlgorithm = "GOST3411WITHECGOST3410-2012-256"; // <-- Use appropriate signature algorithm based on your keyPair algorithm.

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(x500issuerName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());

        // Extensions --------------------------

        // Basic Constraints
        BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity

        //certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.

        // -------------------------------------

        return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
    }

    public void genBoth() throws KeyStoreException, java.security.cert.CertificateException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, OperatorCreationException, NoSuchProviderException {
        Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider() );

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( "ECGOST3410", "BC" );
        keyPairGenerator.initialize( new ECGenParameterSpec( "GostR3410-2001-CryptoPro-A" ) );
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        org.bouncycastle.asn1.x500.X500Name subject = new org.bouncycastle.asn1.x500.X500Name( "CN=Me" );
        org.bouncycastle.asn1.x500.X500Name issuer = subject; // self-signed
        BigInteger serial = BigInteger.ONE; // serial number for self-signed does not matter a lot
        Date notBefore = new Date();
        Date notAfter = new Date( notBefore.getTime() + TimeUnit.DAYS.toMillis( 365 ) );

        org.bouncycastle.cert.X509v3CertificateBuilder certificateBuilder = new org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder(
                issuer, serial,
                notBefore, notAfter,
                subject, keyPair.getPublic()
        );
        org.bouncycastle.cert.X509CertificateHolder certificateHolder = certificateBuilder.build(
                new org.bouncycastle.operator.jcajce.JcaContentSignerBuilder( "GOST3411withECGOST3410" )
                        .build( keyPair.getPrivate() )
        );
        org.bouncycastle.cert.jcajce.JcaX509CertificateConverter certificateConverter = new org.bouncycastle.cert.jcajce.JcaX509CertificateConverter();
        X509Certificate certificate = certificateConverter.getCertificate( certificateHolder );

        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        keyStore.load( null, null ); // initialize new keystore
        keyStore.setEntry(
                "alias",
                new KeyStore.PrivateKeyEntry(
                        keyPair.getPrivate(),
                        new X509Certificate[] { certificate }
                ),
                new KeyStore.PasswordProtection( "entryPassword".toCharArray() )
        );
        keyStore.store( new FileOutputStream( "test.jks" ), "keystorePassword".toCharArray());
    }
}
