package models.com.gamecode_share.database.JPARepository;

import models.com.gamecode_share.database.DatabaseExecutionContext;
import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.User;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;


import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAUserRepository implements UserRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAUserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<User> add(User user, char[] password) {
        return supplyAsync(() -> wrap(em -> insert(em, user, password)), executionContext);
    }

    @Override
    public void editUser(Long id, String name){
        jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            User user = em.find(User.class,id);
            if(!name.isEmpty()){
                user.setUsername(name);
            }
        });
    }

    @Override
    public boolean login(String username, char[] password) {
        return jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            return login(em, username, password);
        });
    }

    @Override
    public User getUserByUsername(String username) {
        return jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            TypedQuery<User> query = em.createQuery("select p from User p where username = :username", User.class);
            return query.setParameter("username", username).getSingleResult();
        });
    }

    @Override
    public User getUserByEmail(String email) {
        return jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            TypedQuery<User> query = em.createQuery("select p from User p where email = :email", User.class);
            return query.setParameter("email", email).getSingleResult();
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            User user = em.find(User.class, id);
            em.remove(user);
        });
    }

    @Override
    @Transactional
    public List<User> list() {
        return jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            return em.createQuery("select p from User p", User.class).getResultList();
        });
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private User insert(EntityManager em, User user, char[] password) {

        user.setSalt(getNextSalt());
        user.setHashedPassword(hash(password, user.getSalt(), 1000, 256));

        em.persist(user);
        return user;
    }

    private boolean login(EntityManager em, String username, char[] password) {
        TypedQuery<User> query = em.createQuery("select p from User p where username = :username", User.class);
        User userToLogin = query.setParameter("username", username).getSingleResult();

        byte[] passwordToCheck = hash(password, userToLogin.getSalt(), 1000, 256);

        return Arrays.equals(passwordToCheck, userToLogin.getHashedPassword());
    }

    private byte[] getNextSalt() {
        byte[] salt = new byte[16];
        Random random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hash(char[] password, byte[] salt, int iterations, int key) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, key);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }
}
