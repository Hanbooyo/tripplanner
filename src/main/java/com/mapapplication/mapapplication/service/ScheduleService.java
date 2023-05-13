package com.mapapplication.mapapplication.service;

import com.mapapplication.mapapplication.entity.TripDailySchedule;
import com.mapapplication.mapapplication.entity.TripSchedule;
import com.mapapplication.mapapplication.repository.ScheduleRepository;
import com.mapapplication.mapapplication.repository.TripDailyScheduleRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TripDailyScheduleRepository tripDailyScheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, TripDailyScheduleRepository tripDailyScheduleRepository) {
        this.scheduleRepository = scheduleRepository;
        this.tripDailyScheduleRepository = tripDailyScheduleRepository;
    }

    public TripSchedule createTripSchedule(String title, LocalDate startDate, LocalDate endDate) {
        TripSchedule tripSchedule = new TripSchedule();
        tripSchedule.setTitle(title);
        tripSchedule.setStartDate(startDate);
        tripSchedule.setEndDate(endDate);

        // TripSchedule 저장
        return scheduleRepository.save(tripSchedule);
    }

    public void deleteTripSchedule(Long tripScheduleId) {

        // 연관된 TripDailySchedule 삭제
        List<TripDailySchedule> tripDailySchedules = tripDailyScheduleRepository.findByParentId(tripScheduleId);
        tripDailyScheduleRepository.deleteAll(tripDailySchedules);

        // TripSchedule 삭제
        TripSchedule tripSchedule = scheduleRepository.findById(tripScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("아이디를 찾을 수 없음: " + tripScheduleId));
        scheduleRepository.delete(tripSchedule);

    }


}
