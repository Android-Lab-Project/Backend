package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Review;
import com.healthtechbd.backend.exception.ApiException;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.ReviewRepository;
import com.healthtechbd.backend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppUserRepository userRepository;

    @PostMapping("/review/create/{id1}/{id2}")
    public ResponseEntity<?> saveReview(@RequestParam(name="review") String reviewStr , @PathVariable(name="id1") Long id1, @PathVariable(name="id2") Long id2 )
    {
        Optional<AppUser> optionalReviewer = userRepository.findById(id1);
        Optional<AppUser> optionalSubject = userRepository.findById(id2);

        AppUser reviewer=null,subject=null;

        if(optionalReviewer.isPresent())
        {
            reviewer = optionalReviewer.get();
        }
        else
        {
           return new ResponseEntity<>(ApiResponse.create("error","Reviewer not found"), HttpStatus.BAD_REQUEST);
        }

        if(optionalSubject.isPresent())
        {
            subject = optionalSubject.get();
        }
        else
        {
            return new ResponseEntity<>(ApiResponse.create("error","Subject not found"), HttpStatus.BAD_REQUEST);
        }

        Review review = new Review();
        review.setReview(reviewStr);
        review.setReviewer(reviewer);
        review.setSubject(subject);

        reviewRepository.save(review);
        ApiResponse createResponse = ApiResponse.create("create","Review Saved");
        return new ResponseEntity<>(createResponse,HttpStatus.OK);

    }


}
