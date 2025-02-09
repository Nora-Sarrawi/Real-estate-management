package com.example.loborems.util;

import com.example.loborems.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create a configuration instance and configure programmatically
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml"); // Load default settings from the XML file

            // Add annotated classes dynamically
            configuration.addAnnotatedClass(Interaction.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Permission.class);
            configuration.addAnnotatedClass(Role.class);
            configuration.addAnnotatedClass(Property.class);
            configuration.addAnnotatedClass(ResidentialProperty.class);
            configuration.addAnnotatedClass(CommercialProperty.class);
            configuration.addAnnotatedClass(Offer.class);

            // Build the SessionFactory
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
