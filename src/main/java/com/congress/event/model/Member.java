package com.congress.event.model;

import com.congress.event.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Member extends User {

    @Temporal(TemporalType.DATE)
    private Date birthday;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String country;
    private String city;

    @ManyToMany
    @JoinTable(
            name = "member_favorites",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> favoris;
}

