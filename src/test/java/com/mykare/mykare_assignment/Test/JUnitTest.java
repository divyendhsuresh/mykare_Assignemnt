package com.mykare.mykare_assignment.Test;

import com.mykare.mykare_assignment.DTO.SignupRequest;
import com.mykare.mykare_assignment.Entity.Role;
import com.mykare.mykare_assignment.Entity.User;
import com.mykare.mykare_assignment.Repository.UserRepository;
import com.mykare.mykare_assignment.Response.ApiResponse;
import com.mykare.mykare_assignment.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private SignupRequest signupRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@ex.com");
        signupRequest.setPassword("password123");
        signupRequest.setName("TestUser");
        signupRequest.setGender("Male");
        signupRequest.setRole(Role.ADMIN);

        mockUser = new User();
        mockUser.setEmail(signupRequest.getEmail());
        mockUser.setPassword("hashedPassword");
        mockUser.setName(signupRequest.getName());
        mockUser.setGender(signupRequest.getGender());
        mockUser.setRole(Role.ADMIN);

    }


    // Test: Should register user successfully
    @Test
    public void testSignupSuccess(){
        SignupRequest request = new SignupRequest("testuser","testtest@gmail.com","password","male",Role.ADMIN);
        User user = new User(12L,"testuser", "test@example.com", passwordEncoder.encode("password123"),"male","18.00.00.01","India",Role.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.registerUser(request));

    }

    //Test:if email is already registered
    @Test
    public void testSignupEmailAlreadyRegistered() {
        SignupRequest request = new SignupRequest("testuser", "testtest@gmail.com", "password", "male", Role.ADMIN);
        User existingUser = new User(1L, "testuser", "testtest@gmail.com", passwordEncoder.encode("password123"), "male", "18.00.00.01", "India", Role.ADMIN);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        ResponseEntity<ApiResponse> response = userService.registerUser(request);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("already registered"));
    }

    @Test
    public void testSignInUserInvalidEmail() {
        // Arrange: Email not found.
        String email = "nonexistent@example.com";
        String password = "anyPassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = userService.signInUser(email, password);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid email or password", response.getBody().getMessage());
    }

    @Test
    public void testSignInUserInvalidPassword() {
        String email = "test@example.com";
        String inputPassword = "wrongPassword";
        String storedPassword = "correctPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(storedPassword);
        user.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(inputPassword, storedPassword)).thenReturn(false);

        ResponseEntity<ApiResponse> response = userService.signInUser(email, inputPassword);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid email or password", response.getBody().getMessage());
    }

    @Test
    public void testSignInUserAdminSuccess() {
        String email = "admin@example.com";
        String inputPassword = "adminPass";

        User user = new User();
        user.setEmail(email);
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();
        String storedPassword = realEncoder.encode(inputPassword);
        user.setPassword(storedPassword);
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = userService.signInUser(email, inputPassword);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Sign in successful", response.getBody().getMessage());
        assertEquals("ADMIN logged in successfuly", response.getBody().getData());
    }

    @Test
    public void testSignInUserUserSuccess() {
        String email = "user@example.com";
        String inputPassword = "userPass";
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();

        String storedPassword = realEncoder.encode(inputPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword(storedPassword);
        user.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = userService.signInUser(email, inputPassword);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Sign in successful", response.getBody().getMessage());
        assertEquals("USER logged in Successfuly", response.getBody().getData());
    }

    @Test
    public void testGetAllUsersAdminSuccess() {
        String adminEmail = "admin@example.com";
        String adminPassword = "adminPass";
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();
        String encodedAdminPassword = realEncoder.encode(adminPassword);

        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(encodedAdminPassword);
        adminUser.setRole(Role.ADMIN);

        List<User> usersList = new ArrayList<>();

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword(realEncoder.encode("user1Pass"));
        user1.setRole(Role.USER);
        usersList.add(user1);

        usersList.add(adminUser);

        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll()).thenReturn(usersList);

        ResponseEntity<ApiResponse> response = userService.getAllUsers(adminEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Users are ", apiResponse.getMessage());
        assertEquals(usersList, apiResponse.getData());
    }

    @Test
    public void testDeleteByEmailAdminSuccess() {
        String adminEmail = "admin@example.com";
        String adminPassword = "adminPass";
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();
        String encodedAdminPassword = realEncoder.encode(adminPassword);

        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(encodedAdminPassword);
        adminUser.setRole(Role.ADMIN);

        String userEmail = "user@example.com";
        String userPassword = "userPass";
        String encodedUserPassword = realEncoder.encode(userPassword);

        User userToDelete = new User();
        userToDelete.setEmail(userEmail);
        userToDelete.setPassword(encodedUserPassword);
        userToDelete.setRole(Role.USER);

        // Stub repository methods
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userToDelete));

        ResponseEntity<ApiResponse> response = userService.deleteByEmail(adminEmail, userEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("User deleted successfully", apiResponse.getMessage());

        verify(userRepository).delete(userToDelete);
    }

}