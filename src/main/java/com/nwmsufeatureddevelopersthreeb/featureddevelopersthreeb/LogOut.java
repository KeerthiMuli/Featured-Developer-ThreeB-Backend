/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nwmsufeatureddevelopersthreeb.featureddevelopersthreeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 *
 * @author S541906
 */
@Controller
public class LogOut extends SecurityContextLogoutHandler {
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Create a new instance with a {@code ClientRegistrationRepository}, so that we can look up information about the
     * configured provider to call the Auth0 logout endpoint. Called by the Spring framework.
     *
     * @param clientRegistrationRepository the {@code ClientRegistrationRepository} for this application.
     */
    @Autowired
    public LogOut(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * Delegates to {@linkplain SecurityContextLogoutHandler} to log the user out of the application, and then logs
     * the user out of Auth0.
     *
     * @param httpServletRequest the request.
     * @param httpServletResponse the response.
     * @param authentication the current authentication.
     */
    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       Authentication authentication) {

        // Invalidate the session and clear the security context
        super.logout(httpServletRequest, httpServletResponse, authentication);

        // Build the URL to log the user out of Auth0 and redirect them to the home page.
        // URL will look like https://YOUR-DOMAIN/v2/logout?clientId=YOUR-CLIENT-ID&returnTo=http://localhost:3000
        String issuer = (String) getClientRegistration().getProviderDetails().getConfigurationMetadata().get("issuer");
        String clientId = getClientRegistration().getClientId();
        String returnTo = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

       String logoutUrl = UriComponentsBuilder
               .fromHttpUrl(issuer + "v2/logout?client_id={clientId}&returnTo={returnTo}")
                .encode()
               .buildAndExpand(clientId, returnTo)
                .toUriString();

        try {
           httpServletResponse.sendRedirect(logoutUrl);
       } catch (IOException ioe) {
            // Handle or log error redirecting to logout URL
        }
    }

