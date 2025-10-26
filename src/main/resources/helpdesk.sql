DROP DATABASE IF EXISTS helpdesk;

CREATE DATABASE helpdesk
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE helpdesk;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE faqs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question VARCHAR(200) NOT NULL,
    answer VARCHAR(4000) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'General',
    created_by_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    

    CONSTRAINT fk_faq_user FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE CASCADE,
    

    INDEX idx_created_by (created_by_id),
    INDEX idx_created_at (created_at),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    assigned_to_id BIGINT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'Other',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    

    CONSTRAINT fk_ticket_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_assigned FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL,
    

    INDEX idx_student_id (student_id),
    INDEX idx_assigned_to_id (assigned_to_id),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE ticket_replies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ticket_reply_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_reply_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NULL,
    student_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    rating VARCHAR(20) NOT NULL CHECK (rating IN ('POOR', 'FAIR', 'GOOD', 'VERY_GOOD', 'EXCELLENT')),
    description VARCHAR(2000) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    

    CONSTRAINT fk_feedback_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    

    INDEX idx_ticket_id (ticket_id),
    INDEX idx_student_id (student_id),
    INDEX idx_rating (rating),
    INDEX idx_submitted_at (submitted_at),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(200) NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    hall_room_number VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    available_slots INT NOT NULL,
    description VARCHAR(2000) NOT NULL,
    created_by_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    

    CONSTRAINT fk_event_created_by FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE CASCADE,
    

    INDEX idx_event_date (event_date),
    INDEX idx_event_time (event_time),
    INDEX idx_hall_room (hall_room_number),
    INDEX idx_created_by (created_by_id),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active TINYINT(1) NOT NULL DEFAULT 1,
    

    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    

    UNIQUE KEY unique_user_event (user_id, event_id),
    

    INDEX idx_event_id (event_id),
    INDEX idx_user_id (user_id),
    INDEX idx_booked_at (booked_at),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE event_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    proposed_date DATE NOT NULL,
    proposed_time TIME NOT NULL,
    hall_room_number VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    description VARCHAR(2000) NOT NULL,
    requester_name VARCHAR(100) NOT NULL,
    requester_email VARCHAR(150) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    

    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_requester_email (requester_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE resources (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100) UNIQUE,
    status ENUM('AVAILABLE', 'IN_USE', 'UNDER_MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    location VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    

    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_location (location),
    INDEX idx_serial_number (serial_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE resource_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    purpose VARCHAR(500) NOT NULL,
    duration_days INT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_id BIGINT NULL,
    admin_comments VARCHAR(1000),
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_date TIMESTAMP NULL,

    CONSTRAINT fk_resource_request_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_resource_request_resource FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE,
    CONSTRAINT fk_resource_request_admin FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE SET NULL,
    

    INDEX idx_user_id (user_id),
    INDEX idx_resource_id (resource_id),
    INDEX idx_status (status),
    INDEX idx_request_date (request_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE issue_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    issue_description VARCHAR(2000) NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
    admin_comments VARCHAR(1000),
    report_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_date TIMESTAMP NULL,
    

    CONSTRAINT fk_issue_report_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_issue_report_resource FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE,
    

    INDEX idx_user_id (user_id),
    INDEX idx_resource_id (resource_id),
    INDEX idx_status (status),
    INDEX idx_report_date (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(100) NOT NULL,
    target_user VARCHAR(100) NULL,
    details TEXT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45) NULL,
    
    -- Indexes for performance
    INDEX idx_performed_by (performed_by),
    INDEX idx_target_user (target_user),
    INDEX idx_timestamp (timestamp),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO users (username, email, password_hash, role, active) VALUES
-- Students
('student1', 'student1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'STUDENT', 1),
('student2', 'student2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'STUDENT', 1),
('student3', 'student3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'STUDENT', 1),

-- Staff
('staff1', 'staff1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'STAFF', 1),

-- Faculty
('faculty1', 'faculty1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'FACULTY', 1),
('faculty2', 'faculty2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'FACULTY', 1),

-- IT Support
('itsupport1', 'itsupport1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'IT_SUPPORT', 1),
('itsupport2', 'itsupport2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'IT_SUPPORT', 1),

-- Communication Service
('comm1', 'comm1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'COMM_SERVICE', 1),
('comm2', 'comm2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'COMM_SERVICE', 1),

-- Department Head
('depthead1', 'depthead1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'DEPT_HEAD', 1),
('depthead2', 'depthead2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'DEPT_HEAD', 1),

-- Admins
('admin1', 'admin1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 1),
('admin2', 'admin2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 1),

-- Super Admin
('superadmin', 'superadmin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'SUPER_ADMIN', 1);

-- Insert comprehensive FAQs with all categories
INSERT INTO faqs (question, answer, category, created_by_id) VALUES
-- Financial Aid Category
('How do I apply for financial aid?', 'Complete the FAFSA form online at fafsa.gov. You can also visit the Financial Aid office for assistance with the application process. Make sure to submit all required documents by the deadline.', 'Financial Aid', 7),
('When is the financial aid deadline?', 'The priority deadline for financial aid is March 1st each year. However, you can still apply after this date, though funding may be limited. Check with the Financial Aid office for specific deadlines.', 'Financial Aid', 7),
('What types of financial aid are available?', 'We offer grants, scholarships, work-study programs, and student loans. Grants and scholarships do not need to be repaid, while loans must be repaid after graduation.', 'Financial Aid', 7),
('How do I check my financial aid status?', 'Log into your student portal and navigate to the Financial Aid section. You can view your award letter, required documents, and disbursement dates.', 'Financial Aid', 7),

-- Transfer Credits Category
('How do I transfer credits from another institution?', 'Submit official transcripts from your previous institution to the Registrar\'s office. Credits will be evaluated for transferability based on course content and grades.', 'Transfer Credits', 5),
('What is the minimum grade required for transfer credits?', 'A minimum grade of C (2.0 GPA) is required for credits to be eligible for transfer. Some programs may have higher requirements.', 'Transfer Credits', 5),
('How long does it take to process transfer credits?', 'Transfer credit evaluation typically takes 2-4 weeks after receiving official transcripts. You will be notified via email once the evaluation is complete.', 'Transfer Credits', 5),
('Can I transfer credits from international institutions?', 'Yes, but international transcripts must be evaluated by a recognized credential evaluation service. Contact the Registrar\'s office for approved services.', 'Transfer Credits', 5),

-- Enrollment Category
('How do I enroll in courses?', 'Log into your student portal, go to the Course Registration section, and select your desired courses. Make sure to check prerequisites and course availability.', 'Enrollment', 5),
('What is the maximum number of credits I can take?', 'Full-time students can take up to 18 credits per semester. To take more than 18 credits, you need approval from your academic advisor.', 'Enrollment', 5),
('How do I drop a course?', 'You can drop courses through your student portal during the add/drop period. After this period, you may need to withdraw, which could affect your financial aid.', 'Enrollment', 5),
('When does course registration open?', 'Registration typically opens 2-3 weeks before the start of each semester. Check your student portal for specific dates and your assigned registration time.', 'Enrollment', 5),

-- Technical Information Category
('How to reset LMS password?', 'Use the Forgot Password link on the LMS login page. If that doesn\'t work, contact IT support at itsupport@university.edu or visit the IT help desk.', 'Technical Information', 7),
('Wi-Fi not connecting on campus?', 'Forget the network and reconnect using your student ID and password. Make sure you\'re in a coverage area. Contact IT support if issues persist.', 'Technical Information', 7),
('How do I access my student email?', 'Go to the university email portal and log in with your student ID and password. You can also set up email forwarding to your personal email.', 'Technical Information', 7),
('What software is available for students?', 'Students have access to Microsoft Office 365, Adobe Creative Suite, and various academic software. Download from the Software Center on your device.', 'Technical Information', 7),

-- Course Information Category
('How to access course materials?', 'Log into the LMS and navigate to your enrolled courses. All materials are available under the course content section. Contact your instructor if materials are missing.', 'Course Information', 5),
('How to submit assignments?', 'Log into the LMS, go to your course, and click on the Assignments section. Upload your files before the deadline. Late submissions may incur penalties.', 'Course Information', 5),
('How do I view my grades?', 'Grades are available in the LMS under the Grades section for each course. Final grades are also posted in your student portal.', 'Course Information', 5),
('What if I miss a class?', 'Contact your instructor immediately. Check the course syllabus for attendance policies and make-up work procedures. Some classes have strict attendance requirements.', 'Course Information', 5),

-- International Students Category
('How do I maintain my F-1 visa status?', 'Maintain full-time enrollment (12+ credits), keep your I-20 valid, and report any changes to the International Student Office within 10 days.', 'International Students', 9),
('Can I work on campus as an international student?', 'Yes, F-1 students can work on campus up to 20 hours per week during the semester. You must maintain your visa status and have a valid I-20.', 'International Students', 9),
('How do I apply for OPT?', 'Apply for Optional Practical Training (OPT) through the International Student Office. You can apply up to 90 days before graduation.', 'International Students', 9),
('What is the SEVIS fee?', 'The SEVIS fee is required for F-1 students and must be paid before your visa interview. The current fee is $350 and can be paid online.', 'International Students', 9),

-- Student Resources Category
('Where is the library located?', 'The main library is located in the center of campus, building A-100. It\'s open 24/7 during the semester with your student ID card.', 'Student Resources', 9),
('How do I access tutoring services?', 'Free tutoring is available in the Learning Center. Schedule appointments online or walk in during open hours. Subjects include math, science, and writing.', 'Student Resources', 9),
('Where can I find career counseling?', 'The Career Services office offers resume help, job search assistance, and interview preparation. Schedule an appointment through the student portal.', 'Student Resources', 9),
('How do I access mental health services?', 'Counseling services are available through the Student Wellness Center. Services are confidential and free for all students. Call or visit to schedule an appointment.', 'Student Resources', 9),

-- Campus Life Category
('What student organizations are available?', 'We have over 100 student organizations including academic clubs, cultural groups, and recreational activities. Visit the Student Activities office for a complete list.', 'Campus Life', 9),
('How do I join a fraternity or sorority?', 'Greek life recruitment happens at the beginning of each semester. Contact the Greek Life office for information about recruitment events and requirements.', 'Campus Life', 9),
('Where can I find information about campus events?', 'Check the university calendar, student portal, and social media pages for upcoming events. The Student Activities office also posts event flyers around campus.', 'Campus Life', 9),
('How do I get involved in student government?', 'Student government elections are held each spring. You can also apply for appointed positions or join committees throughout the year.', 'Campus Life', 9);

-- Insert sample tickets with statuses from TicketStatus.java
INSERT INTO tickets (student_id, assigned_to_id, title, description, category, status) VALUES
-- PENDING tickets (matching TicketStatus.PENDING)
(1, NULL, 'Cannot access LMS', 'Getting error 403 when trying to access course page. Need help to resolve this issue.', 'IT', 'PENDING'),
(2, NULL, 'Email quota exceeded', 'Mailbox is full and cannot receive new emails. Need assistance to increase quota.', 'IT', 'PENDING'),
(3, NULL, 'Wi-Fi connection issues', 'Unable to connect to campus Wi-Fi in the library area. Signal is weak.', 'IT', 'PENDING'),

-- IN_PROGRESS tickets (matching TicketStatus.IN_PROGRESS)
(1, 7, 'LMS password reset', 'Requested password reset for LMS account. Need to regain access to course materials.', 'IT', 'IN_PROGRESS'),
(2, 8, 'Library access problem', 'Student ID not working for library door access. Need to update access permissions.', 'Other', 'IN_PROGRESS'),

-- RESOLVED tickets (matching TicketStatus.RESOLVED)
(3, 7, 'Email setup on mobile', 'Need help setting up student email on mobile device. Issue has been resolved.', 'IT', 'RESOLVED'),
(1, 8, 'Course enrollment issue', 'Unable to enroll in required course. Issue resolved after contacting registrar.', 'Academics', 'RESOLVED'),

-- CLOSED tickets (matching TicketStatus.CLOSED)
(2, 7, 'Software installation', 'Need help installing required software for coursework. Successfully installed.', 'IT', 'CLOSED'),
(3, 8, 'Library book renewal', 'Need to renew library books online. Successfully renewed.', 'Other', 'CLOSED');

-- Insert sample feedback (ticket-related feedback)
INSERT INTO feedback (ticket_id, student_id, name, email, rating, description) VALUES
(1, 1, 'Student One', 'student1@example.com', 'EXCELLENT', 'Excellent support! The IT team was very helpful and resolved the issue quickly.'),
(2, 2, 'Student Two', 'student2@example.com', 'VERY_GOOD', 'Good service, but took a bit longer than expected. Overall satisfied.'),
(3, 3, 'Student Three', 'student3@example.com', 'EXCELLENT', 'Outstanding help! The staff went above and beyond to assist me.'),
(4, 1, 'Student One', 'student1@example.com', 'GOOD', 'Issue was resolved but communication could have been better.'),
(5, 2, 'Student Two', 'student2@example.com', 'EXCELLENT', 'Perfect! Quick resolution and very professional service.');

-- =====================================================
-- FEEDBACK SYSTEM (Enhanced)
-- =====================================================

-- Insert sample feedback submissions (standalone feedback, not tied to tickets)
INSERT INTO feedback (name, email, rating, description) VALUES
('John Smith', 'john.smith@student.edu', 'EXCELLENT', 'The IT support team was incredibly helpful when I had issues with my student portal. They resolved my problem quickly and were very professional throughout the process.'),
('Sarah Johnson', 'sarah.j@student.edu', 'VERY_GOOD', 'Great service overall. The staff is knowledgeable and friendly. The only suggestion I have is to reduce the response time during peak hours.'),
('Mike Chen', 'mike.chen@student.edu', 'GOOD', 'The helpdesk system works well. I was able to submit my ticket easily and got a response within 24 hours. Could be improved with better mobile interface.'),
('Emily Davis', 'emily.davis@student.edu', 'FAIR', 'The service is okay, but sometimes the staff takes too long to respond. The system itself is user-friendly though.'),
('David Wilson', 'david.w@student.edu', 'EXCELLENT', 'Outstanding support! The team went above and beyond to help me with my enrollment issues. Highly recommend their services.'),
('Lisa Brown', 'lisa.brown@student.edu', 'VERY_GOOD', 'Very satisfied with the support provided. The staff is professional and the system is easy to use. Keep up the good work!'),
('Alex Martinez', 'alex.m@student.edu', 'GOOD', 'Good service overall. The response time could be better, but the quality of support is solid. The online system is intuitive.'),
('Rachel Green', 'rachel.green@student.edu', 'POOR', 'Had a very poor experience. The staff was unhelpful and the issue took weeks to resolve. Needs significant improvement.'),
('Tom Anderson', 'tom.a@student.edu', 'VERY_GOOD', 'Excellent support team. They were very patient and helped me understand the process. The system is well-designed.'),
('Jessica Lee', 'jessica.lee@student.edu', 'EXCELLENT', 'Amazing service! The staff was incredibly helpful and resolved my complex issue quickly. Very impressed with the professionalism.'),
('Kevin Taylor', 'kevin.t@student.edu', 'GOOD', 'The helpdesk system is user-friendly and the staff is generally helpful. Some minor improvements could be made to the interface.'),
('Amanda White', 'amanda.white@student.edu', 'FAIR', 'The service is adequate but could be improved. Response times are sometimes slow and the staff could be more proactive.'),
('Chris Rodriguez', 'chris.r@student.edu', 'VERY_GOOD', 'Great experience overall. The support team is knowledgeable and the system works well. Very satisfied with the service.'),
('Nicole Kim', 'nicole.kim@student.edu', 'EXCELLENT', 'Outstanding service! The team was very professional and resolved my issue quickly. I would definitely recommend this service.'),
('Ryan Thompson', 'ryan.t@student.edu', 'GOOD', 'Good service with room for improvement. The staff is helpful but the system could be more intuitive for new users.');

-- Insert sample resources
INSERT INTO resources (name, category, serial_number, status, location, description) VALUES
-- Electronics
('Dell Laptop XPS 13', 'Electronics', 'DL001', 'AVAILABLE', 'IT Storage Room', 'High-performance laptop with 16GB RAM and 512GB SSD'),
('Dell Laptop XPS 13', 'Electronics', 'DL002', 'IN_USE', 'Lab 1', 'High-performance laptop with 16GB RAM and 512GB SSD'),
('MacBook Pro 14"', 'Electronics', 'MB001', 'AVAILABLE', 'IT Storage Room', 'Apple MacBook Pro with M1 chip, 16GB RAM'),
('Epson Projector', 'Electronics', 'EP001', 'AVAILABLE', 'Conference Room A', 'Full HD projector with wireless connectivity'),
('Epson Projector', 'Electronics', 'EP002', 'UNDER_MAINTENANCE', 'Maintenance Room', 'Full HD projector - lamp needs replacement'),
('iPad Pro 12.9"', 'Electronics', 'IP001', 'AVAILABLE', 'IT Storage Room', 'Latest iPad Pro with Apple Pencil support'),
('Wireless Microphone', 'Electronics', 'WM001', 'AVAILABLE', 'Audio Equipment Room', 'Professional wireless microphone system'),

-- Software
('Adobe Creative Suite License', 'Software', 'ACS001', 'AVAILABLE', 'Digital License', 'Full Adobe Creative Cloud license for 1 year'),
('Microsoft Office 365', 'Software', 'MS001', 'AVAILABLE', 'Digital License', 'Office 365 Business Premium license'),
('AutoCAD License', 'Software', 'AC001', 'IN_USE', 'Digital License', 'Professional AutoCAD license for engineering'),
('MATLAB License', 'Software', 'ML001', 'AVAILABLE', 'Digital License', 'MATLAB with Simulink for mathematical computing'),

-- Furniture
('Adjustable Desk', 'Furniture', 'AD001', 'AVAILABLE', 'Storage Room B', 'Height-adjustable standing desk'),
('Ergonomic Chair', 'Furniture', 'EC001', 'AVAILABLE', 'Storage Room B', 'Ergonomic office chair with lumbar support'),
('Whiteboard Mobile', 'Furniture', 'WB001', 'AVAILABLE', 'Storage Room B', 'Mobile whiteboard on wheels'),

-- Lab Equipment
('3D Printer', 'Lab Equipment', '3DP001', 'AVAILABLE', 'Maker Lab', 'Professional 3D printer with PLA/ABS support'),
('Oscilloscope', 'Lab Equipment', 'OSC001', 'AVAILABLE', 'Electronics Lab', 'Digital oscilloscope 100MHz bandwidth'),
('Multimeter', 'Lab Equipment', 'MM001', 'AVAILABLE', 'Electronics Lab', 'Digital multimeter with data logging'),
('Arduino Kit', 'Lab Equipment', 'ARD001', 'AVAILABLE', 'Electronics Lab', 'Complete Arduino starter kit with sensors');

-- Insert sample resource requests
INSERT INTO resource_requests (user_id, resource_id, purpose, duration_days, status, admin_id, admin_comments, approved_date) VALUES
-- Approved requests
(1, 2, 'Final year project development', 30, 'APPROVED', 11, 'Approved for academic project', '2024-10-01 10:00:00'),
(2, 10, 'CAD design coursework', 14, 'APPROVED', 11, 'Approved for coursework', '2024-10-01 11:00:00'),

-- Pending requests
(3, 1, 'Research presentation preparation', 7, 'PENDING', NULL, NULL, NULL),
(1, 4, 'Conference presentation', 3, 'PENDING', NULL, NULL, NULL),
(2, 16, 'Engineering project prototyping', 21, 'PENDING', NULL, NULL, NULL),

-- Rejected requests
(3, 5, 'Personal project', 10, 'REJECTED', 12, 'Not approved for personal use', '2024-09-30 15:00:00');

-- Insert sample issue reports
INSERT INTO issue_reports (user_id, resource_id, issue_description, status, admin_comments, resolved_date) VALUES
-- Open issues
(1, 2, 'Laptop battery drains very quickly, only lasts 2 hours', 'OPEN', NULL, NULL),
(2, 10, 'AutoCAD crashes frequently when opening large files', 'IN_PROGRESS', 'Investigating compatibility issues', NULL),

-- Resolved issues
(3, 4, 'Projector display is blurry and colors are washed out', 'RESOLVED', 'Cleaned lens and calibrated display settings', '2024-10-01 14:00:00');

-- Insert sample audit logs
INSERT INTO audit_logs (action, performed_by, target_user, details, ip_address) VALUES
('USER_LOGIN', 'student1', NULL, 'User logged in successfully', '192.168.1.100'),
('TICKET_CREATED', 'student1', NULL, 'Created new ticket: Cannot access LMS', '192.168.1.100'),
('TICKET_ASSIGNED', 'admin1', 'itsupport1', 'Assigned ticket #1 to IT support', '192.168.1.101'),
('TICKET_STATUS_UPDATED', 'itsupport1', NULL, 'Updated ticket #1 status to IN_PROGRESS', '192.168.1.102'),
('USER_ROLE_UPDATED', 'superadmin', 'faculty1', 'Updated user role from FACULTY to ADMIN', '192.168.1.103'),
('TICKET_RESOLVED', 'itsupport1', NULL, 'Resolved ticket #3 for student3', '192.168.1.102'),
('FEEDBACK_SUBMITTED', 'student3', NULL, 'Submitted feedback for ticket #3 with rating 5', '192.168.1.100'),
('FEEDBACK_SUBMITTED', 'john.smith', NULL, 'Submitted standalone feedback with rating EXCELLENT', '192.168.1.104'),
('FEEDBACK_SUBMITTED', 'sarah.j', NULL, 'Submitted standalone feedback with rating VERY_GOOD', '192.168.1.105'),
('RESOURCE_REQUESTED', 'student1', NULL, 'Requested Dell Laptop XPS 13 for project development', '192.168.1.100'),
('RESOURCE_APPROVED', 'admin1', 'student1', 'Approved laptop request for student1', '192.168.1.101'),
('ISSUE_REPORTED', 'student1', NULL, 'Reported battery issue with laptop DL002', '192.168.1.100'),
('USER_LOGOUT', 'student1', NULL, 'User logged out successfully', '192.168.1.100');

-- =====================================================
-- EVENT MANAGEMENT SYSTEM
-- =====================================================

-- Insert sample events
INSERT INTO events (event_name, event_date, event_time, hall_room_number, capacity, available_slots, description, created_by_id) VALUES
('Welcome Week Orientation', '2024-10-15', '09:00:00', 'Main Auditorium', 200, 200, 'Welcome new students to the university with orientation sessions, campus tours, and meet-and-greet activities.', 6),
('Career Fair 2024', '2024-10-20', '10:00:00', 'Exhibition Hall A', 150, 150, 'Annual career fair featuring top companies and organizations. Network with recruiters and explore job opportunities.', 6),
('Tech Talk: AI in Education', '2024-10-25', '14:00:00', 'Lecture Hall B-101', 80, 80, 'Expert presentation on artificial intelligence applications in modern education. Q&A session included.', 7),
('Cultural Night', '2024-10-30', '18:00:00', 'Student Center', 120, 120, 'Celebrate diversity with cultural performances, food, and music from around the world.', 6),
('Study Skills Workshop', '2024-11-05', '15:30:00', 'Library Conference Room', 30, 30, 'Learn effective study techniques, time management, and exam preparation strategies.', 8),
('Sports Tournament Finals', '2024-11-10', '16:00:00', 'Sports Complex', 100, 100, 'Championship matches for basketball, volleyball, and badminton tournaments.', 6),
('Research Symposium', '2024-11-15', '09:30:00', 'Science Building A-201', 60, 60, 'Student research presentations and academic discussions on latest findings.', 7),
('Alumni Networking Event', '2024-11-20', '17:00:00', 'Business School Hall', 90, 90, 'Connect with successful alumni and learn about career paths and industry insights.', 6),
('Hackathon 2024', '2024-11-25', '08:00:00', 'Computer Lab C-301', 50, 50, '48-hour coding competition with prizes for innovative software solutions.', 7),
('Graduation Ceremony Prep', '2024-12-01', '10:00:00', 'Main Auditorium', 300, 300, 'Information session for graduating students about ceremony procedures and requirements.', 6);

-- Insert sample bookings
INSERT INTO bookings (event_id, user_id) VALUES
(1, 1), (1, 2), (1, 3), -- Welcome Week bookings
(2, 1), (2, 3), -- Career Fair bookings
(3, 2), (3, 3), -- Tech Talk bookings
(4, 1), (4, 2), (4, 3), -- Cultural Night bookings
(5, 1), (5, 2), -- Study Skills Workshop bookings
(6, 2), (6, 3), -- Sports Tournament bookings
(7, 1), (7, 3), -- Research Symposium bookings
(8, 1), (8, 2), -- Alumni Networking bookings
(9, 2), (9, 3), -- Hackathon bookings
(10, 1), (10, 2), (10, 3); -- Graduation Ceremony Prep bookings

-- Update available slots for events with bookings
UPDATE events SET available_slots = capacity - (
    SELECT COUNT(*) FROM bookings WHERE event_id = events.id AND active = 1
) WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Show database structure
SELECT 'helpdesk database created successfully' as status;

-- Show table counts
SELECT 
    'users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'faqs', COUNT(*) FROM faqs
UNION ALL
SELECT 'tickets', COUNT(*) FROM tickets
UNION ALL
SELECT 'feedback', COUNT(*) FROM feedback
UNION ALL
SELECT 'events', COUNT(*) FROM events
UNION ALL
SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL
SELECT 'audit_logs', COUNT(*) FROM audit_logs;

-- Show user roles distribution (matching UserRole.java)
SELECT role, COUNT(*) as user_count FROM users GROUP BY role ORDER BY role;

-- Show ticket status distribution (matching TicketStatus.java)
SELECT status, COUNT(*) as ticket_count FROM tickets GROUP BY status ORDER BY status;

-- Show ticket categories
SELECT category, COUNT(*) as ticket_count FROM tickets GROUP BY category ORDER BY category;

-- Show FAQ categories
SELECT category, COUNT(*) as faq_count FROM faqs GROUP BY category ORDER BY category;

-- Show feedback rating distribution
SELECT rating, COUNT(*) as feedback_count FROM feedback GROUP BY rating ORDER BY 
    CASE rating 
        WHEN 'EXCELLENT' THEN 1 
        WHEN 'VERY_GOOD' THEN 2 
        WHEN 'GOOD' THEN 3 
        WHEN 'FAIR' THEN 4 
        WHEN 'POOR' THEN 5 
    END;

-- Show event statistics
SELECT 
    'Total Events' as metric, COUNT(*) as count FROM events WHERE active = 1
UNION ALL
SELECT 'Upcoming Events', COUNT(*) FROM events WHERE event_date >= CURDATE() AND active = 1
UNION ALL
SELECT 'Total Bookings', COUNT(*) FROM bookings WHERE active = 1
UNION ALL
SELECT 'Events with Bookings', COUNT(DISTINCT event_id) FROM bookings WHERE active = 1;

SELECT 'SE2030 HelpDesk database setup completed successfully!' as message,
       'All tables created with proper relationships and comprehensive seed data inserted.' as details,
       'Database: helpdesk' as database_name,
       'Enhanced FAQ system with 8 categories and 32 FAQs ready for admin and user access.' as faq_system,
       'Complete feedback system with 15 sample submissions and rating analytics ready for admin management and user submission.' as feedback_system,
       'Full event management system with 10 sample events, booking functionality, and calendar integration ready for admin event management and user booking.' as event_system;
