package org.somuga.converter;

import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserListDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserConverter {

    private UserConverter() {
    }

    public static UserPublicDto fromEntityToPublicDto(User user) {
        if (user == null) return null;
        return new UserPublicDto(user.getId(),
                user.getUserName(),
                user.getJoinDate(),
                user.getEmail());
    }

    public static UserListDto fromEntityListToPublicDtoList(List<User> users, Long count) {
        if (users == null) return new UserListDto(new ArrayList<>(0), 0L);
        List<UserPublicDto> userPublicDtos = users.stream()
                .map(UserConverter::fromEntityToPublicDto)
                .toList();
        return new UserListDto(userPublicDtos, count);
    }

    public static User fromCreateDtoToEntity(UserCreateDto user, String id) {
        if (user == null) return null;
        return User.builder()
                .id(id)
                .userName(user.userName())
                .email(user.email())
                .build();
    }
}
