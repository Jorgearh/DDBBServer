package servidorfisql.interpretes.Analizadores;

import java.util.ArrayList;

/**
 *
 * @author jorge
 */
public class Nodo {
    
    public String token;
    public String valor;
    public ArrayList<Nodo> hijos;
    
    int col;
    int row;

    public Nodo() {
        this.token = "";
        this.valor = "";
        this.hijos = new ArrayList<>();
    }

    public Nodo(String token) {
        this.token = token;
        this.valor = "";
        this.hijos = new ArrayList<>();
    }

    public Nodo(String token, String valor) {
        this.token = token;
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }
    
    public Nodo(String token, String valor, int fila, int columna) {
        this.token = token;
        this.valor = valor;
        this.hijos = new ArrayList<>();
        
        this.row = fila;
        this.col = columna;
    }

    
    
    public void agregarHijo(Nodo hijo){
        this.hijos.add(hijo);
    }
    
    public void agregarHijos(Nodo hijo1, Nodo hijo2){
        this.hijos.add(hijo1);
        this.hijos.add(hijo2);
    }
    
    
    
    public void setToken(String token){
        this.token = token;
    }
 
    
    public Nodo getHijo(int index){
        if(index < this.hijos.size())
            return this.hijos.get(index);
        else{
            System.err.println("Error accesando hijo ["+ index +"], solo existen ["+ this.hijos.size()+"] hijos");
            return null;
        }
    }
}
