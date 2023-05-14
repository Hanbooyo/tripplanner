package com.mapapplication.mapapplication.controller;

import com.mapapplication.mapapplication.dto.ScheduleDto;
import com.mapapplication.mapapplication.entity.TripDailySchedule;
import com.mapapplication.mapapplication.entity.TripSchedule;
import com.mapapplication.mapapplication.repository.ScheduleRepository;
import com.mapapplication.mapapplication.repository.TripDailyScheduleRepository;
import com.mapapplication.mapapplication.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;
    private final TripDailyScheduleRepository tripDailyScheduleRepository;

    public ScheduleController(ScheduleService scheduleService, ScheduleRepository scheduleRepository,
                              TripDailyScheduleRepository tripDailyScheduleRepository) {
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
        this.tripDailyScheduleRepository = tripDailyScheduleRepository;
    }

    @PostMapping("/save")
    public String createTripSchedule(@RequestParam("title") String title,
                                     @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                     @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                     RedirectAttributes redirectAttributes) {
        // 요청 받은 데이터를 이용하여 TripSchedule 생성 로직 수행
        TripSchedule createdSchedule = scheduleService.createTripSchedule(title, startDate, endDate);

        // 생성된 TripSchedule 정보를 리다이렉트할 URL의 경로 변수로 설정
        Long parentId = createdSchedule.getId();
        redirectAttributes.addAttribute("parentId", parentId);

        // 리다이렉트할 URL을 반환
        return "redirect:/schedules/{parentId}/daily";
    }


    @GetMapping("/{parentId}/daily")
    public ResponseEntity<List<TripDailySchedule>> getDailySchedulesByParentId(@PathVariable("parentId") Long parentId) {
        List<TripDailySchedule> dailySchedules = scheduleRepository.findByParentId(parentId);
        return ResponseEntity.ok(dailySchedules);
    }

    @DeleteMapping("/{tripScheduleId}")
    public RedirectView deleteTripSchedule(@PathVariable Long tripScheduleId, RedirectAttributes redirectAttributes) {
        scheduleService.deleteTripSchedule(tripScheduleId);
        redirectAttributes.addFlashAttribute("message", "삭제 성공");
        return new RedirectView("/schedules");
    }

    @PutMapping("/{parentId}/updateDates")
    public ResponseEntity<String> updateTripDates(
            @PathVariable("parentId") Long parentId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        //일정 업데이트

        return ResponseEntity.ok("여행 일정 업데이트 완료");
    }


    // 일정의 sortOrder 업데이트
    @PutMapping("/{parentId}/daily/{scheduleId}/sortOrder/{newSortOrder}")
    public ResponseEntity<String> updateSortOrder(
            @PathVariable("parentId") Long parentId,
            @PathVariable("scheduleId") Long scheduleId,
            @PathVariable("newSortOrder") int newSortOrder) {

        scheduleService.updateSortOrder(parentId, scheduleId, newSortOrder);
        return ResponseEntity.ok("일정순서 업데이트 완료");
    }


}
