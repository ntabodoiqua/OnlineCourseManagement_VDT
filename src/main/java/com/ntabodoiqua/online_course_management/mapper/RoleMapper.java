package com.ntabodoiqua.online_course_management.mapper;

import com.ntabodoiqua.online_course_management.dto.request.role.RoleRequest;
import com.ntabodoiqua.online_course_management.dto.response.role.RoleResponse;
import com.ntabodoiqua.online_course_management.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
