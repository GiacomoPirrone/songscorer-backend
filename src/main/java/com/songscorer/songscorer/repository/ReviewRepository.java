package com.songscorer.songscorer.repository;

import com.songscorer.songscorer.model.Review;
import com.songscorer.songscorer.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUser(UserAccount user);
}
