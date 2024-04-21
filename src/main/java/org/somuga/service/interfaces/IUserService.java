package org.somuga.service.interfaces;

import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.dto.user.UserUpdateNameDto;
import org.somuga.entity.User;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    List<UserPublicDto> getAll(Pageable page);

    List<UserPublicDto> getAllByName(Pageable page, String name);

    UserPublicDto getById(Long id) throws UserNotFoundException;

    UserPublicDto create(UserCreateDto user) throws DuplicateFieldException;

    UserPublicDto updateUserName(Long id, UserUpdateNameDto user) throws UserNotFoundException, DuplicateFieldException;

    void delete(Long id) throws UserNotFoundException;

    User findById(Long id) throws UserNotFoundException;

}
