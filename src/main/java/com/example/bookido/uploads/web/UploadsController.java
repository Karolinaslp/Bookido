package com.example.bookido.uploads.web;

import com.example.bookido.uploads.application.ports.UploadUseCase;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/uploads")
@AllArgsConstructor
public class UploadsController {
    private UploadUseCase upload;

    @GetMapping("/{id}")
    public ResponseEntity<UploadResponse> getUpload(@PathVariable Long id) {
        return upload.getById(id)
                     .map(file -> {
                         UploadResponse response = new UploadResponse(
                            file.getId(),
                            file.getContentType(),
                            file.getFilename(),
                            file.getCreatedAt()
                         );
                         return ResponseEntity.ok(response);
                     })
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getUploadFile(@PathVariable Long id) {
        return upload.getById(id)
                .map(file -> {
                    String contentDisposition = "attachment; filename=\"" + file.getFilename() + "\"";
                    Resource resource = new ByteArrayResource(file.getFile());
                    return ResponseEntity
                            .ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .contentType(MediaType.parseMediaType(file.getContentType()))
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Value
    @AllArgsConstructor
    static class UploadResponse {
        Long id;
        String contentType;
        String fileName;
        LocalDateTime createdAt;
    }
}
