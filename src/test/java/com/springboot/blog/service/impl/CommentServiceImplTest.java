package com.springboot.blog.service.impl;


import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    ModelMapper modelMapper;


    //Cristian Pulido
    @Test
    void createComment() {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setName("CommentName");
        commentDto.setEmail("myemail@gmail.com");
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

    //Alejandro Rubens
    @Disabled
    @Test
    void updateComment() {
        Post post = new Post();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);
        comment.setName("nombre");
        comment.setEmail("email@email.com");
        comment.setBody("El cuerpo del comentario");
        post.setId(1L);
        post.setTitle("titulo");
        post.setDescription("descripcion de los post");
        post.setContent("contenido");
        post.setComments(Set.of(comment));

        postRepository.save(post);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail("emailCambiado@email.com");
        commentDto.setBody(comment.getBody());


        CommentDto expectedResult = new CommentDto();
        expectedResult.setId(comment.getId());
        expectedResult.setName(comment.getName());
        expectedResult.setEmail("emailCambiado@email.com");
        expectedResult.setBody(comment.getBody());

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(postRepository.save(Mockito.any(Post.class))).thenAnswer(i -> i.getArguments()[0]);
        when(modelMapper.map(eq(comment), eq(CommentDto.class))).thenReturn(expectedResult);

        CommentDto result = commentService.updateComment(comment.getId(), post.getId(), expectedResult);

        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertNotEquals(comment.getEmail(), result.getEmail());
    }
}