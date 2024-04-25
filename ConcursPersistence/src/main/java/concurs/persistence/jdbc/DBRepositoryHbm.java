package concurs.persistence.jdbc;

import com.fasterxml.classmate.AnnotationConfiguration;
import concurs.model.Admin;
import concurs.model.validators.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DBRepositoryHbm {
    private static SessionFactory sessionFactory;
    private final Validator<Admin> adminValidator;

    public DBRepositoryHbm(Validator<Admin> validator) {
        initialize();
        adminValidator = validator;
    }

    static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Exceptie " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    static void close() {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    public Admin findOne(Integer idS){
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            Admin admin = session.get(Admin.class, idS);
            session.getTransaction().commit();
            return admin;
        }catch (RuntimeException ex) {
            System.err.println("Eroare la select " + ex);
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    public Admin findBy(String username, String password){
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(Admin.class);
            criteria.add(Restrictions.eq("username", username));
            criteria.add(Restrictions.eq("password", password));
            Admin admin = (Admin) criteria.uniqueResult();

            session.getTransaction().commit();
            return admin;
        }catch (RuntimeException ex) {
            System.err.println("Eroare la findBY " + ex);
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    public Iterable<Admin> findAll(){
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            List<Admin> admins = session.createQuery("from Admin as a order by a.id", Admin.class)
                    .list();
            session.getTransaction().commit();
            return admins;
        }catch (RuntimeException ex) {
            System.err.println("Eroare la findBY " + ex);
            if (tx != null)
                tx.rollback();
        }
        return null;
    }


    public Admin save(Admin entity) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
            return entity;
        }catch (RuntimeException ex) {
            System.err.println("Eroare la save " + ex);
            if (tx != null)
                tx.rollback();
        }
        return entity;
    }

    public Admin update(Admin entity) {
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
            return entity;
        }catch (RuntimeException ex) {
            System.err.println("Eroare la update " + ex);
            if (tx != null)
                tx.rollback();
        }
        return entity;
    }

    public Admin delete(Integer idS){
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            Admin entity = (Admin)session.load(Admin.class, idS);
            session.delete(entity);
            session.flush();
            session.getTransaction().commit();
            return entity;
        }catch (RuntimeException ex) {
            System.err.println("Eroare la update " + ex);
            if (tx != null)
                tx.rollback();
        }
        return null;
    }
}
