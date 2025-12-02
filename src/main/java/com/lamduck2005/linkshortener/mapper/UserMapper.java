package com.lamduck2005.linkshortener.mapper;

import com.lamduck2005.linkshortener.dto.response.AdminUserResponse;
import com.lamduck2005.linkshortener.dto.response.UserProfileResponse;
import com.lamduck2005.linkshortener.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Map entity User sang UserProfileResponse.
     * Trường roles sẽ được set riêng (vì là List<String>).
     */
    @Mapping(target = "roles", ignore = true)
    UserProfileResponse toUserProfile(User user);

    /**
     * Map entity User sang AdminUserResponse.
     * Trường roles sẽ được set riêng.
     */
    @Mapping(target = "roles", ignore = true)
    AdminUserResponse toAdminUser(User user);
}
