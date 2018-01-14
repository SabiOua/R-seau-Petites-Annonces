//created by SabiOua

import java.net.Socket;

public class Annonces {


    private String nom;
    private   int id =0;
    private int idClient;
    String ennonce;
    private Socket socket_client;
    public Annonces(String nom,int idClient,Socket sc,int idan){
        this.nom = nom;
        this.id = idan;
        this.idClient = idClient;
        this.ennonce = "";
        this.socket_client = sc;
    }




    public int getId(){return this.id;}
    public String getNom(){return this.nom;}
    public String getEnnonce(){return this.ennonce;}
    public int getIdClient(){return this.idClient;}
    public Socket getSocket_client(){
        return socket_client;
    }

    public void setEnnonce(String s){this.ennonce=s;}
    public void setNom(String nom){this.nom=nom;}
    public void setId(int id){this.id=id;}
    public void setIdClient(int id){this.idClient=id;}
    public void setSocket(Socket s){
        socket_client = s;
    }

}
