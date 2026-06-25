package com.logopeda.therapy.repository;

import com.logopeda.therapy.model.SessionGoalProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionGoalProgressRepository extends JpaRepository<SessionGoalProgress, String> {

    Optional<SessionGoalProgress> findByIdAndClinicId(String id, String clinicId);

    List<SessionGoalProgress> findBySessionIdAndClinicIdOrderByCreatedAtAsc(String sessionId, String clinicId);

    List<SessionGoalProgress> findByGoalIdAndClinicIdOrderByCreatedAtAsc(String goalId, String clinicId);
}
