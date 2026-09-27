package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}