import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Processo extends Thread{

    private long id;
    private List<Processo> threads;
    private boolean coordenador = false;
    private Processo coordenadorAtual;
    private boolean rodando = true;
    private static boolean emEleicao = false;
    private List<Request> requests = new ArrayList<Request>();
    private boolean recursoEmUso = false;

    //construtor
    public Processo(long id,List<Processo> threads) {
        this.id = id;
        this.threads = threads;
        this.coordenadorAtual = getCoordenador();
    }
    @Override
    public void run(){
        while(rodando){
            this.requisitaAcesso();
            try{
                Thread.sleep(15000 + (long) (Math.random() * 5000)); //15 a 20 segundos
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }

    public void requisitaAcesso(){
        
        if(this.coordenadorAtual == null){ 
            System.out.println("Processo " + this.id + " tentou requisitar acesso á area critica para o coordenador, mas não havia coordenador, uma eleição foi convocada ");
            realizaEleicao();
            return;
        }

        try{
            this.coordenadorAtual.agendarRequest(new Request(this.id, new Date(System.currentTimeMillis())));
            System.out.println("Processo " + this.id + " requisitou acesso á area critica para o coordenador " + this.coordenadorAtual.getId());
        }catch(Exception e){
            realizaEleicao();
            System.out.println("Processo " + this.id + " tentou requisitar acesso á area critica para o coordenador, mas não teve sucesso");
        }

        
    }

    private void realizaEleicao(){
        if(this.emEleicao){
            System.out.println("Processo " + this.id + " já está em eleição");
            return;
        }else{
            this.emEleicao = true;
        }
        new Thread(){
            @Override
            public void run() {
                System.out.println("Eleição em andamento");
                eleicao();
            }
        }.start();
    }

    public void eleicao(){
        boolean resultado = false;
        for(int i = 0; i < this.threads.size(); i++){
            if(this.threads.get(i).getId() > this.id){
                resultado =  this.threads.get(i).isRodando();
                Processo p = this.threads.get(i);
                new Thread(){
                    @Override
                    public void run() {
                        p.eleicao();
                    }
                }.start();
            }
        }
        if(!resultado && emEleicao){
            emEleicao = false;
            Processo novoCoordenador = this;
            this.coordenadorAtual = novoCoordenador;
            this.coordenador = true;
            System.out.println("Processo " + this.id +" se auto intitulou o novo coordenador");
            for(int i = 0; i < this.threads.size(); i++){
                this.threads.get(i).setCoordenadorAtual(this.coordenadorAtual);
            }
            new Thread(){
                @Override
                public void run() {
                    novoCoordenador.requests = new ArrayList<Request>();
                    novoCoordenador.ProcessarFila();
                }
            }.start();
            
        }

    }

    private boolean request( Request r){
        this.recursoEmUso =true;
        System.out.println(" --> Requisição do Processo " + r.id + ", requisitada em "+r.data+ " ATENDIDA\n");
        try{
            // acesso a recursos em area critica
            Thread.sleep(1000 + (int) (Math.random() * 1000)); //1 a 4 segundos
        }catch(Exception e){
            e.printStackTrace();
            this.recursoEmUso =false;
        }
        this.recursoEmUso =false;
        return true;
    }

    public void agendarRequest(Request r){
        System.out.println(" AGENDADO");
        this.requests.add(r);
    }

    private void ProcessarFila(){
        if(this.coordenador){   
            while(true){
                for(int i = 0; i < this.requests.size(); i++){           
                    if(!this.recursoEmUso){
                        this.request(this.requests.get(i));
                        this.requests.remove(i);
                    }
                }
                try{
                    Thread.sleep(300 ); //1 a 4 segundos
                }catch(Exception e){
                    e.printStackTrace();
                   
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public boolean isCoordenador() {
        return coordenador;
    }
    
    public Processo getCoordenador(){
        for(int i = 0; i < this.threads.size(); i++){
            if(this.threads.get(i).isCoordenador()){
              return this.threads.get(i);
            }
        }
        return null;
     }

     public void parar(){
        this.rodando = false;
     }
     public boolean isRodando(){
        return this.rodando;
     }
    public void setCoordenadorAtual(Processo coordenador){
        this.coordenadorAtual = coordenador;
    }
}
