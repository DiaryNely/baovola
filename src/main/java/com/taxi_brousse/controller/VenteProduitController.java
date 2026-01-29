package com.taxi_brousse.controller;

import com.taxi_brousse.dto.StockDepartDTO;
import com.taxi_brousse.dto.VenteProduitDTO;
import com.taxi_brousse.entity.Client;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.repository.ClientRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.service.StockDepartService;
import com.taxi_brousse.service.VenteProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/departs/{departId}/ventes")
public class VenteProduitController {

    @Autowired
    private VenteProduitService venteProduitService;

    @Autowired
    private StockDepartService stockDepartService;

    @Autowired
    private DepartRepository departRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public String listVentes(@PathVariable Long departId, Model model) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new RuntimeException("Départ non trouvé"));

        List<VenteProduitDTO> ventes = venteProduitService.findByDepartId(departId);
        BigDecimal totalVentes = venteProduitService.calculateTotalByDepartId(departId);

        model.addAttribute("depart", depart);
        model.addAttribute("ventes", ventes);
        model.addAttribute("totalVentes", totalVentes);
        model.addAttribute("nombreVentes", ventes.size());

        return "taxi_brousse/ventes/list";
    }

    @GetMapping("/new")
    public String newVente(@PathVariable Long departId, Model model) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new RuntimeException("Départ non trouvé"));

        // Vérifier qu'il y a du stock disponible
        List<StockDepartDTO> stocksDisponibles = stockDepartService.findAvailableByDepartId(departId);
        if (stocksDisponibles.isEmpty()) {
            throw new RuntimeException("Aucun produit en stock pour ce départ");
        }

        VenteProduitDTO vente = new VenteProduitDTO();
        vente.setDepartId(departId);

        List<Client> clients = clientRepository.findAll();

        model.addAttribute("vente", vente);
        model.addAttribute("depart", depart);
        model.addAttribute("stocks", stocksDisponibles);
        model.addAttribute("clients", clients);

        return "taxi_brousse/ventes/form";
    }

    @PostMapping("/save")
    public String saveVente(@PathVariable Long departId,
                           @ModelAttribute VenteProduitDTO vente,
                           RedirectAttributes redirectAttributes) {
        try {
            vente.setDepartId(departId);
            venteProduitService.save(vente);
            redirectAttributes.addFlashAttribute("success", "Vente enregistrée avec succès");
            return "redirect:/departs/" + departId + "/ventes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/departs/" + departId + "/ventes/new";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteVente(@PathVariable Long departId,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            venteProduitService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Vente annulée avec succès. Stock remis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departs/" + departId + "/ventes";
    }

    @GetMapping("/ajax/stock/{stockId}")
    @ResponseBody
    public StockDepartDTO getStockDetails(@PathVariable Long stockId) {
        return stockDepartService.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé"));
    }
}
