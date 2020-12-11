package salman.rest.example.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import salman.rest.example.entity.UserDocument;

import java.util.List;

public interface UserDocumentService {

    List<UserDocument> findAll();

    String storeFile(MultipartFile file, Long userId, String docType);

    Resource loadFileAsResource(String filename) throws Exception;

    String getDocumentName(Long userId, String docType);
}
