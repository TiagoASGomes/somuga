package org.somuga.service;

import org.somuga.converter.UserConverter;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.dto.user.UserUpdateNameDto;
import org.somuga.entity.User;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.repository.UserRepository;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.somuga.util.message.Messages.*;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public List<UserPublicDto> getAll(Pageable page) {
        return UserConverter.fromEntityListToPublicDtoList(userRepo.findByActiveTrue(page).toList());
    }

    @Override
    public List<UserPublicDto> getAllByName(Pageable page, String name) {
        return UserConverter.fromEntityListToPublicDtoList(userRepo.findByUserNameContaining(name, page).toList());
    }

    @Override
    public UserPublicDto getById(Long id) throws UserNotFoundException {
        return UserConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public UserPublicDto create(UserCreateDto userDto) throws DuplicateFieldException {
        User user = UserConverter.fromCreateDtoToEntity(userDto);
        checkDuplicateFields(user.getEmail(), user.getUserName());
        user.setActive(true);
        user.setJoinDate(new Date());
        return UserConverter.fromEntityToPublicDto(userRepo.save(user));
    }


    @Override
    public UserPublicDto updateUserName(Long id, UserUpdateNameDto userDto) throws UserNotFoundException, DuplicateFieldException {
        User user = findById(id);
        checkDuplicateFields("XXXXXX", userDto.userName());
        user.setUserName(userDto.userName());
        return UserConverter.fromEntityToPublicDto(userRepo.save(user));
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        User user = findById(id);
        user.setUserName("");
        user.setEmail("");
        user.setActive(false);
        userRepo.save(user);
    }

    @Override
    public User findById(Long id) throws UserNotFoundException {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
    }

    private void checkDuplicateFields(String email, String userName) throws DuplicateFieldException {
        Optional<User> opt1 = userRepo.findByEmail(email);
        Optional<User> opt2 = userRepo.findByUserName(userName);
        if (opt1.isPresent()) {
            throw new DuplicateFieldException(DUPLICATE_EMAIL + email);
        }
        if (opt2.isPresent()) {
            throw new DuplicateFieldException(DUPLICATE_USERNAME + userName);
        }
    }
}
