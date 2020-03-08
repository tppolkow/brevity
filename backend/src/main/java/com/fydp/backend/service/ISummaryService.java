package com.fydp.backend.service;

import com.fydp.backend.model.Summary;
import com.fydp.backend.model.User;

import java.util.Optional;

public interface ISummaryService {

    Long createSummary(String title, User user);

    Long finishSummary(Long id, String data);

    Optional<Summary> findById(Long id);

}
