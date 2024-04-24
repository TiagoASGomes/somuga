package org.somuga.service;

import org.somuga.converter.DeveloperConverter;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.repository.DeveloperRepository;
import org.somuga.service.interfaces.IDeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.somuga.message.Messages.*;

@Service
public class DeveloperService implements IDeveloperService {

    private final DeveloperRepository developerRepo;

    @Autowired
    public DeveloperService(DeveloperRepository developerRepo) {
        this.developerRepo = developerRepo;
    }

    @Override
    public List<DeveloperPublicDto> getAll(Pageable page) {
        return DeveloperConverter.fromEntityListToPublicDtoList(developerRepo.findAll(page).toList());
    }

    @Override
    public DeveloperPublicDto getById(Long id) throws DeveloperNotFoundException {
        return DeveloperConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public List<DeveloperPublicDto> searchByName(String name, Pageable page) {
        return DeveloperConverter.fromEntityListToPublicDtoList(developerRepo.findByDeveloperNameContaining(name.toLowerCase(), page).toList());
    }

    @Override
    public DeveloperPublicDto create(DeveloperCreateDto developerDto) throws DuplicateFieldException {
        checkDuplicateDeveloperName(developerDto.developerName().toLowerCase());
        Developer developer = new Developer();
        developer.setDeveloperName(developerDto.developerName().toLowerCase());
        return DeveloperConverter.fromEntityToPublicDto(developerRepo.save(developer));
    }

    @Override
    public Developer findByDeveloperName(String developerName) throws DeveloperNotFoundException {
        return developerRepo.findByDeveloperName(developerName.toLowerCase()).orElseThrow(() -> new DeveloperNotFoundException(DEVELOPER_NOT_FOUND_NAME + developerName));
    }

    private Developer findById(Long id) throws DeveloperNotFoundException {
        return developerRepo.findById(id).orElseThrow(() -> new DeveloperNotFoundException(DEVELOPER_NOT_FOUND + id));
    }

    private void checkDuplicateDeveloperName(String developerName) throws DuplicateFieldException {
        try {
            findByDeveloperName(developerName);
            throw new DuplicateFieldException(DEVELOPER_ALREADY_EXISTS);
        } catch (DeveloperNotFoundException ignored) {
        }
    }
}
