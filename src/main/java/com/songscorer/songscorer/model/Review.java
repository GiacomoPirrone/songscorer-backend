package com.songscorer.songscorer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
/*
 * This is the general review entity used for creating each
 * individual album review, albums are tracked by their spotify
 * url. This allows the backend to offload most of the heavy
 * loading onto the spotify api to get information such as
 * the artist associated, release date, songs, etc...
 *
 * Essentially we are associating an albums id on spotify
 * with each user account assigning a description of the users
 * thoughts on the album and a rating.
 */
public class Review {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long reviewId;
    // Cannot be null, because then this would be associated with no album
    @NotNull
    private String spotifyAlbumId;
    // Users can choose to just rate the album rather than also leaving a description
    @Nullable
    @Lob
    private String reviewDescription;
    // User must leave a review in order for a review to qualify and be created
    @NotNull
    private Integer rating;
    // Users will be able to create an unlimited amount of album reviews, therefore many to one
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userAccountId", referencedColumnName = "userAccountId")
    private UserAccount userAccount;
    private Instant createdDate;
}
