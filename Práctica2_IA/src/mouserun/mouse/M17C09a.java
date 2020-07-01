package mouserun.mouse;
import java.util.LinkedList;
import java.util.TreeMap;
import mouserun.game.Mouse;
import mouserun.game.Grid;
import mouserun.game.Cheese;

class Celda{
    private int visitas;
    private boolean deadEnd;
    int x,y;
    LinkedList<Celda> sucesores;
    
    public Celda(){
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

    public LinkedList<Celda> getSucesores() {
        return sucesores;
    }

    public void insertarsucesor(Celda c){
        sucesores.add(c);
    }
} 


class MatrizDinamica {
    private Celda[][] matriz;
    private int tamx;
    private int tamy;
    
    public MatrizDinamica(){
        tamx=5;
        tamy=5;
        matriz=new Celda [tamx][tamy];
    }
    
    public void set(int x,int y, Celda valor){
        if(x>=tamx){
            Celda[][]aux=matriz;
            matriz= new Celda[x+1][];
            System.arraycopy(aux,0,matriz,0,aux.length);
            for(int i=tamx;i<x+1;i++){
                matriz[i]=new Celda[tamy];
            }
            tamx=x+1;
        }
        
        if(y>=tamy){
            tamy=y+1;
            for(int i=0;i<tamx;i++){
                Celda[]aux=matriz[i];
                matriz[i]= new Celda[tamy];
                System.arraycopy(aux,0,matriz[i],0,aux.length);
            }
        }
        matriz[x][y]=valor;
    }
    
    public Celda get(int x, int y) {
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


public class M17C09a extends Mouse{
    private MatrizDinamica matriz;
    private int queso=0;
    private boolean ultimoBomba=false;
    private LinkedList<Integer> camino;
    
    public M17C09a(){
        super ("superRaton1");
        matriz=new MatrizDinamica();
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
            Celda celda=new Celda();
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
                TreeMap cerrados=new TreeMap();
                PrimeroProfundidadRecursiva(matriz.get(x, y),false, camino,cheese[queso].getX(),cheese[queso].getY(),cerrados);
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
            Celda celda=new Celda();
            matriz.set(x,y+1,celda);
            }
            int v=matriz.getVisitas(x,y+1);
            if(v<min)
                min=v;
        }
        if(abajo){  
            if(matriz.get(x,y-1)==null){
            Celda celda=new Celda();
            matriz.set(x,y-1,celda);
            }
            int v=matriz.getVisitas(x,y-1);
            if(v<min)
                min=v;
        }
        if(derecha){  
            if(matriz.get(x+1,y)==null){
            Celda celda=new Celda();
            matriz.set(x+1,y,celda);
            }
            int v=matriz.getVisitas(x+1,y);
            if(v<min)
                min=v;
        }
        if(izquierda){  
            if(matriz.get(x-1,y)==null){
            Celda celda=new Celda();
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
            Celda celda=new Celda();
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
    
    //busqueda primero en profundidad de forma recursiva
    private boolean PrimeroProfundidadRecursiva(Celda celdaActual, boolean fin, LinkedList<Integer> camino, int xQueso, int yQueso, TreeMap<String,Celda> map){
        if(objetivo(celdaActual, xQueso, yQueso)){
            fin=true;
        }
        map.put(celdaActual.getX()+","+celdaActual.getY(),celdaActual);
        LinkedList<Celda> sucesores= celdaActual.getSucesores();
        int i=0;
        while(i<sucesores.size()&&(!fin)){
            if(!map.containsKey(sucesores.get(i).getX()+","+sucesores.get(i).getY())){
                camino.addLast(calcularDireccion(celdaActual, sucesores.get(i)));
                fin=PrimeroProfundidadRecursiva(sucesores.get(i),fin,camino,xQueso,yQueso,map);
            }
            i++;
        }
        if(!fin && !camino.isEmpty()){
            camino.removeLast();
        }
        return fin;
    }
    
    //indica si la celda pasada como parametro es la del queso que buscamos
    private boolean objetivo(Celda celda, int xQueso, int yQueso){
        if(celda.getX()==xQueso && celda.getY()==yQueso)
            return true;
        return false;
    }
    
    //calcula la direccion en la que se ha de mover para pasar de la celda de origen a la de destino, siendo adyacentes entre si
    private int calcularDireccion(Celda origen, Celda destino){
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