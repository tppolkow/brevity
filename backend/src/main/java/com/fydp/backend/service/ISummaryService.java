package com.fydp.backend.service;

import com.fydp.backend.model.Summary;

import java.util.Optional;

public interface ISummaryService {

    Long createSummary(String title);

    Long finishSummary(Long id, String data);

    Optional<Summary> findById(Long id);

}
