package org.somuga.service;

import org.somuga.converter.UserConverter;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.dto.user.UserUpdateNameDto;
import org.somuga.entity.User;
import org.somuga.exception.user.UserDuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.repository.UserRepository;
import org.somuga.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.somuga.message.Messages.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRep;

    @Autowired
    public UserServiceImpl(UserRepository userRep) {
        this.userRep = userRep;
    }


    @Override
    public List<UserPublicDto> getAll(Pageable page, String name) {
        return List.of();
    }

    @Override
    public UserPublicDto getById(Long id) throws UserNotFoundException {
        return UserConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public UserPublicDto create(UserCreateDto userDto) throws UserDuplicateFieldException {
        User user = UserConverter.fromCreateDtoToEntity(userDto);
        checkDuplicateFields(user.getEmail(), user.getUserName());
        user.setActive(true);
        user.setJoinDate(new Date());
        return UserConverter.fromEntityToPublicDto(userRep.save(user));
    }


    @Override
    public UserPublicDto updateUserName(Long id, UserUpdateNameDto userDto) throws UserNotFoundException, UserDuplicateFieldException {
        User user = findById(id);
        checkDuplicateFields("", user.getUserName());
        user.setUserName(user.getUserName());
        return UserConverter.fromEntityToPublicDto(userRep.save(user));
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        User user = findById(id);
        user.setUserName("");
        user.setEmail("");
        user.setActive(false);
        userRep.save(user);
    }

    @Override
    public User findById(Long id) throws UserNotFoundException {
        return userRep.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
    }

    private void checkDuplicateFields(String email, String userName) throws UserDuplicateFieldException {
        Optional<User> opt1 = userRep.findByEmail(email);
        Optional<User> opt2 = userRep.findByUserName(userName);
        if (opt1.isPresent()) {
            throw new UserDuplicateFieldException(DUPLICATE_EMAIL);
        }
        if (opt2.isPresent()) {
            throw new UserDuplicateFieldException(DUPLICATE_USERNAME);
        }
    }
}
