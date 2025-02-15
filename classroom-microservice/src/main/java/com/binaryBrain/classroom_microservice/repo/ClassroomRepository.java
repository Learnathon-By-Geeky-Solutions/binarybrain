package com.binaryBrain.classroom_microservice.repo;

import com.binaryBrain.classroom_microservice.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByTeacherId(Long teacherId);
}
