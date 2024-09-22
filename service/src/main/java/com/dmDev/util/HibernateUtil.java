package com.dmDev.util;

import com.dmDev.entity.EventEntity;
import com.dmDev.entity.EventRegistrationEntity;
import com.dmDev.entity.LocationEntity;
import com.dmDev.entity.UserEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.CacheSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

@Slf4j
public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    static {
        try {
            Configuration configuration = new Configuration();

            Properties settings = new Properties();
            settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            settings.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/denis-matveenko-db");
            settings.put("hibernate.connection.username", "postgres");
            settings.put("hibernate.connection.password", "s9td3ixh");
            settings.put(JdbcSettings.SHOW_SQL, "true");
            settings.put(JdbcSettings.FORMAT_SQL, "true");
            settings.put(JdbcSettings.JAKARTA_HBM2DDL_CONNECTION, "validate");
            settings.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
            settings.put("hibernate.hikari.minimumIdle", "5");
            settings.put("hibernate.hikari.maximumPoolSize", "10");
            settings.put("hibernate.hikari.idleTimeout", "30000");
            settings.put(CacheSettings.USE_SECOND_LEVEL_CACHE, "false");
            settings.put(JdbcSettings.NON_CONTEXTUAL_LOB_CREATION, "true");
            settings.put("hibernate.type.preferred_enum_type", "string");

            configuration.setProperties(settings);

            configuration.addAnnotatedClass(LocationEntity.class);
            configuration.addAnnotatedClass(EventEntity.class);
            configuration.addAnnotatedClass(EventRegistrationEntity.class);
            configuration.addAnnotatedClass(UserEntity.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            log.info("SessionFactory created successfully.");
        } catch (Exception ex) {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory closed.");
        }
    }
}
