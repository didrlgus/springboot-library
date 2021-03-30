package gabia.library.controller;

import gabia.library.dto.BookRequestDto;
import gabia.library.dto.NaverBook;
import gabia.library.service.BookRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class BookRequestController {

    private final BookRequestService bookRequestService;

    @PostMapping("/request-list")
    public ResponseEntity<BookRequestDto> addBookRequest(@RequestBody @Valid BookRequestDto bookRequestDto){
        return ResponseEntity.ok(bookRequestService.addBookRequest(bookRequestDto));
    }

    @GetMapping("/request-list/{title}")
    public ResponseEntity<NaverBook> getBookByNaverApi(@PathVariable("title") String title, @RequestParam("page") Long page) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return ResponseEntity.ok(bookRequestService.getBookByNaverApi(title, page));
    }

    @GetMapping("/request-list")
    public ResponseEntity<List<BookRequestDto>> findAll(){
        return ResponseEntity.ok(bookRequestService.findAll());
    }

    @PutMapping("/request-list/{id}/confirm")
    public ResponseEntity<BookRequestDto> confirmBookRequest(@PathVariable("id") Long id){
        return ResponseEntity.ok(bookRequestService.confirmBookRequest(id));
    }

    @DeleteMapping ("/request-list/{id}/cancel")
    public ResponseEntity<BookRequestDto> cancelBookRequest(@PathVariable("id") Long id){
        return ResponseEntity.ok(bookRequestService.cancelBookRequest(id));
    }
}
