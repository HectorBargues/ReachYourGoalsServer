package com.hectorbargues.ReachYourGoals.repository;

import java.util.Optional;
import com.hectorbargues.ReachYourGoals.entity.FileEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByName(String name);
}