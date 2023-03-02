package bg.DNDWarehouse.warehouseApp.SpringSecurity;

import bg.DNDWarehouse.warehouseApp.SpringSecurity.JWT.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@ComponentScan()
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/currentUser/getSecuredUserName").permitAll()
                .antMatchers("/currentUser/username").permitAll()
                .antMatchers("/packet/fetchWarehouses").permitAll()
                .antMatchers("/employee/fetchCity").permitAll()
                .antMatchers("/employee/save").permitAll()
                .antMatchers("/authenticate").permitAll()
                .antMatchers("/task/getEmployeeTasks").permitAll()
                .antMatchers("/task/getEmployeeFinishedTasks").permitAll()
                .antMatchers("/employee/fetch").hasRole("ADMIN")
                .antMatchers("/packet/fetch").hasRole("ADMIN")
                .antMatchers("/packet/receiveNew").hasRole("ADMIN")
                .antMatchers("/packet/remove").hasRole("ADMIN")
                .antMatchers("/task/fetch").hasRole("ADMIN")
                .antMatchers("/task/hireEmployee").hasRole("ADMIN")
                .antMatchers("/task/fireEmployee").hasRole("ADMIN")
                .antMatchers("/task/create").hasRole("ADMIN")
                .antMatchers("/task/remove").hasRole("ADMIN")
                .antMatchers("/task/addPacket").hasRole("ADMIN")
                .antMatchers("/task/removePacket").hasRole("ADMIN")
                .antMatchers("/employee/addWarehouse").hasRole("ADMIN")
                .antMatchers("/employee/addCity").hasRole("ADMIN")
                .antMatchers("/employee/changeEmployeeProfile").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().formLogin()
                .usernameParameter("email")
                .successHandler(successHandler)
                .permitAll()
                .and()
                .logout().permitAll()
                .and().exceptionHandling().accessDeniedPage("/403")
                .and().httpBasic();
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).authorizeRequests();

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        http.cors();
    }

    @Bean
    public LoginSuccessHandler successHandler() {return new LoginSuccessHandler();}

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
