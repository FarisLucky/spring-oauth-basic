package salman.rest.example.service;

import java.util.List;
import salman.rest.example.entity.User;

public interface UserService {

    List<User> getAll();

    User findById(Long id);

    void save(User user);

    User update(User user, Long id);

    void delete(Long id);
}
