package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void createPost() {
    }

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