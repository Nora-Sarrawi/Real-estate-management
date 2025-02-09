package com.example.loborems.services;

import com.example.loborems.interfaces.PermissionDOA;
import com.example.loborems.models.Permission;
import com.example.loborems.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PermissionDOAimp implements PermissionDOA {
    private final SessionFactory sessionFactory;

    public PermissionDOAimp() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void save(Permission permission) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(permission);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Permission permission) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(permission);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Permission permission) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(permission);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<Permission> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Permission", Permission.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Permission findPermission(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Permission.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Permission> findByRoleId(int roleId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId", Permission.class)
                    .setParameter("roleId", roleId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}