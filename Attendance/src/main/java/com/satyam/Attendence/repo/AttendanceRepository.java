package com.satyam.Attendence.repo;

import com.satyam.Attendence.model.Attendance;
import com.satyam.Attendence.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByStudentAndDate(Student student, LocalDate date);
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);
}