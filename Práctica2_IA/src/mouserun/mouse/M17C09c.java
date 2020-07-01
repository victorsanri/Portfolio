package mouserun.mouse;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeMap;
import mouserun.game.Mouse;
import mouserun.game.Grid;
import mouserun.game.Cheese;

class Celda2{
    private int visitas;
    private boolean deadEnd;
    int x,y;
    LinkedList<Celda2> sucesores;
    
    public Celda2(){
        visitas=0;
        deadEnd=false;
        sucesores= new LinkedList();
        x=-1;
        y=-1;
    }
    public int getVisitas(){
        return visitas;
    }
    public boolean getDeadEnd(){
        return deadEnd;
    }
    public void visitar(){
        visitas++;
    }
    public void cerrar(){
        deadEnd=true;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public LinkedList<Celda2> getSucesores() {
        return sucesores;
    }

    public void insertarsucesor(Celda2 c){
        sucesores.add(c);
    }
} 

//Nodos que contienen la informacion necesaria para realizar el la busqueda A*
class Nodo1 {
    private int coste;
    private int evaluacion;
    private Nodo1 padre;
    private Celda2 actual;
    
    public Nodo1 (Celda2 actual){
        this.padre=null;
        this.actual=actual;
        this.coste=0;
        this.evaluacion=-1;
    }
    
    public void setCoste(int coste){
        this.coste=coste;
    }

    public int getCoste() {
        return coste;
    }
    
    public Nodo1 getPadre() {
        return padre;
    }

    public void setPadre(Nodo1 padre) {
        this.padre = padre;
    }

    public int getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(int evaluacion) {
        this.evaluacion = evaluacion;
    }

    public Celda2 getActual() {
        return actual;
    }

    public void setActual(Celda2 actual) {
        this.actual = actual;
    }   
}

//Sera la clase de comparacion usada para ordenar la lista de abiertos en la busqueda A*
class MyComparator1 implements Comparator<Nodo1>
{
    @Override
    public int compare(Nodo1 n1, Nodo1 n2)
    {
        if((n1.getCoste()+n1.getEvaluacion())<(n2.getCoste()+n2.getEvaluacion())) return -1;
        if((n1.getCoste()+n1.getEvaluacion())>(n2.getCoste()+n2.getEvaluacion())) return 1;
        return 0;
    }
}

class MatrizDinamica2 {
    private Celda2[][] matriz;
    private int tamx;
    private int tamy;
    
    public MatrizDinamica2(){
        tamx=5;
        tamy=5;
        matriz=new Celda2 [tamx][tamy];
    }
    
    public void set(int x,int y, Celda2 valor){
        if(x>=tamx){
            Celda2[][]aux=matriz;
            matriz= new Celda2[x+1][];
            System.arraycopy(aux,0,matriz,0,aux.length);
            for(int i=tamx;i<x+1;i++){
                matriz[i]=new Celda2[tamy];
            }
            tamx=x+1;
        }
        
        if(y>=tamy){
            tamy=y+1;
            for(int i=0;i<tamx;i++){
                Celda2[]aux=matriz[i];
                matriz[i]= new Celda2[tamy];
                System.arraycopy(aux,0,matriz[i],0,aux.length);
            }
        }
        matriz[x][y]=valor;
    }
    
    public Celda2 get(int x, int y) {
        if(x<tamx&&y<tamy)
            return matriz[x][y];
        return null;
    }
    
    public int getVisitas(int x, int y) {
        return matriz[x][y].getVisitas();
    }
    
    public void visitar(int x, int y) {
        matriz[x][y].visitar();
    }
    
    public boolean getDeadEnd(int x, int y) {
        return matriz[x][y].getDeadEnd();
    }
    
    public void cerrar(int x, int y) {
        matriz[x][y].cerrar();
    }    
}


public class M17C09c extends Mouse{
    private MatrizDinamica2 matriz;
    private int queso=0;
    private boolean ultimoBomba=false;
    private LinkedList<Integer> camino;
    
    public M17C09c(){
        super ("superRaton3");
        matriz=new MatrizDinamica2();
        camino=new LinkedList<>();
    }
    
    @Override
    public int move(Grid currentGrid, Cheese[] cheese){ 
    //Tratamiento de la informacion
        //obtenemos las coordendas y la distancia al queso para la celda acutal
        int x=currentGrid.getX();
        int y=currentGrid.getY();
        int distX=cheese[queso].getX()-x;
        int distY=cheese[queso].getY()-y;
        
        //cuando lleguemos a un queso avanzamos el contador al siguiente
        if(distX==0 && distY==0)
            queso++;
        
        //si la celda no esta creada la creamos e iniciamos
        if(matriz.get(x,y)==null){
            Celda2 celda=new Celda2();
            celda.setX(x);
            celda.setY(y);
            matriz.set(x,y,celda);
        }
        
        //incrementar el numero de celdas visitadas si es una nueva celda
        if(matriz.getVisitas(x,y)==0)
            incExploredGrids();
        
        //aumentamos las visitas de la celda
        matriz.visitar(x,y);
        
        //ponemos todas las direcciones a falso por defecto
        boolean arriba=false;
        boolean abajo=false;
        boolean derecha=false;
        boolean izquierda=false;
        
        //ponemos a true aquellas direcciones a las que podemos ir
        if (currentGrid.canGoUp()) arriba=true;
	if (currentGrid.canGoDown()) abajo=true;
	if (currentGrid.canGoLeft()) izquierda=true;
	if (currentGrid.canGoRight()) derecha=true;
        
        //descartamos los que sean caminos cortados (salvo que estemos en uno por culpa de una bomba, para garantizar una salida
        if(!caminoCortado(x,y)){
            if(arriba)
                if(caminoCortado(x,y+1))
                    arriba=false;
            if(abajo)
                if(caminoCortado(x,y-1))
                    abajo=false;
            if(derecha)
                if(caminoCortado(x+1,y))
                    derecha=false;
            if(izquierda)
                if(caminoCortado(x-1,y))
                    izquierda=false;
        }
        
        //si es la primera vez que visitamos la celda guardamos sus sucesores
        if(matriz.get(x,y).getVisitas()==1){
            añadirSucesores(arriba, abajo, izquierda, derecha, x, y);
        }
        
        //comprobamos si hay algún queso que no hayamos comido para así no cortar ese camino
        boolean hayQueso=false;
        for(int i=queso+1;i<cheese.length;i++){
            if(cheese[i].getX()==x&&cheese[i].getY()==y){
                hayQueso=true;
            }
        }        
        
        //si solo hay una opcion marcamos la celda como cortada (salvo que haya algun queso)
        if(!hayQueso){
            int cont=0;
            if(arriba)cont++;
            if(abajo)cont++;
            if(izquierda)cont++;
            if(derecha)cont++;
            if(cont==1)
                matriz.cerrar(x,y);
        }
        
    //Colocacion de bombas
        //cuando pasemos por un queso ponemos 1 bomba
        for(int i=0;i<cheese.length;i++){
            if(cheese[i].getX()==x&&cheese[i].getY()==y){
                if(ultimoBomba==false){
                    ultimoBomba=true;
                    return Mouse.BOMB;
                }
            }
        }
        ultimoBomba=false;
        
        
    //Búsqueda
        //iniciaremos una busqueda cuando ya hayamos estado en la celda en la que se encuentra el queso que buscamos
        //y no estemos siguiendo ya un camino que se busco antes
        if(matriz.get(cheese[queso].getX(),cheese[queso].getY())!=null){
            if(matriz.get(cheese[queso].getX(),cheese[queso].getY()).getVisitas()>0 && camino.isEmpty()){
                camino=algoritmoAEstrella(matriz.get(x, y),cheese[queso].getX(),cheese[queso].getY());
            }
        }
        
        //seguimos el camino que hemos encontrado hasta llegar al queso
        if(!camino.isEmpty()){
            int direccion=camino.get(0);
            camino.removeFirst();
            return direccion;
        }
        
        
    //Exploración orientada
        //buscamos el minimo valor de visitas en las celdas adyacentes a la actual
        int min=1000;
        if(arriba){  
            if(matriz.get(x,y+1)==null){
            Celda2 celda=new Celda2();
            matriz.set(x,y+1,celda);
            }
            int v=matriz.getVisitas(x,y+1);
            if(v<min)
                min=v;
        }
        if(abajo){  
            if(matriz.get(x,y-1)==null){
            Celda2 celda=new Celda2();
            matriz.set(x,y-1,celda);
            }
            int v=matriz.getVisitas(x,y-1);
            if(v<min)
                min=v;
        }
        if(derecha){  
            if(matriz.get(x+1,y)==null){
            Celda2 celda=new Celda2();
            matriz.set(x+1,y,celda);
            }
            int v=matriz.getVisitas(x+1,y);
            if(v<min)
                min=v;
        }
        if(izquierda){  
            if(matriz.get(x-1,y)==null){
            Celda2 celda=new Celda2();
            matriz.set(x-1,y,celda);
            }
            int v=matriz.getVisitas(x-1,y);
            if(v<min)
                min=v;
        }
        
        //descartamos las celdas cuyo valor no coincida con el del menor numero de visitas
        if(arriba){
            if(matriz.getVisitas(x,y+1)!=min)
                arriba=false;
        }
        if(abajo){
            if(matriz.getVisitas(x,y-1)!=min)
                abajo=false;
        }
        if(derecha){
            if(matriz.getVisitas(x+1,y)!=min)
                derecha=false;
        }
        if(izquierda){
            if(matriz.getVisitas(x-1,y)!=min)
                izquierda=false;
        }
        
        //si aun hay varias direciones posibles, las ordenamos en prioridad atendiendo a la distancia al proximo queso
        //se prioriza la componente que mas distancia al queso tenga, intentando reducirla y no aumentarla, si no es posible reducirla,
        //se premiara a la que reduzca la distancia al queso en la otra componente
        if(Math.abs(distX)>Math.abs(distY)&& distX>=0){
            if(derecha){
                return Mouse.RIGHT;
            }if(distY>=0){
                if(arriba){
                    return Mouse.UP;
                }if(abajo){
                    return Mouse.DOWN;
                }
            }else{
                if(abajo){
                    return Mouse.DOWN;
                }if(arriba){
                    return Mouse.UP;
                }
            }
            if(izquierda){
                return Mouse.LEFT;
            }
        }
        if(Math.abs(distX)>Math.abs(distY)&& distX<0){
            if(izquierda){
                return Mouse.LEFT;
            }if(distY>=0){
                if(arriba){
                    return Mouse.UP;
                }if(abajo){
                    return Mouse.DOWN;
                }
            }else{
                if(abajo){
                    return Mouse.DOWN;
                }if(arriba){
                    return Mouse.UP;
                }
            }
            if(derecha){
                return Mouse.RIGHT;
            }
        }
        if(Math.abs(distX)<=Math.abs(distY)&& distY>=0){
            if(arriba){
                return Mouse.UP;
            }if(distX>=0){
                if(derecha){
                    return Mouse.RIGHT;
                }if(izquierda){
                    return Mouse.LEFT;
                }
            }else{
                if(izquierda){
                    return Mouse.LEFT;
                }if(derecha){
                    return Mouse.RIGHT;
                }    
            }
            if(abajo){
                return Mouse.DOWN;
            }
        }
        if(Math.abs(distX)<=Math.abs(distY)&& distY<0){
            if(abajo){
                return Mouse.DOWN;
            }if(distX>=0){
                if(derecha){
                    return Mouse.RIGHT;
                }if(izquierda){
                    return Mouse.LEFT;
                }
            }else{
                if(izquierda){
                    return Mouse.LEFT;
                }if(derecha){
                    return Mouse.RIGHT;
                }
            }
            if(arriba){
                return Mouse.UP;
            }
        }
        return Mouse.UP;
    }
    
    @Override
    public void newCheese(){
        
    }
    
    //si nos comemos una bomba borramos el camino que estabamos siguiendo porque ya no sirve
    @Override
    public void respawned(){
        camino.clear();
    }
    
    //nos devuelve si la celda con esas coordenadas esta o no cortada
    private boolean caminoCortado(int x,int y){
        if(matriz.get(x,y)==null){
            Celda2 celda=new Celda2();
            celda.setX(x);
            celda.setY(y);
            matriz.set(x,y,celda);
        }
        return matriz.getDeadEnd(x,y);
    }

    //se añaden como sucesores las celdas a las que puedo llegar desde la actual
    private void añadirSucesores(boolean arriba, boolean abajo, boolean izquierda, boolean derecha, int x, int y){
        if(arriba){
            matriz.get(x, y).insertarsucesor(matriz.get(x,y+1));
        }
        if(abajo){
            matriz.get(x, y).insertarsucesor(matriz.get(x,y-1));
        }
        if(izquierda){
            matriz.get(x, y).insertarsucesor(matriz.get(x-1,y));
        }
        if(derecha){
            matriz.get(x, y).insertarsucesor(matriz.get(x+1,y));
        }
    }
    
    //realiza la busqueda A* y devuelve el camino encontrado, camino vacio si no lo encuentra
    private LinkedList<Integer> algoritmoAEstrella(Celda2 actual, int xQueso, int yQueso ){
        Comparator<Nodo1> comparator = new MyComparator1();
        TreeMap<String,Nodo1> Tabla_A=new TreeMap<>();
        PriorityQueue<Nodo1> abierta=new PriorityQueue<>(11,comparator);
        
        //inicializamos abierta con el nodo actual, e iteramos mientras haya nodos en abierta
        Nodo1 n=new Nodo1(actual);
        abierta.add(n);
        Tabla_A.put(n.getActual().getX()+","+n.getActual().getY(), n);
        while(!abierta.isEmpty()){
            n=abierta.poll();
            
            if(objetivo(n.getActual(),xQueso,yQueso)){
                return calcularCamino(n, Tabla_A);
            }
            
            LinkedList<Celda2> sucesores= n.getActual().getSucesores();
            for(int i=0;i<sucesores.size();i++){
                if(Tabla_A.containsKey(sucesores.get(i).getX()+","+sucesores.get(i).getY())){
                    Nodo1 q=Tabla_A.get(sucesores.get(i).getX()+","+sucesores.get(i).getY());    
                    rectificar(q,n,1,Tabla_A); 
                    abierta=reordenar(abierta);
                } else {
                    Nodo1 q=new Nodo1(sucesores.get(i));
                    q.setPadre(n);
                    q.setCoste(q.getPadre().getCoste()+1);
                    q.setCoste(n.getCoste()+1);
                    q.setEvaluacion(numParedes(q));
                    Tabla_A.put(q.getActual().getX()+","+q.getActual().getY(), q);
                    abierta.add(q);
                }
            }
       }
       LinkedList<Integer> caminoA= new LinkedList();
       return   caminoA;
    }
    
    //Si se han rectificado los caminos a los nodos se reordena abiertos, para reflejar dichos cambios
    private PriorityQueue<Nodo1> reordenar (PriorityQueue<Nodo1> cola){
        Comparator<Nodo1> comparator = new MyComparator1();
        PriorityQueue<Nodo1> nuevo=new PriorityQueue<>(11,comparator);
        while(!cola.isEmpty()){
            nuevo.add(cola.poll());
        }
        return nuevo;
    }
    
    //Se corrigen los caminos a los nodos si se encuentra un camino de mejor
    private void rectificar(Nodo1 n,Nodo1 p, int costepn, TreeMap<String,Nodo1> Tabla_A){
        if(p.getCoste()+costepn< n.getCoste()){
            n.setCoste(p.getCoste()+costepn);
            n.setPadre(p);
            RectificarLista(n, Tabla_A);
                    
        }
    }
    
    //Se hace lo mismo con todos sus sucesores
    private void RectificarLista(Nodo1 n, TreeMap<String,Nodo1> Tabla_A){
        LinkedList<Celda2> lista=n.getActual().getSucesores();
        for(int i=0;i<lista.size();i++){
            if(Tabla_A.containsKey(lista.get(i).getX()+","+lista.get(i).getY())){
                Nodo1 q=Tabla_A.get(lista.get(i).getX()+","+lista.get(i).getY()); 
                rectificar(q,n,1,Tabla_A);
            }
        }
    }
    
    //Cuando se llega al nodo objetivo se calcula el camino, viendo la direccion que hay que seguir para venir desde el padre, llegando hasta el nodo inicial
    private LinkedList<Integer> calcularCamino(Nodo1 n, TreeMap<String,Nodo1> Tabla_A ){
        Nodo1 padre=n.getPadre();
        LinkedList<Integer> caminoA=new LinkedList();
        do{
            int direccion=calcularDireccion(padre.getActual(),n.getActual());
            caminoA.addFirst(direccion);
            n=padre;
            padre=padre.getPadre();
           
        } while(padre!=null);
        return caminoA;
    }
    
    //Función que sirve de heurística
    private int numParedes(Nodo1 n){
        return 4-n.getActual().getSucesores().size();
    }
    
    //indica si la celda pasada como parametro es la del queso que buscamos
    private boolean objetivo(Celda2 celda, int xQueso, int yQueso){
        if(celda.getX()==xQueso && celda.getY()==yQueso)
            return true;
        return false;
    }
    
    //calcula la direccion en la que se ha de mover para pasar de la celda de origen a la de destino, siendo adyacentes entre si
    private int calcularDireccion(Celda2 origen, Celda2 destino){
        int varX=destino.getX()-origen.getX();
        int varY=destino.getY()-origen.getY();
        if(varX==0){
            if(varY==1)
                return Mouse.UP;
            if(varY==-1)
                return Mouse.DOWN;
        }else{
            if(varX==1)
                return Mouse.RIGHT;
            if(varX==-1)
                return Mouse.LEFT;
        }
        return -1;
    }
    
}