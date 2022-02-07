package com.gobr.pragrisk.repository;

import com.gobr.pragrisk.domain.Technology;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Technology entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {}
