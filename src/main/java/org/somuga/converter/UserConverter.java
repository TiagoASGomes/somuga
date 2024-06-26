package org.somuga.converter;

import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;

import java.util.List;

public class UserConverter {

    private UserConverter() {
    }

    public static UserPublicDto fromEntityToPublicDto(User user) {
        return new UserPublicDto(user.getId(),
                user.getUserName(),
                user.getJoinDate());
    }

    public static List<UserPublicDto> fromEntityListToPublicDtoList(List<User> users) {
        return users.stream()
                .map(UserConverter::fromEntityToPublicDto)
                .toList();
    }

    public static User fromCreateDtoToEntity(UserCreateDto user, String id) {
        return new User(id, user.userName());
    }
}
