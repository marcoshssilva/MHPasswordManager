package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserDto;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public Set<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::fromEntity)
                .collect(Collectors.toSet());
    }
}
