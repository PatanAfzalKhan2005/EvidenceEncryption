package com.imageseal.controller;
import com.imageseal.entity.AppUser;import com.imageseal.repository.UserRepository;import com.imageseal.security.JwtService;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/auth") public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthController(UserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
 public record Credentials(@Email @NotBlank String email,@NotBlank @Size(min=8,max=72) String password){}
 private Map<String,Object> response(AppUser u){return Map.of("token",jwt.issue(u.getId(),u.getEmail()),"user",Map.of("id",u.getId(),"email",u.getEmail()));}
 @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody Credentials c){String email=c.email().trim().toLowerCase();if(users.findByEmail(email).isPresent())return ResponseEntity.status(409).body(Map.of("message","An account with this email already exists."));AppUser u=users.save(new AppUser(email,encoder.encode(c.password())));return ResponseEntity.status(201).body(response(u));}
 @PostMapping("/login") public ResponseEntity<?> login(@Valid @RequestBody Credentials c){var u=users.findByEmail(c.email().trim().toLowerCase()).orElse(null);if(u==null||!encoder.matches(c.password(),u.getPasswordHash()))return ResponseEntity.status(401).body(Map.of("message","Invalid email or password."));return ResponseEntity.ok(response(u));}
}
