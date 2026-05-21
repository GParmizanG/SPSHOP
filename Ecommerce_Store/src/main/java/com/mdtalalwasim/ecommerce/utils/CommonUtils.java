package com.mdtalalwasim.ecommerce.utils;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtils {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public Boolean sendEmail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messagehelper = new MimeMessageHelper(message);
		
		messagehelper.setFrom("artjomupatoka@gmail.com", "SPSHOP Support");
		messagehelper.setTo(reciepentEmail);
		
		String content = "<p>Hello, </p>" + "<p> You have requested to reset your password. </p> "
						 + "<p>Please Click the link to change your password:</p>"+ "<p><a href=\""+ url + "\">Change my password</a></p>";
							
		messagehelper.setSubject("Password Reset for SPSHOP");
		messagehelper.setText(content, true);
		
		mailSender.send(message);
		
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {
		String fullUrl = request.getRequestURL().toString();
		String servletPath = request.getServletPath();
		if ("/".equals(servletPath)) {
			return fullUrl.endsWith("/") ? fullUrl.substring(0, fullUrl.length() - 1) : fullUrl;
		}
		return fullUrl.replace(servletPath, "");
	}

	
}
