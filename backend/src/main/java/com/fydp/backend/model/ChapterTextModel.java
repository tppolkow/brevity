package com.fydp.backend.model;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChapterTextModel {
    private Map<String, String> chpTxt;
    private Map<String, Long> chpId;

    public Map<String, String> getChpTextMap() {
        return chpTxt;
    }

    public void setChpTextMap(Map<String, String> chpTextMap) {
        this.chpTxt = chpTextMap;
    }

    public Map<String, Long> getChpId() {
        return chpId;
    }

    public void setChpId(Map<String, Long> chpId) {
        this.chpId = chpId;
    }
}
