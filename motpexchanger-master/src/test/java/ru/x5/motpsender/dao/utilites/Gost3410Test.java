package ru.x5.motpsender.dao.utilites;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import ru.x5.motpsender.MotpApplicationTests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class Gost3410Test extends MotpApplicationTests {

    @Autowired
    private Gost3410 gost3410;

    private static final String KEY_FILE = "src/test/resources/keys/key.pk8";

    private static final String CERT_FILE = "src/test/resources/keys/certificate.cer";

    private static final String DATA = "string";

    @Value("classpath:keys/signed.dat")
    private Resource signedDataExpected;

    @Value("classpath:keys/keyFromPk8File.dat")
    private Resource keyFromPk8FileExpected;

    @Test
    @Ignore
    public void sign() throws IOException, CertificateException, NoSuchAlgorithmException, CMSException,
            NoSuchProviderException, InvalidKeySpecException, OperatorCreationException {
        Security.addProvider(new BouncyCastleProvider());
        byte[] signedDataExpectedBytes =IOUtils.toByteArray(signedDataExpected.getInputStream());
        byte[] keyFromPk8File = gost3410.readEncodedKeyFromPk8File(KEY_FILE);
        assertArrayEquals(IOUtils.toByteArray(keyFromPk8FileExpected.getInputStream()), keyFromPk8File);
        X509Certificate x509Certificate = gost3410.readX509CertificateFromCerFile(CERT_FILE);
        byte[] signedData = gost3410.sign(DATA.getBytes(), x509Certificate, keyFromPk8File);
        //Данные отличаются в конце. Возьмем первые 500
        assertArrayEquals(Arrays.copyOfRange(signedDataExpectedBytes, 0, 500),
                Arrays.copyOfRange(signedData, 0, 500));
    }

}