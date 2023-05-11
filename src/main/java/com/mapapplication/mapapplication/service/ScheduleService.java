package com.mapapplication.mapapplication.service;

import com.mapapplication.mapapplication.entity.TripSchedule;
import com.mapapplication.mapapplication.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public TripSchedule createTripSchedule(String title, LocalDate startDate, LocalDate endDate) {
        TripSchedule tripSchedule = new TripSchedule();
        tripSchedule.setTitle(title);
        tripSchedule.setStartDate(startDate);
        tripSchedule.setEndDate(endDate);

        // TripSchedule 저장
        return scheduleRepository.save(tripSchedule);
    }
}
