package org.somuga.service.interfaces;

import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDeveloperService {

    List<DeveloperPublicDto> getAll();

    DeveloperPublicDto getById(Long id) throws DeveloperNotFoundException;

    List<DeveloperPublicDto> searchByName(String name, Pageable page);

    DeveloperPublicDto create(DeveloperCreateDto developerDto) throws DuplicateFieldException;

    Developer findByDeveloperName(String developerName) throws DeveloperNotFoundException;

    DeveloperPublicDto update(Long id, DeveloperCreateDto developerDto) throws DeveloperNotFoundException, InvalidPermissionException;

    void delete(Long id) throws DeveloperNotFoundException, InvalidPermissionException;
}
