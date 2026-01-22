package com.taxi_brousse.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.PubliciteCaStatRowDTO;
import com.taxi_brousse.dto.PubliciteCaStatsDTO;
import com.taxi_brousse.repository.DepartPubliciteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PubliciteStatsService {

    private final DepartPubliciteRepository departPubliciteRepository;

    public PubliciteCaStatsDTO getCaParMois(int mois, int annee) {
        YearMonth yearMonth = YearMonth.of(annee, mois);
        LocalDateTime dateDebut = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime dateFin = LocalDateTime.of(yearMonth.atEndOfMonth(), LocalTime.MAX);

        List<PubliciteCaStatRowDTO> details = departPubliciteRepository.findCaParPublicite(dateDebut, dateFin);

        PubliciteCaStatsDTO stats = new PubliciteCaStatsDTO();
        stats.setMois(mois);
        stats.setAnnee(annee);
        stats.setDetails(details);
        stats.setNombrePublicites(details.size());

        long totalRepetitions = details.stream()
                .mapToLong(d -> d.getTotalRepetitions() != null ? d.getTotalRepetitions() : 0)
                .sum();
        stats.setTotalRepetitions(totalRepetitions);

        BigDecimal montantTotal = details.stream()
                .map(d -> d.getMontantTotal() != null ? d.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMontantTotal(montantTotal);

        if (!details.isEmpty()) {
            stats.setDeviseCode(details.get(0).getDeviseCode());
            stats.setDeviseSymbole(details.get(0).getDeviseSymbole());
        }

        return stats;
    }

    public PubliciteCaStatsDTO getCaParMoisCourant() {
        LocalDate now = LocalDate.now();
        return getCaParMois(now.getMonthValue(), now.getYear());
    }
}
