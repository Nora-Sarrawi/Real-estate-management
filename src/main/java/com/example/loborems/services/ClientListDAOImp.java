package com.example.loborems.services;

import com.example.loborems.interfaces.ClientListDAO;
import com.example.loborems.models.Client;
import com.example.loborems.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ClientListDAOImp implements ClientListDAO {

    @Override
    public void addClient(Client client) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void updateClient(Client client) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void deleteClient(int clientId) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Client client = session.get(Client.class, clientId);
            if (client != null) {
                session.delete(client);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Client getClientById(int clientId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Client.class, clientId);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Client> getAllClients() throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Client> query = session.createQuery("FROM Client", Client.class);
            return query.list();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Client> searchClientsByName(String name) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Client> query = session.createQuery(
                    "FROM Client WHERE LOWER(name) LIKE LOWER(:name)",
                    Client.class
            );
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            throw e;
        }
    }
}
