package com.logopeda.center.repository;

import com.logopeda.center.model.Center;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenterRepository extends JpaRepository<Center, String> {

    Optional<Center> findByIdAndClinicId(String id, String clinicId);

    List<Center> findByClinicIdOrderByNameAsc(String clinicId);

    boolean existsByIdAndClinicId(String id, String clinicId);
}
