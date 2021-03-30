package gabia.library.utils.page;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PagingResponseDto {

    private List<?> responseDtoList;
    private PageResponseData pageResponseData;

    @Builder
    public PagingResponseDto(List<?> responseDtoList, PageResponseData pageResponseData) {
        this.responseDtoList = responseDtoList;
        this.pageResponseData = pageResponseData;
    }

}
