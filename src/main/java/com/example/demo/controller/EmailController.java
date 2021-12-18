package com.example.demo.controller;

import com.example.demo.controller.dto.EmailDto;

import com.example.demo.entity.Employee;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RestController
@RequestMapping(value = {"/emails"})
public class EmailController {

    private final EmployeeRepository repository;

    private final JavaMailSender mailSender;

    EmailController(EmployeeRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    @PostMapping("/send-email/{id}")
    void sendEmail(@PathVariable Long id, @RequestBody EmailDto dto) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        String email = employee.getEmail();
        String konu = dto.getKonu();
        String icerik = dto.getIcerik();
        sendMail(email,konu,icerik);
    }

    public String sendMail(String emailAddress, String konu, String icerik) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        try {
            messageHelper.setTo(emailAddress);
            messageHelper.setText(icerik);
            messageHelper.setSubject(konu);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error...";
        }
        mailSender.send(mimeMessage);
        return "Mail Sent!";
    }
}
