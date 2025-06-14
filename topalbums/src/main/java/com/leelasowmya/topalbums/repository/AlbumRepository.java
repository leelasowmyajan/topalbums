package com.leelasowmya.topalbums.repository;

import com.leelasowmya.topalbums.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {
    Optional<Album> findById(String id);
}
