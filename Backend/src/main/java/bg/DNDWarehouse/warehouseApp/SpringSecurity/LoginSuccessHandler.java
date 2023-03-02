package bg.DNDWarehouse.warehouseApp.SpringSecurity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        String redirectURL = request.getContextPath();

        if (userDetails.getAuthorities().hashCode() == -1142751725) //Това е хеш кодът на ROLE_USER
        {
            redirectURL += "https://de221.github.io/WarehouseApp/Frontend/user-home";
        }
        else if (userDetails.getAuthorities().hashCode() == -1084475835) // ROLE_ADMIN
        {

            redirectURL += "https://de221.github.io/WarehouseApp/Frontend/admin-home";
        }
        else
        {
            redirectURL += "https://google.com/"; // unreachable code
        }
        response.sendRedirect(redirectURL);
    }
}
