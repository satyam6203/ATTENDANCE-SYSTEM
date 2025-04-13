document.addEventListener('DOMContentLoaded', async () => {
    // Load dashboard stats
    await loadDashboardStats();
    
    // Load recent activity
    await loadRecentActivity();
    
    // Refresh data every 30 seconds
    setInterval(async () => {
        await loadDashboardStats();
        await loadRecentActivity();
    }, 30000);
});

async function loadDashboardStats() {
    try {
        // Get total students
        const students = await fetchData(`${API_BASE_URL}/students`);
        document.getElementById('total-students').textContent = students ? students.length : 0;
        
        // Get today's attendance
        const today = formatDate(new Date());
        const attendance = await fetchData(`${API_BASE_URL}/report?startDate=${today}&endDate=${today}`);
        
        if (attendance) {
            document.getElementById('today-attendance').textContent = attendance.length;
            
            const presentCount = attendance.filter(a => a.status === 'PRESENT').length;
            document.getElementById('present-today').textContent = presentCount;
            
            const totalStudents = students ? students.length : 0;
            document.getElementById('absent-today').textContent = totalStudents - presentCount;
        }
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
        showAlert('Failed to load dashboard data', 'error');
    }
}

async function loadRecentActivity() {
    try {
        const today = formatDate(new Date());
        const attendance = await fetchData(`${API_BASE_URL}/report?startDate=${today}&endDate=${today}`);
        
        const tbody = document.querySelector('#activity-table tbody');
        tbody.innerHTML = '';
        
        if (attendance && attendance.length > 0) {
            // Sort by time in descending order
            attendance.sort((a, b) => new Date(b.timeIn) - new Date(a.timeIn));
            
            // Show last 10 records
            const recent = attendance.slice(0, 10);
            
            recent.forEach(record => {
                const row = document.createElement('tr');
                
                const timeCell = document.createElement('td');
                timeCell.textContent = formatTime(record.timeIn);
                
                const nameCell = document.createElement('td');
                nameCell.textContent = record.student.name;
                
                const statusCell = document.createElement('td');
                statusCell.textContent = record.status;
                statusCell.className = record.status.toLowerCase();
                
                row.appendChild(timeCell);
                row.appendChild(nameCell);
                row.appendChild(statusCell);
                
                tbody.appendChild(row);
            });
        } else {
            const row = document.createElement('tr');
            const cell = document.createElement('td');
            cell.colSpan = 3;
            cell.textContent = 'No attendance records for today';
            row.appendChild(cell);
            tbody.appendChild(row);
        }
    } catch (error) {
        console.error('Error loading recent activity:', error);
        showAlert('Failed to load recent activity', 'error');
    }
}