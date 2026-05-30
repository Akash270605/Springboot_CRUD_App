// ============================================================
// FILE: UserMapper.java
// This is a MapStruct Mapper interface.
//
// Its single job is to convert a User (database entity) into
// a UserResponse (DTO that gets sent to the client).
//
// MapStruct generates the actual implementation code at
// compile-time, saving us from writing boilerplate like:
//     new UserResponse(user.getId(), user.getUsername(), ...)
// ============================================================
package com.example.crud.auth;

import com.example.crud.user.User;
import org.mapstruct.Mapper;

/**
 * ---------------------------------------------------------------
 * USER MAPPER (MapStruct)
 * ---------------------------------------------------------------
 * "componentModel = spring" makes this a Spring Bean so we
 * can inject it into AuthController.
 *
 * Since both User and UserResponse have fields named the same
 * (id, username, email, role, createdAt), MapStruct
 * automatically maps them without needing extra @Mapping annotations.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User (database entity) into a UserResponse DTO.
     *
     * @param user The user entity loaded from the database.
     * @return A UserResponse ready to be serialized to JSON.
     */
    UserResponse toResponse(User user);
}