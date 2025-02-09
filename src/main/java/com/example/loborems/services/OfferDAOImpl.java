package com.example.loborems.services;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.loborems.interfaces.OfferDAO;
import com.example.loborems.models.Offer;
import com.example.loborems.util.HibernateUtil;

public class OfferDAOImpl implements OfferDAO {

    @Override
    public void save(Offer offer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(offer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Offer findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Offer.class, id);
        }
    }

    @Override
    public List<Offer> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Offer", Offer.class).list();
        }
    }

    @Override
    public void update(Offer offer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(offer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Offer offer = session.get(Offer.class, id);
            if (offer != null) {
                session.delete(offer);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
