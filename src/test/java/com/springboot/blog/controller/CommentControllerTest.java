package com.springboot.blog.controller;

import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;

    private Long postId;
    private CommentDto commentDto;

    @BeforeEach
    void setUp(){
        postId=1L;
        commentDto= new CommentDto();
        commentDto.setId(1L);
        commentDto.setBody("Me ha encantado la reseña");
        commentDto.setEmail("sebas@gmail.com");
        commentDto.setName("Sebastián");

    }

    //Sebastián Millán
    @Test
    @WithMockUser
    void whenPostIdExistsAndNewCommentIsValidAndAuth_thenReturnHttp201() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/{postId}/comments",postId).
                        content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());
        verify(commentService, times(1)).createComment(postId, commentDto);

    }
    //Sebastián Millán
    @Test
    @WithMockUser
    void whenPostIdExistsAndNewCommentIsInvalidAndAuth_thenReturnHttp400() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/{postId}/comments",postId).
                        content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(commentService, never()).createComment(postId, commentDto);
    }

    //Sebastián Millán
    @Test
    @WithMockUser
    void whenPostIdIsWrongAndNewCommentIsValidAndAuth_thenReturnHttp400() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/{postId}/comments","a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(commentService, never()).createComment(postId, commentDto);
    }
    //Sebastián Millán
    @Test
    void whenPostIdExistsAndWithoutAuth_thenReturnHttp401() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/{postId}/comments",postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(commentService, never()).createComment(postId, commentDto);
    }
    //Sebastián Millán
    @Test
    void whenPostIdExistsAndAuth_thenReturnHttp200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/{postId}/comments",postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(commentService, times(1)).getCommentsByPostId(postId);
    }
    //Sebastián Millán
    @Test
    void whenPostIdIsWrongAndAuth_thenReturnHttp401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/{postId}/comments","a")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(commentService, never()).getCommentsByPostId(postId);
    }

    @Test
    void getCommentById() {
    }

    @Test
    void updateComment() {
    }

    @Test
    void deleteComment() {
    }
}