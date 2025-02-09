package com.example.loborems.services;

import com.example.loborems.interfaces.UserDOA;
import com.example.loborems.models.Permission;
import com.example.loborems.models.User;
import com.example.loborems.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDOAimp implements UserDOA {
    private static final Logger logger = Logger.getLogger(UserDOAimp.class.getName());
    private final SessionFactory sessionFactory;

    public UserDOAimp() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    public User getByResetToken(String resetToken) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE resetToken = :resetToken", User.class);
            query.setParameter("resetToken", resetToken);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.severe("Error getting user by reset token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void save(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();


            session.save(user);


            if (user.getRole() != null) {
                session.saveOrUpdate(user.getRole());
            }


            if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
                for (Permission permission : user.getPermissions()) {
                    session.saveOrUpdate(permission);
                }
            }

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Error during save: " + e.getMessage());
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();


            session.update(user);

            if (user.getRole() != null) {
                session.saveOrUpdate(user.getRole());
            }

            if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
                for (Permission permission : user.getPermissions()) {
                    session.saveOrUpdate(permission);
                }
            }

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Error during update: " + e.getMessage());
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            User managedUser = session.get(User.class, user.getId());

            if (managedUser != null) {
                if (managedUser.getPermissions() != null) {
                    managedUser.getPermissions().clear();
                    session.flush();
                }

                managedUser.setRole(null);
                session.flush();

                session.delete(managedUser);
            }

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Error during delete: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            logger.severe("Error getting all users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public User getUserByToken(String token) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE resetToken = :token", User.class);
            query.setParameter("token", token);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.severe("Error finding user by token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public User findClient(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                Hibernate.initialize(user.getRole());
                Hibernate.initialize(user.getPermissions());
            }
            return user;
        } catch (Exception e) {
            logger.severe("Error finding client: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            if (user != null) {
                Hibernate.initialize(user.getRole());
                Hibernate.initialize(user.getPermissions());
            }
            return user;
        } catch (Exception e) {
            logger.severe("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public User getUserWithDetails(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                Hibernate.initialize(user.getRole());
                Hibernate.initialize(user.getPermissions());
            }
            return user;
        } catch (Exception e) {
            logger.severe("Error getting user with details: " + e.getMessage());
            e.printStackTrace();
            return null;
        }


    }
}