package ltphat.cloudvault.backend.iam.application.mapper;

import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.domain.model.User;

public class ManualAuthApplicationMapper implements AuthApplicationMapper {

    @Override
    public UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
