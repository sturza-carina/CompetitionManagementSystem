package persistence.hibernate;

import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import persistence.HibernateUtils;
import persistence.interfaces.IProbaRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class ProbaHibernateRepo implements IProbaRepository {
    private static final Logger logger = LogManager.getLogger(ProbaHibernateRepo.class);
    private final SessionFactory sessionFactory;

    public ProbaHibernateRepo() {
        logger.info("Initializing ProbaHibernateRepo");
        this.sessionFactory = HibernateUtils.getSessionFactory();
    }

    @Override
    public Iterable<Proba> findByCategorieVarsta(String categorieVarsta) {
        logger.traceEntry("findByCategorieVarsta {}", categorieVarsta);

        try (Session session = sessionFactory.openSession()) {
            List<Proba> list = session.createQuery(
                    "FROM Proba WHERE categorieVarsta = :categorie", Proba.class
            ).setParameter("categorie", categorieVarsta).list();

            logger.traceExit("found {}", list.size());
            return list;

        } catch (Exception e) {
            logger.error("Error in findByCategorieVarsta: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void add(Proba elem) {
        logger.traceEntry("add proba");
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(elem);
            tx.commit();

            logger.traceExit("saved with id {}", elem.getId());

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in add: ", e);
        }
    }

    @Override
    public void delete(Proba elem) {
        logger.traceEntry("delete proba {}", elem.getId());
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Proba managed = session.get(Proba.class, elem.getId());

            if (managed != null) session.remove(managed);
            tx.commit();

            logger.traceExit("deleted");

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in delete: ", e);
        }
    }

    @Override
    public void update(Proba elem, Long id) {
        logger.traceEntry("update proba {}", id);
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Proba managed = session.get(Proba.class, id);

            if (managed != null) {
                managed.setNume(elem.getNume());
                managed.setCategorie(elem.getCategorie());
                session.merge(managed);
            }
            tx.commit();

            logger.traceExit("updated");

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in update: ", e);
        }
    }

    @Override
    public Proba findById(Long id) {
        logger.traceEntry("findById {}", id);

        try (Session session = sessionFactory.openSession()) {
            Proba p = session.get(Proba.class, id);
            logger.traceExit(p);
            return p;

        } catch (Exception e) {
            logger.error("Error in findById: ", e);
            return null;
        }
    }

    @Override
    public Iterable<Proba> findAll() {
        return getAll();
    }

    @Override
    public Collection<Proba> getAll() {
        logger.traceEntry("getAll");

        try (Session session = sessionFactory.openSession()) {
            List<Proba> list = session.createQuery(
                    "FROM Proba", Proba.class
            ).list();

            logger.traceExit("found {}", list.size());
            return list;

        } catch (Exception e) {
            logger.error("Error in getAll: ", e);
            return new ArrayList<>();
        }
    }

}
