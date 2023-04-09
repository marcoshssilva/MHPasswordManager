package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class RSAUtilServiceTests {
    static final String encodedKeyPKCS8 =
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDD0RTu/rB1wVTm" +
            "niIyzabiDDQvNG+47iSUg/VT/OkQQaCi6F1SurbV1xwTtkvOzqzvyMcOGhX8JUx9" +
            "BSbB8TjjuXr+ZU/HGK9s1Fo64Pv6z+AwpQP/FIphdwJ9mN5gkUaH+1cuoPq348VC" +
            "hrZuy127FgUGTTffVW6SD8S4hGKkdUismSIG+zIiI+t3hYMgrgoII2RdyTfJg7T4" +
            "MWgA6Bc6gqYoq0+mpb6FcH8GH5qymPo0qXKdaRpCevbkB2ECIhz0+v7cB9mF0s5M" +
            "nBaQrWRAMBUC9hf3eGSQqX9QZehbHbD7o0Yy9rVQbNDwrmZmnHs42If9g1ZaMcsa" +
            "/PYt9oA9AgMBAAECggEADGA0gH5cTLTxVrn701YoC/VVfv5DaV6ztSc55BsB5+Md" +
            "8e3ZqBbg41b7EUveMq3hRC5eVkfTx+HbmbMvpeZHAC2C1n00s5Kd07UAFuSNSGbG" +
            "9owXWpHDxTEuWR3GQDguSv3ZnrrR3blpiDDXat0ziVV079cFhvnxSQ3yEu9bBKXA" +
            "mcqawX9A1tNiWjtVnCoIkpXh0ICF/VWlnx6Yplhr+K4Irw7kVRivUN8WoXmGGN00" +
            "jLoLMRiMMpuMoujbOkB44atyLXffbiq3x9Mzsw9lyxRwmL3WZATT07MjCc4HaMzb" +
            "8nF0DZmjrPKFwO3934XA6xq2o4I8F/gaMMjDp1jGAQKBgQDv3Bv01SGPoEW7AWqP" +
            "jgVlHyQyPtARSQR4feKs9L4e2Lu7nv/0Xp5WyOwhNe+DaWAx3utcOqLpTRqnLWOz" +
            "w6tTtRmilqzkeAODsGDs3OB5Xv8j63yfdet8mMBaF1AQn9iVaPRQsEDWgvYHMfn1" +
            "djFvUBdyvH96W5pJkdrKbkfYfQKBgQDQ/kZCU3jTc+LQUKuWRugilccsyC1IwE2b" +
            "29LiytSolZHtEO1HgRMfc3AAU/LOVMLFAGnpsQlbKY5XeP0KIa5XZPGQIaDdsFmd" +
            "xHfDFp9Ag1r9YT5wG/1nAjwLFSwEjSVquka5Px0hhEYKrMwAW9wfwfZ/yyi4Ybdj" +
            "UdHrdrmSwQKBgHQ2mBD6xsT46XAe0ot1SyXgfcAecN8/Gnxquc4E0qzNY8AW3HqA" +
            "8YSo2seIg2CPSL1A/ZX1DwOsiPJg6oKpljP7dc0x5djYUROX/I4mwlfr8ABuUzZL" +
            "guoedGvyCafUWrKhZGZHz1Jfp2Z7D1mfh3ogOwwrSjR1d36XvYMBBaxdAoGAKD9f" +
            "rnQ+x5IAsxdmA/nByK9JfTFLr0ABciuCUFS5YYH0aVAiMewEj8D+Z8HC98NRAvlS" +
            "bEPAxoKZO/U73dCgr4nsD1K6lRbaG0zzt2lK3/Sr+DHbAWISidzfTU8VfAbmlsgq" +
            "yJAD/ylnB7VBUCxglukWnIO2YtPGqmTeZVcm48ECgYA/QWnejc88uAwUW6GKbeJy" +
            "LVAIXe72/xC5heRMgVi3S3eGhqnKAKLr5qHDGZuCXE6eXjQXMgvUuKINomW5poSz" +
            "rPtlfJuqFxLZ8fZnkutmi34J529tBJCHR9xqh48fqv9bvyKH4n5tdcoX/eJFfydp" +
            "+5WOFhNNctSXX5EBLuXDiw==";

    static final String encodedKeyX509 =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw9EU7v6wdcFU5p4iMs2m" +
            "4gw0LzRvuO4klIP1U/zpEEGgouhdUrq21dccE7ZLzs6s78jHDhoV/CVMfQUmwfE4" +
            "47l6/mVPxxivbNRaOuD7+s/gMKUD/xSKYXcCfZjeYJFGh/tXLqD6t+PFQoa2bstd" +
            "uxYFBk0331Vukg/EuIRipHVIrJkiBvsyIiPrd4WDIK4KCCNkXck3yYO0+DFoAOgX" +
            "OoKmKKtPpqW+hXB/Bh+aspj6NKlynWkaQnr25AdhAiIc9Pr+3AfZhdLOTJwWkK1k" +
            "QDAVAvYX93hkkKl/UGXoWx2w+6NGMva1UGzQ8K5mZpx7ONiH/YNWWjHLGvz2LfaA" +
            "PQIDAQAB";

    static final String decodedKeyPkcs8 = """
            -----BEGIN PRIVATE KEY-----
            MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbW4A4+jJlUHx6
            KdqSvZTfQJbFh9fKKBWV7Kjpk/Yq3V63/qz8CA+nWV7vEhFLn0V7X9jK1Edq3iB
            f90vRZuR7V9Xg78u0lVdD6zfqh6xOafOWd4ruFgPvAgclqxrl2j9X6cSgy/VzsYv
            2I2mrldi1jukncv/Bk+MF9H9lLrWZnDEJjMwRtC5/f5EjmjK07jyv19G1Iq3nvpK
            ZfzO5l5a+PzE0zwNR/tt1zji/Km3+PN9jKCTeA2ljY+ayIbEC2/ro8Ab/MfnKj+c
            nsN9AnrO/fUbZoYJw6UHx6/P+y8/T4W4nLlHfzrnjQXPLT+T1T6vZhdRHUNqu3qG
            VQyOdROPAgMBAAECggEBAJZuVWmsCE1cOsZpniJ71vJgWXBp8cYZkI9r+zkQ2Yt3
            w0CF+1sgFvzIZJbO8O2NkX9D+k5v5Y5lB8c/1+5bZIO7V+wkPaDkLY7V1xX9uKF6
            jj/Y6QwU6ZuUzfgg92q3/T/KJ06nS9S1MG7s/bYrmDnb+Zl4UGZ4l4ulUK/wz3Of
            Dr9T/q1s7R1otg0FK0oM35ZeB4Hv7NyPShOzKx7l1aEEyJ+d6NkL62eiO2OwMJN6
            Bb+h8ocQdUp2Xy9Dv5C5ppW8B/tccIwJZL98FvRAGzW8gEAFPGugyHrIv2aYfyGT
            nUL65tD3NVq3AgUpCQkr/ZS9BhduzNSLrmCU/T7sNEECgYEA4A53wRhLYzOrV7AF
            VdNTHe1aUuV6UkGV6U/fVcU08D/h36Cv0J+t9y+0qbz84SyXXW8EDv/fY5pZckPh
            WlFLvRc/ekXrC+UEgr40Pq8qNAX""";

    static final String decodedKeyX509 = """
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 7852311160567264240 (0x6c2d9bca96cde8e0)
    Signature Algorithm: sha256WithRSAEncryption
        Issuer: C=US, ST=California, L=San Francisco, O=Example Inc., CN=Example Root CA
        Validity
            Not Before: Apr 9 00:00:00 2022 GMT
            Not After : Apr 9 23:59:59 2023 GMT
        Subject: C=US, ST=New York, L=New York City, O=Example Inc., CN=www.example.com
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                Public-Key: (2048 bit)
                Modulus:
                    00:b6:d2:2f:55:ed:0f:e5:27:64:91:22:43:15:6a:
                    7e:1d:f4:7d:5e:24:7c:00:4f:f7:ea:60:34:7e:2f:
                    3f:3a:df:7d:e8:27:26:6a:97:53:fd:e8:e9:21:15:
                    57:cf:fd:8c:dc:49:3a:94:45:90:8e:49:1a:51:57:
                    e7:8b:00:be:95:dc:aa:12:7f:f6:d1:d9:2d:57:49:
                    f8:14:43:39:13:e2:e2:2b:4c:e4:4e:71:43:ac:cb:
                    3e:35:be:fa:47:3c:1f:d3:35:8e:3a:3a:db:2c:8a:
                    28:39:20:5d:28:eb:8d:2f:9c:6e:63:81:d8:1b:6e:
                    5f:dc:8a:e5:2d:9c:6b:68:10:45:fc:03:fc:1a:60:
                    8f:96:3d:45:db:2c:8a:89:f5:39:68:4f:16:7e:25:
                    bb:7b:1c:41:11:19:bb:3d:13:22:bb:6e:1a:ec:9b:
                    09:f1:8f:38:48:16:41:4f:7f:4e:46:7b:ac:fc:28:
                    f3:fc:a3:fc:3c:24:8b:44:81:72:9d:36:cd:12:1e:
                    07:11:14:56:5d:1a:5e:31:ce:11:d6:8e:f6:f5:6c:
                    32:89:ed:d8:61:19:3a:0f:2e:18:e6:69:77:0c:ea:
                    8b:80:56:dd:3d:2c:ff:b8:81
            """;

    @Autowired
    RSAUtilService rsaUtilService;

    @DisplayName("Test if can generate PrivateKey instance from PKCS8 encoded key")
    @Test
    void mustGeneratePrivateKeyFromPKCS8() {
        assertDoesNotThrow(() -> rsaUtilService.getPrivateFromPKCS8(encodedKeyPKCS8));
    }

    @DisplayName("Test if can generate PublicKey instance from an PrivateKey in PKCS8")
    @Test
    void mustGeneratePublicKeyFromPrivateKeyPKCS8() {
        assertDoesNotThrow(() -> rsaUtilService.getPublic(rsaUtilService.getPrivateFromPKCS8(encodedKeyPKCS8)));
    }

    @DisplayName("Test if can generate PublicKey instance from an PrivateKey generated by KeyPairGenerator")
    @Test
    void mustGeneratePublicKeyFromKeyGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        assertDoesNotThrow(() -> rsaUtilService.getPublic(keyPair.getPrivate()));
    }

    @DisplayName("Test if can generate PublicKey instance from an X509 encoded key")
    @Test
    void mustGeneratePublicKeyFromX509() {
        assertDoesNotThrow(() -> rsaUtilService.getPublicFromX509(encodedKeyX509));
    }

    @DisplayName("Test if can get a instance of X509EncodedKeySpec by decoded key")
    @Test
    void mustGeneratePublicKeyFromX509FromDecoded() {
        byte[] x509Certify = decodedKeyX509.getBytes();
        assertDoesNotThrow(() -> rsaUtilService.getX509EncodedKeySpec(x509Certify, true));
    }

    @DisplayName("test if can get a instance of Pkcs8EncodedKeySpec by decoded key")
    @Test
    void mustGeneratePrivateKeyFromPkcs8FromDecoded() {
        byte[] pkc8Key = decodedKeyPkcs8.getBytes();
        assertDoesNotThrow(() -> rsaUtilService.getPKCS8EncodedKeySpec(pkc8Key, true));
    }
}
