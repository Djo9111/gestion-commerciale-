package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.ExcelRowDTO;
import cds.dsi.gestion_commerciale.dto.PersistenceResultDTO;
import cds.dsi.gestion_commerciale.entity.WeeklyProduction;
import cds.dsi.gestion_commerciale.repository.WeeklyProductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelPersistenceServiceImpl implements ExcelPersistenceService {

    private final WeeklyProductionRepository weeklyProductionRepository;

    @Autowired
    public ExcelPersistenceServiceImpl(WeeklyProductionRepository weeklyProductionRepository) {
        this.weeklyProductionRepository = weeklyProductionRepository;
    }

    @Override
    @Transactional
    public PersistenceResultDTO persistRows(List<ExcelRowDTO> validRows) {
        List<String> errors = new ArrayList<>();
        List<WeeklyProduction> toSave = new ArrayList<>();
        int inserted = 0;
        int updated = 0;

        for (ExcelRowDTO dto : validRows) {
            try {
                // Cherche s'il y a déjà une ligne pour cet utilisateur + période
                Optional<WeeklyProduction> existingOpt =
                        weeklyProductionRepository.findByNomUtilisateurAndDateDebutAndDateFin(
                                dto.getNomUtilisateur(), dto.getDateDebut(), dto.getDateFin()
                        );

                if (existingOpt.isPresent()) {
                    // update
                    WeeklyProduction existing = existingOpt.get();
                    updateEntityFromDto(existing, dto);
                    toSave.add(existing);
                    updated++;
                } else {
                    // insert
                    WeeklyProduction newEntity = mapDtoToEntity(dto);
                    toSave.add(newEntity);
                    inserted++;
                }

            } catch (Exception e) {
                errors.add("Ligne " + dto.getRowNumber() + " : échec persistence -> " + e.getMessage());
            }
        }

        // Persister en batch (saveAll). Pour très gros fichiers, penser chunking.
        if (!toSave.isEmpty()) {
            // Optionnel : chunk/save par 500 si gros volume
            weeklyProductionRepository.saveAll(toSave);
            // weeklyProductionRepository.flush(); // si flush explicite nécessaire
        }

        PersistenceResultDTO res = new PersistenceResultDTO();
        res.setInserted(inserted);
        res.setUpdated(updated);
        res.setErrors(errors);
        return res;
    }

    private WeeklyProduction mapDtoToEntity(ExcelRowDTO d) {
        WeeklyProduction w = new WeeklyProduction();
        w.setDateDebut(d.getDateDebut());
        w.setDateFin(d.getDateFin());
        w.setCodeAgence(d.getCodeAgence());
        w.setFdc(d.getFdc());
        w.setNombreClientPortefeuille(d.getNombreClientPortefeuille());
        w.setFdcPrincipale(d.getFdcPrincipale());
        w.setNomUtilisateur(d.getNomUtilisateur());
        w.setNomGestionnaire(d.getNomGestionnaire());
        w.setVenteSecheCarte(d.getVenteSecheCarte());
        w.setPackages(d.getPackages());
        w.setCumulPretImmobilier(d.getCumulPretImmobilier());
        w.setCumulCreditConso(d.getCumulCreditConso());
        w.setCumulDepots(d.getCumulDepots());
        w.setNouveauxComptesOuverts(d.getNouveauxComptesOuverts());
        return w;
    }

    private void updateEntityFromDto(WeeklyProduction w, ExcelRowDTO d) {
        // Mettre à jour uniquement les champs que l'on souhaite écraser
        w.setCodeAgence(d.getCodeAgence());
        w.setFdc(d.getFdc());
        w.setNombreClientPortefeuille(d.getNombreClientPortefeuille());
        w.setFdcPrincipale(d.getFdcPrincipale());
        w.setNomGestionnaire(d.getNomGestionnaire());
        w.setVenteSecheCarte(d.getVenteSecheCarte());
        w.setPackages(d.getPackages());
        w.setCumulPretImmobilier(d.getCumulPretImmobilier());
        w.setCumulCreditConso(d.getCumulCreditConso());
        w.setCumulDepots(d.getCumulDepots());
        w.setNouveauxComptesOuverts(d.getNouveauxComptesOuverts());
        // NOTE: ne pas toucher created_at ; id reste inchangé
    }
}
