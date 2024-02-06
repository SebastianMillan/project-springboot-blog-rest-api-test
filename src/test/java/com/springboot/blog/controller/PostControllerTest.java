package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.service.PostService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PostService categoryService;
    @InjectMocks
    private PostController commentController;

    private Long idPost;
    private Long idCategory;


    @BeforeEach
    void setUp(){
        idPost=1L;
        idCategory=1L;
    }

    @Test
    void createPost() {
    }

    @Test
    void getAllPosts() {
    }

    @Test
    void getPostById() {
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenUpdatePostWithValidData_thenReturnHttp200() throws Exception {
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(1L);
        updatedPostDto.setTitle("Title");
        updatedPostDto.setDescription("Description");
        updatedPostDto.setContent("Content");

        long postId = 1L;
        when(categoryService.updatePost(updatedPostDto, postId)).thenReturn(updatedPostDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", postId)
                        .content(objectMapper.writeValueAsString(updatedPostDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedPostDto.getTitle()));
        verify(categoryService, times(1)).updatePost(updatedPostDto, postId);
    }
    @Test
    @WithMockUser(authorities = {"USER"})
    void whenUpdatePostWithValidData_thenReturnHttp401() throws Exception {
        // Arrange
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(1L);
        updatedPostDto.setTitle("Title");
        updatedPostDto.setDescription("Description");
        updatedPostDto.setContent("Content");

        long postId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", postId)
                        .content(objectMapper.writeValueAsString(updatedPostDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(categoryService, never()).updatePost(updatedPostDto, postId);
    }



    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenUpdatePostWithInvalidData_thenReturnHttp400() throws Exception {
        PostDto updatedPostDto = new PostDto();
        long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}", postId)
                        .content(objectMapper.writeValueAsString(updatedPostDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).updatePost(updatedPostDto, postId);
    }



    //Sebastián Millán
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenPostIdExistAndAdminRole_thenReturnHttp200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}",idPost))
                .andExpect(content().string("Post entity deleted successfully."))
                .andExpect(status().isOk());
        verify(categoryService, times(1)).deletePostById(idPost);
    }

    //Sebastián Millán
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenPostIdIsWrongAndAdminRole_thenReturnHttp400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}","null"))
                .andExpect(status().isBadRequest());
        verify(categoryService, never()).deletePostById(idPost);
    }

    //Sebastián Millán
    @Test
    @WithMockUser(authorities = {"USER"})
    void whenPostIdExistsAndUserRole_thenReturnHttp401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}",idPost))
                .andExpect(status().isUnauthorized());
        verify(categoryService, never()).deletePostById(idPost);

    }

    //Sebastián Millán
    @Test
    void whenPostIdExistAndNotAuth_thenReturnHttp401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}",idPost)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(categoryService, never()).deletePostById(idPost);

    }

    //Sebastián Millán
    @Test
    void whenCategoryIdExists_thenReturnHttp200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/category/{id}",idCategory)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(categoryService, times(1)).getPostsByCategory(idCategory);
    }
    //Sebastián Millán
    @Test
    void whenCategoryIdIsWrong_thenReturnHttp400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/category/{id}","null")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(categoryService, never()).getPostsByCategory(idCategory);
    }

}