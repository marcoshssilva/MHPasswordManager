package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserDto;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface JpaUserService {
    Set<UserDto> getAllUsers();
}
