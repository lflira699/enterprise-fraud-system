package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.Timezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimezoneRepository
        extends JpaRepository<Timezone, UUID> {

    Optional<Timezone> findByTimezoneCode(
            String timezoneCode
    );

    List<Timezone> findByStatusOrderByTimezoneNameAsc(
            String status
    );

    List<Timezone> findAllByOrderByTimezoneNameAsc();
}