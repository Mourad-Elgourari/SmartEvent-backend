package com.congress.event.service;

import com.congress.event.model.Badge;
import com.congress.event.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> getBadgeById(Long id) {
        return badgeRepository.findById(id);
    }

    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    public Badge updateBadge(Long id, Badge badgeDetails) {
        return badgeRepository.findById(id)
                .map(badge -> {
                    badge.setName(badgeDetails.getName());
                    badge.setDescription(badgeDetails.getDescription());
                    badge.setDesign(badgeDetails.getDesign());
                    badge.setPublished(badgeDetails.isPublished());
                    return badgeRepository.save(badge);
                })
                .orElseThrow(() -> new RuntimeException("Badge not found with id " + id));
    }

    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
}
