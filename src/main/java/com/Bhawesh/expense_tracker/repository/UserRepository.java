package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findByEmail(String email);
    User findById(long id);
    boolean existsByEmail(String email);

    String email(String email);
}
// this automatically writes a sql to find user by their email
