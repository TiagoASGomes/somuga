package org.somuga.service;

import org.somuga.converter.UserConverter;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.User;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.repository.UserRepository;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public List<UserPublicDto> getAll(Pageable page, String name) {
        if (name != null) {
            return UserConverter.fromEntityListToPublicDtoList(userRepo.findAllByUserNameContainingIgnoreCaseAndActiveTrue(name, page).toList());
        }
        return UserConverter.fromEntityListToPublicDtoList(userRepo.findAllByActiveTrue(page).toList());
    }

    @Override
    public UserPublicDto getById(String id) throws UserNotFoundException {
        User user = findById(id);
        if (!user.isActive()) {
            throw new UserNotFoundException(USER_NOT_FOUND + id);
        }
        return UserConverter.fromEntityToPublicDto(user);
    }

    @Override
    public UserPublicDto create(UserCreateDto userDto) throws DuplicateFieldException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String id = auth.getName();
        Optional<User> duplicate = userRepo.findById(id);
        if (duplicate.isPresent()) {
            throw new DuplicateFieldException(DUPLICATE_USER + id);
        }
        User user = UserConverter.fromCreateDtoToEntity(userDto, id);
        checkDuplicateFields(user.getUserName());
        user.setActive(true);
        user.setJoinDate(new Date());
        return UserConverter.fromEntityToPublicDto(userRepo.save(user));
    }

    @Override
    public UserPublicDto updateUserName(UserCreateDto userDto) throws UserNotFoundException, DuplicateFieldException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = findById(auth.getName());
        if (!user.isActive()) {
            throw new UserNotFoundException(USER_NOT_FOUND + auth.getName());
        }
        checkDuplicateFields(userDto.userName());
        user.setUserName(userDto.userName());
        return UserConverter.fromEntityToPublicDto(userRepo.save(user));
    }

    @Override
    public void delete() throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = findById(auth.getName());
        if (!user.isActive()) {
            throw new UserNotFoundException(USER_NOT_FOUND + auth.getName());
        }
        user.setUserName("");
        user.setActive(false);
        userRepo.save(user);
    }

    @Override
    public User findById(String id) throws UserNotFoundException {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
    }

    @Override
    public void adminDelete(String id) throws UserNotFoundException {
        findById(id);
        userRepo.deleteById(id);
    }

    private void checkDuplicateFields(String userName) throws DuplicateFieldException {
        Optional<User> opt = userRepo.findByUserNameIgnoreCase(userName);
        if (opt.isPresent()) {
            throw new DuplicateFieldException(DUPLICATE_USERNAME + userName);
        }
    }
}
