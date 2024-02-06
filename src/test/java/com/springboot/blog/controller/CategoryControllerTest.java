package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.service.CategoryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;
    @InjectMocks
    private CategoryController commentController;

    private CategoryDto categoryDto;

    @BeforeEach
    public void setUp(){
        categoryDto= new CategoryDto(1L,"Análisis","Análisis profundo de la politica nacional");
    }

    //Sebastián Millán
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenNewCategoryIsValidAndAdminRole_thenReturnHttp201() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    //Sebastián Millán
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenNewCategoryIsInvalidAndAdminRole_thenReturnHttp400() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(categoryService, never()).addCategory(categoryDto);

    }

    @Test
    @WithMockUser()
    void whenNewCategoryIsValidAndUserRole_thenReturnHttp401() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(categoryService, never()).addCategory(categoryDto);

    }


    //Sebastián Millán
    @Test
    void whenNotAuth_thenReturnHttp401() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(categoryService, never()).addCategory(categoryDto);

    }


    @Test
    void getCategory() {
    }

    @Test
    void getCategories() {
    }

    @Test
    void updateCategory() {
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenDeleteCategoryExistsAndAdminRole_thenReturnHttp200() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk());

        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenDeleteCategoryNotExistsAndAdminRole_thenReturnHttp400() throws Exception {
        Long nonExistentCategoryId = 99L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{id}","null"))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).deleteCategory(nonExistentCategoryId);
    }

    @Test
    @WithMockUser()
    void whenDeleteCategoryAndUserRole_thenReturnHttp401() throws Exception {
        Long categoryId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isUnauthorized());

        verify(categoryService, never()).deleteCategory(categoryId);
    }
}