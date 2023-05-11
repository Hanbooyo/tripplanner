package com.mapapplication.mapapplication.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tripDailySchedule")
@Getter @Setter
public class TripDailySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parentId")
    private Long parentId;

    @Column(name = "title")
    private String title;

    @Column(name = "tripDate")
    private LocalDate date;

    @Column(name = "sortOrder")
    private Integer sortOrder;

}

