package org.xchange.java.controller;

/**
 * La classe LoginRequest représente une requête de connexion avec un nom
 * d'utilisateur et un mot de passe.
 * 
 * <p>
 * Cette classe contient deux champs : username et password, ainsi que les
 * méthodes getter et setter associées.
 * Elle fournit également deux constructeurs : un constructeur par défaut et un
 * constructeur avec paramètres.
 * </p>
 * 
 * <p>
 * Exemple d'utilisation :
 * </p>
 * 
 * <pre>
 * {@code
 * LoginRequest request = new LoginRequest("utilisateur", "motdepasse");
 * String username = request.getUsername();
 * String password = request.getPassword();
 * }
 * </pre>
 * 
 * @author VotreNom
 * @version 1.0
 */
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
