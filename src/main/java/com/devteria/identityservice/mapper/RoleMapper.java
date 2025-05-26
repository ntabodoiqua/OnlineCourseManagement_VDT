package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.role.RoleRequest;
import com.devteria.identityservice.dto.response.role.RoleResponse;
import com.devteria.identityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
