package in.vedutech.cloudstorage.controller;

import in.vedutech.cloudstorage.document.UserCredits;
import in.vedutech.cloudstorage.dto.FileMetadataDTO;
import in.vedutech.cloudstorage.service.FileMetadataService;
import in.vedutech.cloudstorage.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileMetadataService fileMetadataService;
    private final UserCreditsService userCreditsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files") MultipartFile[] files) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<FileMetadataDTO> list = fileMetadataService.uploadFiles(files);

        UserCredits finalCredits = userCreditsService.getUserCredits();

        response.put("files", list);
        response.put("remainingCredits", finalCredits.getCredits());
        return ResponseEntity.ok(response);

    }

    @org.springframework.web.bind.annotation.GetMapping("/my")
    public ResponseEntity<?> getMyFiles() {
        List<FileMetadataDTO> files = fileMetadataService.getMyFiles();
        return ResponseEntity.ok(files);
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/toggle-public")
    public ResponseEntity<?> togglePublicStatus(@org.springframework.web.bind.annotation.PathVariable("id") String id) {
        fileMetadataService.togglePublicStatus(id);
        return ResponseEntity.ok(Map.of("message", "Success"));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@org.springframework.web.bind.annotation.PathVariable("id") String id) {
        fileMetadataService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @org.springframework.web.bind.annotation.GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicFile(@org.springframework.web.bind.annotation.PathVariable("id") String id) {
        FileMetadataDTO file = fileMetadataService.getPublicFile(id);
        return ResponseEntity.ok(file);
    }

    @org.springframework.web.bind.annotation.GetMapping("/download/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@org.springframework.web.bind.annotation.PathVariable("id") String id) throws IOException {
        org.springframework.core.io.Resource resource = fileMetadataService.downloadFile(id);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment;")
                .body(resource);
    }
}
