package models.com.gamecode_share.utility;

import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

public class MailService {
    private MailerClient mailerClient;

    public MailService(MailerClient mailerClient) {
        this.mailerClient = mailerClient;
    }

    public void sendSignUpConfirmation(String emailAdress) {
        Email email = new Email()
                .setSubject("Test")
                .setFrom("info@gamecode-share.com")
                .addTo(emailAdress)
                // sends text, HTML or both...
                .setBodyText("Dit is een test email, als u niet weet waarvan deze afkomstig is kunt u deze verwijderen, excuses voor het ongemak");
        mailerClient.send(email);
    }
}
