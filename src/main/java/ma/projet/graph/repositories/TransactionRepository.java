package ma.projet.graph.repositories;

import ma.projet.graph.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompteId(Long compteId);
    List<Transaction> findByCompteIdOrderByDateTransactionDesc(Long compteId);
}