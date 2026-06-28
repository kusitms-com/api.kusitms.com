package com.kusitms.website.domain.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String BANNER_URL = "https://kusitms-bucket.s3.ap-northeast-2.amazonaws.com/meetup/OG/456ed518-7489-4106-9d33-94c491ef358732__OG_.png";
    private static final String BRAND_COLOR = "#4A3AFF";

    private final JavaMailSender mailSender;

    @Async
    public void sendApprovalEmail(String toEmail, String name) {
        String subject = "[큐시즘] 회원가입이 승인되었습니다";
        String html = buildEmail(name,
                "회원가입 승인 완료",
                "큐시즘 회원가입이 승인되었습니다.<br>지금 바로 로그인하여 서비스를 이용하실 수 있습니다.",
                "로그인하기",
                "https://kusitms.com");
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendRejectionEmail(String toEmail, String name) {
        String subject = "[큐시즘] 회원가입 신청이 반려되었습니다";
        String html = buildEmail(name,
                "회원가입 반려 안내",
                "큐시즘 회원가입 신청이 반려되었습니다.<br>문의 사항이 있으시면 운영진에게 연락해 주세요.",
                null, null);
        sendHtmlEmail(toEmail, subject, html);
    }

    private void sendHtmlEmail(String toEmail, String subject, String html) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mime);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", toEmail, e);
        }
    }

    private String buildEmail(String name, String title, String body, String buttonText, String buttonUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head>");
        sb.append("<body style='margin:0;padding:0;background-color:#f4f4f7;font-family:-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f7;padding:40px 0;'><tr><td align='center'>");
        sb.append("<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);'>");

        // Banner
        sb.append("<tr><td style='padding:0;'>");
        sb.append("<img src='").append(BANNER_URL).append("' width='600' style='display:block;width:100%;height:auto;' alt='KUSITMS' />");
        sb.append("</td></tr>");

        // Title
        sb.append("<tr><td style='padding:32px 40px 0 40px;'>");
        sb.append("<h1 style='margin:0;font-size:22px;color:#1a1a1a;'>").append(title).append("</h1>");
        sb.append("</td></tr>");

        // Body
        sb.append("<tr><td style='padding:16px 40px 0 40px;'>");
        sb.append("<p style='margin:0;font-size:15px;line-height:1.7;color:#555555;'>");
        sb.append(name).append("님, 안녕하세요.<br><br>");
        sb.append(body);
        sb.append("</p></td></tr>");

        // Button
        if (buttonText != null && buttonUrl != null) {
            sb.append("<tr><td style='padding:28px 40px 0 40px;'>");
            sb.append("<a href='").append(buttonUrl).append("' style='display:inline-block;padding:12px 32px;background-color:").append(BRAND_COLOR);
            sb.append(";color:#ffffff;text-decoration:none;border-radius:8px;font-size:14px;font-weight:600;'>").append(buttonText).append("</a>");
            sb.append("</td></tr>");
        }

        // Footer
        sb.append("<tr><td style='padding:32px 40px;'>");
        sb.append("<hr style='border:none;border-top:1px solid #eeeeee;margin:0 0 20px 0;' />");
        sb.append("<p style='margin:0;font-size:12px;color:#999999;line-height:1.6;'>");
        sb.append("본 메일은 큐시즘에서 발송한 자동 알림 메일입니다.<br>");
        sb.append("© KUSITMS. All rights reserved.");
        sb.append("</p></td></tr>");

        sb.append("</table></td></tr></table></body></html>");
        return sb.toString();
    }
}
