package com.mapapplication.mapapplication.controller;

import com.mapapplication.mapapplication.dto.PlaceDto;
import com.mapapplication.mapapplication.entity.Place;
import com.mapapplication.mapapplication.entity.TripDailySchedule;
import com.mapapplication.mapapplication.repository.PlaceRepository;
import com.mapapplication.mapapplication.repository.TripDailyScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceRepository placeRepository;
    private final TripDailyScheduleRepository tripDailyScheduleRepository;

    public PlaceController(PlaceRepository placeRepository, TripDailyScheduleRepository tripDailyScheduleRepository) {
        this.placeRepository = placeRepository;
        this.tripDailyScheduleRepository = tripDailyScheduleRepository;
    }

    @PostMapping("/save/{parentId}")
    public ResponseEntity<String> createPlace(
            @PathVariable("parentId") Long parentId,
            @RequestParam(value = "placeId") String placeId,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "rating", required = false) Double rating,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber)
    {
        try {
            // 부모 엔티티 가져오기
            TripDailySchedule parent = tripDailyScheduleRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("부모 엔티티를 찾을 수 없음: " + parentId));

            // 엔티티 생성 및 속성 설정
            Place place = new Place();
            place.setPlaceId(placeId);
            place.setLatitude(latitude);
            place.setLongitude(longitude);
            place.setName(name);
            place.setRating(rating);
            place.setPhoneNumber(phoneNumber);
            place.setParent(parent); // 부모 엔티티 설정
            placeRepository.save(place);

            return ResponseEntity.status(HttpStatus.CREATED).body("장소 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장소 저장 실패");
        }
    }

    @GetMapping("/lists/{parentId}")
    public ModelAndView getPlacesByParentId(@PathVariable("parentId") Long parentId) {
        List<Place> places = placeRepository.findByParentId(parentId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("places", places);
        modelAndView.setViewName("table");

        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePlace(@PathVariable("id") Long id) {
        try {
            // 삭제할 장소 엔티티 가져오기
            Place place = placeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("삭제할 장소를 찾을 수 없음: " + id));

            // 삭제
            placeRepository.delete(place);

            return ResponseEntity.status(HttpStatus.OK).body("장소 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장소 삭제 실패");
        }
    }



    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }

}

