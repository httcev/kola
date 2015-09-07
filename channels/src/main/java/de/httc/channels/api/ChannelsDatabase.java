package de.httc.channels.api;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;

@Transactional
public class ChannelsDatabase {
	private EntityManager em;
	
	public Comment findComment(String id) {
		System.out.println("--- findComment -> em="+em);
		return em.find(Comment.class, id);
	}
	
	public void update(JpaObject o) {
		em.persist(o);
	}

	@PersistenceUnit(unitName = "futurenet")
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.em = emf.createEntityManager();
	}
}