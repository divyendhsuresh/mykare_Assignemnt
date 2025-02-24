package com.mykare.mykare_assignment.Controller;
import com.mykare.mykare_assignment.DTO.SignInRequest;
import com.mykare.mykare_assignment.DTO.SignupRequest;
import com.mykare.mykare_assignment.Entity.User;
import com.mykare.mykare_assignment.Repository.UserRepository;
import com.mykare.mykare_assignment.Response.ApiResponse;
import com.mykare.mykare_assignment.Service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class Controller {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody SignupRequest request) {
        System.out.println("first hit");
        ResponseEntity<ApiResponse> newUser = userService.registerUser(request);
        return  newUser;
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse>signInUser(@RequestBody SignInRequest request){
        return userService.signInUser(request.getEmail(), request.getPassword());
    }

    @GetMapping("/allusers")
    public ResponseEntity<ApiResponse>getAllUsers(@RequestParam String email){
        return userService.getAllUsers(email);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse>deleteByEmail(@RequestParam String adminEmail,@RequestParam String userEmail){
        return userService.deleteByEmail(adminEmail,userEmail);
    }

}
