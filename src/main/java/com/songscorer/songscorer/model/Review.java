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
public class Review {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long reviewId;
    @NotNull
    private String spotifyAlbumUrl;
    @Nullable
    @Lob
    private String reviewDescription;
    @NotNull
    private Integer rating;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userAccountId", referencedColumnName = "userAccountId")
    private UserAccount user;
    private Instant createdDate;


}
