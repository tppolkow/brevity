package com.fydp.backend.repository;

import com.fydp.backend.model.Summary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends CrudRepository<Summary, Long> {
}
