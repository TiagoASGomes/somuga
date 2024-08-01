package org.somuga.service.interfaces;

import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;

import java.util.List;

public interface IDeveloperService {

    List<DeveloperPublicDto> getAll(String name);

    DeveloperPublicDto getById(Long id) throws DeveloperNotFoundException;

    DeveloperPublicDto create(DeveloperCreateDto developerDto) throws DuplicateFieldException;

    DeveloperPublicDto update(Long id, DeveloperCreateDto developerDto) throws DeveloperNotFoundException, DuplicateFieldException;

    void delete(Long id) throws DeveloperNotFoundException;

    Developer findById(Long id) throws DeveloperNotFoundException;
}
