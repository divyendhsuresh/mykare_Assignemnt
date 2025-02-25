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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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

//        lenient().when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
//        lenient().when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashedPassword");
//        lenient().when(userRepository.save(any(User.class))).thenReturn(mockUser);
    }

    /** ✅ Test: Should register user successfully */
    @Test
    void shouldRegisterUserSuccessfully() {

//        doReturn("127.0.0.1").when(spyUserService).getPublicIP();
//        doReturn("India").when(spyUserService).getCountryFromIP("127.0.0.1");

        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        ResponseEntity<ApiResponse> response = userService.registerUser(signupRequest);

        assertNotNull(response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("User registered successfully", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(signupRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(signupRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));    }

    /** ❌ Test: Should fail if email is already registered */
    @Test
    void shouldNotRegisterDuplicateUser() {
        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.of(mockUser));

        ResponseEntity<ApiResponse> response = userService.registerUser(signupRequest);

        assertFalse(response.getBody().isSuccess());
        assertEquals("Email is already registered.", response.getBody().getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /** ✅ Test: Should assign ADMIN role correctly */
    @Test
    void shouldAssignAdminRole() {
        signupRequest.setRole(Role.ADMIN);

        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashedPassword");

        ResponseEntity<ApiResponse> response = userService.registerUser(signupRequest);

        assertEquals(Role.ADMIN, ((User) response.getBody().getData()).getRole());
    }

    /** ✅ Test: Should assign USER role correctly */
    @Test
    void shouldAssignUserRole() {
        signupRequest.setRole(Role.ADMIN);

        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashedPassword");

        ResponseEntity<ApiResponse> response = userService.registerUser(signupRequest);

        assertEquals(Role.USER, ((User) response.getBody().getData()).getRole());
    }
}