package com.dmdev.util;

import com.dmdev.entity.EventEntity;
import com.dmdev.entity.EventRegistrationEntity;
import com.dmdev.entity.LocationEntity;
import com.dmdev.entity.UserEntity;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@UtilityClass
@Slf4j
public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configureHibernate(configuration);
            addAnnotatedClasses(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            log.info("SessionFactory создана успешно.");
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            log.error("Ошибка при создании начальной SessionFactory.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void configureHibernate(Configuration configuration) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.yaml")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("hibernate.yaml не найден");
            }
            Map<String, Object> yamlMap = yaml.load(inputStream);
            Map<String, Object> hibernateConfig = (Map<String, Object>) yamlMap.get("hibernate");

            for (Map.Entry<String, Object> entry : hibernateConfig.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    for (Map.Entry<String, Object> subEntry : ((Map<String, Object>) entry.getValue()).entrySet()) {
                        configuration.setProperty("hibernate." + entry.getKey() + "." + subEntry.getKey(), subEntry.getValue().toString());
                    }
                } else {
                    configuration.setProperty("hibernate." + entry.getKey(), entry.getValue().toString());
                }
            }
        } catch (Exception ex) {
            log.error("Ошибка при чтении конфигурации из hibernate.yaml", ex);
            throw new RuntimeException("Ошибка при чтении конфигурации из hibernate.yaml", ex);
        }
    }

    private static void addAnnotatedClasses(Configuration configuration) {
        configuration.addAnnotatedClass(LocationEntity.class);
        configuration.addAnnotatedClass(EventEntity.class);
        configuration.addAnnotatedClass(EventRegistrationEntity.class);
        configuration.addAnnotatedClass(UserEntity.class);
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory закрыта.");
        }
    }
}
