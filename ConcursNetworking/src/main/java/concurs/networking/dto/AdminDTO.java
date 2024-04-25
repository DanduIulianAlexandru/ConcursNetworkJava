package concurs.networking.dto;

import java.io.Serializable;

public class AdminDTO implements Serializable {
    private String id;
    private String username;
    private String passwd;

    public AdminDTO(){
        this.id = "";
        this.username = "";
        this.passwd = "";
    }

    public AdminDTO(String id, String username, String pass){
        this.id = id;
        this.username = username;
        this.passwd = pass;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
