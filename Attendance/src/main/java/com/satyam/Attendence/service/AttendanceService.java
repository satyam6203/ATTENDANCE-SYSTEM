package com.satyam.Attendence.service;

import com.satyam.Attendence.model.*;
import com.satyam.Attendence.repo.AttendanceRepository;
import com.satyam.Attendence.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    public ResponseEntity<String> findByRollNumber(String rollNumber){
        Student student= studentRepository.findByRollNumber(rollNumber);
        if(student != null){
            return ResponseEntity.ok().body("student is find by roll_number :"+rollNumber);
        }
        else{
            return ResponseEntity.badRequest().body("stundet is not found :");
        }

    }

    public  ResponseEntity<String> findByEnrollNo(String enrollNo){
        Student student= studentRepository.findByEnrollNo(enrollNo);
        if(student != null){
            return ResponseEntity.ok().body("student name :"+student.getName()+
                    "\nEnrollmentNumber :"+student.getEnrollNo());
        }
        else{
            return ResponseEntity.badRequest().body("Student is not found with the id :");
        }
    }

    public Attendance markAttendanceWithLocation(LocationRequest request) {
        Student student = studentRepository.findById(request.getStudentId()).orElse(null);
        if (student == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        List<Attendance> todayAttendance = attendanceRepository.findByStudentAndDate(student, today);

        // Set your classroom's fixed location here (lat, lon)
        double classroomLat = 26.9124;
        double classroomLon = 75.7873;

        double distance = calculateDistance(classroomLat, classroomLon, request.getLatitude(), request.getLongitude());

        Attendance attendance;
        if (todayAttendance.isEmpty()) {
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(today);
            attendance.setTimeIn(LocalDateTime.now());

            if (distance <= 25.0) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            } else {
                attendance.setStatus(AttendanceStatus.ABSENT);
            }
        } else {
            attendance = todayAttendance.get(0);
            if (attendance.getTimeOut() == null) {
                attendance.setTimeOut(LocalDateTime.now());
            }
        }

        return attendanceRepository.save(attendance);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth in meters

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in meters
    }

}