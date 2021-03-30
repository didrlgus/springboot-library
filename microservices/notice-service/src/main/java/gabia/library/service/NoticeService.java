package gabia.library.service;

import gabia.library.domain.Notice;
import gabia.library.domain.NoticeRespository;
import gabia.library.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRespository noticeRespository;
    private final ModelMapper modelMapper;

    public NoticeDto addNotice(NoticeDto noticeDto){
        Notice notice = noticeRespository.save(Notice.builder()
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .isImportant(noticeDto.isImportant())
                .build());

        return modelMapper.map(notice, NoticeDto.class);
    }

    public List<NoticeDto> findAll(){
        return noticeRespository.findAll()
                .stream()
                .map(notice -> modelMapper.map(notice, NoticeDto.class))
                .collect(Collectors.toList());
    }

    public NoticeDto findNotice(Long id){
        Notice notice = noticeRespository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 글입니다."));

        return modelMapper.map(notice, NoticeDto.class);
    }

    @Transactional
    public NoticeDto updateNotice(NoticeDto noticeDto){
        Notice notice = noticeRespository.findById(noticeDto.getId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 글입니다."));

        notice.updateNotice(noticeDto);

        return modelMapper.map(notice, NoticeDto.class);
    }

    public NoticeDto removeNotice(Long id){
        Notice notice = noticeRespository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 글입니다."));

        noticeRespository.deleteById(id);

        return modelMapper.map(notice, NoticeDto.class);
    }

}
