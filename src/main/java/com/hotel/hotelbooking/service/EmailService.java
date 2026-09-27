package com.hotel.hotelbooking.service;

import com.hotel.hotelbooking.entity.Booking;
import com.hotel.hotelbooking.entity.Customer;
import com.hotel.hotelbooking.entity.Room;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("HotelBooking Login OTP");
        message.setText("Your HotelBooking verification code is: " + otp + "\n\nThis OTP is valid for 5 minutes.");
        mailSender.send(message);
    }

    public void sendBookingConfirmationEmail(String to, Booking booking, Customer customer, Room room) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("HotelBooking - Booking Confirmation");

        String emailText =
                "Dear " + customer.getName() + ",\n\n" +
                        "Your hotel booking has been confirmed successfully.\n\n" +
                        "Booking Details\n" +
                        "-------------------------\n" +
                        "Booking ID: " + booking.getBookingId() + "\n" +
                        "Room Number: " + room.getRoomNumber() + "\n" +
                        "Room Category: " + room.getCategory() + "\n" +
                        "Check-in: " + booking.getCheckIn() + "\n" +
                        "Check-out: " + booking.getCheckOut() + "\n" +
                        "Guests: " + booking.getGuests() + "\n" +
                        "Number of Days: " + booking.getNumberOfDays() + "\n" +
                        "Total Bill: ₹" + booking.getTotalBill() + "\n" +
                        "Booking Status: " + booking.getBookingStatus() + "\n\n" +
                        "Thank you for choosing HotelBooking.\n\n" +
                        "Please keep this email for your records.";

        message.setText(emailText);

        mailSender.send(message);
    }

}
