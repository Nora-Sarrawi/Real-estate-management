package com.example.loborems.services;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.example.loborems.interfaces.DOA;
import com.example.loborems.models.Interaction;
import com.example.loborems.util.HibernateUtil;

public class DOAInteractionImpl implements DOA<Interaction> {

    private final SessionFactory sessionFactory;

    public DOAInteractionImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void save(Interaction interaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(interaction);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Interaction interaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(interaction);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Interaction interaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(interaction);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Interaction findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Interaction.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Interaction> findAll() {
        try (Session session = sessionFactory.openSession()) {
            // Use HQL to fetch all records of Interaction
            return session.createQuery("FROM Interaction", Interaction.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
