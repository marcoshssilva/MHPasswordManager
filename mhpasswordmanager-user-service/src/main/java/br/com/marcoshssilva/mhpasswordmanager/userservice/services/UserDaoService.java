package br.com.marcoshssilva.mhpasswordmanager.userservice.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserHasRoleRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.models.user.UserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDaoService {

    private final UserRepository userRepository;
    private final UserHasRoleRepository userHasRoleRepository;

    public Set<UserDto> getAllUsers() {
        return null;
    }

}
