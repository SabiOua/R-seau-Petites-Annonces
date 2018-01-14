//created by SabiOua
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.net.InetAddress;


public class Server {


    private static final int port = 1027;
    private static final int newPort = 1028;


    private static Vector<Handler> Clients;// changer ça mettre un pour annonce et l'autre pour server
    private static Vector<Annonces> annonces;// changer ça mettre un pour annonce et l'autre pour server




    public static void main(String[] args) throws Exception {
        System.out.println(InetAddress.getLocalHost ());

        ServerSocket server = new ServerSocket(port);  // mettre un verrou de la hashmap
        annonces = new Vector<> ();
        Clients = new Vector<Handler>();
        int nombre = 0;
        try {
            while (true) {
                nombre ++;
                Socket socket = server.accept ();
                Handler h = new Handler(socket,nombre);
                System.out.println("connexion avec le client "+nombre+" etablie");
                Clients.add(h);
                h.start();
            }
        } finally {
            server.close();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader br;
        private  int nbrclients;
        private static int nbrAnnonces = 0;
        private static Handler [] connexion ;
        private static boolean etat = true;

        public Handler(Socket socket,int nombre) {
            this.socket = socket;
            this.nbrclients = nombre;
            this.connexion = new Handler [2];

        }


        public void run() {
            try {
                try {
                    String sentence = "voici les choix :" +
                            "Pour ajouter annoces : add  annonce || "+
                            "pour list d'annonces : list annonces || " +
                            "pour list de cleints : list clients || " +
                            "pour choix annonces : choix num_annonce || " +
                            "pour Discription annonces : contenu id_annonce desciption || " +
                            "pour Informations annonces : info id_annonce || " +
                            "pour connection avec autre client : connect id_client || " +
                            "pour supprimer annonce : supp id_annonce || " +
                            "pour quitter : quit";
                    out = new PrintWriter (new OutputStreamWriter (socket.getOutputStream ()));
                    br = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
                    out.println ("client " + nbrclients + " connexion avec serveur etablie "+sentence);
                    out.flush ();
                    while (true) {
                        String s = br.readLine ();
                        if (s != null) {
                            String[] mots = s.split ("\\s");
                            switch (mots[0]) {
                                case "list":
                                    List (mots[1]);
                                    break;
                                case "add":
                                    ajoutAnnonce (mots[1]);
                                    break;
                                case "supp":
                                    Suppression (mots[1]);
                                    break;
                                case "quit":
                                    quitter ();
                                    break;
                                case "connect":
                                    connecter(mots[1]);
                                    break;
                                case "contenu":
                                    contenuAnnonce(mots,mots[1]);
                                    break;
                                case "info":
                                    infoAnnonce(mots[1]);
                                    break;
                                case "accept":
                                    acceptConnection();
                                    break;
                                case "refuse":
                                    refuseConnection();
                                    break;
                                case "debut":
                                    System.out.println("la discussion a commencé");
                                    break;
                                case "fin":
                                    System.out.println("fin "+this.nbrclients);
                                    etat = false;
                                    run();
                                    break;
                                default:
                                    out.println ("commande non comprise");
                                    out.flush ();
                                    break;
                            }

                        }
                    }
                }catch(Exception e ){System.out.println (e);
                }
            }catch(Exception e){System.out.println(e);}
        }

        public synchronized  void ajoutAnnonce(String s){
            System.out.println("le client "+this.nbrclients+" ajoute une annonce");
            nbrAnnonces+=1;
            Annonces a = new Annonces (s,this.nbrclients,socket,nbrAnnonces);
            a.setIdClient (this.nbrclients);
            a.setNom (s);
            annonces.add (a);
            out.println("annonce bien reçus ("+a.getNom ()+")"+ "veuillez saisir la description (contenu+ id_annonce _contenu)");
            out.flush();

        }
        private synchronized  void List(String s){
            if(s.equals ("annonces")){
                System.out.println("le client "+this.nbrclients+" demande la liste des annoncse");
                String l ="";
                if(annonces.size ()==0){
                    out.println ("List annonces vide");
                    out.flush ();
                }
                else {
                    for (Annonces a : annonces) {
                        l += "client " + a.getIdClient () + " : " + a.getId () + " - " + a.getNom () + " || ";
                    }

                    out.println (l);
                    out.flush ();
                }
            }
            else if (s.equals ("clients")) {
                System.out.println ("le client " + this.nbrclients + " demande la liste des clients");
                String l = "";
                if(Clients.size ()==0){
                    out.println ("List clients vide");
                    out.flush ();
                }
                else {
                    for (Handler i : Clients) {
                        l += i.getNbrClient () + " || ";
                    }
                    out.println (l);
                    out.flush ();
                }
            }else if (s.equals ("mes_annonces")) {
                System.out.println(this.nbrclients+" demande la liste de ses annonces");
                String l ="";
                for (Annonces a : annonces) {
                    if(a.getIdClient ()==this.nbrclients)
                        l+=a.getId ()+": "+a.getNom ()+" || ";
                }
                out.println (l);
                out.flush ();

            }

            else{
                out.println ("Voulez vous dire annonces ou clients");
                out.flush ();
            }

        }
        private synchronized  void Suppression(String s){
            try{
                Annonces k = null;
                for(Annonces a :annonces){
                    if(a.getId ()==Integer.parseInt (s)){
                        if((a.getIdClient ()==this.nbrclients)) {
                            k = a;
                            break;
                        }else{
                            out.println("vous n'etes pas le proprietaire de cette annonce");
                            out.flush();
                        }
                    }else{
                        out.println("vous n'avez aucune annonce sous cet Id");
                        out.flush();
                    }

                }
                System.out.println("le client "+this.nbrclients+"  veut supprimer "+k.getNom());
                annonces.remove (k);
                out.println("suppression effectuée");
                out.flush();
            }catch(NumberFormatException ex){
                out.println("Veuillez saisir supp + id_annonce (pour la liste : list mes_annonces|annonces)");
                out.flush ();
            }

        }
        private synchronized  void quitter(){
            System.out.println("le client "+this.nbrclients+" vient de se deconnecté ");
            Set<Annonces> k = new HashSet<>();
            for(Annonces a : annonces){
                if(a.getIdClient ()==this.nbrclients){
                    k.add(a);

                }
            }

            if(k.size ()>0)
                annonces.removeAll(k);

            System.out.println("connexion terminée");


        }
        private synchronized  void connecter(String s){
            System.out.println ("le client "+this.nbrclients+" essaye de se connecter avec le client "+s);
            try {
                for(Annonces h : annonces){  //id de l'annonce
                    if(h.getId ()==Integer.parseInt (s)){
                        if(h.getIdClient ()==this.nbrclients){
                            out.println("vous etes le propriétaire de cette annonce");
                            out.flush ();
                        }else {
                            connexion[0] = this;
                            for(Handler i : Clients){
                                if(i.getNbrClient ()==h.getIdClient ()){
                                    System.out.println (h.getIdClient ());
                                    connexion[1] = i;
                                    System.out.println (i);
                                    PrintWriter p = new PrintWriter (new OutputStreamWriter (i.getSocket ().getOutputStream ()));
                                    p.println ("connect");
                                    p.flush ();
                                    break;
                                }
                            }
                        }
                    }
                }
            }catch (Exception e ){System.out.println(e);}
        }

        private synchronized void contenuAnnonce(String []s,String nom){
            String chaine = "";
            try{
                for(Annonces a : annonces){
                    if(a.getId () == Integer.parseInt (nom)){
                        for(int i =2;i<s.length;i++){
                            chaine+=s[i]+" ";
                        }
                        a.setEnnonce (chaine);
                    }
                }
                System.out.println("le client "+this.nbrclients+" a ajouté une Description");
                out.println ("Description enregistre");
                out.flush ();
            }catch(NumberFormatException ex){
                out.println(" Si vous Voulez ajouter uune discription à l'anonce. Veuillez saisir contenu + id_annonce (pour la liste : list mes_annonces|annonces)");
                out.flush ();
            }

        }
        private synchronized void infoAnnonce(String nom){
            try{
                for(Annonces a : annonces){
                    if(a.getId ()==Integer.parseInt (nom)){
                        out.println (a.getEnnonce ());
                        out.flush ();
                    }
                }
            }catch(NumberFormatException ex){
                out.println("Veuillez saisir info + id_annonce (pour la liste : list mes_annonces|annonces)");
                out.flush ();
            }

        }
        private synchronized void  acceptConnection(){
            System.out.println ("le client "+this.nbrclients +" a accepté l'invitation ");
            try{
                out.println ("go_chat "+newPort+" "+connexion[1].getNbrClient ());
                out.flush ();
                PrintWriter p = new PrintWriter (new OutputStreamWriter (connexion[0].getSocket ().getOutputStream ()));
                p.println ("host_chat "+newPort+" "+connexion[0].getNbrClient ());
                p.flush ();
                while(etat) {
                }

            }catch (Exception e ){System.out.println(e);}


        }
        private synchronized void  refuseConnection(){
            System.out.println ("le client "+this.nbrclients +" a refusé l'invitation ");
            out.println ("le lcient a refuse votre demande ");
            out.flush ();





        }

        private int getNbrClient(){ return this.nbrclients;}
        private Socket getSocket(){ return this.socket;}


    }
}


