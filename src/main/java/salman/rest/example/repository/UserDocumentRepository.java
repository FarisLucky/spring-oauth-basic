package salman.rest.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import salman.rest.example.entity.User;
import salman.rest.example.entity.UserDocument;

public interface UserDocumentRepository extends JpaRepository<UserDocument,Long> {

    @Query(value = "select * from user_document where user_id=?1 and document_type=?2",nativeQuery = true)
    UserDocument findByUserId(Long id,String docType);

    @Query(value = "select user_document.filename from user_document where user_document.user_id=?1 and document_type=?2",nativeQuery = true)
    String findUserDocumentByUserIdAndAndDocumentType(Long id, String docType);
}
