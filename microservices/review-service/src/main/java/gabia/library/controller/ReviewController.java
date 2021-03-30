package gabia.library.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import gabia.library.dto.ReviewRequestDto;
import gabia.library.dto.ReviewResponseDto;
import gabia.library.service.ReviewService;
import gabia.library.utils.jwt.JwtUtils;
import gabia.library.utils.page.PagingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final JwtUtils jwtUtils;
    private final ReviewService reviewService;

    @PostMapping("/books/{id}/reviews")
    public ResponseEntity<ReviewResponseDto.Add> addReview(@PathVariable("id") Long bookId, @RequestBody @Valid ReviewRequestDto.Post reviewRequestDto, HttpServletRequest request)
            throws AccessDeniedException {

        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(reviewService.addReview(bookId, reviewRequestDto, jwtUtils.getIdentifierFromJwt(jwt)));
    }

    @GetMapping("/books/reviews")
    public ResponseEntity<PagingResponseDto> getReviewsOfUser(@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(reviewService.getReviewsOfUser(jwtUtils.getIdentifierFromJwt(jwt), page));
    }

    @GetMapping("/books/reviews/{id}")
    public ResponseEntity<ReviewResponseDto.Details> getReviewDetails(@PathVariable("id") Long id) {

        return ResponseEntity.ok(reviewService.getReviewDetails(id));
    }

    @GetMapping("/books/{id}/reviews")
    public ResponseEntity<PagingResponseDto> getReviewsOfBook(@PathVariable("id") Long bookId, @RequestParam(value = "page", required = false) Integer page) {

        return ResponseEntity.ok(reviewService.getReviewsOfBook(bookId, page));
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDto.Normal> updateReview(@PathVariable("id") Long id, @RequestBody @Valid ReviewRequestDto.Put reviewRequestDto,
                                                               HttpServletRequest request) throws AccessDeniedException, JsonProcessingException {

        return ResponseEntity.ok(reviewService.updateReview(id, reviewRequestDto, jwtUtils.getJwtFromRequest(request)));
    }

    @DeleteMapping("/books/{bookId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto.Delete> deleteReview(@PathVariable("bookId") Long bookId, @PathVariable("reviewId") Long reviewId,
                                          HttpServletRequest request) throws AccessDeniedException, JsonProcessingException {

        return ResponseEntity.ok(reviewService.deleteReview(bookId, reviewId, jwtUtils.getJwtFromRequest(request)));
    }

}
