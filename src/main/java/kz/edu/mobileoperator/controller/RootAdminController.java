package kz.edu.mobileoperator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Простой контроллер для отображения админ-панели.
 * <p>
 * Он перенаправляет запросы с корня приложения и /admin на статическую страницу admin.html.
 */
@Controller
public class RootAdminController {

    @GetMapping({"/", "/admin"})
    public String adminPage() {
        // redirect: говорит Spring отдать редирект на статический ресурс /admin.html
        return "redirect:/admin.html";
    }
}


