CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    role ENUM('CUSTOMER', 'STYLIST', 'STAFF', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE','BLOCKED') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE stylist_profile (
    user_id INT PRIMARY KEY,
    experience_years INT,
    level ENUM('Junior','Senior','Master'),
    specialties VARCHAR(255),
    intro VARCHAR(255),         -- Thêm dòng giới thiệu ngắn
    bio TEXT,
    served_customers INT DEFAULT 0,
    salary DECIMAL(12,2) DEFAULT 0,
    commission_percent DECIMAL(5,2) DEFAULT 10.00,
    FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    intro VARCHAR(255),         -- Thêm dòng giới thiệu ngắn
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration INT NOT NULL,
    image VARCHAR(255),
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'
);
CREATE TABLE service_stylist (
    service_id INT,
    stylist_id INT,
    PRIMARY KEY (service_id, stylist_id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (stylist_id) REFERENCES users(id)
);
CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    stylist_id INT NOT NULL,
    booking_time DATETIME NOT NULL,
    total_amount DECIMAL(12,2),
    status ENUM('PENDING','CONFIRMED','CANCELLED','DONE','NO_SHOW') DEFAULT 'PENDING',
    payment_method ENUM('OFFLINE','ONLINE') DEFAULT 'OFFLINE',
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (stylist_id) REFERENCES users(id)
);
CREATE TABLE booking_service (
    booking_id INT,
    service_id INT,
    price DECIMAL(10,2), -- Lưu giá tại thời điểm đặt
    duration INT,        -- Lưu thời lượng tại thời điểm đặt
    PRIMARY KEY (booking_id, service_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);
CREATE TABLE feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    customer_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    reply TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (customer_id) REFERENCES users(id)
);
CREATE TABLE loyalty_points (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    points INT,
    type ENUM('EARN','REDEEM'),
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id)
);
CREATE TABLE support_tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    type ENUM('booking','payment','account','technical','suggestion','other'),
    title VARCHAR(100),
    message TEXT,
    image VARCHAR(255),
    status ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') DEFAULT 'OPEN',
    reply TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (username, password, full_name, email, phone, avatar, role, status, created_at) VALUES
('admin1',    'adminpass',  'Phạm Quản Trị',   'admin@hairsalon.vn',   '0912345678', 'admin1.jpg',    'ADMIN',   'ACTIVE',   NOW()),
('manager1',  'managerpass','Nguyễn Quản Lý',  'manager@hairsalon.vn', '0901234567', 'manager1.jpg',  'MANAGER', 'ACTIVE',   NOW()),
('staff1',    'staffpass',  'Trần Thị Nhân',   'staff@hairsalon.vn',   '0938765432', 'staff1.jpg',    'STAFF',   'ACTIVE',   NOW()),
('stylist_hoai', 'stylistpass', 'Trần Thị Hoài',  'hoai@hairsalon.vn',  '0912121212', 'stylist_hoai.jpg', 'STYLIST', 'ACTIVE', NOW()),
('stylist_kiet', 'stylistpass', 'Lê Quốc Kiệt',   'kiet@hairsalon.vn',  '0912555222', 'stylist_kiet.jpg', 'STYLIST', 'ACTIVE', NOW()),
('khach1',    '123456',     'Nguyễn Văn A',    'nva@gmail.com',        '0912333444', 'khach1.jpg',    'CUSTOMER','ACTIVE',   NOW()),
('khach2',    '123456',     'Trần Thị Mai',    'mai@gmail.com',        '0909222333', 'khach2.jpg',    'CUSTOMER','ACTIVE',   NOW());


-- Giả sử id của stylist_hoai là 4, stylist_kiet là 5
INSERT INTO stylist_profile (user_id, experience_years, level, specialties, intro, bio, served_customers, salary, commission_percent)
VALUES
(4, 10, 'Senior', 'Uốn, Nhuộm', 'Chuyên gia tạo kiểu tóc nữ', '10 năm kinh nghiệm làm tóc Hàn Quốc, top stylist tại Salon.', 215, 8000000, 15.00),
(5, 2, 'Junior', 'Gội đầu, Massage', 'Tay nghề trẻ, nhiệt tình', 'Đam mê nghề tóc và học hỏi không ngừng.', 36, 5000000, 10.00);
INSERT INTO services (name, intro, description, price, duration, image, status) VALUES
('Uốn tóc', 'Uốn nhẹ, vào nếp tự nhiên', 'Dịch vụ uốn tóc chuyên nghiệp với hóa chất cao cấp...', 250000, 45, 'uon-toc.jpg', 'ACTIVE'),
('Nhuộm tóc', 'Nhuộm lên màu chuẩn', 'Nhuộm tóc với bảng màu đa dạng...', 350000, 60, 'nhuom-toc.jpg', 'ACTIVE'),
('Gội đầu thư giãn', 'Gội & massage thư giãn', 'Gội đầu với tinh dầu, massage da đầu nhẹ nhàng...', 80000, 20, 'goi-dau.jpg', 'ACTIVE'),
('Cắt tạo kiểu', 'Cắt theo xu hướng', 'Cắt tóc theo phong cách hiện đại, phù hợp khuôn mặt...', 120000, 30, 'cat-toc.jpg', 'ACTIVE');
-- Uốn tóc, Nhuộm tóc: Hoài & Kiệt đều làm được, Gội đầu: chỉ Kiệt
INSERT INTO service_stylist (service_id, stylist_id) VALUES
(1, 4), (1, 5),
(2, 4), (2, 5),
(3, 5),
(4, 4), (4, 5);
INSERT INTO bookings (customer_id, stylist_id, booking_time, total_amount, status, payment_method, note, created_at) VALUES
(6, 4, '2025-08-03 14:30:00', 350000, 'DONE', 'ONLINE', 'Cần uốn tóc + hấp', NOW()),
(7, 5, '2025-08-04 10:00:00', 120000, 'CONFIRMED', 'OFFLINE', '', NOW());
-- Booking 1: Uốn tóc, Booking 2: Cắt tạo kiểu
INSERT INTO booking_service (booking_id, service_id, price, duration) VALUES
(1, 1, 250000, 45),  -- Uốn tóc
(1, 3, 80000, 20),   -- Gội đầu
(2, 4, 120000, 30);  -- Cắt tạo kiểu
INSERT INTO feedback (booking_id, customer_id, rating, comment, reply, created_at) VALUES
(1, 6, 5, 'Stylist làm tóc rất đẹp, nhẹ nhàng dễ thương ❤️', 'Cảm ơn bạn đã tin tưởng HairSalon!', NOW()),
(2, 7, 2, 'Làm tóc không đúng kiểu mong muốn', NULL, NOW());
INSERT INTO loyalty_points (customer_id, points, type, description, created_at) VALUES
(6, 100, 'EARN', 'Đặt dịch vụ lần đầu', NOW()),
(6, 30, 'EARN', 'Feedback 5 sao', NOW()),
(6, 50, 'REDEEM', 'Đổi voucher giảm giá', NOW());
INSERT INTO support_tickets (user_id, type, title, message, image, status, reply, created_at) VALUES
(6, 'booking', 'Không thấy stylist đến', 'Đặt lịch 14h nhưng stylist không xuất hiện...', 'issue1.jpg', 'OPEN', NULL, NOW()),
(7, 'suggestion', 'Góp ý giao diện', 'Nên có nút “Hủy lịch” nhanh hơn...', NULL, 'RESOLVED', 'Cảm ơn bạn đã góp ý, chúng tôi sẽ bổ sung.', NOW());


