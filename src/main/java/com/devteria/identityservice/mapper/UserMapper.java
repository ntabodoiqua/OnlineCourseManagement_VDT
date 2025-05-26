package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.user.UserCreationRequest;
import com.devteria.identityservice.dto.request.user.UserUpdateRequest;
import com.devteria.identityservice.dto.response.user.UserResponse;
import com.devteria.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
