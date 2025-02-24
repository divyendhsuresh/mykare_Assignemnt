package com.mykare.mykare_assignment.Service;

import com.mykare.mykare_assignment.DTO.SignupRequest;
//import com.mykare.mykare_assignment.Entity.Role;
import com.mykare.mykare_assignment.Entity.Role;
import com.mykare.mykare_assignment.Entity.User;
import com.mykare.mykare_assignment.Repository.UserRepository;
import com.mykare.mykare_assignment.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final WebClient webClient;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.webClient = WebClient.create();
    }

    private String getPublicIP() {
        String url = "https://api64.ipify.org?format=json";
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> map.get("ip").toString())
                .block();
    }

    private String getCountryFromIP(String ip) {
        String url = "http://ip-api.com/json/" + ip + "?fields=status,message,country";
        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && "success".equals(response.get("status"))) {
            return response.get("country").toString();
        }
        return "cant get result from ip";
    }

    public ResponseEntity<ApiResponse> registerUser(SignupRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            System.out.println("test-1" + existingUser);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email is already registered."));
        } else {
            String ip = getPublicIP();
            String country = getCountryFromIP(ip);

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setGender(request.getGender());
            user.setIpAddress(ip);
            user.setCountry(country);

            if (request.getRole().toString().equals("ADMIN")) {
                user.setRole(Role.ADMIN);
            } else {
                user.setRole(Role.USER);
            }

            User result = userRepository.save(user);
            System.out.println(result);
            return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", user));
        }
    }


    public ResponseEntity<ApiResponse> signInUser(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid email or password"));
        }

        User user = existingUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid email or password"));
        }
        String message;
        if (user.getRole() == Role.ADMIN){
            message = "ADMIN logged in successfuly";
        }else {
            message = "USER logged in Successfuly";
        }

        return ResponseEntity.ok(new ApiResponse(true, "Sign in successful", message));
    }

}
