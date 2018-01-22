package models.com.gamecode_share.utility;

import models.com.gamecode_share.models.User;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

public class MailService {
    private MailerClient mailerClient;

    public MailService(MailerClient mailerClient) {
        this.mailerClient = mailerClient;
    }

    public void sendSignUpConfirmation(User user) {
        Email email = new Email()
                .setSubject("Test")
                .setFrom("info@gamecode-share.com")
                .addTo(user.getEmail())
                // sends text, HTML or both...
                .setBodyText("Click this link to verify your account: http://localhost:9000/auth/verify/" + user.getEmail() + "/" + user.getVerificationCode() + "\n" +
                            "Copy this code: "+ user.getVerificationCode()  + "\n" +
                            "This is a test email,if you do not know what this is you can safely ignore and delete it.");
        mailerClient.send(email);
    }
}
