package org.somuga.service.interfaces;

import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    List<UserPublicDto> getAll(Pageable page, String name);

    UserPublicDto getById(String id) throws UserNotFoundException;

    UserPublicDto create(UserCreateDto user) throws DuplicateFieldException;

    UserPublicDto updateUserName(UserCreateDto user) throws UserNotFoundException, DuplicateFieldException;

    void delete() throws UserNotFoundException;

    User findById(String id) throws UserNotFoundException;

    void adminDelete(String id) throws UserNotFoundException;
}
