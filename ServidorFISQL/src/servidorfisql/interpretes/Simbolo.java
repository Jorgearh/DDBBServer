package servidorfisql.interpretes;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jorge
 */
public class Simbolo implements Cloneable{
    public String name;
    public String value;
    public String tipe;
    public String rol;
    public String ambito;

    boolean statico;
    public String acceso;

    //parte de objeto
    boolean isobjeto;
    Hashtable<String, Simbolo> atributos;

    //parte de funcion
    public Proc metodo;

    public Simbolo() {
        this.value = "";
        this.tipe = "";
        this.name = "";
        this.rol = "";
        this.ambito = "";
        statico = false;
        acceso = "publico";
        isobjeto = false;
        atributos = null;
        metodo = null;
    }

    @Override
    @SuppressWarnings({"CloneDeclaresCloneNotSupported", "RedundantStringConstructorCall", "null"})
    protected Object clone() {
        Simbolo obj = null;
        try {
            obj = (Simbolo) super.clone(); //To change body of generated methods, choose Tools | Templates.
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Simbolo.class.getName()).log(Level.SEVERE, null, ex);
        }
        obj.name = new String(name);
        obj.value = new String(value);
        obj.tipe = new String(tipe);
        obj.rol = new String(rol);
        obj.ambito = new String(ambito);
        obj.isobjeto = isobjeto;
        obj.statico = statico;
        obj.atributos=atributos;


        return obj;
    }
}
