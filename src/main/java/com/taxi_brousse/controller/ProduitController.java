package com.taxi_brousse.controller;

import com.taxi_brousse.dto.ProduitDTO;
import com.taxi_brousse.dto.RefCategorieProduitDTO;
import com.taxi_brousse.entity.reference.RefCategorieProduit;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.mapper.RefCategorieProduitMapper;
import com.taxi_brousse.repository.RefCategorieProduitRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private RefCategorieProduitRepository refCategorieProduitRepository;

    @Autowired
    private RefCategorieProduitMapper refCategorieProduitMapper;

    @Autowired
    private RefDeviseRepository refDeviseRepository;

    @GetMapping
    public String listProduits(
            @RequestParam(required = false) Long categorie,
            @RequestParam(required = false) Boolean actif,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProduitDTO> produitsPage = produitService.searchProduits(categorie, actif, search, pageable);

        List<RefCategorieProduitDTO> categories = refCategorieProduitRepository.findAllByOrderByLibelle()
                .stream()
                .map(refCategorieProduitMapper::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("produits", produitsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produitsPage.getTotalPages());
        model.addAttribute("totalItems", produitsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategorie", categorie);
        model.addAttribute("selectedActif", actif);
        model.addAttribute("search", search);

        return "taxi_brousse/produits/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ProduitDTO produitDTO = new ProduitDTO();
        produitDTO.setCode(produitService.generateNextCode());
        produitDTO.setActif(true);

        List<RefCategorieProduit> categories = refCategorieProduitRepository.findAllActive();
        List<RefDevise> devises = refDeviseRepository.findAll();

        model.addAttribute("produit", produitDTO);
        model.addAttribute("categories", categories);
        model.addAttribute("devises", devises);
        model.addAttribute("isEdit", false);

        return "taxi_brousse/produits/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProduitDTO produitDTO = produitService.findById(id);

        List<RefCategorieProduit> categories = refCategorieProduitRepository.findAllActive();
        List<RefDevise> devises = refDeviseRepository.findAll();

        model.addAttribute("produit", produitDTO);
        model.addAttribute("categories", categories);
        model.addAttribute("devises", devises);
        model.addAttribute("isEdit", true);

        return "taxi_brousse/produits/form";
    }

    @PostMapping("/save")
    public String saveProduit(@ModelAttribute ProduitDTO produitDTO, RedirectAttributes redirectAttributes) {
        try {
            produitService.save(produitDTO);
            redirectAttributes.addFlashAttribute("success", "Produit enregistré avec succès");
            return "redirect:/produits";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
            return "redirect:/produits/new";
        }
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        ProduitDTO produitDTO = produitService.findById(id);
        model.addAttribute("produit", produitDTO);
        return "taxi_brousse/produits/details";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/produits";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActif(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.toggleActif(id);
            redirectAttributes.addFlashAttribute("success", "Statut du produit modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
        }
        return "redirect:/produits";
    }
}
