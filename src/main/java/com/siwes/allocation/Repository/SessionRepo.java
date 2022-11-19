package com.siwes.allocation.Repository;

import com.siwes.allocation.Model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SessionRepo extends JpaRepository<Session, String> {
    @Query(value = "SELECT u FROM Session u WHERE u.year=:year")
    Optional<Session> findByYear(int year);
}
