package ru.x5.motpsender.dao.utilites;

import lombok.extern.log4j.Log4j2;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

/**
 * @deprecated
 * Класс для подписи данных при получении токена по ГОСТ 3410 2012.
 * Используется закрытый ключ в формате PKCS8 и сертификат
 * Может быть использован только для тестирования, так как основан на Bouncy Castle.
 * Целевое решение КриптоПро DSS.
 */
@Deprecated
@Log4j2
@Component
public class Gost3410 implements DataSigner {

    @Value("${motp.auth.algorithm}")
    private String algorithm;

    @Value("${motp.auth.signature}")
    private String signatureAlgorithm;

    @Value("${motp.auth.provider}")
    private String provider;


    @Value("${motp.certificate.path}")
    private String certificatePath;

    @Value("${motp.privatekey.path}")
    private String privateKeyPath;

    public byte[] sign(byte[] data, X509Certificate certificate, byte[] encodedPrivateKey) throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeySpecException, CertificateEncodingException, CMSException, IOException,
            OperatorCreationException {

        X509Certificate[] certificates = new X509Certificate[1];
        certificates[0] = certificate;

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm, provider);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        CMSTypedData msg = new CMSProcessableByteArray(data);
        Store certStore = new JcaCertStore(Arrays.asList(certificates));
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(provider).build(privateKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(provider).build()).build(signer, certificates[0]));
        gen.addCertificates(certStore);
        CMSSignedData sigData = gen.generate(msg, true);

        return sigData.getEncoded();
    }

    public byte[] readEncodedKeyFromPk8File(String filename) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(filename));
        if ("pk8".equals(Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf('.') + 1)).toString()))
        {
            String key = new String(content);
            key = key.replace("-----BEGIN PRIVATE KEY-----", "");
            key = key.replace("-----END PRIVATE KEY-----", "");
            key = key.replace("\r", "");
            key = key.replace("\n", "");

            return Base64.getDecoder().decode(key);
        } else return content;
    }

    public X509Certificate readX509CertificateFromCerFile(String filename) throws CertificateException, FileNotFoundException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(new FileInputStream(filename));
        return (X509Certificate) certificate;
    }

    @Override
    public String signData(String data) {
        X509Certificate x509Certificate = null;
        try {
            x509Certificate = this.readX509CertificateFromCerFile(certificatePath);
        } catch (CertificateException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(certificatePath + " " + e.getMessage());
        }
        byte[] key = new byte[0];
        try {
            key = this.readEncodedKeyFromPk8File(privateKeyPath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        byte[] signedData = new byte[0];
        try {
            signedData = this.sign(data.getBytes(), x509Certificate, key);
        } catch (NoSuchProviderException | InvalidKeySpecException | CertificateEncodingException | CMSException
                | IOException | OperatorCreationException | NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
        return new String(signedData);
    }
}
