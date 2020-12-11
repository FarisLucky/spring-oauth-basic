package salman.rest.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import salman.rest.example.entity.UserDocument;
import salman.rest.example.model.WebResponse;
import salman.rest.example.model.response.UploadFileResponse;
import salman.rest.example.service.UserDocumentService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "api/user/")
public class DocumentController {

    private final UserDocumentService userDocumentService;

    @Autowired
    public DocumentController(UserDocumentService userDocumentService) {
        this.userDocumentService = userDocumentService;
    }

    @GetMapping("document")
    public WebResponse<List<UserDocument>> allDocument() {
        return WebResponse.<List<UserDocument>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(userDocumentService.findAll())
                .build();
    }

    @PostMapping("uploadFile")
    public WebResponse<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("userId") Long id,
                                                      @RequestParam("docType") String docType) {

        String fileName = userDocumentService.storeFile(file, id, docType);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/file/")
                .path(fileName)
                .toUriString();
        UploadFileResponse uploadFileResponse = new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
        return WebResponse.<UploadFileResponse>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(uploadFileResponse)
                .build();
    }

    @GetMapping("downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("userId") Long userId,
                                                 @RequestParam("docType") String docType,
                                                 HttpServletRequest request) {
        String fileName = userDocumentService.getDocumentName(userId, docType);
        Resource resource = null;
        if (fileName != null && !fileName.isEmpty()) {
            try {
                resource = userDocumentService.loadFileAsResource(fileName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+resource.getFilename())
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
