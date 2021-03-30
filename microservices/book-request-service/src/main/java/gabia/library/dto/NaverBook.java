package gabia.library.dto;

import lombok.Data;

@Data
public class NaverBook {

    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private Item[] items;

    @Data
    static class Item {
        private String title;
        private String link;
        private String image;
        private String author;
        private String price;
        private String discount;
        private String publisher;
        private String pubdate;
        private String isbn;
        private String description;
    }
}
