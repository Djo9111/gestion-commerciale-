package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.LoginRequest;
import cds.dsi.gestion_commerciale.dto.PerformanceDto;
import cds.dsi.gestion_commerciale.entity.Manager;
import cds.dsi.gestion_commerciale.entity.Objective;
import cds.dsi.gestion_commerciale.entity.WeeklyProduction;
import cds.dsi.gestion_commerciale.repository.ManagerRepository;
import cds.dsi.gestion_commerciale.repository.ObjectiveRepository;
import cds.dsi.gestion_commerciale.repository.WeeklyProductionRepository;
import cds.dsi.gestion_commerciale.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final ManagerRepository managerRepository;
    private final WeeklyProductionRepository weeklyProductionRepository;
    private final ObjectiveRepository objectiveRepository;

    public AuthServiceImpl(ManagerRepository managerRepository,
                           WeeklyProductionRepository weeklyProductionRepository,
                           ObjectiveRepository objectiveRepository) {
        this.managerRepository = managerRepository;
        this.weeklyProductionRepository = weeklyProductionRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @Override
    public Map<String, String> authenticate(LoginRequest loginRequest) {
        return managerRepository.findByNomUtilisateur(loginRequest.getNomUtilisateur())
                .filter(m -> Objects.equals(m.getMotDePasse(), loginRequest.getMotDePasse()))
                .<Map<String, String>>map(m -> Collections.singletonMap("message", "Connexion réussie !"))
                .orElseGet(() -> Collections.singletonMap("message", "Nom d'utilisateur ou mot de passe incorrect."));
    }

    @Override
    public Map<String, String> getUserInfo(String nomUtilisateur) {
        return managerRepository.findByNomUtilisateur(nomUtilisateur)
                .map(m -> Collections.singletonMap("nomComplet", m.getNom()))
                .orElse(Collections.singletonMap("message", "Utilisateur non trouvé."));
    }

    @Override
    public List<PerformanceDto> getPerformance(String nomUtilisateur, LocalDate startOfWeek, LocalDate endOfWeek) {
        Optional<WeeklyProduction> weeklyProduction = weeklyProductionRepository
                .findByNomUtilisateurAndDateDebutAndDateFin(nomUtilisateur, startOfWeek, endOfWeek);

        Optional<Objective> objective = objectiveRepository.findByNomUtilisateurAndActifTrue(nomUtilisateur);

        if (weeklyProduction.isEmpty() && objective.isEmpty()) {
            return Collections.emptyList();
        }

        List<PerformanceDto> data = new ArrayList<>();

        // 1) Packages
        double packagesRealisation = weeklyProduction.map(WeeklyProduction::getPackages)
                .map(Integer::doubleValue).orElse(0.0);
        double packagesObjectif = objective.map(Objective::getObjectifHebdoPackages)
                .map(Integer::doubleValue).orElse(0.0);
        double packagesTaux = (packagesObjectif > 0) ? (packagesRealisation / packagesObjectif) * 100 : 0.0;
        data.add(new PerformanceDto("Packages", packagesRealisation, packagesObjectif, packagesTaux));

        // 2) Crédits Conso
        double creditConsoRealisation = weeklyProduction.map(WeeklyProduction::getCumulCreditConso)
                .orElse(BigDecimal.ZERO).doubleValue();
        double creditConsoObjectif = objective.map(Objective::getObjectifHebdoCreditConso)
                .orElse(BigDecimal.ZERO).doubleValue();
        double creditConsoTaux = (creditConsoObjectif > 0) ? (creditConsoRealisation / creditConsoObjectif) * 100 : 0.0;
        data.add(new PerformanceDto("Crédits Conso", creditConsoRealisation, creditConsoObjectif, creditConsoTaux));

        // 3) Crédits Immo
        double creditImmoRealisation = weeklyProduction.map(WeeklyProduction::getCumulPretImmobilier)
                .orElse(BigDecimal.ZERO).doubleValue();
        double creditImmoObjectif = objective.map(Objective::getObjectifHebdoCreditImmo)
                .orElse(BigDecimal.ZERO).doubleValue();
        double creditImmoTaux = (creditImmoObjectif > 0) ? (creditImmoRealisation / creditImmoObjectif) * 100 : 0.0;
        data.add(new PerformanceDto("Crédits Immo", creditImmoRealisation, creditImmoObjectif, creditImmoTaux));

        // 4) Monétique (cartes vendues)
        double monetiqueRealisation = weeklyProduction.map(WeeklyProduction::getVenteSecheCarte)
                .map(Integer::doubleValue).orElse(0.0);
        double monetiqueObjectif = objective.map(Objective::getObjectifHebdoMonetique)
                .map(Integer::doubleValue).orElse(0.0);
        double monetiqueTaux = (monetiqueObjectif > 0) ? (monetiqueRealisation / monetiqueObjectif) * 100 : 0.0;
        data.add(new PerformanceDto("Monétique", monetiqueRealisation, monetiqueObjectif, monetiqueTaux));

        return data;
    }

}
