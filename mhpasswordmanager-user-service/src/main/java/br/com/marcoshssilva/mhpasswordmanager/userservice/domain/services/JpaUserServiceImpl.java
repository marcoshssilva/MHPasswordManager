package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserDto;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserHasRoleRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JpaUserServiceImpl implements JpaUserService {

    private final UserRepository userRepository;
    private final UserHasRoleRepository userHasRoleRepository;

    @Transactional
    public Set<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserDto.toUserDto(user, userHasRoleRepository.searchAllByUser(user)))
                .collect(Collectors.toSet());
    }

}
