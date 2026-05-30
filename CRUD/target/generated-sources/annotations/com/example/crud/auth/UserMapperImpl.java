package com.example.crud.auth;

import com.example.crud.user.Role;
import com.example.crud.user.User;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-30T17:56:49+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String email = null;
        Role role = null;
        Instant createdAt = null;

        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        role = user.getRole();
        createdAt = user.getCreatedAt();

        UserResponse userResponse = new UserResponse( id, username, email, role, createdAt );

        return userResponse;
    }
}
