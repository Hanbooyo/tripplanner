package com.mapapplication.mapapplication.repository;

import com.mapapplication.mapapplication.entity.TripDailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripDailyScheduleRepository extends JpaRepository<TripDailySchedule, Long> {

    List<TripDailySchedule> findByParentId(Long parentId);
    // 동일한 부모를 가진 일정들을 sortOrder 오름차순으로 조회
    List<TripDailySchedule> findByParentIdOrderBySortOrderAsc(Long parentId);

}
