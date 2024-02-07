package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
    @InjectMocks
    PostServiceImpl postService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    PostRepository postRepository;

    //Marco Pertegal
    @Test
    void whenPostDtoThenCreateNewPost() {
        Long categoryId = 3L;
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Mi mejor verano");
        postDto.setDescription("El año pasado fui a Marruecos a hacer turismo");
        postDto.setContent("Mucho texto y muchas imagenes");
        postDto.setComments(new HashSet<>());
        postDto.setCategoryId(3L);
        Category category = new Category(categoryId, "Vacaciones","Mis vacaciones", List.of());
        Post post = new Post(1L, "Sample Title", "Sample Description", "Sample Content", new HashSet<>(), category);

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(postService.mapToDTO(post)).thenReturn(postDto);
        Mockito.when(postService.mapToEntity(postDto)).thenReturn(post);
        Mockito.when(postRepository.save(post)).thenReturn(post);

        Optional <Category> result = categoryRepository.findById(categoryId);
        assertNotNull(result);
        assertEquals("Vacaciones", result.get().getName());

        PostDto createdPost = postService.createPost(postDto);
        assertNotNull(createdPost);
        assertEquals("Mi mejor verano", createdPost.getTitle());
        assertEquals("Mucho texto y muchas imagenes", createdPost.getContent());

        Mockito.verify(postRepository).save(post);
    }

    //Marco Pertegal
    @Test
    void whenCategoryIdNotFoundThenThrowException(){
        Long categoryId = 3L;
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Mi mejor verano");
        postDto.setDescription("El año pasado fui a Marruecos a hacer turismo");
        postDto.setContent("Mucho texto y muchas imagenes");
        postDto.setComments(new HashSet<>());
        postDto.setCategoryId(categoryId);

        Exception exception = assertThrows(ResourceNotFoundException.class,()->{
            postService.createPost(postDto);
        });

        assertEquals("Category not found with id : '"+ categoryId+"'", exception.getMessage());
    }

    //Cristian Pulido
    @Test
    void getAllPosts() {
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "title";
        String sortDir = "ASC";

        Post post1 = Post.builder()
                .id(1L)
                .title("Post 1")
                .description("Description 1")
                .content("Content 1").comments(null)
                .category(null)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .title("Post 2")
                .description("Description 2")
                .content("Content 2")
                .comments(null)
                .category(null)
                .build();

        List<Post> mockedPosts = List.of(post1, post2);

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc(sortBy)));
        Page<Post> mockedPage = new PageImpl<>(mockedPosts, pageable, mockedPosts.size());

        Mockito.when(postRepository.findAll(any(Pageable.class))).thenReturn(mockedPage);

        PostResponse postResponse = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        assertEquals(pageNo, postResponse.getPageNo());
        assertEquals(pageSize, postResponse.getPageSize());
        assertEquals(mockedPosts.size(), postResponse.getTotalElements());
        assertEquals(1, postResponse.getTotalPages());

        List<PostDto> content = postResponse.getContent();
        assertEquals(mockedPosts.size(), content.size());
    }

    @Test
    void getPostById() {
    }

    //Cristian Pulido
    @Test
    void updatePost() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Updated Post");
        postDto.setDescription("Updated Description");
        postDto.setContent("Updated Content");
        postDto.setCategoryId(1L);

        Post existingPost = Post.builder()
                .id(1L)
                .title("Original Post")
                .description("Original Description")
                .content("Original Content")
                .comments(new HashSet<>())
                .category(new Category())
                .build();

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);

        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(updatedCategory));
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(modelMapper.map(Mockito.any(Post.class), eq(PostDto.class))).thenReturn(postDto);

        PostDto updatedPostDto = postService.updatePost(postDto, 1L);

        Mockito.verify(postRepository).findById(1L);
        Mockito.verify(categoryRepository).findById(1L);
        Mockito.verify(postRepository).save(Mockito.any());

        assertEquals(postDto.getId(), updatedPostDto.getId());
        assertEquals(postDto.getTitle(), updatedPostDto.getTitle());
        assertEquals(postDto.getDescription(), updatedPostDto.getDescription());
        assertEquals(postDto.getContent(), updatedPostDto.getContent());
        assertEquals(postDto.getCategoryId(), updatedPostDto.getCategoryId());
    }

    @Test
    void deletePostById() {
    }

    @Test
    void getPostsByCategory() {
    }
}