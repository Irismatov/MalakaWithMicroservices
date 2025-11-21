package com.malaka.aat.internal.service;

import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.model.File;
import com.malaka.aat.internal.repository.FileRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${app.file.path}")
    private String filePath;

    @Value("${server.port}")
    private String serverPort;

    private final FileRepository repository;

    public File save (MultipartFile multipartFile) throws IOException {

        String hash = HashUtil.sha256(multipartFile.getBytes());

        Optional<File> byHash = repository.findByHash(hash);

        if (byHash.isPresent()) {
            File oldFile = byHash.get();
            File file = new  File();
            file.setHash(hash);
            file.setContentType(oldFile.getContentType());
            file.setFileSize(oldFile.getFileSize());
            file.setOriginalName(oldFile.getOriginalName());
            file.setPath(oldFile.getPath());
            file.setExtension(oldFile.getExtension());
            return repository.save(file);
        }

        LocalDate today = LocalDate.now();
        String folderPath = String.format("%s/%d/%02d/%02d",
                filePath, today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        java.io.File folder = new java.io.File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String originalName = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String filePath = folderPath + "/" + hash + extension;

        java.io.File file = new java.io.File(filePath);

        if (!file.exists()) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File newFile = new File();
        newFile.setOriginalName(originalName);
        newFile.setFileSize(multipartFile.getSize());
        newFile.setContentType(multipartFile.getContentType());
        newFile.setHash(hash);
        newFile.setPath(filePath);
        newFile.setExtension(extension);
        return repository.save(newFile);
    }

    /**
     * Convert file system path to public URL
     * Example: D:/MalakaFiles/images/2025/01/15/abc123.jpg -> http://localhost:8585/uploads/images/2025/01/15/abc123.jpg
     */
    public String getPublicUrl(File file) {
        if (file == null || file.getPath() == null) {
            return null;
        }

        String absolutePath = file.getPath();

        // Remove the base path prefix to get the relative path
        String relativePath = absolutePath.replace(filePath, "").replace("\\", "/");
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // Construct the public URL
        return "http://localhost:" + serverPort + "/uploads/images/" + relativePath;
    }

    /**
     * Convert file system path to public URL (for any File object)
     */
    public String getPublicUrl(String absolutePath) {
        if (absolutePath == null) {
            return null;
        }

        // Remove the base path prefix to get the relative path
        String relativePath = absolutePath.replace(filePath, "").replace("\\", "/");
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // Construct the public URL
        return "http://localhost:" + serverPort + "/uploads/images/" + relativePath;
    }

    /**
     * Load file as Resource for serving
     */
    public Resource loadFileAsResource(String fileId) {
        try {
            File file = repository.findById(fileId)
                    .orElseThrow(() -> new NotFoundException("File not found with id: " + fileId));

            Path filePath = Paths.get(file.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new NotFoundException("File not found or not readable: " + fileId);
            }
        } catch (MalformedURLException e) {
            throw new NotFoundException("File not found: " + fileId);
        }
    }

    /**
     * Get content type for file
     */
    public String getContentType(String fileId) {
        File file = repository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found with id: " + fileId));
        return file.getContentType() != null ? file.getContentType() : "application/octet-stream";
    }


    public File getFileById(String fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found with id: " + fileId));
    }

}
