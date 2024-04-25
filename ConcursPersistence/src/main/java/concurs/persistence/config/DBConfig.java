package concurs.persistence.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DBConfig {
    private String url;
    private String username;
    private String password;

    public DBConfig(){

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public void loadConfig(String fileName){
        try{
            File configFile = new File(fileName);
            Scanner myReader = new Scanner(configFile);

            this.url = myReader.nextLine();
            this.username = myReader.nextLine();
            this.password = myReader.nextLine();

        }catch (FileNotFoundException e) {
            System.out.println("bd.config is missing");
            e.printStackTrace();
        }
    }

    public void loadConfigWithoutFile(){
        url = "jdbc:postgresql://localhost:5432/mpp";
        username = "postgres";
        password = "master";
    }
}
