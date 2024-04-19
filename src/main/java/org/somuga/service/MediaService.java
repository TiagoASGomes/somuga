package org.somuga.service;

import org.somuga.entity.Media;
import org.somuga.service.interfaces.IMediaService;
import org.springframework.stereotype.Service;

@Service
public class MediaService implements IMediaService {
    @Override
    public Media findById(Long id) {
        return null;
    }
}
