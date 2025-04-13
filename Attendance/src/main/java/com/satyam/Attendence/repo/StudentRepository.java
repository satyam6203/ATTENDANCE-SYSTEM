package com.satyam.Attendence.repo;

import com.satyam.Attendence.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByRfid(String rfid);
    Student findByRollNumber(String rollNumber);
    Student findByEnrollNo(String enrollNo);
}
