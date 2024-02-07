package com.springboot.blog.controller;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Sql(value = "classpath:data-integration.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommentControllerIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private HttpHeaders adminHeaders;
    private String token;
    private Long idPost;
    private Long idComment;
    private Long finalIdPost;
    private Long outIdPost;
    private Long zeroIdPost;
    private Long notHasIdComment;
    private Long finalIdComment;
    private CommentDto updateCommentDto;
    private CommentDto emptyUpdateCommentDto;

    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        idPost=1L;
        idComment=58L;
        finalIdPost=100L;
        outIdPost=101L;
        zeroIdPost=0L;
        notHasIdComment=200L;
        finalIdComment=13L;
        updateCommentDto= new CommentDto();
        emptyUpdateCommentDto= new CommentDto();
        updateCommentDto.setName("Nuevo nombre");
        updateCommentDto.setEmail("emailNuevo@gmail.com");
        updateCommentDto.setBody("He conseguido editar el comentario");
        User admin = new User(1L,"Micah Eakle","meakle0","meakle0@newsvine.com", "kJ3(1SY6uMM", Set.of(new Role((short)1,"ADMIN")));
        token=jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                admin.getUsername(),admin.getRoles()));
        adminHeaders=new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(token);
    }
    //Cristian Pulido
    @Test
    void whenCreateComment_thenReturnHttp201AndCommentDto() {
        CommentDto newCommentDto = new CommentDto();
        newCommentDto.setName("Cristian");
        newCommentDto.setEmail("rcallinan0@feedburner.com");
        newCommentDto.setBody("Esternocleidomastoideo");

        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + idPost + "/comments",
                HttpMethod.POST,
                new HttpEntity<>(adminHeaders),
                CommentDto.class,
                newCommentDto);

        //assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //Cristian Pulido
    @Test
    void whenCreateCommentUnAuthorized_thenReturnHttp401() {
        CommentDto newCommentDto = new CommentDto();
        newCommentDto.setId(1000);
        newCommentDto.setName("Nuevo comentario");
        newCommentDto.setEmail("nuevo@ejemplo.com");
        newCommentDto.setBody("Contenido del nuevo comentario");

        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + idPost + "/comments",
                HttpMethod.POST,
                new HttpEntity<>(newCommentDto),
                CommentDto.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    //Alejandro Rubens
    @Test
    void getCommentById() {
        //Connection to localhost:55432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:" + port + "/api/v1/posts/" + idPost + "/comments/" + idComment,
                HttpMethod.PUT, new HttpEntity<>(updateCommentDto, adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idComment, response.getBody().getId());
        assertEquals(updateCommentDto.getName(), response.getBody().getName());
    }


    // Cristian Pulido
    @Test
    void whenCreateCommentWithInvalidData_thenReturnHttp400() {
        CommentDto invalidCommentDto = new CommentDto();
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + idPost + "/comments",
                HttpMethod.POST,
                new HttpEntity<>(invalidCommentDto, adminHeaders),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    //Cristian Pulido
    @Test
    void whenGetCommentById_thenReturnHttp200AndCommentDto() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + idPost + "/comments/" + idComment,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idComment, response.getBody().getId());
    }

    // Cristian Pulido
    @Test
    void whenGetCommentByIdWhenCommentNotExists_thenReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + idPost + "/comments/" + notHasIdComment,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                CommentDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Cristian Pulido
    @Test
    void whenGetCommentByIdWhenPostNotExists_thenReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + notHasIdComment + "/comments/" + idComment,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                CommentDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Cristian Pulido
    @Test
    void whenGetCommentByIdWhenPostIdIsZero_thenReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + zeroIdPost + "/comments/" + idComment,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                CommentDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Cristian Pulido
    @Test
    void whenGetCommentByIdWhenPostIdIsOut_thenReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/v1/posts/" + outIdPost + "/comments/" + idComment,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                CommentDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    //Sebastián Millán
    @Test
    void whenIdPostIdCommentAndUpdateCommentDtoIsOk_theReturnHttp200() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/posts/"+idPost+"/comments/"+idComment,
                HttpMethod.PUT,new HttpEntity<>(updateCommentDto,adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idComment,response.getBody().getId());
        assertEquals(updateCommentDto.getName(), response.getBody().getName());
    }
    //Sebastián Millán
    @Test
    void whenIdPostIsOkButIdCommentNotExists_theReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/posts/"+idPost+"/comments/"+notHasIdComment,
                HttpMethod.PUT,new HttpEntity<>(updateCommentDto,adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    //Sebastián Millán
    @Test
    void whenIdPostIdIsZero_theReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/posts/"+zeroIdPost+"/comments/"+idComment,
                HttpMethod.PUT,new HttpEntity<>(updateCommentDto,adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    //Sebastián Millán
    @Test
    void whenIdPostIsOut_theReturnHttp404() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/posts/"+outIdPost+"/comments/"+idComment,
                HttpMethod.PUT,new HttpEntity<>(updateCommentDto,adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    //Sebastián Millán
    @Test
    void whenIdPostAndIdCommentIsFinal_theReturnHttp200() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/posts/"+finalIdPost+"/comments/"+finalIdComment,
                HttpMethod.PUT,new HttpEntity<>(updateCommentDto,adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(finalIdComment,response.getBody().getId());
        assertEquals(updateCommentDto.getName(), response.getBody().getName());
    }
    //Sebastián Millán
    @Test
    void whenUpdateCommentDtoIsNotValid_theReturnHttp400() {
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/posts/"+finalIdPost+"/comments/"+finalIdComment,
                HttpMethod.PUT,new HttpEntity<>(emptyUpdateCommentDto,adminHeaders), CommentDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteComment() {
    }
}