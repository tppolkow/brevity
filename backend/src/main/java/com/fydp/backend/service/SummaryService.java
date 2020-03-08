package com.fydp.backend.service;

import com.fydp.backend.model.Summary;
import com.fydp.backend.model.User;
import com.fydp.backend.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SummaryService implements ISummaryService {

    @Autowired
    private SummaryRepository repository;

    public Long createSummary(String title, User user){
        Summary summary = new Summary(title, null, false, user);
        repository.save(summary);
        return summary.getSummary_id();
    }

    public Long finishSummary(Long id, String data){
        Summary summary = repository.findById(id).get();
        summary.setData(data);
        summary.setFinished(true);
        repository.save(summary);
        return summary.getSummary_id();
    }

    @Override
    public Optional<Summary> findById(Long id){
        return repository.findById(id);
    }

}
