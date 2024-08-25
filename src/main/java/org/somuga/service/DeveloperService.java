package org.somuga.service;

import org.somuga.converter.DeveloperConverter;
import org.somuga.dto.developer.DeveloperCreateDto;
import org.somuga.dto.developer.DeveloperListDto;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.entity.Developer;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.repository.DeveloperRepository;
import org.somuga.service.interfaces.IDeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.somuga.util.message.Messages.DEVELOPER_ALREADY_EXISTS;
import static org.somuga.util.message.Messages.DEVELOPER_NOT_FOUND;

@Service
public class DeveloperService implements IDeveloperService {

    private final DeveloperRepository developerRepo;

    @Autowired
    public DeveloperService(DeveloperRepository developerRepo) {
        this.developerRepo = developerRepo;
    }

    @Override
    public DeveloperListDto getAll(String name, Pageable page) {
        if (name != null) {
            Long count = developerRepo.countByDeveloperNameContainingIgnoreCase(name);
            return DeveloperConverter.fromEntityListToPublicDtoList(developerRepo.findAllByDeveloperNameContainingIgnoreCase(name, page).toList(), count);
        }
        Long count = developerRepo.count();
        return DeveloperConverter.fromEntityListToPublicDtoList(developerRepo.findAll(page).toList(), count);
    }

    @Override
    public DeveloperPublicDto getById(Long id) throws DeveloperNotFoundException {
        return DeveloperConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public DeveloperPublicDto create(DeveloperCreateDto developerDto) throws DuplicateFieldException {
        Optional<Developer> duplicateDeveloper = findByDeveloperName(developerDto.developerName());
        if (duplicateDeveloper.isPresent()) {
            throw new DuplicateFieldException(DEVELOPER_ALREADY_EXISTS + developerDto.developerName());
        }
        Developer developer = DeveloperConverter.fromCreateDtoToEntity(developerDto);
        return DeveloperConverter.fromEntityToPublicDto(developerRepo.save(developer));
    }

    @Override
    public DeveloperPublicDto update(Long id, DeveloperCreateDto developerDto) throws DeveloperNotFoundException, DuplicateFieldException {
        Optional<Developer> duplicateDeveloper = findByDeveloperName(developerDto.developerName());
        if (duplicateDeveloper.isPresent() && !duplicateDeveloper.get().getId().equals(id)) {
            throw new DuplicateFieldException(DEVELOPER_ALREADY_EXISTS + developerDto.developerName());
        }
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
    public Developer findById(Long id) throws DeveloperNotFoundException {
        return developerRepo.findById(id).orElseThrow(() -> new DeveloperNotFoundException(DEVELOPER_NOT_FOUND + id));
    }

    private Optional<Developer> findByDeveloperName(String developerName) {
        return developerRepo.findByDeveloperNameIgnoreCase(developerName);
    }
}
