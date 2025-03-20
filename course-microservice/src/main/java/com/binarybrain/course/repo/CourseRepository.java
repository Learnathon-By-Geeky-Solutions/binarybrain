package com.binarybrain.course.repo;

import com.binarybrain.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCreatedBy(Long id);
    List<Course> findByIdIn(List<Long> ids);
}
