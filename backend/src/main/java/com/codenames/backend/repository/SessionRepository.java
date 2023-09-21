package com.codenames.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codenames.backend.model.Session;

public interface SessionRepository extends JpaRepository<Session, Integer> {

}
