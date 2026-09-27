package com.hotel.hotelbooking.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.hotel.hotelbooking.service.OtpService;
import com.hotel.hotelbooking.entity.Booking;
import com.hotel.hotelbooking.entity.Customer;
import com.hotel.hotelbooking.entity.Room;
import com.hotel.hotelbooking.repository.BookingRepository;
import com.hotel.hotelbooking.repository.CustomerRepository;
import com.hotel.hotelbooking.repository.RoomRepository;
import com.hotel.hotelbooking.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class HomeController {

    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public HomeController(RoomRepository roomRepository,
                          CustomerRepository customerRepository,
                          BookingRepository bookingRepository,
                          EmailService emailService,
                          OtpService otpService,
                          PasswordEncoder passwordEncoder) {
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null) {
            model.addAttribute("error", "No account found with this email.");
            return "login";
        }

        if (!customer.isEmailVerified()) {
            model.addAttribute("error", "Please verify your email before logging in.");
            return "login";
        }

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            model.addAttribute("error", "Incorrect email or password.");
            return "login";
        }

        session.setAttribute("loginEmail", email);
        session.setAttribute("customerEmail", email);

        return "redirect:/home";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                 Model model,
                                 HttpSession session) {

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null || !customer.isEmailVerified()) {
            model.addAttribute("error", "No verified account found with this email.");
            return "forgot-password";
        }

        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
            session.setAttribute("forgotPasswordEmail", email);
            return "redirect:/forgot-password-verify";
        } catch (Exception e) {
            model.addAttribute("error", "We couldn't send a verification code. Please check the email address and try again.");
            return "forgot-password";
        }
    }

    @GetMapping("/forgot-password-verify")
    public String forgotPasswordVerifyPage(HttpSession session) {
        String email = (String) session.getAttribute("forgotPasswordEmail");

        if (email == null) {
            return "redirect:/forgot-password";
        }

        return "forgot-password-verify";
    }

    @PostMapping("/forgot-password-verify")
    public String verifyForgotPasswordOtp(@RequestParam String otp,
                                          Model model,
                                          HttpSession session) {

        String email = (String) session.getAttribute("forgotPasswordEmail");

        if (email == null) {
            return "redirect:/forgot-password";
        }

        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "Invalid or expired OTP.");
            return "forgot-password-verify";
        }

        session.setAttribute("forgotPasswordVerified", true);

        return "redirect:/reset-password";
    }

    @PostMapping("/resend-forgot-password-otp")
    public String resendForgotPasswordOtp(Model model,
                                          HttpSession session) {

        String email = (String) session.getAttribute("forgotPasswordEmail");

        if (email == null) {
            return "redirect:/forgot-password";
        }

        if (!otpService.canResend(email)) {
            long seconds = otpService.getRemainingResendSeconds(email);
            model.addAttribute("error", "Please wait " + seconds + " seconds before requesting another OTP.");
            return "forgot-password-verify";
        }

        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
            model.addAttribute("success", "A new verification code has been sent to your email.");
        } catch (Exception e) {
            model.addAttribute("error", "We couldn't send a new verification code. Please try again.");
        }

        return "forgot-password-verify";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session) {
        Boolean verified = (Boolean) session.getAttribute("forgotPasswordVerified");

        if (!Boolean.TRUE.equals(verified)) {
            return "redirect:/forgot-password";
        }

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String password,
                                @RequestParam String confirmPassword,
                                Model model,
                                HttpSession session) {

        Boolean verified = (Boolean) session.getAttribute("forgotPasswordVerified");
        String email = (String) session.getAttribute("forgotPasswordEmail");

        if (!Boolean.TRUE.equals(verified) || email == null) {
            return "redirect:/forgot-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "reset-password";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "reset-password";
        }

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null) {
            session.removeAttribute("forgotPasswordEmail");
            session.removeAttribute("forgotPasswordVerified");
            return "redirect:/forgot-password";
        }

        customer.setPassword(passwordEncoder.encode(password));
        customerRepository.save(customer);

        session.removeAttribute("forgotPasswordEmail");
        session.removeAttribute("forgotPasswordVerified");

        model.addAttribute("success", "Password reset successfully. Please login.");

        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           Model model,
                           HttpSession session) {

        Customer existingCustomer = customerRepository.findByEmail(email).orElse(null);

        if (existingCustomer != null && existingCustomer.isEmailVerified()) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            model.addAttribute("error", "We couldn't send a verification code to this email. Please check the email address and try again.");
            return "register";
        }

        session.setAttribute("registerEmail", email);

        return "redirect:/register-verify";
    }

    @GetMapping("/register-verify")
    public String registerVerifyPage(HttpSession session) {

        String email = (String) session.getAttribute("registerEmail");

        if (email == null) {
            return "redirect:/register";
        }

        return "register-verify";
    }

    @PostMapping("/register-verify")
    public String verifyRegistrationOtp(@RequestParam String otp,
                                        Model model,
                                        HttpSession session) {

        String email = (String) session.getAttribute("registerEmail");

        if (email == null) {
            return "redirect:/register";
        }

        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", "Invalid or expired OTP.");
            return "register-verify";
        }

        session.setAttribute("registrationVerified", true);

        return "redirect:/register-details";
    }

    @PostMapping("/resend-register-otp")
    public String resendRegisterOtp(Model model,
                                    HttpSession session) {

        String email = (String) session.getAttribute("registerEmail");

        if (email == null) {
            return "redirect:/register";
        }

        if (!otpService.canResend(email)) {
            long seconds = otpService.getRemainingResendSeconds(email);
            model.addAttribute("error", "Please wait " + seconds + " seconds before requesting another OTP.");
            return "register-verify";
        }

        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
            model.addAttribute("success", "A new verification code has been sent to your email.");
        } catch (Exception e) {
            model.addAttribute("error", "We couldn't send a new verification code. Please try again.");
        }

        return "register-verify";
    }

    @GetMapping("/register-details")
    public String registerDetailsPage(HttpSession session) {
        Boolean verified = (Boolean) session.getAttribute("registrationVerified");

        if (!Boolean.TRUE.equals(verified)) {
            return "redirect:/register";
        }

        return "register-details";
    }

    @PostMapping("/register-details")
    public String registerDetails(@RequestParam String name,
                                  @RequestParam String phone,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  Model model,
                                  HttpSession session) {

        Boolean verified = (Boolean) session.getAttribute("registrationVerified");
        String email = (String) session.getAttribute("registerEmail");

        if (!Boolean.TRUE.equals(verified) || email == null) {
            return "redirect:/register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register-details";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "register-details";
        }

        Customer existingCustomer = customerRepository.findByEmail(email).orElse(null);

        if (existingCustomer != null && existingCustomer.isEmailVerified()) {
            session.removeAttribute("registerEmail");
            session.removeAttribute("registrationVerified");
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        Customer customer;

        if (existingCustomer != null) {
            customer = existingCustomer;
        } else {
            customer = new Customer();
        }

        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setEmailVerified(true);

        customerRepository.save(customer);

        session.removeAttribute("registerEmail");
        session.removeAttribute("registrationVerified");

        model.addAttribute("success", "Account created successfully. Please login.");

        return "login";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session) {
        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail == null) {
            return "redirect:/";
        }

        return "home";
    }

    @GetMapping("/account")
    public String accountPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loginEmail");

        if (email == null) {
            return "redirect:/";
        }

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null) {
            return "redirect:/";
        }

        model.addAttribute("customer", customer);

        return "account";
    }

    @PostMapping("/account")
    public String updateAccount(@RequestParam String name,
                                @RequestParam String phone,
                                Model model,
                                HttpSession session) {

        String email = (String) session.getAttribute("loginEmail");

        if (email == null) {
            return "redirect:/";
        }

        name = name.trim();
        phone = phone.trim();

        if (name.isEmpty()) {
            model.addAttribute("error", "Name cannot be empty.");
            return accountPage(session, model);
        }

        if (phone.isEmpty()) {
            model.addAttribute("error", "Phone number cannot be empty.");
            return accountPage(session, model);
        }

        if (name.length() > 100) {
            model.addAttribute("error", "Name cannot be longer than 100 characters.");
            return accountPage(session, model);
        }

        if (phone.length() > 15) {
            model.addAttribute("error", "Phone number cannot be longer than 15 characters.");
            return accountPage(session, model);
        }

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null) {
            session.invalidate();
            return "redirect:/";
        }

        customer.setName(name);
        customer.setPhone(phone);
        customerRepository.save(customer);

        model.addAttribute("customer", customer);
        model.addAttribute("success", "Account information updated successfully.");

        return "account";
    }

    @GetMapping("/delete-account")
    public String deleteAccountPage(HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");

        if (email == null) {
            return "redirect:/";
        }

        return "delete-account";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@RequestParam String password,
                                Model model,
                                HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");

        if (email == null) {
            return "redirect:/";
        }

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null) {
            session.invalidate();
            return "redirect:/";
        }

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            model.addAttribute("error", "Incorrect password. Account was not deleted.");
            return "delete-account";
        }

        List<Booking> bookings = bookingRepository.findByCustomerId(customer.getCustomerId());

        for (Booking booking : bookings) {
            if ("Booked".equals(booking.getBookingStatus())) {
                Room room = roomRepository.findById(booking.getRoomNumber()).orElse(null);

                if (room != null) {
                    room.setAvailable(true);
                    roomRepository.save(room);
                }
            }

            bookingRepository.delete(booking);
        }

        customerRepository.delete(customer);

        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/rooms")
    public String roomsPage(Model model, HttpSession session) {
        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail == null) {
            return "redirect:/";
        }

        List<Room> rooms = roomRepository.findAll();
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

    @GetMapping("/room/{roomNumber}")
    public String roomDetails(@PathVariable int roomNumber, Model model, HttpSession session) {
        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail == null) {
            return "redirect:/";
        }

        Room room = roomRepository.findById(roomNumber).orElse(null);

        if (room == null) {
            return "redirect:/rooms";
        }

        model.addAttribute("room", room);
        return "room-details";
    }

    @GetMapping("/book/{roomNumber}")
    public String bookingPage(@PathVariable int roomNumber,
                              Model model,
                              HttpSession session) {

        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail == null) {
            return "redirect:/";
        }

        Room room = roomRepository.findById(roomNumber).orElse(null);

        if (room == null || !room.isAvailable()) {
            return "redirect:/rooms";
        }

        model.addAttribute("room", room);
        return "booking";
    }

    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loginEmail");

        if (email == null) {
            return "redirect:/";
        }

        Customer customer = customerRepository.findByEmail(email).orElse(null);

        if (customer == null) {
            model.addAttribute("bookings", List.of());
            return "my-bookings";
        }

        List<Booking> bookings = bookingRepository.findByCustomerId(customer.getCustomerId());

        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookings);

        return "my-bookings";
    }

    @PostMapping("/book")
    public String bookRoom(@RequestParam int roomNumber,
                           @RequestParam String name,
                           @RequestParam String phone,
                           @RequestParam LocalDate checkIn,
                           @RequestParam LocalDate checkOut,
                           @RequestParam int guests,
                           Model model,
                           HttpSession session) {

        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail == null) {
            return "redirect:/";
        }

        Room room = roomRepository.findById(roomNumber).orElse(null);

        if (room == null || !room.isAvailable()) {
            return "redirect:/rooms";
        }

        if (!checkOut.isAfter(checkIn)) {
            model.addAttribute("error", "Check-out date must be after check-in date.");
            model.addAttribute("room", room);
            return "booking";
        }

        if (guests < 1 || guests > room.getCapacity()) {
            model.addAttribute("error", "Number of guests exceeds room capacity.");
            model.addAttribute("room", room);
            return "booking";
        }

        long numberOfDays = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalBill = numberOfDays * room.getPrice();

        Customer customer = customerRepository.findByEmail(loginEmail).orElse(null);

        if (customer == null) {
            customer = new Customer();
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(loginEmail);
            customer = customerRepository.save(customer);
        } else {
            customer.setName(name);
            customer.setPhone(phone);
            customerRepository.save(customer);
        }

        session.setAttribute("customerEmail", loginEmail);

        Booking booking = new Booking();
        booking.setCustomerId(customer.getCustomerId());
        booking.setRoomNumber(roomNumber);
        booking.setNumberOfDays((int) numberOfDays);
        booking.setTotalBill(totalBill);
        booking.setBookingStatus("Booked");
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuests(guests);

        bookingRepository.save(booking);

        room.setAvailable(false);
        roomRepository.save(room);

        try {
            emailService.sendBookingConfirmationEmail(loginEmail, booking, customer, room);
        } catch (Exception e) {
            System.out.println("Booking confirmation email could not be sent: " + e.getMessage());
        }

        model.addAttribute("booking", booking);
        model.addAttribute("customer", customer);
        model.addAttribute("room", room);

        return "booking-confirmation";
    }

    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam int bookingId,
                                HttpSession session) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return "redirect:/my-bookings";
        }

        String loginEmail = (String) session.getAttribute("loginEmail");

        if (loginEmail == null) {
            return "redirect:/";
        }

        Customer customer = customerRepository.findByEmail(loginEmail).orElse(null);

        if (customer == null || booking.getCustomerId() != customer.getCustomerId()) {
            return "redirect:/my-bookings";
        }

        if ("Cancelled".equals(booking.getBookingStatus())) {
            return "redirect:/my-bookings";
        }

        booking.setBookingStatus("Cancelled");
        bookingRepository.save(booking);

        Room room = roomRepository.findById(booking.getRoomNumber()).orElse(null);

        if (room != null) {
            room.setAvailable(true);
            roomRepository.save(room);
        }

        return "redirect:/my-bookings";
    }

}