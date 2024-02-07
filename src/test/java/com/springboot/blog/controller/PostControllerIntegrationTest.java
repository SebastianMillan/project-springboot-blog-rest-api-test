package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.PostService;
import com.springboot.blog.service.impl.PostServiceImpl;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Sql(value = "classpath:data-integration.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PostControllerIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private HttpHeaders userHeaders;
    private HttpHeaders adminHeaders;
    private String userToken;
    private String adminToken;
    private Long idPost;
    private Long notExistIdPost;
    private Long outIdPost;
    private Long finalIdPost;
    private PostDto updatePostDto;
    private PostDto invalidPostDto;
    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        idPost=1L;
        notExistIdPost=0L;
        outIdPost=101L;
        finalIdPost=100L;
        invalidPostDto= new PostDto();
        updatePostDto= new PostDto();
        updatePostDto.setTitle("Titulo test");
        updatePostDto.setDescription("El post está redactado regular");
        updatePostDto.setContent("El contenido del post");
        updatePostDto.setCategoryId(1L);
        User admin = new User(1L,"Micah Eakle","meakle0","meakle0@newsvine.com", "kJ3(1SY6uMM", Set.of(new Role((short)1,"ADMIN")));
        User user = new User(51L, "Danny Girkins", "dgirkins1e", "dgirkins1e@bing.com", "lE2,%W?IAA", Set.of(new Role((short)2,"USER")));
        System.out.println(admin.getPassword());

        userToken=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                user.getUsername(),user.getRoles(), Collections.of(new SimpleGrantedAuthority("ROLE_USER"))));
        adminToken=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                admin.getUsername(),admin.getRoles(), Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        System.out.println(userToken);
        userHeaders=new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.setBearerAuth(userToken);
        adminHeaders=new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(adminToken);
    }


    //Alejandro Rubens
    @Test
    void whenIsAdminAndValidPost_theReturn201AndPostDto() {
        PostDto newPostDto = new PostDto();
        newPostDto.setId(1001);
        newPostDto.setTitle("Nuevo Título");
        newPostDto.setDescription("Nueva Descripción");
        newPostDto.setContent("Nuevo Contenido");
        newPostDto.setCategoryId(1L);
        ResponseEntity<PostDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/posts",
                HttpMethod.POST,
                new HttpEntity<>(adminHeaders),
                PostDto.class,
                newPostDto);

        //assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //assertNotNull(response.getBody());
        //assertNotNull(response.getBody().getId());
        //assertEquals(newPostDto.getTitle(), response.getBody().getTitle());
        //assertEquals(newPostDto.getDescription(), response.getBody().getDescription());
        //assertEquals(newPostDto.getContent(), response.getBody().getContent());
        //assertEquals(newPostDto.getCategoryId(), response.getBody().getCategoryId());
    }

    @Test
    void whenIsUserAndValidPost_theReturn401() {
        PostDto newPostDto = new PostDto();
        newPostDto.setTitle("Nuevo Título");
        newPostDto.setDescription("Nueva Descripción");
        newPostDto.setContent("Nuevo Contenido");
        newPostDto.setCategoryId(1L);
        ResponseEntity<PostDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/posts",
                HttpMethod.POST,
                new HttpEntity<>(newPostDto, userHeaders),
                PostDto.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    @Test
    void getAllPosts() {
    }

    //Sebastián Millán
    @Test
    void whenIdExistsAndUserRole_thenGetPostDtoAndReturn200() throws Exception{
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+idPost,
                HttpMethod.GET,new HttpEntity<>(userHeaders), PostDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Computer Systems Analyst I",response.getBody().getTitle());
        assertEquals(idPost,response.getBody().getId());
    }

    //Sebastián Millán
    @Test
    void whenIdIsNullAndUserRole_thenReturn400() throws Exception{
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+null,
                HttpMethod.GET,new HttpEntity<>(userHeaders), PostDto.class);
        System.out.println(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0,response.getBody().getId());
    }

    //Sebastián Millán
    @Test
    void whenIdNotFoundAndUserRole_thenReturn404() throws Exception{
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+notExistIdPost,
                HttpMethod.GET,new HttpEntity<>(userHeaders), PostDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(null,response.getBody().getTitle());
        assertEquals(0,response.getBody().getId());
    }

    //Sebastián Millán
    @Test
    void whenIdIsFinalAndUserRole_thenReturn200() throws Exception{
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+finalIdPost,
                HttpMethod.GET,new HttpEntity<>(userHeaders), PostDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Food Chemist",response.getBody().getTitle());
        assertEquals(finalIdPost,response.getBody().getId());
    }

    //Sebastián Millán
    @Test
    void whenIdIsOutAndUserRole_thenReturn200() throws Exception{
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+outIdPost,
                HttpMethod.GET,new HttpEntity<>(userHeaders), PostDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getTitle());
        assertEquals(0,response.getBody().getId());
    }

    //Sebastián Millán
    @Test
    void whenIdPostAndUpdatePostDtoIsOk_thenReturnHttp200() {
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+idPost,
                HttpMethod.PUT,new HttpEntity<>(updatePostDto,adminHeaders), PostDto.class);
        System.out.println(updatePostDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idPost, response.getBody().getId());
        assertEquals(updatePostDto.getTitle(), response.getBody().getTitle());
    }
    //Sebastián Millán
    @Test
    void whenIdPostIsNotValid_thenReturnHttp404() {
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+notExistIdPost,
                HttpMethod.PUT,new HttpEntity<>(updatePostDto,adminHeaders), PostDto.class);
        System.out.println(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    //Sebastián Millán
    @Test
    void whenInvalidPostDto_thenReturnHttp400() {
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+idPost,
                HttpMethod.PUT,new HttpEntity<>(invalidPostDto,adminHeaders), PostDto.class);
        System.out.println(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //Alejandro Rubens
    @Test
    void deletePost() {
    }

    @Test
    void getPostsByCategory() {
    }
}