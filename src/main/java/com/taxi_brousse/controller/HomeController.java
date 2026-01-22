package com.taxi_brousse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.taxi_brousse.dto.DashboardDTO;
import com.taxi_brousse.service.DashboardService;

@Controller
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping({"/", "/index", "/home"})
    public String index(Model model) {
        // Charger toutes les statistiques du dashboard
        DashboardDTO dashboard = dashboardService.getDashboardStats();
        model.addAttribute("dashboard", dashboard);
        
        return "taxi_brousse/index";
    }
}
