package org.somuga.dto.movie;

import org.somuga.dto.crew_role.CrewRolePublicDto;
import org.somuga.dto.media.MediaPublicDto;

import java.util.Date;
import java.util.List;

public record MoviePublicDto(
        Long id,
        String title,
        Date releaseDate,
        String description,
        Integer duration,
        List<CrewRolePublicDto> crew
) implements MediaPublicDto {
}
