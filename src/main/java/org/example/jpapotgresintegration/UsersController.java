package org.example.jpapotgresintegration;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return usersRepository.findAll();

    }

    @PostMapping
    public Users createUser(@RequestBody Users users) {
        return usersRepository.save(users);
    }
}