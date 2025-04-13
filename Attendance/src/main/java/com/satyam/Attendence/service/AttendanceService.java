package com.satyam.Attendence.service;

import com.satyam.Attendence.model.*;
import com.satyam.Attendence.repo.AttendanceRepository;
import com.satyam.Attendence.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student getStudentByRfid(String rfid) {
        return studentRepository.findByRfid(rfid);
    }

    public Attendance markAttendance(String rfid) {
        Student student = studentRepository.findByRfid(rfid);
        if (student == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        List<Attendance> todayAttendance = attendanceRepository.findByStudentAndDate(student, today);

        if (todayAttendance.isEmpty()) {
            // First attendance of the day
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(today);
            attendance.setTimeIn(LocalDateTime.now());
            attendance.setStatus(AttendanceStatus.PRESENT);
            return attendanceRepository.save(attendance);
        } else {
            // Update existing attendance (e.g., for time out)
            Attendance attendance = todayAttendance.get(0);
            if (attendance.getTimeOut() == null) {
                attendance.setTimeOut(LocalDateTime.now());
                return attendanceRepository.save(attendance);
            }
            return attendance; // Already marked both in and out
        }
    }

    public List<Attendance> getStudentAttendance(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return null;
        }
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceReport(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByDateBetween(startDate, endDate);
    }
}