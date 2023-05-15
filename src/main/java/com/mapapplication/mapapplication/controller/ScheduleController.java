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
import org.springframework.web.servlet.ModelAndView;
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
    public RedirectView createTripSchedule(@RequestParam("title") String title,
                                           @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                           @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                           RedirectAttributes redirectAttributes) {
        // 요청 받은 데이터를 이용하여 TripSchedule 생성 로직 수행
        TripSchedule createdSchedule = scheduleService.createTripSchedule(title, startDate, endDate);

        // 생성된 TripSchedule 정보를 리다이렉트할 URL의 경로 변수로 설정
        Long parentId = createdSchedule.getId();
        redirectAttributes.addAttribute("parentId", parentId);

        // 리다이렉트할 URL을 RedirectView로 생성하여 반환
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:8080/schedules/");
        return redirectView;
    }



    @GetMapping("/")
    public ModelAndView getCalendarPage() {
        List<TripSchedule> tripSchedules = scheduleRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("calendar"); // HTML 템플릿 파일의 이름 (calendar.html)
        modelAndView.addObject("tripSchedules", tripSchedules);
        return modelAndView;
    }

/*
    @GetMapping("/")
    public ResponseEntity<List<TripSchedule>> getAllTripSchedules() {
        List<TripSchedule> tripSchedules = scheduleRepository.findAll();
        return ResponseEntity.ok(tripSchedules);
    }
*/

    @PutMapping("/{tripId}")
    public String updateTrip(
            @PathVariable("tripId") Long tripId,
            @RequestParam("title") String title,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            RedirectAttributes redirectAttributes) {

        scheduleService.updateTripSchedule(tripId, title, startDate, endDate);
        redirectAttributes.addFlashAttribute("message", "Trip updated successfully");

        return "redirect:/";
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



    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<String> updateTripDailySchedule(
            @PathVariable("scheduleId") Long scheduleId,
            @RequestParam("title") String title,
            @RequestParam("newTripDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newTripDate) {

        // 업데이트 대상인 TripDailySchedule 가져오기
        TripDailySchedule schedule = tripDailyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("TripDailySchedule을 찾을 수 없음: " + scheduleId));

        //

        if (!schedule.getTitle().equals(title) || !schedule.getDate().equals(newTripDate)) {
            // 같은 parentId를 가진 다른 TripDailySchedule들 가져오기
            List<TripDailySchedule> otherSchedules = tripDailyScheduleRepository.findByParentId(schedule.getParent().getId());

            // title 업데이트
            schedule.setTitle(title);

            // tripDate 맞바꾸기
            for (TripDailySchedule otherSchedule : otherSchedules) {
                if (otherSchedule.getId().equals(scheduleId)) {
                    otherSchedule.setDate(newTripDate);
                } else if (otherSchedule.getDate().equals(newTripDate)) {
                    otherSchedule.setDate(schedule.getDate());
                }
            }

            // 변경된 TripDailySchedule들 저장
            tripDailyScheduleRepository.saveAll(otherSchedules);
            tripDailyScheduleRepository.save(schedule); // 변경된 schedule 엔티티 저장

            return ResponseEntity.ok("TripDailySchedule 업데이트 완료");
        }

        return ResponseEntity.ok("변경 사항 없음");
    }




}
