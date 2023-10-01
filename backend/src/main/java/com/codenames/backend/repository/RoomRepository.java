package com.codenames.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codenames.backend.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
