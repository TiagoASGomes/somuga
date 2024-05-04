package org.somuga.dto.user;

import java.util.Date;

public record UserPublicDto(
        String id,
        String userName,
        String email,
        Date joinedDate
) {
}
