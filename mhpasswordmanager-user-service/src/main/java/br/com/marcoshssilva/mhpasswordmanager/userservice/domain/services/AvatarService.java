package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountAvatarDataModel;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

public interface AvatarService {
    AccountAvatarDataModel upload(String username, MultipartFile file);
}
