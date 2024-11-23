package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.Transaction;
import ma.projet.graph.repositories.CompteRepository;
import ma.projet.graph.repositories.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class TransactionControllerGraphQL {

    private TransactionRepository transactionRepository;
    private CompteRepository compteRepository;

    @QueryMapping
    public List<Transaction> transactionsByCompte(@Argument Long compteId) {
        return transactionRepository.findByCompteIdOrderByDateTransactionDesc(compteId);
    }

    @MutationMapping
    public Transaction addTransaction(@Argument TransactionInput transaction) {
        Compte compte = compteRepository.findById(transaction.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        // Vérifier le solde pour les retraits
        if (transaction.getType().equals("RETRAIT")) {
            if (compte.getSolde() < transaction.getMontant()) {
                throw new RuntimeException("Solde insuffisant");
            }
            compte.setSolde(compte.getSolde() - transaction.getMontant());
        } else if (transaction.getType().equals("DEPOT")) {
            compte.setSolde(compte.getSolde() + transaction.getMontant());
        }

        compteRepository.save(compte);

        Transaction newTransaction = new Transaction();
        newTransaction.setMontant(transaction.getMontant());
        newTransaction.setType(transaction.getType());
        newTransaction.setDescription(transaction.getDescription());
        newTransaction.setDateTransaction(new Date());
        newTransaction.setCompte(compte);

        return transactionRepository.save(newTransaction);
    }
}

// Classe pour l'input de la transaction
class TransactionInput {
    private double montant;
    private String type;
    private Long compteId;
    private String description;

    // Getters and setters
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getCompteId() { return compteId; }
    public void setCompteId(Long compteId) { this.compteId = compteId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}