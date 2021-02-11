package com.songscorer.songscorer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String spotifyAlbumId;
    private String reviewDescription;
    private Integer rating;
    private String username;
    private Instant createdDate;
}
