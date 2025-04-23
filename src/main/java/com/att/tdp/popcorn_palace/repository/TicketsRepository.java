package com.att.tdp.popcorn_palace.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.att.tdp.popcorn_palace.model.Tickets;

public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    
    
}
