package com.example.bookido.uploads.application.ports;

import com.example.bookido.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import lombok.Value;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
      String filename;
      byte[] file;
      String contentType;
    }
}
