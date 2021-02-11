package com.songscorer.songscorer.service;

import com.songscorer.songscorer.dto.ReviewRequest;
import com.songscorer.songscorer.dto.ReviewResponse;
import com.songscorer.songscorer.exceptions.ReviewNotFoundException;
import com.songscorer.songscorer.mapper.ReviewMapper;
import com.songscorer.songscorer.model.Review;
import com.songscorer.songscorer.model.UserAccount;
import com.songscorer.songscorer.repository.ReviewRepository;
import com.songscorer.songscorer.repository.UserAccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final ReviewMapper reviewMapper;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public void save(ReviewRequest reviewRequest){
        reviewRepository.save(reviewMapper.map(reviewRequest, authService.getCurrentUserAccount()));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review with id of '" + id.toString() + "' was not found!"));
        return reviewMapper.mapToDto(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUsername(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return reviewRepository.findAllByUserAccount(userAccount)
                .stream()
                .map(reviewMapper::mapToDto)
                .collect(toList());
    }
}
