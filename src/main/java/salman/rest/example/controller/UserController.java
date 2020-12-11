package salman.rest.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import salman.rest.example.entity.User;
import salman.rest.example.model.WebResponse;
import salman.rest.example.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "api/")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("user")
    public WebResponse<List<User>> getUser() {
        List<User> usersList = userService.getAll();
        return WebResponse.<List<User>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(usersList)
                .build();
    }

    @PostMapping("user")
    public WebResponse<StringBuilder> insert(@RequestBody User user) {
        StringBuilder status = new StringBuilder();
        userService.save(user);
        status.append("Success To Insert");
        return WebResponse.<StringBuilder>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(status)
                .build();
    }

    @PutMapping("user/update/{id_user}")
    public WebResponse<User> update(@PathVariable("id_user") Long id, @RequestBody User user) {
        User user1 = userService.update(user,id);
        return WebResponse.<User>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(user1)
                .build();
    }

    @DeleteMapping("user/delete/{id_user}")
    public WebResponse<StringBuilder> delete(@PathVariable("id_user") Long id) {
        StringBuilder message = new StringBuilder();
        userService.delete(id);
        message.append("Success to Delete User");
        return WebResponse.<StringBuilder>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(message)
                .build();
    }
}
