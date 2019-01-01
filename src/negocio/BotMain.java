/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

/**
 *
 * @author raphael
 */
public class BotMain {
    
    public static void main(String[]args){
        String endereco = "localhost"; //127.0.0.1
        int porta = 12345;

        int nBots = 1;
        
        System.out.println("=================BOTS====================");
        
        for(int i=0; i< nBots; i++){
            Bot bot = new Bot(endereco,porta,i+1);
            Thread botThread = new Thread(bot);
            botThread.setName("Bot "+(i+1));
            botThread.start();
        }
        
    }
    
}
