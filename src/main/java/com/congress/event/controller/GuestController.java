package com.congress.event.controller;

import com.congress.event.model.Guest;
import com.congress.event.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "*") // optional for frontend access
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        return guestService.getGuestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Guest> createGuest(@RequestBody Guest guest) {
        try {
            Guest createdGuest = guestService.createGuest(guest);
            return ResponseEntity.ok(createdGuest);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build(); // Internal Server Error
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guestDetails) {
        try {
            Guest updatedGuest = guestService.updateGuest(id, guestDetails);
            return ResponseEntity.ok(updatedGuest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}
