package serviceTests.entity;

import com.dmDev.entity.EventEntity;
import com.dmDev.entity.EventRegistrationEntity;
import com.dmDev.entity.EventStatus;
import com.dmDev.entity.LocationEntity;
import com.dmDev.entity.UserEntity;
import com.dmDev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityMappingIT {

    private SessionFactory sessionFactory;

    @BeforeAll
    void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @AfterAll
    void tearDown() {
        HibernateUtil.shutdown();
    }

    void verifyEntityMapping() {
        LocationEntity location = createLocation();
        UserEntity user = createUser();
        EventEntity event = createEvent(user, location);
        EventRegistrationEntity registration = createRegistration(user);

        persistEntities(location, user, event, registration);

        verifyPersistedEntities(location, user, event, registration);
    }

    private void persistEntities(
            LocationEntity location,
            UserEntity user,
            EventEntity event,
            EventRegistrationEntity registration
    ) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(location);
            session.persist(user);
            session.flush();

            event.setOwnerId(user.getId());
            event.setLocationId(location.getId());
            session.persist(event);

            registration.setUserId(user.getId());
            session.persist(registration);

            transaction.commit();
        }
    }

    private void verifyPersistedEntities(
            LocationEntity location,
            UserEntity user,
            EventEntity event,
            EventRegistrationEntity registration
    ) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            assertThat(location.getId()).isNotNull();
            assertThat(user.getId()).isNotNull();
            assertThat(event.getId()).isNotNull();
            assertThat(registration.getId()).isNotNull();

            verifyLocation(session, location);
            verifyUser(session, user);
            verifyEvent(session, event);
            verifyRegistration(session, registration);

            transaction.rollback();
        }
    }

    private LocationEntity createLocation() {
        LocationEntity location = new LocationEntity();
        location.setName("Test Location");
        location.setAddress("123 Test St");
        location.setCapacity(100L);
        location.setDescription("Test Description");
        return location;
    }

    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setAge(30);
        user.setRole("USER");
        user.setPasswordHash("hashedpassword");
        return user;
    }

    private EventEntity createEvent(UserEntity user, LocationEntity location) {
        EventEntity event = new EventEntity();
        event.setName("Test Event");
        event.setOwnerId(user.getId());
        event.setMaxPlaces(50);
        event.setDate(LocalDateTime.now());
        event.setCost(1000);
        event.setDuration(120);
        event.setLocationId(location.getId());
        event.setStatus(EventStatus.WAIT_START);
        return event;
    }

    private EventRegistrationEntity createRegistration(UserEntity user) {
        EventRegistrationEntity registration = new EventRegistrationEntity();
        registration.setUserId(user.getId());
        return registration;
    }

    private void verifyLocation(Session session, LocationEntity location) {
        LocationEntity retrievedLocation = session.get(LocationEntity.class, location.getId());
        assertThat(retrievedLocation)
                .isNotNull()
                .extracting(LocationEntity::getName,
                        LocationEntity::getAddress,
                        LocationEntity::getCapacity,
                        LocationEntity::getDescription)
                .containsExactly("Test Location", "123 Test St", 100L, "Test Description");
    }

    private void verifyUser(Session session, UserEntity user) {
        UserEntity retrievedUser = session.get(UserEntity.class, user.getId());
        assertThat(retrievedUser)
                .isNotNull()
                .extracting(UserEntity::getEmail,
                        UserEntity::getAge,
                        UserEntity::getRole,
                        UserEntity::getPasswordHash)
                .containsExactly("test@example.com", 30, "USER", "hashedpassword");
    }

    private void verifyEvent(Session session, EventEntity event) {
        EventEntity retrievedEvent = session.get(EventEntity.class, event.getId());
        assertThat(retrievedEvent)
                .isNotNull()
                .extracting(
                        EventEntity::getName,
                        EventEntity::getOwnerId,
                        EventEntity::getMaxPlaces,
                        EventEntity::getCost,
                        EventEntity::getDuration,
                        EventEntity::getLocationId,
                        EventEntity::getStatus
                )
                .containsExactly(
                        "Test Event",
                        event.getOwnerId(),
                        50,
                        1000,
                        120,
                        event.getLocationId(),
                        EventStatus.WAIT_START
                );
    }

    private void verifyRegistration(
            Session session,
            EventRegistrationEntity registration
    ) {
        EventRegistrationEntity retrievedRegistration = session.get(EventRegistrationEntity.class,
                registration.getId());
        assertThat(retrievedRegistration)
                .isNotNull()
                .extracting(EventRegistrationEntity::getUserId)
                .isEqualTo(registration.getUserId());
    }
}
