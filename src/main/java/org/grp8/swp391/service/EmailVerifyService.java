package org.grp8.swp391.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailVerifyService {
    @Autowired
    private MailSender mailSender;

    public void sendEmailToUser(String to, String title, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(title);
        message.setText(text);
        mailSender.send(message);
    }


}
