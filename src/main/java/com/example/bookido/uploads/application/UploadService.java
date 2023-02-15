package com.example.bookido.uploads.application;

import com.example.bookido.uploads.application.ports.UploadUseCase;
import com.example.bookido.uploads.db.UploadJpaRepository;
import com.example.bookido.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService implements UploadUseCase {
    private final UploadJpaRepository repository;

    @Override
    public Upload save(SaveUploadCommand command) {
        Upload upload = new Upload(
                command.getFilename(),
                command.getContentType(),
                command.getFile()
        );
        repository.save(upload);
        log.info("Upload save: " + upload.getFilename() + " with id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
