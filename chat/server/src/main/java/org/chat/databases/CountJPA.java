package org.chat.databases;

import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;


@Stateful
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CountJPA implements CountRepository {


  @PersistenceContext(unitName = "count", type= PersistenceContextType.TRANSACTION)
  private EntityManager entityManager;

  @Override
  public int clear(){
    int countDelete;
    System.out.println("Clear Count");
    countDelete = entityManager.createQuery("DELETE from Count").executeUpdate();
    return countDelete;
  }

  @Override
  public Count findByUserName(String username) {
    final Query find  = entityManager.createQuery
        ("SELECT c FROM Count c WHERE c.username LIKE :username");
    find.setParameter("username", username);
    final List results = find.getResultList();
    if (results.isEmpty()) {
      Count count = new Count();
      count.setCounting(0);
      count.setUsername(username);
      return count;
    }
    return (Count) results.get(0);
  }

  @Override
  public List<Count> findAll() {
    final Query query = entityManager.createQuery("SELECT c FROM Count c");
    final List<Count> result;
    result = query.getResultList();
    if (result.isEmpty()) {
      System.out.println("no count found");
    }
    return result;
  }

  @Override
  public void updateCount(String name) {
    final Count counta = findByUserName(name);
    counta.setCounting(counta.getCounting() + 1);
    entityManager.persist(counta);
  }
}
