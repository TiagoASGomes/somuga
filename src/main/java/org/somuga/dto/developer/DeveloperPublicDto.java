package org.somuga.dto.developer;

import java.util.List;

public record DeveloperPublicDto(
        Long id,
        String developerName,
        List<String> socials
) {
}
