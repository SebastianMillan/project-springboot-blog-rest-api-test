package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @InjectMocks
    private AuthController authController;
    private LoginDto loginDto;
    @BeforeEach
    void setUp(){
        loginDto = new LoginDto("username", "password");
    }
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


    @Test
    @Disabled
    void register() {
    }
}