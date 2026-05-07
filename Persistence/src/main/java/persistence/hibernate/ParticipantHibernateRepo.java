package persistence.hibernate;

import model.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import persistence.HibernateUtils;
import persistence.interfaces.IParticipantRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class ParticipantHibernateRepo implements IParticipantRepository {
    private static final Logger logger = LogManager.getLogger(ParticipantHibernateRepo.class);
    private final SessionFactory sessionFactory;

    public ParticipantHibernateRepo() {
        logger.info("Initializing ParticipantHibernateRepo");
        this.sessionFactory = HibernateUtils.getSessionFactory();
    }

    @Override
    public Participant findByCnp(String cnp) {
        logger.traceEntry("findByCnp {}", cnp);

        try (Session session = sessionFactory.openSession()) {
            Participant p = session.createQuery(
                    "FROM Participant WHERE cnp = :cnp", Participant.class
            ).setParameter("cnp", cnp).uniqueResult();

            logger.traceExit(p);
            return p;

        } catch (Exception e) {
            logger.error("Error in findByCnp: ", e);
            return null;
        }
    }

    @Override
    public void add(Participant elem) {
        logger.traceEntry("add participant");
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
    public void delete(Participant elem) {
        logger.traceEntry("delete participant {}", elem.getId());
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Participant managed = session.get(Participant.class, elem.getId());

            if (managed != null) session.remove(managed);
            tx.commit();

            logger.traceExit("deleted");

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in delete: ", e);
        }
    }

    @Override
    public void update(Participant elem, Long id) {
        logger.traceEntry("update participant {}", id);
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Participant managed = session.get(Participant.class, id);

            if (managed != null) {
                managed.setNume(elem.getNume());
                managed.setCnp(elem.getCnp());
                managed.setVarsta(elem.getVarsta());
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
    public Participant findById(Long id) {
        logger.traceEntry("findById {}", id);

        try (Session session = sessionFactory.openSession()) {
            Participant p = session.get(Participant.class, id);
            logger.traceExit(p);
            return p;

        } catch (Exception e) {
            logger.error("Error in findById: ", e);
            return null;
        }
    }

    @Override
    public Iterable<Participant> findAll() {
        return getAll();
    }

    @Override
    public Collection<Participant> getAll() {
        logger.traceEntry("getAll");

        try (Session session = sessionFactory.openSession()) {
            List<Participant> list = session.createQuery(
                    "FROM Participant", Participant.class
            ).list();

            logger.traceExit("found {}", list.size());
            return list;

        } catch (Exception e) {
            logger.error("Error in getAll: ", e);
            return new ArrayList<>();
        }
    }
}
