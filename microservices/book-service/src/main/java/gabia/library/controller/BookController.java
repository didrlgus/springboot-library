package gabia.library.controller;

import gabia.library.dto.*;
import gabia.library.service.BookService;
import gabia.library.service.RentService;
import gabia.library.utils.jwt.JwtUtils;
import gabia.library.utils.page.PagingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;
    private final RentService rentService;
    private final JwtUtils jwtUtils;

    @PostMapping("/books")
    public ResponseEntity<BookResponseDto> addBook(@RequestBody @Valid BookRequestDto.Post bookRequestDto) {

        return ResponseEntity.ok(bookService.addBook(bookRequestDto));
    }

    @GetMapping("/books")
    public ResponseEntity<PagingResponseDto> getBooks(@RequestParam(value = "page", required = false) Integer page) {

        return ResponseEntity.ok(bookService.getBooks(page));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponseDto> getBookDetails(@PathVariable("id") Long id) {

        return ResponseEntity.ok(bookService.getBookDetails(id));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable("id") Long id, @RequestBody @Valid BookRequestDto.Put bookRequestDto) {

        return ResponseEntity.ok(bookService.updateBook(id, bookRequestDto));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<BookResponseDto> deleteBook(@PathVariable("id") Long id) {

        return ResponseEntity.ok(bookService.deleteBook(id));
    }

    @PutMapping("/books/{id}/rent")
    public ResponseEntity<RentResponseDto> rentBook(@PathVariable("id") Long id, HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(rentService.rentBook(id, jwtUtils.getIdentifierFromJwt(jwt)));
    }

    @PutMapping("/books/{bookId}/rent/{rentId}/extension")
    public ResponseEntity<BookResponseDto> extendRent(@PathVariable("bookId") Long bookId, @PathVariable("rentId") Long rentId, HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(rentService.extendRent(bookId, rentId, jwtUtils.getIdentifierFromJwt(jwt)));
    }

    @PutMapping("/books/{bookId}/rent/{rentId}/return")
    public ResponseEntity<BookResponseDto> returnBook(@PathVariable("bookId") Long bookId, @PathVariable("rentId") Long rentId, HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(rentService.returnBook(bookId, rentId, jwtUtils.getIdentifierFromJwt(jwt)));
    }

    @GetMapping("/rent")
    public ResponseEntity<PagingResponseDto> getRentListOfUser(@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(rentService.getRentListOfUser(jwtUtils.getIdentifierFromJwt(jwt), page));
    }

    @GetMapping("/rent-all")
    public ResponseEntity<List<RentResponseDto>> getAllRentListOfUser(HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(rentService.getAllRentListOfUser(jwtUtils.getIdentifierFromJwt(jwt)));
    }

    @PutMapping("/books/{id}/reviews")
    public ResponseEntity<ReviewResponseDto> deleteReview(@PathVariable("id") Long id, @RequestBody ReviewRequestDto reviewRequestDto,
                                                          HttpServletRequest request) throws AccessDeniedException {
        String jwt = jwtUtils.getJwtFromRequest(request);

        return ResponseEntity.ok(bookService.deleteReview(id, reviewRequestDto, jwtUtils.getIdentifierFromJwt(jwt)));
    }

    @GetMapping("/books/latest")
    public ResponseEntity<List<BookResponseDto>> getLatestBooks() {

        return ResponseEntity.ok(bookService.getLatestBooks());
    }

    @GetMapping("/books/many-reviews")
    public ResponseEntity<List<BookResponseDto>> getManyReviewsBooks() {

        return ResponseEntity.ok(bookService.getManyReviewsBooks());
    }

    @GetMapping("/books/search")
    public ResponseEntity<PagingResponseDto> getSearchedBooks(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "page", required = false) Integer page) {

        return ResponseEntity.ok(bookService.getSearchedBooks(keyword, page));
    }

}
