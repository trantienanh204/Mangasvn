package com.example.demo.baove.controller.DTO;


public class ImageDTO {
    private String imageUrl;
    private int pageNumber;

    public ImageDTO(String imageUrl, int pageNumber) {
        this.imageUrl = imageUrl;
        this.pageNumber = pageNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
