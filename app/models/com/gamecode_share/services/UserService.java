package models.com.gamecode_share.services;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.User;
import models.com.gamecode_share.utility.MailService;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;
import java.util.Date;

public class UserService {

    private final UserRepository userRepository;
    private MailService mailer;

    @Inject
    public UserService(UserRepository userRepository,MailerClient client) {
        this.userRepository = userRepository;
        mailer = new MailService(client);
    }

    public void add(Form<User> filledForm) {
        User user = new User();
        user.setEmail(filledForm.field("email").value());
        user.setUsername(filledForm.field("username").value());
        user.setReputation(0);
        user.setJoinDate(new Date());

        char[] password = filledForm.field("password").value().toCharArray();

        User usr = userRepository.add(user, password);

        sendVerificationEmail(usr);
    }

    public boolean login(DynamicForm form) {
        String email = form.get("email");
        char[] password = form.get("password").toCharArray();

        try {
            return userRepository.login(email, password);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyAccount(String email, String verificationCode) {
        return userRepository.verifyUser(email , verificationCode);
    }

    private void sendEmail(User user) {
        mailer.sendSignUpConfirmation(user);
    }

    public void sendVerificationEmail(String email) {
        User user  = userRepository.getUserByEmail(email);

        user.generateVerificationCode();
        userRepository.updateUser(user);
        sendEmail(user);
    }

    public void sendVerificationEmail(User user) {
        user.generateVerificationCode();
        userRepository.updateUser(user);
        sendEmail(user);
    }

    public boolean checkEmail(String email) {
        User user  = userRepository.getUserByEmail(email);

        if(user != null) {
            return true;
        } else {
            return false;
        }
    }
}
