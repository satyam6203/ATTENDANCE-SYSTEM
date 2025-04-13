package com.satyam.Attendence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Roll number is required")
    @Column(unique = true)
    private String rollNumber;

    @NotBlank(message = "Enrollment number is required")
    @Column(unique = true)
    private String enrollNo;

    @NotBlank(message = "RFID is required")
    @Column(unique = true)
    private String rfid;

    @NotBlank(message = "Department is required")
    private String department;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Attendance> attendanceRecords;
}