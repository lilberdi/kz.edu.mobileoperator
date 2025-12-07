package kz.edu.mobileoperator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Общая MVC-конфигурация.
 * <p>
 * Здесь мы настраиваем маппинг GET /login на статическую страницу {@code login.html},
 * которая находится в {@code src/main/resources/static}.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // При GET /login Spring выполнит forward на статический ресурс /login.html
        registry.addViewController("/login").setViewName("forward:/login.html");
    }
}


