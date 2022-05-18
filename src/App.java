import java.util.ArrayList;
import java.util.List;

public class App {
    private static List<Processo> threads = new ArrayList<Processo>();
    public static void main(String[] args) throws Exception {
        
        new Thread(){
            @Override
            public void run() {
                novoProcesso();
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                mataCoordenador();
            }
        }.start();
    }
    public static void novoProcesso(){
        do{
            long id;
            do{
            id = (long) (Math.random() * 10000);
            }while(possuiId(id));
            Processo p = new Processo(id,threads);
            threads.add(p);
            p.start();
            System.out.println(" NOVO processo " + id + " criado");
            try{
                Thread.sleep(3000);
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }while(true);
    }

    public static void mataCoordenador(){
        do{
            try{
                Thread.sleep(60000);
            }catch(Exception e){
                e.printStackTrace();
            }
            int coordenador = findCoordenador();
            if(coordenador != -1){
                
                System.out.println("Coordenador " + threads.get(coordenador).getId()+" morreu");
                threads.get(coordenador).parar();
                threads.remove(coordenador);
            }else{
                System.out.println("Não há coordenadores");
            }
            
        }while(true);
    }

    public static int findCoordenador(){
        for(int i = 0; i < threads.size(); i++){
            if(threads.get(i).isCoordenador()){
              return i;
            }
        }
        return -1;
     }

    public static boolean possuiId(long id){
       for(int i = 0; i < threads.size(); i++){
           if(threads.get(i).getId() == id){
               System.out.println("Id "+id+"já existe");
             return true;
           }
       }
       return false;
    }
}
