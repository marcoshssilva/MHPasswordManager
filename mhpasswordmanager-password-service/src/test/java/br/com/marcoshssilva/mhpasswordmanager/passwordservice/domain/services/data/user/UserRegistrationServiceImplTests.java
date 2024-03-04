package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class UserRegistrationServiceImplTests {
    @Autowired
    UserRegistrationService service;
}