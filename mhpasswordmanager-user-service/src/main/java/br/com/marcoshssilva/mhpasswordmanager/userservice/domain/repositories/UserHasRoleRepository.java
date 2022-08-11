package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.User;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.UserRoles;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.UserRolesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserRoles, UserRolesPK> {

    @Query("SELECT obj FROM UserRoles obj WHERE obj.id.userId = :user")
    Set<UserRoles> searchAllByUser(@Param("user") User user);
}
