package com.example.flatscraper.repository;

import com.example.flatscraper.entity.FlatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatRepository extends JpaRepository<FlatEntity, Integer> {
    boolean existsByUrl(String url);

    Page<FlatEntity> findAllByAddressContainsIgnoreCase(String address, Pageable pageable);
}
