CREATE DATABASE IF NOT EXISTS hotelbooking;
USE hotelbooking;

CREATE TABLE IF NOT EXISTS rooms (
    room_number INT PRIMARY KEY,
    category VARCHAR(50),
    bed_type VARCHAR(100),
    capacity INT,
    price DOUBLE,
    spa BOOLEAN,
    pool BOOLEAN,
    balcony BOOLEAN,
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    room_number INT NOT NULL,
    number_of_days INT NOT NULL,
    total_bill DOUBLE NOT NULL,
    booking_status VARCHAR(20) DEFAULT 'Booked',
    check_in DATE,
    check_out DATE,
    guests INT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_number) REFERENCES rooms(room_number)
);

INSERT INTO rooms (room_number, category, bed_type, capacity, price, spa, pool, balcony, available) VALUES
(101, 'Economy Family', '2 Double Beds', 4, 1800, FALSE, FALSE, FALSE, TRUE),
(102, 'Economy Family', '2 Double Beds', 6, 2200, FALSE, FALSE, TRUE, TRUE),
(103, 'Standard Family', '2 Double Beds', 4, 2500, FALSE, FALSE, TRUE, TRUE),
(104, 'Standard Family', '2 Double Beds', 6, 2900, FALSE, FALSE, TRUE, TRUE),
(201, 'Deluxe Double', '1 King Bed', 2, 3000, FALSE, FALSE, TRUE, TRUE),
(202, 'Deluxe Family', '2 Double Beds', 4, 3500, TRUE, FALSE, TRUE, TRUE),
(203, 'Deluxe Family', '2 Double Beds', 6, 4000, TRUE, TRUE, TRUE, TRUE),
(204, 'Executive Family', '1 King + 2 Single Beds', 4, 4500, TRUE, FALSE, TRUE, TRUE),
(301, 'Executive Family', '1 King + 2 Single Beds', 6, 5000, TRUE, TRUE, TRUE, TRUE),
(302, 'Premium Suite', '1 King + 2 Single Beds', 4, 6000, TRUE, TRUE, TRUE, TRUE),
(401, 'Luxury Suite', '2 King Beds', 6, 7500, TRUE, TRUE, TRUE, TRUE),
(402, 'Presidential Suite', '2 King Beds', 6, 10000, TRUE, TRUE, TRUE, TRUE);