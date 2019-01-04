package aplication.bots;

import java.util.ArrayList;

public class BotMain {
/*public static final String ANSI_RESET = "\u001B[0m";
public static final String ANSI_BLACK = "\u001B[30m";
public static final String ANSI_RED = "\u001B[31m";
public static final String ANSI_GREEN = "\u001B[32m";
public static final String ANSI_YELLOW = "\u001B[33m";
public static final String ANSI_BLUE = "\u001B[34m";
public static final String ANSI_PURPLE = "\u001B[35m";
public static final String ANSI_CYAN = "\u001B[36m";*/

    public static void main(String[]args){
        String endereco = "localhost"; //127.0.0.1
        int porta = 12345;
        ArrayList<String> cores = new ArrayList();
        cores.add("\033[1;30m");cores.add("\033[1;31m");
        cores.add("\033[1;32m");
        cores.add("\033[1;34m");cores.add("\033[1;35m");
        cores.add("\033[1;36m");

        
        int nBots = 10;
        
        System.out.println("=================BOTS====================");
            System.out.println(cores.size());
        int y=0;
        for(int i=0; i< nBots; i++){
            Bot bot = new Bot(endereco,porta,i+1,cores.get(y));
            Thread botThread = new Thread(bot);
            botThread.setName("Bot "+(i+1));
            botThread.start();
            if(y+1==cores.size()){y=0;}else y++;
        }
        
    }
    
}
