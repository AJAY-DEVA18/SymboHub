package com.symbohub.symbohub_backend.service;

import com.symbohub.symbohub_backend.dto.BrochureDTO;
import com.symbohub.symbohub_backend.dto.CollegeDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendCollegeRegistrationConfirmation(CollegeDTO college) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(college.getEmail());
            helper.setSubject("College Registration Confirmation - SymboHUB");
            helper.setFrom("noreply@symbohub.com");

            Context context = new Context();
            context.setVariable("collegeName", college.getName());
            context.setVariable("collegeEmail", college.getEmail());
            context.setVariable("status", college.getStatus());
            context.setVariable("registrationDate", college.getRegistrationDate());
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("college-registration-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Registration confirmation email sent to: {}", college.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send registration confirmation email to: {}", college.getEmail(), e);
        }
    }

    @Async
    public void sendCollegeApprovalNotification(CollegeDTO college) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(college.getEmail());
            helper.setSubject("🎉 College Approved - SymboHUB");
            helper.setFrom("noreply@symbohub.com");

            Context context = new Context();
            context.setVariable("collegeName", college.getName());
            context.setVariable("approvalDate", college.getApprovalDate() != null ?
                    college.getApprovalDate() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            context.setVariable("loginUrl", "http://localhost:3000/college-login");
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("college-approval-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Approval notification email sent to: {}", college.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send approval notification email to: {}", college.getEmail(), e);
        }
    }

    @Async
    public void sendCollegeRejectionNotification(CollegeDTO college, String rejectionReason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(college.getEmail());
            helper.setSubject("College Registration Update - SymboHUB");
            helper.setFrom("noreply@symbohub.com");

            Context context = new Context();
            context.setVariable("collegeName", college.getName());
            context.setVariable("rejectionReason", rejectionReason);
            context.setVariable("registrationDate", college.getRegistrationDate());
            context.setVariable("supportEmail", "support@symbohub.com");
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("college-rejection-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Rejection notification email sent to: {}", college.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send rejection notification email to: {}", college.getEmail(), e);
        }
    }

    @Async
    public void sendBrochureNotification(String toEmail, String brochureTitle,
                                         String senderDepartment, String description) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("New Brochure: " + brochureTitle);
            helper.setFrom("noreply@symbohub.com");

            Context context = new Context();
            context.setVariable("brochureTitle", brochureTitle);
            context.setVariable("senderDepartment", senderDepartment);
            context.setVariable("description", description);
            context.setVariable("viewUrl", "http://localhost:3000/brochures");
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("brochure-notification-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Brochure notification email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send brochure notification email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - SymboHUB");
            helper.setFrom("noreply@symbohub.com");

            Context context = new Context();
            context.setVariable("resetLink", "http://localhost:3000/reset-password?token=" + resetToken);
            context.setVariable("expiryTime", "24 hours");
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("password-reset-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String userName, String userType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to SymboHUB!");
            helper.setFrom("noreply@symbohub.com");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("userType", userType);
            context.setVariable("loginUrl", "http://localhost:3000/login");
            context.setVariable("dashboardUrl", "http://localhost:3000/dashboard");
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("welcome-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }
}