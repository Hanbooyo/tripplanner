package com.mapapplication.mapapplication.repository;

import com.mapapplication.mapapplication.entity.TripDailySchedule;
import com.mapapplication.mapapplication.entity.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<TripSchedule, Long> {
    @Query("SELECT tds FROM TripDailySchedule tds WHERE tds.parentId = :parentId")
    List<TripDailySchedule> findByParentId(@Param("parentId") Long parentId);

    // sortOrder 업데이트
    @Modifying
    @Query("UPDATE TripDailySchedule SET sortOrder = :newSortOrder WHERE id = :scheduleId")
    void updateSortOrder(@Param("scheduleId") Long scheduleId, @Param("newSortOrder") int newSortOrder);
}
