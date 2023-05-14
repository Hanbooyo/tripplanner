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

    // sortOrder 업데이트
    public void updateSortOrder(Long parentId, Long scheduleId, int newSortOrder) {
        TripDailySchedule targetSchedule = tripDailyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없음: " + scheduleId));

        // 동일한 부모를 가진 일정들을 sortOrder 오름차순으로 조회
        List<TripDailySchedule> schedules = tripDailyScheduleRepository.findByParentIdOrderBySortOrderAsc(parentId);

        // 대상 일정의 sortOrder 업데이트
        targetSchedule.setSortOrder(newSortOrder);
        tripDailyScheduleRepository.save(targetSchedule);

        // 다른 일정들의 sortOrder 재조정
        for (int i = 0; i < schedules.size(); i++) {
            TripDailySchedule schedule = schedules.get(i);
            if (!schedule.getId().equals(scheduleId)) {
                // 다른 일정들의 sortOrder를 리스트 내 위치에 따라 업데이트
                int newOrder = i + 1;
                schedule.setSortOrder(newOrder);
                tripDailyScheduleRepository.save(schedule);
            }
        }
    }


}
