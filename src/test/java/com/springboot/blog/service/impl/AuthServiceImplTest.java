package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {


    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void login() {
    }

    //Sebastián Millán
    @Test
    void whenUsernameExists_thenThrowException() {
        RegisterDto registerDto = new RegisterDto("Sebastián","sebas123","sebas@gmail.com","1234");
        String expectedMessage = "Username is already exists!.";
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(Boolean.TRUE);

        Exception exception = assertThrows(BlogAPIException.class,()->{
            authService.register(registerDto);
        });

        assertEquals(exception.getMessage(), expectedMessage);

        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    //Sebastián Millán
    @Test
    void whenEmailExists_thenThrowException() {
        RegisterDto registerDto = new RegisterDto("Sebastián","sebas123","sebas@gmail.com","1234");
        String expectedMessage = "Email is already exists!.";
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(Boolean.TRUE);

        Exception exception = assertThrows(BlogAPIException.class,()->{
            authService.register(registerDto);
        });

        assertEquals(exception.getMessage(), expectedMessage);

        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());
        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }
    //Sebastián Millán
    @Test
    void whenRegisterDtoIsValid_thenUserRegisteredSuccessfully() {
        RegisterDto registerDto = new RegisterDto("Sebastián","sebas123","sebas@gmail.com","1234");
        String roleName = "ROLE_USER";
        Role role = new Role((short) 1,roleName);

        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(Boolean.FALSE);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("{bcrypt}"+registerDto.getPassword());
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i->i.getArguments()[0]);

        String expectedResult = "User registered successfully!.";
        String result = authService.register(registerDto);
        assertNotNull(result);
        assertEquals(result, expectedResult);

        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());
        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
        verify(passwordEncoder, times(1)).encode(registerDto.getPassword());
        verify(roleRepository,times(1)).findByName(roleName);
        verify(userRepository, times(1)).save(Mockito.any());
        verifyNoMoreInteractions(userRepository);




    }
}