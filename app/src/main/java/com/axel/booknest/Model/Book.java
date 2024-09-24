package com.axel.booknest.Model;

public class Book {

    private String title;
    private String imageUrl;
    private String pdfUrl;

    public Book() {
        // Default constructor required for Firebase
    }

    public Book(String title, String imageUrl, String pdfUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.pdfUrl = pdfUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}
