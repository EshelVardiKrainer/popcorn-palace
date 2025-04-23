package com.att.tdp.popcorn_palace.service;
import com.att.tdp.popcorn_palace.repository.TicketRepository;


@Service
public class TicketRipository {
    private final TicketRepository ticketRepository;

    public TicketRipository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
}
