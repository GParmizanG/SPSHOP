package com.mdtalalwasim.ecommerce.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mdtalalwasim.ecommerce.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@Service
public class CommonServiceImpl implements CommonService {

	@Override
	public boolean removeSessionMessage() {
		try {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();
				if (request != null) {
					HttpSession session = request.getSession(false);
					if (session != null) {
						session.removeAttribute("successMsg");
						session.removeAttribute("errorMsg");
					}
				}
			}
		} catch (Exception e) {
			// Safely ignore or log to avoid template rendering crash
		}
		return true;
	}

}
