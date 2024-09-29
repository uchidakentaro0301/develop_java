package jp.co.sss.java_ec_program.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.co.sss.java_ec_program.entity.Users;
import jp.co.sss.java_ec_program.repository.UserRepository;
import jp.co.sss.java_ec_program.session.UserSession;

@Component
public class LoginCheckFilter extends HttpFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSession userSession;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if ("/views/users/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Users user = userRepository.findByEmail(email);
            if (user != null && user.getPasswords().equals(password)) {
                userSession.setUser(user);
                response.sendRedirect(request.getContextPath() + "/views/product_detail/1"); // ここにはログイン後のリダイレクトページ
                return;
            } else {
                response.sendRedirect(request.getContextPath() + "/views/users/login?error");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}