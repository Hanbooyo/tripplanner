package com.mapapplication.mapapplication.service;

import com.mapapplication.mapapplication.entity.TripDailySchedule;
import com.mapapplication.mapapplication.entity.TripSchedule;
import com.mapapplication.mapapplication.repository.PlaceRepository;
import com.mapapplication.mapapplication.repository.ScheduleRepository;
import com.mapapplication.mapapplication.repository.TripDailyScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TripDailyScheduleRepository tripDailyScheduleRepository;
    private final PlaceRepository placeRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, TripDailyScheduleRepository tripDailyScheduleRepository, PlaceRepository placeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.tripDailyScheduleRepository = tripDailyScheduleRepository;
        this.placeRepository = placeRepository;
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
        deleteTripDailySchedule(tripScheduleId);

        // TripSchedule 삭제
        TripSchedule tripSchedule = scheduleRepository.findById(tripScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("아이디를 찾을 수 없음: " + tripScheduleId));
        scheduleRepository.delete(tripSchedule);

    }

    public void updateTripSchedule(Long tripScheduleId) {

        // 연관된 TripDailySchedule 삭제
        deleteTripDailySchedule(tripScheduleId);

        // TripSchedule 삭제
        TripSchedule tripSchedule = scheduleRepository.findById(tripScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("아이디를 찾을 수 없음: " + tripScheduleId));
        scheduleRepository.delete(tripSchedule);

    }

    public void deleteTripDailySchedule(Long tripScheduleId) {

        // 연관된 TripDailySchedule 삭제
        List<TripDailySchedule> tripDailySchedules = tripDailyScheduleRepository.findByParentId(tripScheduleId);

        tripDailyScheduleRepository.deleteAll(tripDailySchedules);

        // 연관된 Place 삭제
        List<Long> tripDailyScheduleIds = tripDailySchedules.stream()
                .map(TripDailySchedule::getId)
                .collect(Collectors.toList());

        placeRepository.deleteByParentIdIn(tripDailyScheduleIds);

    }

    public ResponseEntity<String> updateTripSchedule(Long scheduleId, String title, LocalDate startDate, LocalDate endDate) {
        TripSchedule tripSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("TripSchedule을 찾을 수 없음: " + scheduleId));
        if (!tripSchedule.getTitle().equals(title)) {
            tripSchedule.setTitle(title);
            scheduleRepository.save(tripSchedule);
        }

        if (!tripSchedule.getStartDate().equals(startDate) || !tripSchedule.getEndDate().equals(endDate)) {
            tripSchedule.setStartDate(startDate);
            tripSchedule.setEndDate(endDate);
            deleteTripDailySchedule(tripSchedule.getId());

            LocalDate currentDate = tripSchedule.getStartDate();

            while (currentDate.isBefore(tripSchedule.getEndDate()) || currentDate.isEqual(tripSchedule.getEndDate())) {
                TripDailySchedule tripDailySchedule = new TripDailySchedule();
                tripDailySchedule.setParent(tripSchedule);
                tripDailySchedule.setTitle("일일 일정");
                tripDailySchedule.setDate(currentDate);

                tripDailyScheduleRepository.save(tripDailySchedule);

                currentDate = currentDate.plusDays(1);
            }


            tripSchedule = scheduleRepository.save(tripSchedule);
        }

        return ResponseEntity.ok("TripSchedule 업데이트 완료");

    }

}

