package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.TypeCompte;
import ma.projet.graph.repositories.CompteRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private CompteRepository compteRepository;

    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        Compte compte = compteRepository.findById(id).orElse(null);
        if(compte == null) throw new RuntimeException(String.format("Compte %s not found", id));
        else return compte;
    }

    @MutationMapping
    public Boolean deleteCompte(@Argument Long id) {
        try {
            if (!compteRepository.existsById(id)) {
                throw new RuntimeException(String.format("Compte %s not found", id));
            }
            compteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting compte: " + e.getMessage());
            return false;
        }
    }
    @MutationMapping
    public Compte saveCompte(@Argument CompteInput compteInput) {
        try {
            Compte compte = new Compte();
            compte.setSolde(compteInput.getSolde());
            compte.setType(compteInput.getType());

            // Gestion de la date
            if (compteInput.getDateCreation() == null) {
                compte.setDateCreation(new Date());
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                compte.setDateCreation(format.parse(compteInput.getDateCreation()));
            }

            return compteRepository.save(compte);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date: " + e.getMessage());
        }
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }

    @QueryMapping
    public List<Compte> comptesByType(@Argument TypeCompte type) {
        return compteRepository.findByType(type);
    }
}

@Data
class CompteInput {
    private double solde;
    private String dateCreation;
    private TypeCompte type;
}