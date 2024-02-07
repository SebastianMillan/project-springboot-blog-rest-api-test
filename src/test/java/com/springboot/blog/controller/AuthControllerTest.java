package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.service.AuthService;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.springboot.blog.payload.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @InjectMocks
    AuthController authController;

    private LoginDto loginDto;
    @BeforeEach
    void setUp(){
        loginDto = new LoginDto("username", "password");
    }

    //Cristian Pulido
    @Test
    void whenLoginIsValid_thenReturnHttp200() throws Exception {

        String token = "token";

        Mockito.when(authService.login(Mockito.any(LoginDto.class))).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"));
    }
    //Cristian Pulido
    @Test
    void whenLoginIsNull_thenReturnHttp400()  throws Exception{
        String token = "token";

        Mockito.when(authService.login(Mockito.any(LoginDto.class))).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
    //editar este método
    @Test
    void whenUserNotFound_thenReturnHttp500() throws Exception {
        Mockito.when(authService.login(Mockito.any(LoginDto.class))).thenThrow(InternalServerErrorException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    //Marco Pertegal
    @Test
    void whenValidCredentialsThenReturnHttp201() throws Exception{
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("marco@gmail.com");
        registerDto.setUsername("Marco");
        registerDto.setPassword("12345");

        mockMvc.perform(post("/api/auth/register")
                                .content(objectMapper.writeValueAsString(registerDto))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isCreated());
    }
    //Marco Pertegal
    @Test
    void whenExistEmailThenReturnHttp400() throws Exception{
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("marco@gmail.com");
        registerDto.setUsername("Marco");
        registerDto.setPassword("12345");

        Mockito.doThrow(new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!."))
                .when(authService).register(Mockito.any(RegisterDto.class));

        mockMvc.perform(post("/api/auth/register")
                                .content(objectMapper.writeValueAsString(registerDto))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isBadRequest());

    }
    //Marco Pertegal
    @Test
    void whenExistUsernameThenReturnHttp400() throws Exception{
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("marco@gmail.com");
        registerDto.setUsername("Marco");
        registerDto.setPassword("12345");

        Mockito.doThrow(new BlogAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!."))
                .when(authService).register(Mockito.any(RegisterDto.class));

        mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(registerDto))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

}