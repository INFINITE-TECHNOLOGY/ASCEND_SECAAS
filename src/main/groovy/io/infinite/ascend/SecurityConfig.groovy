package io.infinite.ascend

import groovy.util.logging.Slf4j
import io.infinite.blackbox.BlackBox
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

import javax.servlet.http.HttpServletResponse

@EnableWebSecurity
@Slf4j
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @BlackBox
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint({
            req, rsp, e ->
                rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        })
                .and()
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/ascend/public/**").permitAll()
                .anyRequest().authenticated()
    }

}