package com.gobr.pragrisk.repository;

import com.gobr.pragrisk.domain.Mitigation;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Mitigation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MitigationRepository extends JpaRepository<Mitigation, UUID> {}
