package com.congress.event.service;


import com.congress.event.model.Guest;
import com.congress.event.model.User;
import com.congress.event.repository.GuestRepository;
import com.congress.event.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public Optional<Guest> getGuestById(Long id) {
        return guestRepository.findById(id);
    }

    /**
     * Get the currently logged-in User entity
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            String username = null;

            if (principal instanceof org.springframework.security.core.userdetails.User) {
                username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            }

            if (username != null) {
                // Fetch the actual User entity from the database
                return userRepository.findByUsername(username).orElse(null);
            }
        }
        return null;
    }

    /**
     * Create a guest and set invitedBy to the current user
     */
    public Guest createGuest(Guest guest) throws Exception {
        // Get the currently logged-in user
        User currentUser = getCurrentUser();

        if (currentUser != null) {
            guest.setInvitedBy(currentUser); // set the inviter
        }

        // Save the guest
        Guest savedGuest = guestRepository.save(guest);

        // Generate QR code containing guest data
        String qrData = String.format(
                "Guest ID: %d\nEmail: %s\nName: %s\nEvent: %s\nInvited by: %s",
                savedGuest.getId(),
                savedGuest.getEmail(),
                savedGuest.getMember() != null ? savedGuest.getMember().getUsername() : "",
                savedGuest.getEvent() != null ? savedGuest.getEvent().getTitle() : "",
                currentUser != null ? currentUser.getUsername() : "Unknown"
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitMatrix matrix = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 200, 200);
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);

        savedGuest.setQrCode(Base64.getEncoder().encodeToString(baos.toByteArray()));
        guestRepository.save(savedGuest);

        // Send QR code via email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(savedGuest.getEmail());
        helper.setSubject("🎟️ Your Event QR Code");
        helper.setText(
                "<p>Hello!</p>" +
                        "<p>Here is your personal QR code for the event:</p>" +
                        "<img src='cid:qrImage' alt='QR Code' style='width:200px;height:200px;'/>" +
                        "<p>We look forward to seeing you!</p>",
                true
        );
        helper.addInline("qrImage", new ByteArrayResource(baos.toByteArray()), "image/png");
        javaMailSender.send(mimeMessage);

        return savedGuest;
    }

    public Guest updateGuest(Long id, Guest guestDetails) {
        return guestRepository.findById(id)
                .map(guest -> {
                    guest.setEmail(guestDetails.getEmail());
                    guest.setQrCode(guestDetails.getQrCode());
                    guest.setVerified(guestDetails.isVerified());
                    guest.setStatus(guestDetails.getStatus());
                    guest.setMember(guestDetails.getMember());
                    guest.setEvent(guestDetails.getEvent());
                    guest.setInvitedBy(guestDetails.getInvitedBy());
                    return guestRepository.save(guest);
                })
                .orElseThrow(() -> new RuntimeException("Guest not found with id " + id));
    }

    public void deleteGuest(Long id) {
        guestRepository.deleteById(id);
    }
}
