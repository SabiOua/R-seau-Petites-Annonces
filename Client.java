//created by SabiOua

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public final static int port = 1027;
    public  static int newport ;
    private static  int numClient =0;
    private static boolean etat = false;


    private static void startSender(int port,Socket socket,PrintWriter p,BufferedReader b) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Scanner sc = new Scanner (System.in);
                    Socket s = new Socket(InetAddress.getLocalHost (), port);
                    System.out.println("connexion effectuee avec le client ");
                    BufferedReader br = new BufferedReader (new InputStreamReader (s.getInputStream ()));
                    PrintWriter pw = new PrintWriter (new OutputStreamWriter (s.getOutputStream ()));
                    pw.println("hello");
                    pw.flush ();
                    String sen = br.readLine ();
                    if(sen!=null)
                        System.out.println(sen);
                    while(true){
                        String h = sc.nextLine ();
                        if(h.equals ("exit")){
                            p.println("fin");
                            p.flush ();
                            pw.println (h);
                            pw.flush ();
                            Thread.currentThread().interrupt();
                            s.close();
                            break;
                        }
                        pw.println (h);
                        pw.flush ();
                        String sent = br.readLine ();
                        if(sent!=null){
                            if(sent.equals ("exit")){
                                System.out.println("le client vient de fermer la discussion ");
                                p.println("fin");
                                p.flush ();
                                System.out.println("envoyé");
                                s.close();
                                Thread.currentThread().interrupt ();

                                break;

                              }
                              System.out.println(sent);


                          }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void startServer(int port,Socket socket, PrintWriter p) {
        new Thread() {
            @Override
            public void run() {

                try {
                    Scanner sc = new Scanner (System.in);
                    ServerSocket  ss = new ServerSocket(port);
                    Socket s = ss.accept();
                    System.out.println("connexion etablie avec le client ");
                    BufferedReader in = new BufferedReader (new InputStreamReader (s.getInputStream ()));
                    PrintWriter pw = new PrintWriter (new OutputStreamWriter (s.getOutputStream ()));
                     String sen = in.readLine ();
                     if(sen!=null) System.out.println(sen);
                     pw.println ("thank you for invitation");
                     pw.flush ();
                     while(true){
                         String sent = in.readLine ();
                         if(sent!=null){
                             if(sent.equals ("exit")){
                                 System.out.println("fini");
                                 p.println("fin");
                                 p.flush ();
                                 s.close ();
                                 break;

                             }
                             System.out.println(sent);
                         }
                         String h = sc.nextLine ();
                         if(h.equals ("exit")){


                                 p.println("fin");
                                 p.flush ();
                                 Thread.currentThread().interrupt();
                                 pw.println (h);
                                 pw.flush ();
                                 s.close();
                                 break;




                         }

                         pw.println (h);
                         pw.flush ();

                     }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




    public static void main(String [] args){
        try{

            Socket socket = new Socket (InetAddress.getLocalHost (),port);
            System.out.println (InetAddress.getLocalHost ());
            //Socket socket = new Socket ("192.168.43.104",port);
            BufferedReader br = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
            PrintWriter pw = new PrintWriter (new OutputStreamWriter(socket.getOutputStream ()));
            Scanner sc = new Scanner (System.in);
            while(true){

                String chaine = br.readLine ();
                if(chaine!=null) {
                    String [] sentence = chaine.split ("\\s");
                    String s = "";
                    switch(sentence[0]) {

                        case "client":
                            numClient = Integer.parseInt (sentence[1]);
                            System.out.println (chaine);
                            s = sc.nextLine ();
                            pw.println (s);
                            pw.flush ();
                            break;
                        case "connect":
                            System.out.println("jai reçus une demande de connexion");
                            s = sc.nextLine ();
                            pw.println (s);
                            pw.flush ();
                            break;
                        case "host_chat":
                            System.out.println("connexion avec client étaiblie");
                            newport = Integer.parseInt (sentence[1]);
                            startServer (newport,socket,pw);
                            break;
                        case "go_chat":
                            System.out.println("connexion avec client étaiblie");
                            newport = Integer.parseInt (sentence[1]);
                            startSender (newport,socket,pw,br);
                            break;
                        default:
                            System.out.println (chaine);
                            s = sc.nextLine ();


                            if(s.equals ("quit")){
                                pw.println (s);
                                pw.flush ();
                                pw.close ();
                                socket.close ();

                            }
                            else{
                                pw.println (s);
                                pw.flush ();
                            }

                    }



                }

            }






        }catch(Exception e ){ System.out.println(e); }
    }
}
