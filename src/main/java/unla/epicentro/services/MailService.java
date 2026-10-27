package unla.epicentro.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username:no-reply@epicentrogourmet.com}")
	private String remitente;

	public MailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	// Devuelve false si no se pudo enviar (ej: SMTP sin configurar), así no se corta el flujo que lo llama
	public boolean enviar(String destinatario, String asunto, String texto) {
		try {
			SimpleMailMessage mensaje = new SimpleMailMessage();
			mensaje.setFrom(remitente);
			mensaje.setTo(destinatario);
			mensaje.setSubject(asunto);
			mensaje.setText(texto);
			mailSender.send(mensaje);
			return true;
		} catch (MailException e) {
			log.warn("No se pudo enviar el mail a {}: {}", destinatario, e.getMessage());
			return false;
		}
	}
}
