package com.songscorer.songscorer.mapper;

import com.songscorer.songscorer.dto.ReviewRequest;
import com.songscorer.songscorer.dto.ReviewResponse;
import com.songscorer.songscorer.model.Review;
import com.songscorer.songscorer.model.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "userAccount", source = "userAccount")
    @Mapping(target = "spotifyAlbumId", source = "reviewRequest.spotifyAlbumId")
    @Mapping(target = "reviewDescription", source = "reviewRequest.reviewDescription")
    @Mapping(target = "rating", source = "reviewRequest.rating")
    public abstract Review map(ReviewRequest reviewRequest, UserAccount userAccount);

    @Mapping(target = "id", source = "reviewId")
    @Mapping(target = "spotifyAlbumId", source = "spotifyAlbumId")
    @Mapping(target = "reviewDescription", source = "reviewDescription")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "username", source = "userAccount.username")
    @Mapping(target = "createdDate", source = "createdDate")
    public abstract ReviewResponse mapToDto(Review review);
}
