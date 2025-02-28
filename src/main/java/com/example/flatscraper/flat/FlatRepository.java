package com.example.flatscraper.flat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Integer> {
    boolean existsByUrl(String url);

    Page<Flat> findAllByAddressContainsIgnoreCase(String address, Pageable pageable);
}
