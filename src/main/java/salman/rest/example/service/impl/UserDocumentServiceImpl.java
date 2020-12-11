package salman.rest.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import salman.rest.example.entity.User;
import salman.rest.example.entity.UserDocument;
import salman.rest.example.exception.DocumentStorageException;
import salman.rest.example.repository.UserDocumentRepository;
import salman.rest.example.repository.UserRepository;
import salman.rest.example.service.UserDocumentService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class UserDocumentServiceImpl implements UserDocumentService {

    private final UserDocumentRepository userDocumentRepository;

    private final UserRepository userRepository;

    private final Path fileStorageLocation;

    @Autowired
    public UserDocumentServiceImpl(UserDocumentRepository userDocumentRepository, UserDocument userDocument, UserRepository userRepository) {
        this.userDocumentRepository = userDocumentRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get(userDocument.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new DocumentStorageException("TIdak dapat membuat direktori untuk menyimpan data file", ex);
        }
    }

    @Override
    public List<UserDocument> findAll() {
        return userDocumentRepository.findAll();
    }

    @Override
    public String storeFile(MultipartFile file, Long userId, String docType) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = "";
        try {
            if (originalFileName.contains("..")) {
                throw new DocumentStorageException("Maaf! Path tidak valid" + originalFileName);
            }
            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                fileExtension = "";
            }
            fileName = userId + "_" + docType + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UserDocument document = userDocumentRepository.findByUserId(userId,docType);
            if (document != null) {
                document.setDocumentFormat(file.getContentType());
                document.setFilename(fileName);
                userDocumentRepository.save(document);
            } else {
                User user = userRepository.getOne(userId);
                UserDocument newDocument = new UserDocument();
                newDocument.setUserId(user);
                newDocument.setDocumentFormat(file.getContentType());
                newDocument.setFilename(fileName);
                newDocument.setDocumentType(docType);
                userDocumentRepository.save(newDocument);
            }
            return fileName;
        } catch (IOException ex) {
            throw new DocumentStorageException("tidak bisa upload file " + fileName + ". Silahkan coba kembali", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) throws Exception {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File Not Found "+filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("file Not Found "+filename);
        }
    }

    @Override
    public String getDocumentName(Long userId, String docType) {
        return userDocumentRepository.findUserDocumentByUserIdAndAndDocumentType(userId, docType);
    }
}
