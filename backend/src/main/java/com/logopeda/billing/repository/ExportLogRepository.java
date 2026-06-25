package com.logopeda.billing.repository;

import com.logopeda.billing.model.ExportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportLogRepository extends JpaRepository<ExportLog, String> {
}
