document.addEventListener('DOMContentLoaded', async () => {
    // Load student list
    await loadStudents();
    
    // Setup form submission
    document.getElementById('add-student-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await addStudent();
    });
    
    // Setup search
    document.getElementById('search-btn').addEventListener('click', async () => {
        await searchStudents();
    });
    
    document.getElementById('student-search').addEventListener('keyup', async (e) => {
        if (e.key === 'Enter') {
            await searchStudents();
        }
    });
});

async function loadStudents() {
    try {
        const students = await fetchData(`${API_BASE_URL}/students`);
        renderStudentTable(students);
    } catch (error) {
        console.error('Error loading students:', error);
        showAlert('Failed to load students', 'error');
    }
}

async function searchStudents() {
    try {
        const query = document.getElementById('student-search').value.trim();
        let students = await fetchData(`${API_BASE_URL}/students`);
        
        if (query) {
            students = students.filter(student => 
                student.name.toLowerCase().includes(query.toLowerCase()) ||
                student.rollNumber.toLowerCase().includes(query.toLowerCase()) ||
                student.rfid.toLowerCase().includes(query.toLowerCase())
            );
        }
        
        renderStudentTable(students);
    } catch (error) {
        console.error('Error searching students:', error);
        showAlert('Failed to search students', 'error');
    }
}

function renderStudentTable(students) {
    const tbody = document.querySelector('#student-table tbody');
    tbody.innerHTML = '';
    
    if (students && students.length > 0) {
        students.forEach(student => {
            const row = document.createElement('tr');
            
            const rollCell = document.createElement('td');
            rollCell.textContent = student.rollNumber;
            
            const nameCell = document.createElement('td');
            nameCell.textContent = student.name;
            
            const deptCell = document.createElement('td');
            deptCell.textContent = student.department;
            
            const rfidCell = document.createElement('td');
            rfidCell.textContent = student.rfid;
            
            const actionsCell = document.createElement('td');
            
            const editBtn = document.createElement('button');
            editBtn.textContent = 'Edit';
            editBtn.className = 'btn btn-sm';
            editBtn.addEventListener('click', () => openEditModal(student));
            
            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = 'Delete';
            deleteBtn.className = 'btn btn-sm btn-danger';
            deleteBtn.addEventListener('click', () => deleteStudent(student.id));
            
            actionsCell.appendChild(editBtn);
            actionsCell.appendChild(deleteBtn);
            
            row.appendChild(rollCell);
            row.appendChild(nameCell);
            row.appendChild(deptCell);
            row.appendChild(rfidCell);
            row.appendChild(actionsCell);
            
            tbody.appendChild(row);
        });
    } else {
        const row = document.createElement('tr');
        const cell = document.createElement('td');
        cell.colSpan = 5;
        cell.textContent = 'No students found';
        row.appendChild(cell);
        tbody.appendChild(row);
    }
}

function openEditModal(student) {
    const modal = document.getElementById('edit-student-modal');
    
    document.getElementById('edit-id').value = student.id;
    document.getElementById('edit-name').value = student.name;
    document.getElementById('edit-rollNumber').value = student.rollNumber;
    document.getElementById('edit-enrollNo').value = student.enrollNo;
    document.getElementById('edit-rfid').value = student.rfid;
    document.getElementById('edit-department').value = student.department;
    document.getElementById('edit-email').value = student.email || '';
    document.getElementById('edit-phone').value = student.phone || '';
    
    // Setup form submission
    document.getElementById('edit-student-form').onsubmit = async (e) => {
        e.preventDefault();
        await updateStudent();
    };
    
    modal.style.display = 'block';
}

async function addStudent() {
    const student = {
        name: document.getElementById('name').value,
        rollNumber: document.getElementById('rollNumber').value,
        enrollNo: document.getElementById('enrollNo').value,
        rfid: document.getElementById('rfid').value,
        department: document.getElementById('department').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value
    };
    
    try {
        const response = await fetchData(`${API_BASE_URL}/students`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(student)
        });
        
        if (response) {
            showAlert('Student added successfully');
            document.getElementById('add-student-form').reset();
            await loadStudents();
        }
    } catch (error) {
        console.error('Error adding student:', error);
        showAlert('Failed to add student', 'error');
    }
}

async function updateStudent() {
    const student = {
        id: document.getElementById('edit-id').value,
        name: document.getElementById('edit-name').value,
        rollNumber: document.getElementById('edit-rollNumber').value,
        enrollNo: document.getElementById('edit-enrollNo').value,
        rfid: document.getElementById('edit-rfid').value,
        department: document.getElementById('edit-department').value,
        email: document.getElementById('edit-email').value,
        phone: document.getElementById('edit-phone').value
    };
    
    try {
        const response = await fetchData(`${API_BASE_URL}/students/${student.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(student)
        });
        
        if (response) {
            showAlert('Student updated successfully');
            document.getElementById('edit-student-modal').style.display = 'none';
            await loadStudents();
        }
    } catch (error) {
        console.error('Error updating student:', error);
        showAlert('Failed to update student', 'error');
    }
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) {
        return;
    }
    
    try {
        const response = await fetchData(`${API_BASE_URL}/students/${id}`, {
            method: 'DELETE'
        });
        
        if (response) {
            showAlert('Student deleted successfully');
            await loadStudents();
        }
    } catch (error) {
        console.error('Error deleting student:', error);
        showAlert('Failed to delete student', 'error');
    }
}