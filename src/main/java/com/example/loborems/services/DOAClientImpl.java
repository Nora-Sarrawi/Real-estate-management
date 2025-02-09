package com.example.loborems.services;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.example.loborems.interfaces.DOA;
import com.example.loborems.models.Client;
import com.example.loborems.util.HibernateUtil;

public class DOAClientImpl implements DOA<Client> {

    private final SessionFactory sessionFactory;

    public DOAClientImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void save(Client client) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(client);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Client client) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(client);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Client client) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(client);
            session.getTransaction().commit();
        }
    }

    @Override
    public Client findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Client.class, id);
        }
    }

    public Client findByIdOrName(int id, String name) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Client WHERE id = :id OR name = :name";
            Query<Client> query = session.createQuery(hql, Client.class);
            query.setParameter("id", id);
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Client> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Client", Client.class).list();
        }
    }

    public long getTotalClients() {
        try (Session session = sessionFactory.openSession()) {
            return (long) session.createQuery(
                    "SELECT COUNT(c) FROM Client c WHERE c.roleId = :roleId"
            )
                    .setParameter("roleId", 2)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getActiveClients() {
        try (Session session = sessionFactory.openSession()) {
            return (long) session.createQuery("SELECT COUNT(c) FROM Client c WHERE c.status = :status")
                    .setParameter("status", "active")
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getUserName() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT c.name FROM Client c", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserEmail() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT c.email FROM Client c", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPropertyFeatures() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT p.features FROM Property p", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPropertyLocation() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT p.location FROM Property p", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPropertyPrice() {
        try (Session session = sessionFactory.openSession()) {
            Integer price = session.createQuery("SELECT p.price FROM Property p", Integer.class)
                    .setMaxResults(1)
                    .uniqueResult();
            return price != null ? price : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getPropertyStatus() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT p.status FROM Property p", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
