package com.satyam.Attendence.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class LocationRequest {
    private Long studentId;
    private double latitude;
    private double longitude;
}
