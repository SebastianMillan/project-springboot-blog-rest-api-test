package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;
    @InjectMocks
    private CategoryController categoryController;
    @Mock
    private CategoryRepository repository;

    private CategoryDto categoryDto;
    private CategoryDto categoryDto2;

    @BeforeEach
    public void setUp(){
        categoryDto= new CategoryDto(1L,"Análisis","Análisis profundo de la politica nacional");
        categoryDto2 = new CategoryDto(2L,"categoria2","descripcion");
        when(categoryService.getCategory(categoryDto2.getId())).thenReturn(categoryDto2);
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

    //Alejandro Rubens
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
    void getCategory_expectedResponse200() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}",categoryDto2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(2)))
                .andExpect(jsonPath("$.name",is(categoryDto2.getName())))
                .andExpect(jsonPath("$.description",is(categoryDto2.getDescription())));
    }

    //Alejandro Rubens
    @Test
    void getCategory_expectedResponse200EmptyString() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", 23L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    //Alejandro Rubens
    @Test
    void getCategory_expectedResponse400() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", "s")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //Marco Pertegal
    @Test
    void whenCategoriesFoundThenReturnHttp200() throws Exception{
        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1l);
        categoryDto1.setName("Category");
        categoryDto1.setDescription("Description");
        List<CategoryDto> categoryDtoList = List.of(categoryDto, categoryDto1);

        Mockito.when(categoryService.getAllCategories()).thenReturn(categoryDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //Alejandro Rubens
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateCategory_expectedResponse200() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", 1L)
                        .content(objectMapper.writeValueAsString(categoryDto2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    //Alejandro Rubens
    @Test
    void updateCategory_expectedResponse400() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", "s")
                        .content(objectMapper.writeValueAsString(categoryDto2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //Cristian Pulido
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenDeleteCategoryExistsAndAdminRole_thenReturnHttp200() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk());

        verify(categoryService).deleteCategory(categoryId);
    }

    //Cristian Pulido
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void whenDeleteCategoryNotExistsAndAdminRole_thenReturnHttp400() throws Exception {
        Long nonExistentCategoryId = 99L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{id}","null"))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).deleteCategory(nonExistentCategoryId);
    }

    //Cristian Pulido
    @Test
    @WithMockUser()
    void whenDeleteCategoryAndUserRole_thenReturnHttp401() throws Exception {
        Long categoryId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isUnauthorized());

        verify(categoryService, never()).deleteCategory(categoryId);
    }
}