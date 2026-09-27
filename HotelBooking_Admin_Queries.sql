USE hotelbooking;

-- View all customers
SELECT
    customer_id,
    name,
    phone,
    email,
    email_verified
FROM customers;

-- View all bookings with customer details
SELECT
    b.booking_id,
    c.name AS customer_name,
    c.phone,
    c.email,
    b.room_number,
    b.check_in,
    b.check_out,
    b.guests,
    b.number_of_days,
    b.total_bill,
    b.booking_status
FROM bookings b
JOIN customers c
    ON b.customer_id = c.customer_id;

-- View all rooms
SELECT * FROM rooms;

-- View currently booked rooms
SELECT
    b.booking_id,
    b.room_number,
    c.name AS customer_name,
    b.check_in,
    b.check_out,
    b.guests,
    b.total_bill,
    b.booking_status
FROM bookings b
JOIN customers c
    ON b.customer_id = c.customer_id
WHERE b.booking_status = 'Booked';