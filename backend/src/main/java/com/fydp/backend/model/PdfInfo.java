package com.fydp.backend.model;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PdfInfo {

    private List<Bookmark> chapters;
    private String pdfText;
    private String fileName;
    private Long summaryId;

    public PdfInfo() {

    }

    public List<Bookmark> getChapters() {
        return chapters;
    }

    public void setChapters(List<Bookmark> chapters) {
        this.chapters = chapters;
    }

    public void setPdfText(String pdfText) {
        this.pdfText = pdfText;
    }

    public String getPdfText() {
        return pdfText;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }
}
