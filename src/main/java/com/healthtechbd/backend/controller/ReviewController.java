package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.ReviewDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Review;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.ReviewRepository;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RestController
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/review/{id}")
    public ResponseEntity<?> getAllReviews(@PathVariable(name = "id") Long id) {
        Optional<AppUser> opSubject = userRepository.findById(id);

        if (opSubject.isPresent()) {
            List<Review> reviewList = reviewRepository.findBySubject_Id(opSubject.get().getId());

            if (reviewList.isEmpty()) {
                return new ResponseEntity<>(ApiResponse.create("empty", "No review found"), HttpStatus.OK);
            }

            List<ReviewDTO> reviewDTOS = new ArrayList<>();

            for (var i : reviewList) {
                ReviewDTO reviewDTO = new ReviewDTO();

                reviewDTO.setReviewerId(i.getReviewer().getId());
                reviewDTO.setStarCount(i.getStarCount());
                reviewDTO.setReview(i.getReview());
                reviewDTO.setReviewerName(i.getReviewer().getFirstName() + " " + i.getReviewer().getLastName());

                reviewDTOS.add(reviewDTO);
            }

            return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "User not found"), HttpStatus.NOT_FOUND);
        }
    }


}
