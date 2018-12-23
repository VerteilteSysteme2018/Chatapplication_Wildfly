package org.chat.databases;


import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;


@Stateful
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class TraceJPA implements TraceRepository {


  @PersistenceContext(unitName = "trace", type = PersistenceContextType.TRANSACTION)
  private EntityManager entityManager;

  @SuppressWarnings("unchecked")
  public List<Trace> findAll() {
    final Query q = entityManager.createQuery("SELECT t FROM Trace t");
    List<Trace> traces = q.getResultList();
    if (traces.isEmpty()) {
      System.out.println("no trace found");
    }
    return traces;
  }

  @Override
  public int clear() {
    System.out.println("Clear Trace");
    return entityManager.createQuery("DELETE FROM Trace").executeUpdate();
  }

  @Override
  public Trace findByUserName(String username) {
    final Query q = entityManager.createQuery
        ("SELECT t FROM Trace t WHERE t.userName LIKE :username");
    q.setParameter("username", username);
    final List results = q.getResultList();
    if (results.isEmpty()) {
      return null;
    }
    return (Trace) results.get(0);
  }

  @Override
  public void create(Trace trace) {
    entityManager.persist(trace);
  }
}
