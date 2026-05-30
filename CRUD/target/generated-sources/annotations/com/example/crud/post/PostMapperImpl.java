package com.example.crud.post;

import com.example.crud.user.User;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-30T17:56:50+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public PostResponse toResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        String authorUsername = null;
        Long id = null;
        String title = null;
        String content = null;
        boolean published = false;
        Instant createdAt = null;
        Instant updatedAt = null;

        authorUsername = postAuthorUsername( post );
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        published = post.isPublished();
        createdAt = post.getCreatedAt();
        updatedAt = post.getUpdatedAt();

        PostResponse postResponse = new PostResponse( id, title, content, published, authorUsername, createdAt, updatedAt );

        return postResponse;
    }

    @Override
    public Post toEntity(PostRequest request) {
        if ( request == null ) {
            return null;
        }

        Post post = new Post();

        post.setTitle( request.title() );
        post.setContent( request.content() );
        post.setPublished( request.published() );

        return post;
    }

    @Override
    public void updateEntity(PostRequest request, Post post) {
        if ( request == null ) {
            return;
        }

        post.setTitle( request.title() );
        post.setContent( request.content() );
        post.setPublished( request.published() );
    }

    private String postAuthorUsername(Post post) {
        User author = post.getAuthor();
        if ( author == null ) {
            return null;
        }
        return author.getUsername();
    }
}
