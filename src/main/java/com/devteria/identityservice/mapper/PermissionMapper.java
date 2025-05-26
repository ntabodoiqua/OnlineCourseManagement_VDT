package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.role.PermissionRequest;
import com.devteria.identityservice.dto.response.role.PermissionResponse;
import com.devteria.identityservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
