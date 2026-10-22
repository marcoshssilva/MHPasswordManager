package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountAvatarDataModel;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {
    AccountAvatarDataModel upload(String username, MultipartFile file);
}
