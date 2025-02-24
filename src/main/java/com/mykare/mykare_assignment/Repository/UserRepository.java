package com.mykare.mykare_assignment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mykare.mykare_assignment.Entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}