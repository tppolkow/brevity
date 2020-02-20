package com.fydp.backend.model;

public class Bookmark {
    private String title;
    private int startPage;
    private int endPage;
    private int depth;

    public Bookmark() {}

    public Bookmark(String title, int startPage, int endPage, int depth) {
        this.title = title;
        this.startPage = startPage;
        this.endPage = endPage;
        this.depth = depth;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getStartPage() { return startPage; }
    public void setStartPage(int startPage) { this.startPage = startPage; }
    public int getEndPage() { return endPage; }
    public void setEndPage(int endPage) { this.endPage = endPage; }
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
}
