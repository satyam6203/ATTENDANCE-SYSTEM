package com.satyam.Attendence;

import com.fazecast.jSerialComm.SerialPort;
import com.satyam.Attendence.model.Attendance;
import com.satyam.Attendence.service.AttendanceService;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.InputStream;
import java.util.Scanner;

@Component
public class ArduinoSerialReader {

    private SerialPort serialPort;
    private InputStream inputStream;
    private Thread readerThread;
    private final AttendanceService attendanceService;

    private final String portName = "COM3"; // Change this to match your port
    private final int baudRate = 9600;

    public ArduinoSerialReader(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public boolean connect() {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("Connected to " + portName);
            inputStream = serialPort.getInputStream();
            return true;
        } else {
            System.out.println("Failed to connect to " + portName);
            return false;
        }
    }

    public void readSerial() {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String rfid = scanner.nextLine().trim();
            System.out.println("Received RFID: " + rfid);

            // Process the RFID and mark attendance
            try {
                Attendance attendance = attendanceService.markAttendance(rfid);
                if (attendance != null) {
                    System.out.println("Attendance marked for student: " +
                            attendance.getStudent().getName());
                } else {
                    System.out.println("No student found with RFID: " + rfid);
                }
            } catch (Exception e) {
                System.err.println("Error processing RFID: " + e.getMessage());
            }
        }
    }

    @EventListener
    public void onApplicationStart(ContextRefreshedEvent event) {
        if (connect()) {
            readerThread = new Thread(this::readSerial);
            readerThread.start();
        }
    }

    @PreDestroy
    public void disconnect() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Disconnected from serial port.");
        }
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
        }
    }
}