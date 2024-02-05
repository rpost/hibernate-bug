package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import jakarta.persistence.criteria.*;
import model.*;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh17711_a_Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		saveEntities(entityManager);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<AbstractDcCompany> query = cb.createQuery(AbstractDcCompany.class);
		Root<AbstractDcCompany> root = query.from(AbstractDcCompany.class);
		query.where(
				cb.isNotNull(
						cb.treat(root, DcCompanySeed.class)
								.join("invitedBy", JoinType.LEFT)
								.join("rcCompany", JoinType.LEFT)
				)
		);
		query.select(root);

		List<AbstractDcCompany> result = entityManager.createQuery(query).getResultList();

		Assertions.assertThat(result).isNotEmpty();

		// Do stuff...
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void hhh17711_b_Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		saveEntities(entityManager);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		{
			CriteriaQuery<AbstractDcCompany> query = cb.createQuery(AbstractDcCompany.class);
			Root<AbstractDcCompany> root = query.from(AbstractDcCompany.class);
			Predicate seedPredicate = cb.and(
					cb.equal(root.get("displayName"), "test"),
					cb.isNotNull(cb.treat(root, DcCompanySeed.class).get("invitedBy").get("rcCompany"))
			);
			Predicate dcPredicate = cb.and(
					cb.equal(root.get("displayName"), "test"),
					cb.isNotNull(cb.treat(root, DcCompany.class).get("rcCompany"))
			);
			query.where(cb.or(seedPredicate, dcPredicate));
			query.select(root);
			List<AbstractDcCompany> result = entityManager.createQuery(query).getResultList();

			Assertions.assertThat(result).hasSize(2); // both DcCompany and DcCompanySeed expected
		}

		// same query but limited to only "dcPredicate" returns DcCompany as expected:

		{
			CriteriaQuery<AbstractDcCompany> query = cb.createQuery(AbstractDcCompany.class);
			Root<AbstractDcCompany> root = query.from(AbstractDcCompany.class);
			Predicate dcPredicate = cb.and(
					cb.equal(root.get("displayName"), "test"),
					cb.isNotNull(cb.treat(root, DcCompany.class).get("rcCompany"))
			);
			query.where(dcPredicate);
			query.select(root);
			List<AbstractDcCompany> result = entityManager.createQuery(query).getResultList();

			Assertions.assertThat(result).hasSize(1);
			Assertions.assertThat(result.get(0)).isInstanceOf(DcCompany.class);
		}

		// Do stuff...
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private void saveEntities(EntityManager entityManager) {
		RcCompany rc = new RcCompany();
		rc.setDisplayName("rc");

		RcCompanyUser rcu = new RcCompanyUser();
		rcu.setRcCompany(rc);

		DcCompanySeed seed = new DcCompanySeed();
		seed.setInvitedBy(rcu);
		seed.setDisplayName("test");

		DcCompany dc = new DcCompany();
		dc.setRcCompany(rc);
		dc.setDisplayName("test");

		entityManager.persist(rc);
		entityManager.persist(rcu);
		entityManager.persist(seed);
		entityManager.persist(dc);
		entityManager.flush();
	}
}
