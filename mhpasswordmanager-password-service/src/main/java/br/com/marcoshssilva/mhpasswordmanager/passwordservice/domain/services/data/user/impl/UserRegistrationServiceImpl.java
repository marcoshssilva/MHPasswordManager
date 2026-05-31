package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserAuthorizationModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final UserBucketRepository userBucketRepository;

    public UserRegistrationServiceImpl(UserBucketRepository userBucketRepository) {
        this.userBucketRepository = userBucketRepository;
    }

    @Override
    public UserRegisteredModel getUserRegistration(String tokenSubject) {
        Set<UserBucket> allByOwnerName = userBucketRepository.findAllByOwnerName(tokenSubject);
        return UserRegisteredModel.builder()
                .ownerName(tokenSubject)
                .buckets(allByOwnerName.stream().map(UserBucket::getId).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public UserAuthorizations getUserAuthorizations(Jwt jwt) throws UserAuthorizationCannotBeLoadedException {
        try {
            return UserAuthorizationModel.builder().username(jwt.getSubject()).roles(Collections.emptySet()).profiles(Collections.emptySet())
                    .build();
        } catch (Exception e) {
            throw new UserAuthorizationCannotBeLoadedException(e);
        }
    }

}
