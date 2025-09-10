package com.lotty.wishlysystemapi.mapper;

import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserPrivateInfoResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
import com.lotty.wishlysystemapi.model.Role;
import com.lotty.wishlysystemapi.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "ownedItems", ignore = true)
    @Mapping(target = "wishlists", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateDTO dto, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserCreateResponseDTO toUserCreateResponseDTO(User user);

    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserPrivateInfoResponseDTO toUserPrivateInfoResponseDTO(User user);

    UserUpdateResponseDTO toUserUpdateResponseDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "ownedItems", ignore = true)
    @Mapping(target = "wishlists", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);

    default List<String> mapRoles(List<Role> roles) {
        if (roles == null) return List.of();
        return roles.stream().map(Role::getRoleName).collect(Collectors.toList());
    }

    @AfterMapping
    default void encodePassword(UserCreateDTO dto, @MappingTarget User user, @Context PasswordEncoder passwordEncoder) {
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }
}