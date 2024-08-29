package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserListDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class UserConverterTest {

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        User user = User.builder()
                .id("1")
                .userName("user")
                .joinDate(LocalDate.now())
                .build();

        UserPublicDto userDto = UserConverter.fromEntityToPublicDto(user);

        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getUserName(), userDto.userName());
        assertEquals(user.getJoinDate(), userDto.joinedDate());

    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoNull() {
        assertNull(UserConverter.fromEntityToPublicDto(null));
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert entity list to public dto list")
    void fromEntityListToPublicDtoList() {
        List<User> users = List.of(
                User.builder()
                        .id("1")
                        .userName("user1")
                        .joinDate(LocalDate.now())
                        .build(),
                User.builder()
                        .id("2")
                        .userName("user2")
                        .joinDate(LocalDate.now())
                        .build()
        );

        UserListDto userDtos = UserConverter.fromEntityListToPublicDtoList(users, 2L);

        assertEquals(users.size(), userDtos.users().size());
        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i).getId(), userDtos.users().get(i).id());
            assertEquals(users.get(i).getUserName(), userDtos.users().get(i).userName());
            assertEquals(users.get(i).getJoinDate(), userDtos.users().get(i).joinedDate());
        }
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is empty")
    void fromEntityListToPublicDtoListEmpty() {
        UserListDto userDtos = UserConverter.fromEntityListToPublicDtoList(List.of(), 0L);
        assertEquals(0, userDtos.users().size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is null")
    void fromEntityListToPublicDtoListNull() {
        UserListDto userDtos = UserConverter.fromEntityListToPublicDtoList(null, 0L);
        assertEquals(0, userDtos.users().size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        UserCreateDto userCreateDto = new UserCreateDto("user", "email@example.com");

        User user = UserConverter.fromCreateDtoToEntity(userCreateDto, "1");

        assertEquals("1", user.getId());
        assertEquals(userCreateDto.userName(), user.getUserName());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityNull() {
        assertNull(UserConverter.fromCreateDtoToEntity(null, "1"));
    }
}