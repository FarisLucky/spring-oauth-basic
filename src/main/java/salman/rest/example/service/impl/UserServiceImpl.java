package salman.rest.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salman.rest.example.entity.User;
import salman.rest.example.repository.UserRepository;
import salman.rest.example.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) throws RuntimeException{
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("User tidak ditemukan");
        }
    }

    @Override
    public void save(User user) throws RuntimeException{
        Optional<User> user1 = userRepository.findAllByEmail(user.getEmail());
        if (user1.isPresent()) {
            throw new RuntimeException("Email ada yang sama");
        }
        userRepository.save(user);
    }

    @Override
    public User update(User user, Long id) throws RuntimeException{
        Optional<User> userTarget = userRepository.findById(id);
        if (userTarget.isPresent()) {
            User user1 = userTarget.get();
            BeanUtils.copyProperties(user,user1);
            userRepository.save(user1);
            return user1;
        } else {
            throw new RuntimeException("Gagal di update");
        }
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
