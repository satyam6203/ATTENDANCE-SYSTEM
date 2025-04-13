document.addEventListener('DOMContentLoaded', async () => {
    // Load today's attendance by default
    await loadAttendance();
    
    // Setup date change handler
    document.getElementById('attendance-date').addEventListener('change', async () => {
        await loadAttendance();
    });
    
    // Setup refresh button
    document.getElementById('refresh-btn').addEventListener('click', async () => {
        await loadAttendance();
    });
    
    // Simulate RFID scanner (for demo purposes)
    setupRfidSimulator();
});

async function loadAttendance() {
    const date = document.getElementById('attendance-date').value;
    
    try {
        const attendance = await fetchData(`${API_BASE_URL}/report?startDate=${date}&endDate=${date}`);
        renderAttendanceTable(attendance);
    } catch (error) {
        console.error('Error loading attendance:', error);
        showAlert('Failed to load attendance data', 'error');
    }
}

function renderAttendanceTable(attendance) {
    const tbody = document.querySelector('#attendance-table tbody');
    tbody.innerHTML = '';
    
    if (attendance && attendance.length > 0) {
        // Sort by time in descending order
        attendance.sort((a, b) => new Date(b.timeIn) - new Date(a.timeIn));
        
        attendance.forEach(record => {
            const row = document.createElement('tr');
            
            const timeInCell = document.createElement('td');
            timeInCell.textContent = record.timeIn ? formatTime(record.timeIn) : '-';
            
            const timeOutCell = document.createElement('td');
            timeOutCell.textContent = record.timeOut ? formatTime(record.timeOut) : '-';
            
            const nameCell = document.createElement('td');
            nameCell.textContent = record.student.name;
            
            const rollCell = document.createElement('td');
            rollCell.textContent = record.student.rollNumber;
            
            const deptCell = document.createElement('td');
            deptCell.textContent = record.student.department;
            
            const statusCell = document.createElement('td');
            statusCell.textContent = record.status;
            statusCell.className = record.status.toLowerCase();
            
            row.appendChild(timeInCell);
            row.appendChild(timeOutCell);
            row.appendChild(nameCell);
            row.appendChild(rollCell);
            row.appendChild(deptCell);
            row.appendChild(statusCell);
            
            tbody.appendChild(row);
        });
    } else {
        const row = document.createElement('tr');
        const cell = document.createElement('td');
        cell.colSpan = 6;
        cell.textContent = 'No attendance records for selected date';
        row.appendChild(cell);
        tbody.appendChild(row);
    }
}

// For demo purposes - simulates RFID scanner
function setupRfidSimulator() {
    const rfidInput = document.createElement('input');
    rfidInput.type = 'text';
    rfidInput.style.position = 'fixed';
    rfidInput.style.top = '-100px';
    rfidInput.style.left = '-100px';
    document.body.appendChild(rfidInput);
    
    // Focus the hidden input when clicking anywhere on the page
    document.addEventListener('click', () => {
        rfidInput.focus();
    });
    
    // Handle RFID input
    rfidInput.addEventListener('input', async (e) => {
        const rfid = e.target.value.trim();
        if (rfid.length >= 10) { // Assuming RFID is at least 10 characters
            e.target.value = ''; // Clear the input
            
            // Update scanner status
            const statusDiv = document.getElementById('scanner-status');
            statusDiv.innerHTML = '<p>Scanning...</p>';
            
            try {
                // Mark attendance via API
                const response = await fetchData(`${API_BASE_URL}/mark?rfid=${rfid}`);
                
                if (response) {
                    statusDiv.innerHTML = '<p>Scan successful!</p>';
                    
                    // Update last scan display
                    const lastScanDiv = document.getElementById('last-scan');
                    const student = response.student;
                    lastScanDiv.innerHTML = `
                        <p><strong>Student:</strong> ${student.name}</p>
                        <p><strong>Roll No:</strong> ${student.rollNumber}</p>
                        <p><strong>Time:</strong> ${formatTime(response.timeIn)}</p>
                    `;
                    
                    // Reload attendance table
                    await loadAttendance();
                    
                    // Reset status after 3 seconds
                    setTimeout(() => {
                        statusDiv.innerHTML = '<p>Scanner ready</p>';
                    }, 3000);
                } else {
                    statusDiv.innerHTML = '<p>Scan failed - student not found</p>';
                    setTimeout(() => {
                        statusDiv.innerHTML = '<p>Scanner ready</p>';
                    }, 3000);
                }
            } catch (error) {
                console.error('Error marking attendance:', error);
                statusDiv.innerHTML = '<p>Error processing scan</p>';
                setTimeout(() => {
                    statusDiv.innerHTML = '<p>Scanner ready</p>';
                }, 3000);
            }
        }
    });
}