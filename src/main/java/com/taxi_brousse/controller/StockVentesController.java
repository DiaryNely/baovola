package com.taxi_brousse.controller;

import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.service.StockDepartService;
import com.taxi_brousse.service.VenteProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/stock-ventes")
public class StockVentesController {

    @Autowired
    private DepartRepository departRepository;

    @Autowired
    private StockDepartService stockDepartService;

    @Autowired
    private VenteProduitService venteProduitService;

    @GetMapping
    public String index(@RequestParam(required = false) Long departId, Model model) {
        List<Depart> departs = departRepository.findAll();
        
        model.addAttribute("departs", departs);
        model.addAttribute("selectedDepartId", departId);

        if (departId != null) {
            Depart depart = departRepository.findById(departId).orElse(null);
            if (depart != null) {
                model.addAttribute("selectedDepart", depart);
                
                // Stats stock
                Long nbProduitsStock = stockDepartService.countByDepartId(departId);
                BigDecimal valeurStock = stockDepartService.calculateStockValue(departId);
                
                // Stats ventes
                Long nbVentes = venteProduitService.countByDepartId(departId);
                BigDecimal totalVentes = venteProduitService.calculateTotalByDepartId(departId);
                
                model.addAttribute("nbProduitsStock", nbProduitsStock);
                model.addAttribute("valeurStock", valeurStock);
                model.addAttribute("nbVentes", nbVentes);
                model.addAttribute("totalVentes", totalVentes);
            }
        }

        return "taxi_brousse/stock_ventes/index";
    }
}
