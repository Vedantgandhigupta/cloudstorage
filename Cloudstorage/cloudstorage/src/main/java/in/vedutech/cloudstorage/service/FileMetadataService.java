package in.vedutech.cloudstorage.service;

import in.vedutech.cloudstorage.document.FileMetadataDocument;
import in.vedutech.cloudstorage.document.ProfileDocument;
import in.vedutech.cloudstorage.dto.FileMetadataDTO;
import in.vedutech.cloudstorage.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
    private final FileMetadataRepository fileMetadataRepository;

    public List<FileMetadataDTO> uploadFiles(MultipartFile files[]) throws IOException {
        String clerkId = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        List<FileMetadataDocument> savedFiles = new ArrayList<>();

        if (!userCreditsService.hasEnoughCredits(files.length)) {
            throw new RuntimeException("Not Enough credits to upload files. Please purchase more credits");
        }

        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for (MultipartFile file : files) {
            String filename = UUID.randomUUID()+"."+ StringUtils.getFilename(file.getOriginalFilename());
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetadataDocument fileMetadata = FileMetadataDocument.builder()
                    .fileLocation(targetLocation.toString())
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(file.getContentType())
                    .clerkId(clerkId)
                    .isPublic(false)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            userCreditsService.consumeCredit();

            savedFiles.add(fileMetadataRepository.save(fileMetadata));
        }

        return savedFiles.stream().map(fileMetadataDocument -> mapToDTO(fileMetadataDocument))
                .collect(Collectors.toList());
    }

    public List<FileMetadataDTO> getMyFiles() {
        String clerkId = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        List<FileMetadataDocument> files = fileMetadataRepository.findByClerkId(clerkId);
        return files.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void togglePublicStatus(String id) {
        String clerkId = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        FileMetadataDocument file = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!file.getClerkId().equals(clerkId)) {
            throw new RuntimeException("Unauthorized");
        }
        file.setIsPublic(!file.getIsPublic());
        fileMetadataRepository.save(file);
    }

    public void deleteFile(String id) {
        String clerkId = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        FileMetadataDocument file = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!file.getClerkId().equals(clerkId)) {
            throw new RuntimeException("Unauthorized");
        }
        try {
            Files.deleteIfExists(Paths.get(file.getFileLocation()));
        } catch(IOException e) {}
        fileMetadataRepository.delete(file);
    }

    public FileMetadataDTO getPublicFile(String id) {
        FileMetadataDocument file = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!file.getIsPublic()) {
            throw new RuntimeException("File is not public");
        }
        return mapToDTO(file);
    }

    public org.springframework.core.io.Resource downloadFile(String id) throws IOException {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String clerkId = (auth != null && !auth.getPrincipal().equals("anonymousUser")) ? auth.getName() : null;

        FileMetadataDocument file = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
                
        if (!file.getIsPublic()) {
            if (clerkId == null || !file.getClerkId().equals(clerkId)) {
                throw new RuntimeException("Unauthorized");
            }
        }
        
        Path path = Paths.get(file.getFileLocation());
        return new org.springframework.core.io.UrlResource(path.toUri());
    }

    private FileMetadataDTO mapToDTO(FileMetadataDocument fileMetadataDocument) {
        return FileMetadataDTO.builder()
                .id(fileMetadataDocument.getId())
                .fileLocation(fileMetadataDocument.getFileLocation())
                .name(fileMetadataDocument.getName())
                .size(fileMetadataDocument.getSize())
                .type(fileMetadataDocument.getType())
                .clerkId(fileMetadataDocument.getClerkId())
                .isPublic(fileMetadataDocument.getIsPublic())
                .uploadedAt(fileMetadataDocument.getUploadedAt() != null ? fileMetadataDocument.getUploadedAt().toString() : null)
                .build();
    }
}
