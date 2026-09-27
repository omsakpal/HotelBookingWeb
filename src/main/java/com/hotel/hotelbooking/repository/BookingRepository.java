package com.hotel.hotelbooking.repository;

import com.hotel.hotelbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCustomerId(int customerId);
}