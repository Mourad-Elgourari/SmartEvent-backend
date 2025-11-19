package com.congress.event.model;

import com.congress.event.enums.EventStatus;
import com.congress.event.enums.EventType;
import com.congress.event.enums.EventVisibility;
import com.congress.event.enums.SocialNetwork;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String image;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private EventVisibility visibility;

    @Enumerated(EnumType.STRING)
    private EventType type;

    private String sponsors;

    @Enumerated(EnumType.STRING)
    private SocialNetwork socialNetwork;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToMany
    @JoinTable(
            name = "event_organizers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> organizers;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

