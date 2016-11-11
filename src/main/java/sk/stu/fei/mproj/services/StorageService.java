package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import sk.stu.fei.mproj.configuration.ApplicationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Transactional
public class StorageService {
    private final ApplicationProperties applicationProperties;

    @Autowired
    public StorageService(ApplicationProperties properties) {
        this.applicationProperties = properties;
    }

    @PostConstruct
    public void initStorageService() {
        if ( applicationProperties.getEnableFileUpload() ) {
            if ( applicationProperties.getFileUploadRootFolder() != null ) {
                switch ( applicationProperties.getFileUploadRootFolderInitStrategy() ) {
                    case "create":
                        if ( !Files.exists(Paths.get(applicationProperties.getFileUploadRootFolder())) ) {
                            init();
                        }
                        break;
                    case "recreate":
                        deleteAll();
                        init();
                        break;
                    case "create-delete":
                        init();
                        break;
                    default:
                        throw new StorageException("Wrong parameter in application.file-upload-root-folder-init-strategy.\n " +
                                "Allowed are create, recreate or create-delete");
                }
            }
            else {
                throw new StorageException("Set correct application.file-upload-root-folder in application-default.properties.");
            }
        }
    }

    @PreDestroy
    public void destroyStorageService() {
        if ( applicationProperties.getEnableFileUpload() ) {
            if ( applicationProperties.getFileUploadRootFolder() != null ) {
                switch ( applicationProperties.getFileUploadRootFolderInitStrategy() ) {
                    case "create-delete":
                        deleteAll();
                        break;
                }
            }
            else {
                throw new StorageException("Set correct application.file-upload-root-folder in application-default.properties.");
            }
        }
    }

    public void store(MultipartFile file, String newFileName) {
        if ( applicationProperties.getEnableFileUpload() ) {
            try {
                if ( file.isEmpty() ) {
                    throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
                }
                Files.copy(file.getInputStream(), Paths.get(applicationProperties.buildFilePath(newFileName)));
            }
            catch ( IOException e ) {
                throw new StorageException("Failed to store file " + newFileName, e);
            }
        }
        else {
            throw new StorageService.StorageException("Enable file upload and set correct file-upload-root-folder in application-default.properties.");
        }
    }

    public Resource loadAsResource(String filename) {
        if ( applicationProperties.getEnableFileUpload() ) {
            try {
                Resource resource = new UrlResource(Paths.get(applicationProperties.buildFilePath(filename)).toUri());
                if ( resource.exists() || resource.isReadable() ) {
                    return resource;
                }
                else {
                    throw new FileNotFoundException("Could not read file: " + filename);
                }
            }
            catch ( MalformedURLException e ) {
                throw new FileNotFoundException("Could not read file: " + filename, e);
            }
        }
        else {
            throw new StorageService.StorageException("Enable file upload and set correct file-upload-root-folder in application-default.properties.");
        }
    }

    public void delete(String filename) {
        if ( applicationProperties.getEnableFileUpload() ) {
            try {
                Files.delete(Paths.get(applicationProperties.buildFilePath(filename)));
            }
            catch ( IOException e ) {
                throw new StorageException("Could not delete file " + filename, e);
            }
        }
        else {
            throw new StorageService.StorageException("Enable file upload and set correct application.file-upload-root-folder in application-default.properties.");
        }
    }

    private void deleteAll() {
        FileSystemUtils.deleteRecursively(new File(applicationProperties.getFileUploadRootFolder()));
    }

    private void init() {
        try {
            Files.createDirectory(Paths.get(applicationProperties.getFileUploadRootFolder()));
        }
        catch ( FileAlreadyExistsException e ) {
            throw new StorageException("Folder already exists", e);
        }
        catch ( IOException e ) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public static class FileNotFoundException extends StorageException {
        public FileNotFoundException(String message) {
            super(message);
        }

        public FileNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
