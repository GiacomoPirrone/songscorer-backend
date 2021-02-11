package com.songscorer.songscorer.controller;

import com.songscorer.songscorer.dto.ReviewDto;
import com.songscorer.songscorer.dto.ReviewRequest;
import com.songscorer.songscorer.dto.ReviewResponse;
import com.songscorer.songscorer.service.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/review")
@AllArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewRequest reviewRequest){
        reviewService.save(reviewRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return status(HttpStatus.OK).body(reviewService.getAllReviews());
    }

    @GetMapping("/by-user/{name}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUsername(String username) {
        return status(HttpStatus.OK).body(reviewService.getReviewsByUsername(username));
    }

}
