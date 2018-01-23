package models.com.gamecode_share.database.Interfaces;

import com.google.inject.ImplementedBy;
import models.com.gamecode_share.database.JPARepository.JPAUserRepository;
import models.com.gamecode_share.models.User;
import play.db.jpa.Transactional;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAUserRepository.class)
public interface UserRepository {

    User add(User user, char[] password);

    @Transactional
    List<User> list();

    void delete(Long id);

    void updateUser(User user);

    boolean login(String email, char[] password);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    boolean verifyUser(String email, String verificationCode);
}
