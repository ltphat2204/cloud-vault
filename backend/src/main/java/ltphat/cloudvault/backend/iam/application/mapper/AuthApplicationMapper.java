package ltphat.cloudvault.backend.iam.application.mapper;

import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.domain.model.User;

public interface AuthApplicationMapper {
    UserDto toDto(User user);
}
