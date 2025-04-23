package com.att.tdp.popcorn_palace.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.tdp.popcorn_palace.model.Tickets;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, Long> {
    
    
}
