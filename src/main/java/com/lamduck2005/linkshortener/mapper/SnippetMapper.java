package com.lamduck2005.linkshortener.mapper;

import com.lamduck2005.linkshortener.dto.request.CreateSnippetRequest;
import com.lamduck2005.linkshortener.dto.response.CreateSnippetResponse;
import com.lamduck2005.linkshortener.entity.Snippet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SnippetMapper {

    // Tên field trong Request DTO là 'content'
    // Tên field trong Entity là 'contentData'
    @Mapping(source = "content", target = "contentData")
    @Mapping(source = "type", target = "contentType")
    @Mapping(source = "customCode", target = "shortCode")
    Snippet toEntity(CreateSnippetRequest request);


    @Mapping(source = "contentData", target = "originalContent")
    @Mapping(target = "shortUrl", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    CreateSnippetResponse toResponse(Snippet entity);

    //  Các field có tên giống nhau tự động ánh xạ.
}