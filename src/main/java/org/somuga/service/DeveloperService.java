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
import org.springframework.stereotype.Service;

import java.util.List;

import static org.somuga.util.message.Messages.*;

@Service
public class DeveloperService implements IDeveloperService {

    private final DeveloperRepository developerRepo;

    @Autowired
    public DeveloperService(DeveloperRepository developerRepo) {
        this.developerRepo = developerRepo;
    }

    @Override
    public List<DeveloperPublicDto> getAll(String name) {
        if (name != null) {
            return DeveloperConverter.fromEntityListToPublicDtoList(developerRepo.findByDeveloperNameContainingIgnoreCase(name));
        }
        return DeveloperConverter.fromEntityListToPublicDtoList(developerRepo.findAll());
    }

    @Override
    public DeveloperPublicDto getById(Long id) throws DeveloperNotFoundException {
        return DeveloperConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public DeveloperPublicDto create(DeveloperCreateDto developerDto) throws DuplicateFieldException {
        if (checkDuplicateDeveloperName(developerDto.developerName())) {
            throw new DuplicateFieldException(DEVELOPER_ALREADY_EXISTS + developerDto.developerName());
        }
        Developer developer = DeveloperConverter.fromCreateDtoToEntity(developerDto);
        return DeveloperConverter.fromEntityToPublicDto(developerRepo.save(developer));
    }

    @Override
    public DeveloperPublicDto update(Long id, DeveloperCreateDto developerDto) throws DeveloperNotFoundException {
        Developer developer = findById(id);
        developer.setDeveloperName(developerDto.developerName());
        developer.setSocials(developerDto.socials());
        return DeveloperConverter.fromEntityToPublicDto(developerRepo.save(developer));
    }

    @Override
    public void delete(Long id) throws DeveloperNotFoundException {
        findById(id);
        developerRepo.deleteById(id);
    }

    @Override
    public Developer findByDeveloperName(String developerName) throws DeveloperNotFoundException {
        return developerRepo.findByDeveloperNameIgnoreCase(developerName).orElseThrow(() -> new DeveloperNotFoundException(DEVELOPER_NOT_FOUND_NAME + developerName));
    }

    @Override
    public Developer findById(Long id) throws DeveloperNotFoundException {
        return developerRepo.findById(id).orElseThrow(() -> new DeveloperNotFoundException(DEVELOPER_NOT_FOUND + id));
    }

    private boolean checkDuplicateDeveloperName(String developerName) {
        try {
            findByDeveloperName(developerName);
            return true;
        } catch (DeveloperNotFoundException ignored) {
            return false;
        }
    }
}
