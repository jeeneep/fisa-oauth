package com.fisa.auth.dashboard.repository;

import com.fisa.auth.dashboard.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
}
