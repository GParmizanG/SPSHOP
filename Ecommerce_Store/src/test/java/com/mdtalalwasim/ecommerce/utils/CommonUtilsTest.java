package com.mdtalalwasim.ecommerce.utils;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("CommonUtils Unit Tests")
class CommonUtilsTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private CommonUtils commonUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("sendEmail: должен успешно отправлять письмо при корректных параметрах")
    void sendEmail_Success() throws Exception {
        Boolean result = commonUtils.sendEmail("http://localhost:8080/reset", "user@example.com");
        assertThat(result).isTrue();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("generateUrl: должен корректно извлекать базовый URL из запроса")
    void generateUrl_ValidRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/reset-password"));
        when(request.getServletPath()).thenReturn("/reset-password");

        String baseUrl = CommonUtils.generateUrl(request);
        assertThat(baseUrl).isEqualTo("http://localhost:8080");
    }

    @Test
    @DisplayName("generateUrl: должен корректно работать с корневым URL")
    void generateUrl_RootUrl() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
        when(request.getServletPath()).thenReturn("/");

        String baseUrl = CommonUtils.generateUrl(request);
        assertThat(baseUrl).isEqualTo("http://localhost:8080");
    }

    @Test
    @DisplayName("generateUrl: должен обрабатывать URL с суб-путями")
    void generateUrl_SubPathUrl() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://spshop.ee/api/payment/intent"));
        when(request.getServletPath()).thenReturn("/api/payment/intent");

        String baseUrl = CommonUtils.generateUrl(request);
        assertThat(baseUrl).isEqualTo("https://spshop.ee");
    }

    @Test
    @DisplayName("generateUrl: должен работать с портами по умолчанию")
    void generateUrl_DefaultPort() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getServletPath()).thenReturn("/test");

        String baseUrl = CommonUtils.generateUrl(request);
        assertThat(baseUrl).isEqualTo("http://localhost");
    }
}
