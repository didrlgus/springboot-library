package gabia.library.controller;

import com.fasterxml.jackson.annotation.JsonView;
import gabia.library.config.NoticeJsonView;
import gabia.library.dto.NoticeDto;
import gabia.library.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notices")
    @JsonView(NoticeJsonView.Default.class)
    public ResponseEntity<NoticeDto> addNotice(@RequestBody @Valid NoticeDto noticeDto){
        return ResponseEntity.ok(noticeService.addNotice(noticeDto));
    }

    @GetMapping("/notices")
    @JsonView(NoticeJsonView.Default.class)
    public ResponseEntity<List<NoticeDto>> findAll(){
        return ResponseEntity.ok(noticeService.findAll());
    }

    @GetMapping("/notices/{id}")
    @JsonView(NoticeJsonView.Default.class)
    public ResponseEntity<NoticeDto> findNotice(@PathVariable("id") Long id){
        return ResponseEntity.ok(noticeService.findNotice(id));
    }

    @PutMapping("/notices/{id}")
    @JsonView(NoticeJsonView.Modify.class)
    public ResponseEntity<NoticeDto> updateNotice(@RequestBody @Valid NoticeDto noticeDto){
        return ResponseEntity.ok(noticeService.updateNotice(noticeDto));
    }

    @DeleteMapping("/notices/{id}")
    @JsonView(NoticeJsonView.Modify.class)
    public ResponseEntity<NoticeDto> removeNotice(@PathVariable("id") Long id){
        return ResponseEntity.ok(noticeService.removeNotice(id));
    }
}
