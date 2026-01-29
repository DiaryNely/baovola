package com.taxi_brousse.controller;

import com.taxi_brousse.dto.StockDepartDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.Produit;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.ProduitRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.service.StockDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/departs/{departId}/stock")
public class StockDepartController {

    @Autowired
    private StockDepartService stockDepartService;

    @Autowired
    private DepartRepository departRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private RefDeviseRepository refDeviseRepository;

    @GetMapping
    public String listStock(@PathVariable Long departId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Depart depart = departRepository.findById(departId)
                    .orElseThrow(() -> new RuntimeException("Départ non trouvé"));

            List<StockDepartDTO> stocks = stockDepartService.findByDepartId(departId);
            
            model.addAttribute("depart", depart);
            model.addAttribute("stocks", stocks);
            model.addAttribute("stockValue", stockDepartService.calculateStockValue(departId));
            
            return "taxi_brousse/stock/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/departs/" + departId;
        }
    }

    @GetMapping("/new")
    public String newStock(@PathVariable Long departId, Model model) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new RuntimeException("Départ non trouvé"));

        StockDepartDTO stock = new StockDepartDTO();
        stock.setDepartId(departId);
        
        // Récupérer la devise MGA par défaut
        RefDevise devise = refDeviseRepository.findByCode("MGA")
                .orElse(refDeviseRepository.findAll().get(0));
        stock.setRefDeviseId(devise.getId());

        List<Produit> produitsActifs = produitRepository.findAllActive();
        List<RefDevise> devises = refDeviseRepository.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("depart", depart);
        model.addAttribute("produits", produitsActifs);
        model.addAttribute("devises", devises);

        return "taxi_brousse/stock/form";
    }

    @GetMapping("/{id}/edit")
    public String editStock(@PathVariable Long departId, @PathVariable Long id, Model model) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new RuntimeException("Départ non trouvé"));

        StockDepartDTO stock = stockDepartService.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé"));

        List<Produit> produitsActifs = produitRepository.findAllActive();
        List<RefDevise> devises = refDeviseRepository.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("depart", depart);
        model.addAttribute("produits", produitsActifs);
        model.addAttribute("devises", devises);

        return "taxi_brousse/stock/form";
    }

    @PostMapping("/save")
    public String saveStock(@PathVariable Long departId, 
                           @ModelAttribute StockDepartDTO stock,
                           RedirectAttributes redirectAttributes) {
        try {
            stock.setDepartId(departId);
            stockDepartService.save(stock);
            redirectAttributes.addFlashAttribute("success", "Stock enregistré avec succès");
            return "redirect:/departs/" + departId + "/stock";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/departs/" + departId + "/stock/new";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteStock(@PathVariable Long departId, 
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            stockDepartService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Stock supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departs/" + departId + "/stock";
    }
}
