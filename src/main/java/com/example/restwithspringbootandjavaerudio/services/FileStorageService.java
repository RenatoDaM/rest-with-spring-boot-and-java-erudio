package com.example.restwithspringbootandjavaerudio.services;

import com.example.restwithspringbootandjavaerudio.config.FileStorageConfig;
import com.example.restwithspringbootandjavaerudio.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        this.fileStorageLocation = path;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException(
                    "Could not create the directory where the uploaded files will be stored!", e);
        }

    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // regras de validação
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry, this filename contains invalid path sequence " + fileName);
            }
            // cria arquivo:
            Path targedLocation = this.fileStorageLocation.resolve(fileName);
            // passa oq tem q passar pra dentro do tipo do arquivo:
            Files.copy(file.getInputStream(), targedLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new FileStorageException(
                    "Could not store file " + fileName + ". Please try again." , e);
        }
    }
}
