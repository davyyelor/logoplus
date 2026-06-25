package com.logopeda.user.repository;

import com.logopeda.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByClinicIdOrderByCreatedAtDesc(String clinicId);

    Optional<User> findByIdAndClinicId(String id, String clinicId);
}
