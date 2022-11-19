package com.siwes.allocation.Repository;

import com.siwes.allocation.Model.SiwesCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiwesCenterRepo extends JpaRepository<SiwesCenter, String> {
    @Query(value = "SELECT u FROM SiwesCenter u WHERE u.name=:name AND u.session.id=:session")
    Optional<SiwesCenter> findByName(String name, String session);

    @Query(value = "SELECT u FROM SiwesCenter u WHERE u.session.id =:id")
    Page<SiwesCenter> findBySession(String id, Pageable pageable);
}
