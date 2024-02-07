package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.PostService;
import com.springboot.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    private MultiValueMap<String, String> userHeaders;
    private MultiValueMap<String, String> adminHeaders;
    private String userToken;
    private String adminToken;
    private Long idPost;
    private Long notExistIdPost;
    private Long outIdPost;
    private Long outIdCategory;
    private Long finalIdPost;
    private PostDto updatePostDto;
    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        idPost=1L;
        notExistIdPost=0L;
        outIdPost=101L;
        outIdCategory=101L;
        finalIdPost=100L;
        updatePostDto= new PostDto();
        updatePostDto.setTitle("Titulo test");
        updatePostDto.setDescription("El post está redactado regular");
        updatePostDto.setContent("El contenido del post");
        updatePostDto.setCategoryId(1L);
        User admin = new User(1L,"Micah Eakle","meakle0","meakle0@newsvine.com", passwordEncoder.encode("kJ3(1SY6uMM"), Set.of(new Role((short)1,"ADMIN")));
        User user = new User(51L, "Danny Girkins", "dgirkins1e", "dgirkins1e@bing.com", passwordEncoder.encode("lE2,%W?IAA"), Set.of(new Role((short)2,"USER")));
        System.out.println(admin.getPassword());

        userToken=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                user.getUsername(),user.getRoles()));
        adminToken=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                admin.getUsername(),admin.getRoles()));
        System.out.println(userToken);
        userHeaders=new LinkedMultiValueMap<>();
        userHeaders.add("content-type","application/json");
        userHeaders.add("Authorization","Bearer "+userToken);
        adminHeaders= new LinkedMultiValueMap<>();
        adminHeaders.add("content-type","application/json");
        adminHeaders.add("Authorization","Bearer "+adminToken);
    }

    @Test
    void createPost() {
    }

    //Marco Pertegal
    //Post-getAllPos
    @Test
    void whenFindAllPostThenReturn200() {
        ResponseEntity<PostResponse> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts",
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                PostResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        assertEquals("Computer Systems Analyst I", response.getBody().getContent().get(0).getTitle());
        assertEquals(10, response.getBody().getContent().size());
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

    @Test
    void updatePost() {
        ResponseEntity<PostDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/posts/"+idPost,
                HttpMethod.PUT,new HttpEntity<>(adminHeaders), PostDto.class, updatePostDto);
        System.out.println(updatePostDto);
        System.out.println(idPost);
        System.out.println(adminHeaders);
        System.out.println(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deletePost() {
    }

    //Marco Pertegal
    //Post-getPostByCategory
    @Test
    void whenCategoryIdFoundAndFoundPostsThenReturn200() {
        Long categoryId = 1L;
        ResponseEntity<List<PostDto>> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/posts/category/{id}",
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                new ParameterizedTypeReference<List<PostDto>>() {},
                categoryId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(8, response.getBody().get(0).getId());
        assertEquals("Chief Design Engineer",response.getBody().get(0).getTitle());
    }
    //Marco Pertegal
    //Post-getPostByCategory
    @Test
    void whenCategoryIdNotFoundThenReturnException() {
        ResponseEntity<Object> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/posts/category/" + outIdCategory,
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}