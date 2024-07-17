package org.somuga.converter;

import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;

import java.util.List;

public class UserConverter {

    private UserConverter() {
    }

    public static UserPublicDto fromEntityToPublicDto(User user) {
        if (user == null) return null;
        return new UserPublicDto(user.getId(),
                user.getUserName(),
                user.getJoinDate());
    }

    public static List<UserPublicDto> fromEntityListToPublicDtoList(List<User> users) {
        if (users == null) return List.of();
        return users.stream()
                .map(UserConverter::fromEntityToPublicDto)
                .toList();
    }

    public static User fromCreateDtoToEntity(UserCreateDto user, String id) {
        if (user == null) return null;
        return User.builder()
                .id(id)
                .userName(user.userName())
                .build();
    }
}
