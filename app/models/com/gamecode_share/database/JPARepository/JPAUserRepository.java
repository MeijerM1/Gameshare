package models.com.gamecode_share.database.JPARepository;

import models.com.gamecode_share.database.DatabaseExecutionContext;
import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.Role;
import models.com.gamecode_share.models.User;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;


import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
@Singleton
public class JPAUserRepository implements UserRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAUserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public User add(User user, char[] password) {
        return insert(user, password);
    }

    @Override
    public void updateUser(User user){
        jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            User userToUpdate = em.find(User.class, user.getId());
            userToUpdate.setJoinDate(user.getJoinDate());
            userToUpdate.setVerifyDate(user.getVerifyDate());
            userToUpdate.setVerificationCode(user.getVerificationCode());
            userToUpdate.setReputation(user.getReputation());
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setCodes(user.getCodes());
            userToUpdate.setHashedPassword(user.getHashedPassword());
            userToUpdate.setSalt(user.getSalt());
        });
    }

    @Override
    public boolean login(String email, char[] password) {
        return jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();
            return login(em, email, password);
        });
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return jpaApi.withTransaction(() -> {
                EntityManager em = jpaApi.em();
                TypedQuery<User> query = em.createQuery("select p from User p where username = :username", User.class);
                return query.setParameter("username", username).getSingleResult();
            });
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return jpaApi.withTransaction(() -> {
                EntityManager em = jpaApi.em();
                TypedQuery<User> query = em.createQuery("select p from User p where email = :email", User.class);
                return query.setParameter("email", email).getSingleResult();
            });
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean verifyUser(String email, String verificationCode) {
        User user = getUserByEmail(email);

        Date date = new Date();

        long diff = date.getTime() - user.getVerifyDate().getTime();
        long diffHours = diff / (60 * 60 * 1000) % 24;

        if(diffHours < 1 && user.getVerificationCode().equals(verificationCode)) {
            user.setVerificationCode("");
            user.setVerifyDate(null);
            user.setRole(Role.USER);

            updateUser(user);

            return true;
        } else {
            return false;
        }
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

    private User insert(User user, char[] password) {
        return jpaApi.withTransaction(() -> {
            user.setSalt(getNextSalt());
            user.setHashedPassword(hash(password, user.getSalt(), 1000, 256));

             jpaApi.em().persist(user);
            return user;
        });
    }

    private boolean login(EntityManager em, String email, char[] password) {
        TypedQuery<User> query = em.createQuery("select p from User p where email = :email", User.class);
        User userToLogin = query.setParameter("email", email).getSingleResult();

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
