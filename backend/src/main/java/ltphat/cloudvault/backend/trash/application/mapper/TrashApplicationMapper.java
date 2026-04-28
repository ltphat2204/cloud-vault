package ltphat.cloudvault.backend.trash.application.mapper;

import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import ltphat.cloudvault.backend.trash.domain.model.TrashItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrashApplicationMapper {
    TrashItemDto toDto(TrashItem domain);
    List<TrashItemDto> toDtoList(List<TrashItem> domainList);
}
