package br.com.marcos.product_order_application.dto;

public class AuthRequestDTO {

    private String username;
    private String password;

    public AuthRequestDTO() {
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
