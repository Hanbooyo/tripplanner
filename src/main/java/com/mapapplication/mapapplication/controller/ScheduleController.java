package com.mapapplication.mapapplication.controller;

import com.mapapplication.mapapplication.dto.ScheduleDto;
import com.mapapplication.mapapplication.entity.TripDailySchedule;
import com.mapapplication.mapapplication.entity.TripSchedule;
import com.mapapplication.mapapplication.repository.ScheduleRepository;
import com.mapapplication.mapapplication.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ScheduleService scheduleService, ScheduleRepository scheduleRepository) {
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<?> createTripSchedule(@RequestParam("title") String title,
                                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // 요청 받은 데이터를 이용하여 TripSchedule 생성 로직 수행
        TripSchedule createdSchedule = scheduleService.createTripSchedule(title, startDate, endDate);

        // 생성된 TripSchedule 정보를 반환
        return ResponseEntity.ok(createdSchedule);
    }

    @GetMapping("/{parentId}/daily")
    public ResponseEntity<List<TripDailySchedule>> getDailySchedulesByParentId(@PathVariable("parentId") Long parentId) {
        List<TripDailySchedule> dailySchedules = scheduleRepository.findByParentId(parentId);
        return ResponseEntity.ok(dailySchedules);
    }

}
