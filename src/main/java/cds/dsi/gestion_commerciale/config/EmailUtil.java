package cds.dsi.gestion_commerciale.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EmailUtil {

    private final JavaMailSender mailSender;

    // Expéditeur par défaut (configurable)
    @Value("${app.mail.from:CDS Gestion Commerciale <no-reply@cds.local>}")
    private String defaultFrom;

    public EmailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Envoi texte simple */
    public void sendText(String to, String subject, String body) {
        sendMime(to, subject, body, false, null);
    }

    /** Envoi HTML */
    public void sendHtml(String to, String subject, String html) {
        sendMime(to, subject, html, true, null);
    }

    /** Envoi avec PJ (body HTML si html=true, sinon texte) */
    public void sendWithAttachment(String to, String subject, String body, boolean html, @Nullable File attachment) {
        sendMime(to, subject, body, html, attachment);
    }

    /** Helper spécifique : notification RDV planifié */
    public void sendAppointmentNotification(
            String to,
            String clientNom,
            String numeroClient,
            LocalDateTime dateRdv,
            int dureeMinutes,
            @Nullable String objetRdv,
            @Nullable String commentaires
    ) {
        String when = dateRdv.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String safeObjet = (objetRdv == null || objetRdv.isBlank()) ? "Rendez-vous" : objetRdv;
        String safeCom = (commentaires == null || commentaires.isBlank()) ? "" : ("<p style=\"margin:8px 0\">Commentaires : " + escape(commentaires) + "</p>");

        String subject = "📅 Confirmation de rendez-vous — " + safeObjet;
        String html = """
                <div style="font-family:Arial,sans-serif;color:#111">
                  <h2 style="margin:0 0 12px 0;">Confirmation de rendez-vous</h2>
                  <p style="margin:8px 0">Bonjour %s,</p>
                  <p style="margin:8px 0">
                    Votre rendez-vous est planifié :
                  </p>
                  <ul style="margin:8px 0 12px 16px;">
                    <li><b>Objet :</b> %s</li>
                    <li><b>Client :</b> %s (N° %s)</li>
                    <li><b>Date & heure :</b> %s</li>
                    <li><b>Durée :</b> %d minutes</li>
                  </ul>
                  %s
                  <p style="margin:16px 0 0 0;">Cordialement,<br/>CDS — Gestion commerciale</p>
                </div>
                """.formatted(
                escape(clientNom),
                escape(safeObjet),
                escape(clientNom),
                escape(numeroClient),
                when,
                Math.max(1, dureeMinutes),
                safeCom
        );

        sendHtml(to, subject, html);
    }

    /* ===== internes ===== */

    private void sendMime(String to, String subject, String body, boolean html, @Nullable File attachment) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, attachment != null, StandardCharsets.UTF_8.name());
            helper.setFrom(defaultFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, html);
            if (attachment != null) {
                helper.addAttachment(attachment.getName(), attachment);
            }
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi d'email: " + e.getMessage(), e);
        }
    }

    private String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
