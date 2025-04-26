package com.att.tdp.popcorn_palace.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.att.tdp.popcorn_palace.model.Showtime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List <Showtime> findByTheater(String theater);
    
}
