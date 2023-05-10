package com.mapapplication.mapapplication.repository;

import com.mapapplication.mapapplication.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    // 필요한 추가 메서드 정의
    List<Place> findAll();
}
