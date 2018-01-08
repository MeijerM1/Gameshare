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

    CompletionStage<User> add(User user, char[] password);

    @Transactional
    List<User> list();

    void delete(Long id);

    void editPerson(Long id,String name);

    boolean login(String username, char[] password);

    User getPersonByUsername(String username);
}
