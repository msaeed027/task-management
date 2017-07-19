package codecubes.core;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by msaeed on 1/22/2017.
 */
public class ModelManager {

    private static SessionFactory sessionFactory;
    private static ModelManager instance = null;

    private ModelManager() {
        Configuration configuration = new Configuration().configure();

        Properties prop = new Properties();
        InputStream input = null;

        String dbURL, dbUsername, dbPassword;

        try {
            input = new FileInputStream("config.properties");

            prop.load(input);

            dbURL = prop.getProperty("DB_URL");
            dbUsername = prop.getProperty("DB_USERNAME");
            dbPassword = prop.getProperty("DB_PASSWORD");
            configuration.setProperty("hibernate.connection.url", dbURL);
            configuration.setProperty("hibernate.connection.username", dbUsername);
            configuration.setProperty("hibernate.connection.password", dbPassword);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            System.out.println("sessionFactory issue");
            sessionFactory = null;
        }
    }

    public static ModelManager getInstance() {
        if (instance == null || sessionFactory == null) {
            instance = new ModelManager();
        }
        return instance;
    }

    public static void checkSessionFactory() {
        if (sessionFactory == null) {
            getInstance();
        }
    }

    public void save(Object entity) {
        checkSessionFactory();
        EntityManager entityManager = sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void update(Object entity) {
        checkSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
        session.close();
    }

    public void delete(Object entity) {
        checkSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
        session.close();
    }

    public <T> List<T> all(Class<T> type) {
        checkSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<T> objects = session.createQuery("from " + type.getSimpleName()).list();
        session.close();
        return objects;
    }

    public <T> T findById(Class<T> type, int id) {
        checkSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        T object = session.get(type, id);
        session.close();
        return object;
    }

    public <T> List<T> where(Class<T> type, String where, HashMap parameters) {
        checkSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from " + type.getSimpleName() + " " + where);
        Iterator iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            query.setParameter(pair.getKey().toString(), pair.getValue());
//            iterator.remove(); // avoids a ConcurrentModificationException
        }
        List<T> objects = query.list();
        session.close();
        return objects;
    }

    public <T> T where(String where, HashMap parameters) {
        checkSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery(where);
        Iterator iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            query.setParameter(pair.getKey().toString(), pair.getValue());
//            iterator.remove(); // avoids a ConcurrentModificationException
        }
        T returnValue = (T) query.list();
        session.close();
        return returnValue;
    }
}
