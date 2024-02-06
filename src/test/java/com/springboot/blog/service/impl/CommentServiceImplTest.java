package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createComment() {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setBody("Nuevo Comentario");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setBody("Nuevo Comentario");

        Post post = Post.builder()
                .id(1L)
                .title("Nuevo")
                .description("Nuevo")
                .content("Nuevo")
                .comments(Set.of(comment))
                .category(null)
                .build();

        Mockito.when(postRepository.findById(commentDto.getId())).thenReturn(Optional.of(post));
        Mockito.when(modelMapper.map(commentDto, Comment.class)).thenReturn(comment);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto createdCommentDto = commentService.createComment(post.getId(), commentDto);

        Mockito.verify(postRepository).findById(commentDto.getId());
        Mockito.verify(commentRepository).save(any(Comment.class));

        //Arreglar
        assertEquals(comment.getId(), 1L);
        assertEquals(comment.getBody(), "Nuevo Comentario");

    }
    @Test
    void getCommentsByPostId() {
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