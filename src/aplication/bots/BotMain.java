package aplication.bots;

public class BotMain {
    
    public static void main(String[]args){
        String endereco = "localhost"; //127.0.0.1
        int porta = 12345;

        int nBots = 5;
        
        System.out.println("=================BOTS====================");
        
        for(int i=0; i< nBots; i++){
            Bot bot = new Bot(endereco,porta,i+1);
            Thread botThread = new Thread(bot);
            botThread.setName("Bot "+(i+1));
            botThread.start();
        }
        
    }
    
}
