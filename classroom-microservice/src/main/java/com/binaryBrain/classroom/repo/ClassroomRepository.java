package com.binarybrain.classroom.repo;

import com.binarybrain.classroom.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByTeacherId(Long teacherId);
    List<Classroom> findByStudentIdsContaining(Long id);
}
