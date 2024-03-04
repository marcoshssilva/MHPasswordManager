package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl.AESCryptServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl.RSACryptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CryptServiceConfig {

    @Bean("aesCryptService")
    @Primary
    CryptService aesCryptService() {
        return new AESCryptServiceImpl();
    }

    @Bean("rsaCryptService")
    CryptService rsaCryptService(@Autowired RSAUtilService rsaUtilService) {
        return new RSACryptServiceImpl(rsaUtilService);
    }
}
