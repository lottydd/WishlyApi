package com.lotty.wishlysystemapi.mapper;


import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.model.Role;
import com.lotty.wishlysystemapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")

public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "itemlist" , ignore = true)
    @Mapping(target = "wishlists" , ignore = true)
    @Mapping(target = "password", ignore = true) //временно
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    User toDto(User savedUser);

    default List<String> mapRolesToStrings(List<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}
