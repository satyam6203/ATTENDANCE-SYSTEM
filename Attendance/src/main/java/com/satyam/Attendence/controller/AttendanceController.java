package com.satyam.Attendence.controller;

import com.satyam.Attendence.model.*;
import com.satyam.Attendence.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/AllStudents")
    public List<Student> getAllStudents() {
        return attendanceService.getAllStudents();
    }

    @PostMapping("/students")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(attendanceService.addStudent(student));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<String> getStudentById(@PathVariable Long id) {
        Student student = attendanceService.getStudentById(id);
        if (student != null) {
            return ResponseEntity.ok().body("Student Found:"+student.getName());
        } else {
            return ResponseEntity.badRequest().body("Student not found with ID:"+id);
        }
    }

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestParam String rfid) {
        Attendance attendance = attendanceService.markAttendance(rfid);
        if (attendance == null) {
            return ResponseEntity.badRequest().body("Student not found with RFID: " + rfid);
        }
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/students/{id}/attendance")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@PathVariable Long id) {
        List<Attendance> attendance = attendanceService.getStudentAttendance(id);
        if(attendance != null){
            return ResponseEntity.ok(attendance);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report")
    public ResponseEntity<List<Attendance>> getAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceReport(startDate, endDate));
    }
}
