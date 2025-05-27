package com.ntabodoiqua.online_course_management.mapper;

import com.ntabodoiqua.online_course_management.dto.request.role.PermissionRequest;
import com.ntabodoiqua.online_course_management.dto.response.role.PermissionResponse;
import com.ntabodoiqua.online_course_management.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
