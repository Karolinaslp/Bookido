package com.example.bookido.uploads.application;

import com.example.bookido.uploads.application.ports.UploadUseCase;
import com.example.bookido.uploads.db.UploadJpaRepository;
import com.example.bookido.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


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
        System.out.println("Upload save: " + upload.getFilename() + " with id: " + upload.getId());
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
