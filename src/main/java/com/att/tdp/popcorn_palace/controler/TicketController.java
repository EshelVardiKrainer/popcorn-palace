package com.att.tdp.popcorn_palace.controler;
import org.springframework.web.bind.annotation.RestController;
import com.att.tdp.popcorn_palace.repository.TicketRepository;

@RestController
public class TicketController {
    private TicketRepository ticketRepository;
    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
}
