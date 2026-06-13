package com.chella.userservice.mapper;

import com.chella.userservice.dto.request.RegisterRequest;
import com.chella.userservice.dto.response.UserResponse;
import com.chella.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest registerRequest);

    UserResponse toResponse(User user);
}
