package arbolBMas;

/**
 * A B+ tree Since the structures and behaviors between internal node and
 * external node are different, so there are two different classes for each kind
 * of node.
 *
 * @param <TKey> the data type of the key
 * @param <TValue> the data type of the value
 */
public class ArbolBMas<TKey extends Comparable<TKey>, TValue> {

    private Nodo<TKey> raiz;

    public ArbolBMas() {
        this.raiz = new NodoHoja<TKey, TValue>();
    }

    public Nodo<TKey> getRaiz() {
        return raiz;
    }

    /**
     * Insertar una nueva clave y su valor al árbol B+.
     */
    public void insertar(TKey clave, TValue valor) {
        NodoHoja<TKey, TValue> hoja = this.encontrarElNodoHoja(clave);
        hoja.insertarClave(clave, valor);

        if (hoja.nodoDesbordado()) {
            Nodo<TKey> n = hoja.tratarNodoDesbordado();
            if (n != null) {
                this.raiz = n;
            }
        }
    }

    /**
     *
     * Busque una clave en el árbol y devuelva su valor asociado.
     */
    public TValue buscar(TKey clave) {
        NodoHoja<TKey, TValue> hoja = this.encontrarElNodoHoja(clave);

        int index = hoja.buscar(clave);
        return (index == -1) ? null : hoja.getValor(index);
    }

    /**
     *
     * Elimina una clave y su valor asociado del árbol.
     *
     * @return
     */
    public String eliminar(TKey key) {
        NodoHoja<TKey, TValue> hoja = this.encontrarElNodoHoja(key);

        if (hoja.eliminar(key)) {
            if (hoja.nodoBajo()) {
                Nodo<TKey> n = hoja.tratarNodoBajo();
                if (n != null) {
                    this.raiz = n;
                }
            }else{
               int indice = hoja.nodoPadre.buscar(key)-1;
                //System.out.println("indice:"+indice);
                hoja.nodoPadre.setClave(indice, hoja.getClave(0)); 
            }
        }

        

        return null;
    }

    /**
     *
     * Busque el nodo hoja que debe contener la clave especificada
     */
    @SuppressWarnings("unchecked")
    private NodoHoja<TKey, TValue> encontrarElNodoHoja(TKey key) {
        Nodo<TKey> nodo = this.raiz;
        while (nodo.getTipoNodo() == TipoNodoArbol.NodoInterno) {
            nodo = ((NodoInterno<TKey>) nodo).getHijo(nodo.buscar(key));
        }

        return (NodoHoja<TKey, TValue>) nodo;
    }

    public void inorden(Nodo<TKey> raiz) {
    }
}
