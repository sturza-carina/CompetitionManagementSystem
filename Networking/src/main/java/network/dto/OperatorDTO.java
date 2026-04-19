package network.dto;

import java.io.Serializable;

public class OperatorDTO implements Serializable {
    private Long id;
    private String username;
    private String password;

    public OperatorDTO(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
