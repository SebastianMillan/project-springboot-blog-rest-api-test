package com.springboot.blog.controller;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.security.JwtTokenProvider;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Sql(value = "classpath:data-integration.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private HttpHeaders adminHeaders;
    private String token;
    private LoginDto loginDto;
    private LoginDto notRegisterLoginDto;

    private RegisterDto registerDto;

    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        loginDto= new LoginDto("wbahls2r","sQ0|KHKUN$I|/*");
        notRegisterLoginDto = new LoginDto("username", "pass");
        registerDto = new RegisterDto("name", "username", "email@email.com", "securepassword");


        User user = new User(1L,"Micah Eakle","meakle0","meakle0@newsvine.com", "kJ3(1SY6uMM", Set.of(new Role((short)1,"ADMIN")));
        token=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                user.getUsername(),user.getRoles()));
        adminHeaders=new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(token);
    }

    //Sebastián Millán
    @Test
    void whenLoginDtoIsOk_thenReturnHttp200() {
        ResponseEntity<JWTAuthResponse> response = testRestTemplate.postForEntity("http://localhost:"+port+"/api/auth/login",
                new HttpEntity<>(loginDto,adminHeaders), JWTAuthResponse.class);
        System.out.println(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    //Sebastián Millán
    @Test
    void whenLoginDtoIsNotRegister_thenReturnHttp500() {
        ResponseEntity<JWTAuthResponse> response = testRestTemplate.postForEntity("http://localhost:"+port+"/api/auth/login",
                new HttpEntity<>(notRegisterLoginDto,adminHeaders), JWTAuthResponse.class);
        System.out.println(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    //Alejandro Rubens
    @Test
    void register() {
        ResponseEntity<JWTAuthResponse> response = testRestTemplate.postForEntity("http://localhost:"+port+"/api/auth/register",
                new HttpEntity<>(registerDto ,adminHeaders), JWTAuthResponse.class);
        System.out.println(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}