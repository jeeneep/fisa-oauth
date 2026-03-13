package com.fisa.auth.authentication.repository;

import com.fisa.auth.authentication.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, String> {
}
