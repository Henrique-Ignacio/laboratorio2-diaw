package com.pucminas.aluguel.config;
import com.pucminas.aluguel.model.Usuario;
import com.pucminas.aluguel.repository.UsuarioRepository;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Optional;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UsuarioRepository usuarios;
    public WebConfig(UsuarioRepository usuarios) { this.usuarios = usuarios; }
    @Override public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/api/**").allowedOrigins("http://localhost:5173").allowedMethods("*").allowedHeaders("*");
    }
    @Override public void addInterceptors(InterceptorRegistry r) {
        r.addInterceptor(new HandlerInterceptor() {
            @Override public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object h) throws Exception {
                if (req.getMethod().equals("OPTIONS")) return true;
                String auth = req.getHeader("Authorization");
                Optional<Usuario> u = (auth != null && auth.startsWith("Bearer ")) ? usuarios.findByToken(auth.substring(7)) : Optional.empty();
                if (u.isEmpty()) { res.sendError(401, "Não autenticado"); return false; }
                req.setAttribute("usuario", u.get());
                return true;
            }
        }).addPathPatterns("/api/**").excludePathPatterns("/api/auth/**");
    }
}
