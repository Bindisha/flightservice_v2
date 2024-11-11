package com.tus.flight.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tus.flight.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

}
