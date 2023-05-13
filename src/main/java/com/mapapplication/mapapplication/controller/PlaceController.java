package com.mapapplication.mapapplication.controller;

import com.mapapplication.mapapplication.dto.PlaceDto;
import com.mapapplication.mapapplication.entity.Place;
import com.mapapplication.mapapplication.repository.PlaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceRepository placeRepository;

    public PlaceController(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<String> createPlace(
            @RequestParam(value = "placeId") String placeId,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "rating", required = false) Double rating,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
        try {

            // 엔티티 생성 및 저장
            Place place = new Place();
            place.setPlaceId(placeId);
            place.setLatitude(latitude);
            place.setLongitude(longitude);
            place.setName(name);
            place.setRating(rating);
            if(phoneNumber.equals("undefined")) {
                phoneNumber = "null";
                place.setPhoneNumber(phoneNumber);
            }
            placeRepository.save(place);

            return ResponseEntity.status(HttpStatus.CREATED).body("장소 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장소 저장 실패");
        }
    }
    @GetMapping("/lists")
    public ModelAndView getAllPlaces() {
        List<Place> places = placeRepository.findAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("places", places);
        modelAndView.setViewName("table");

        return modelAndView;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }

}

