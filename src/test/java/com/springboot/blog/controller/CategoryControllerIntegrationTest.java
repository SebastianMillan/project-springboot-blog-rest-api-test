package com.springboot.blog.controller;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Sql(value = "classpath:data-integration.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CategoryControllerIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private HttpHeaders userHeaders;
    private HttpHeaders adminHeaders;
    private String userToken;
    private String adminToken;
    private Long idCategory;
    private Long finalIdCategory;
    private Long outIdCategory;
    private Long zeroIdCategory;

    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        /*
            Configuración del log de peticiones


        ClientHttpRequestFactory factory =
                //new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
                new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
        testRestTemplate.getRestTemplate().setRequestFactory(factory);

        List<ClientHttpRequestInterceptor> interceptors = testRestTemplate.getRestTemplate().getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new LoggingInterceptor());
        testRestTemplate.getRestTemplate().setInterceptors(interceptors);



            Fin de la configuración de log de peticiones
         */

        idCategory=1L;
        finalIdCategory=100L;
        outIdCategory=101L;
        zeroIdCategory=0L;
        User admin = new User(1L,"Micah Eakle","meakle0","meakle0@newsvine.com", passwordEncoder.encode("kJ3(1SY6uMM"), Set.of(new Role((short)1,"ADMIN")));
        User user = new User(51L, "Danny Girkins", "dgirkins1e", "dgirkins1e@bing.com", passwordEncoder.encode("lE2,%W?IAA"), Set.of(new Role((short)2,"USER")));
        userToken=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                user.getUsername(),user.getRoles(),Collections.of(new SimpleGrantedAuthority("ROLE_USER"))));
        adminToken=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                admin.getUsername(),admin.getPassword(), Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        userHeaders=new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.setBearerAuth(userToken);
        adminHeaders=new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(adminToken);
    }
    @Test
    void addCategory() {
    }

    // Cristian Pulido
    @Test
    void whenCategoryIsFound_thenReturnHttp200() {
        ResponseEntity<CategoryDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/categories/" + idCategory,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                CategoryDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateCategory() {
    }

    //Cristian Pulido
    @Test
    void whenCategoriesIsFound_thenReturnHttp200(){
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/categories",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    //Sebastián Millán
    @Test
    void whenCategoryIdIsOkAndAdminRole_thenReturnHttp200() {
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/categories/"+idCategory,
                HttpMethod.DELETE,new HttpEntity<>(adminHeaders), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category deleted successfully!.", response.getBody());
    }
    //Sebastián Millán
    @Test
    void whenCategoryIdIsOkAndUserRole_thenReturnHttp401() {
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/categories/"+idCategory,
                HttpMethod.DELETE,new HttpEntity<>(userHeaders), String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    //Sebastián Millán
    @Test
    void whenCategoryIdIsZeroAndAdminRole_thenReturnHttp404() {
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/categories/"+zeroIdCategory,
                HttpMethod.DELETE,new HttpEntity<>(adminHeaders), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    //Sebastián Millán
    @Test
    void whenCategoryIdIsNearFinalAndAdminRole_thenReturnHttp404() {
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/categories/"+finalIdCategory,
                HttpMethod.DELETE,new HttpEntity<>(adminHeaders), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    //Sebastián Millán
    @Test
    void whenCategoryIdIsOutFinalAndAdminRole_thenReturnHttp404() {
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/categories/"+outIdCategory,
                HttpMethod.DELETE,new HttpEntity<>(adminHeaders), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}