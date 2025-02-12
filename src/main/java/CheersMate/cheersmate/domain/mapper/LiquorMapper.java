package CheersMate.cheersmate.domain.mapper;

import CheersMate.cheersmate.domain.dto.LiquorDTO;
import CheersMate.cheersmate.domain.entity.Liquor;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LiquorMapper {

    // Entity -> DTO
    @Mapping(source = "alcohol", target = "volume")
    @Mapping(source = "imageLink", target = "imageUrl")
    LiquorDTO toDto(Liquor entity);

    // DTO -> Entity
    @InheritInverseConfiguration
    Liquor toEntity(LiquorDTO dto);
}
