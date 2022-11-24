package br.com.marcoshssilva.mhpasswordmanager.userservice.controllers.user.view;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserDto;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/view")
@RequiredArgsConstructor
public class UserViewAllController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @PageableDefault(page = 0, size = 5000)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "username", direction = Sort.Direction.ASC)
            })
                    Pageable pageable
    ) {
        Page<UserDto> users = userService.getAllUsers(pageable).map(UserDto::fromEntity);
        return ResponseEntity.ok(users);
    }
}
