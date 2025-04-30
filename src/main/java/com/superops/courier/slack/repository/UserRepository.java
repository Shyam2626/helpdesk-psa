package com.superops.courier.slack.repository;

import com.superops.courier.slack.entity.Provider;
import com.superops.courier.slack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByEmail(String email);
    List<User> findByTechnicianTrueAndSource(Provider source);
}
