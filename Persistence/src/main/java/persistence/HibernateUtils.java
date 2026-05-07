package persistence;

import model.Participant;
import model.Proba;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            Configuration configuration = new Configuration();

            configuration.addAnnotatedClass(Participant.class);
            configuration.addAnnotatedClass(Proba.class);

            sessionFactory = configuration.buildSessionFactory();
        }

        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

}
