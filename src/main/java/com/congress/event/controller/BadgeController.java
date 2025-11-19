package com.congress.event.controller;

import com.congress.event.model.Badge;
import com.congress.event.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Badge createBadge(@RequestBody Badge badge) {
        return badgeService.createBadge(badge);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Badge> updateBadge(@PathVariable Long id, @RequestBody Badge badgeDetails) {
        try {
            Badge updatedBadge = badgeService.updateBadge(id, badgeDetails);
            return ResponseEntity.ok(updatedBadge);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }
}
