package gabia.library.utils.page;

import lombok.Getter;

/**
 * @author Wade
 * This is a common page response data.
 */

@Getter
public class PageResponseData {

    /**
     * 'page' represents the current page. (requested from client)
     * 'scaleStartPage' is first page of the current scale.
     * 'scaleEndPage' is last page of the current scale.
     * 'totalPage' is a full page size.
     * 'prevPage' represents the previous page of the current scale.
     * 'nextPage' represents the next page of current scale.
     */
    private int page = 1;
    private int scaleStartPage = 1;
    private int scaleEndPage = 1;
    private int totalPage = 1;
    private Integer prevPage;
    private Integer nextPage;

    /**
     * @param pageNumber is currentPageNumber. In fact, It is -1 on the page requested from client.
     * @param totalPage is a full page size.
     * @param scaleSize is the size of the page that will be displayed on the screen.
     */
    public void setPagingInfo(int pageNumber, int totalPage, int scaleSize) {
        this.page = pageNumber + 1;

        int nowScale = (page - 1) / scaleSize + 1;

        int startPage = (nowScale - 1) * scaleSize + 1;
        int endPage = startPage + scaleSize - 1;

        endPage = Math.min(endPage, totalPage);

        this.scaleStartPage = startPage;
        this.scaleEndPage = endPage;
        this.prevPage = (startPage - 1) > 1 ? (startPage - 1) : null;
        this.nextPage = (endPage + 1) <= totalPage ? (endPage + 1) : null;
        this.totalPage = totalPage;
    }

}

