package salman.rest.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import salman.rest.example.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT * from user WHERE user.email = ?1",nativeQuery = true)
    Optional<User> findAllByEmail(String email);
}
