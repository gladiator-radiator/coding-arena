package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.dto.RegisterUserRequest;
import cz.arena.coding_arena.dto.UserResponseDto;
import cz.arena.coding_arena.model.User;
import cz.arena.coding_arena.repository.UserRepository;
import cz.arena.coding_arena.util.PasswordHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Hash the password before saving
        user.setPasswordHash(PasswordHashUtil.hashPassword(request.getPassword()));
        user.setRole("PLAYER");

        User savedUser = userRepository.save(user);

        // Return DTO without password
        UserResponseDto response = new UserResponseDto(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

