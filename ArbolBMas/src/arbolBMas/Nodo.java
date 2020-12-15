package arbolBMas;

enum TipoNodoArbol {
    NodoInterno,
    NodoHoja
}

abstract class Nodo<TKey extends Comparable<TKey>> {

    protected Object[] claves;
    protected int numClaves;
    protected Nodo<TKey> nodoPadre;
    protected Nodo<TKey> nodoHermanoIzquierdo;
    protected Nodo<TKey> nodoHermanoDerecho;

    protected Nodo() {
        this.numClaves = 0;
        this.nodoPadre = null;
        this.nodoHermanoIzquierdo = null;
        this.nodoHermanoDerecho = null;
    }

    public int getNumClaves() {
        return this.numClaves;
    }

    @SuppressWarnings("unchecked")
    public TKey getClave(int index) {
        return (TKey) this.claves[index];
    }

    public void setClave(int index, TKey clave) {
        this.claves[index] = clave;
    }

    public Nodo<TKey> getPadre() {
        return this.nodoPadre;
    }

    public void setPadre(Nodo<TKey> parent) {
        this.nodoPadre = parent;
    }

    public abstract TipoNodoArbol getTipoNodo();

    /**
     * Busque una clave en el nodo actual, si encuentra la clave, devuelva su
     * posición; de lo contrario, devuelva -1 para un nodo hoja, devuelva el
     * índice del nodo secundario que debe contener la clave para un nodo
     * interno.
     */
    public abstract int buscar(TKey key);

    /*Los códigos siguientes se utilizan para respaldar la operación de inserción*/
    public boolean nodoDesbordado() {
        return this.getNumClaves() == this.claves.length;
    }

    public Nodo<TKey> tratarNodoDesbordado() {
        int indiceMedio = this.getNumClaves() / 2;
        TKey claveArriba = this.getClave(indiceMedio);

        Nodo<TKey> newRNode = this.dividir();

        if (this.getPadre() == null) {
            this.setPadre(new NodoInterno<TKey>());
        }
        newRNode.setPadre(this.getPadre());

        /* Mantener enlaces de nodos hermanos */
        newRNode.setNodoHermanoIzquierdo(this);
        newRNode.setNodoHermanoDerecho(this.nodoHermanoDerecho);
        if (this.getNodoHermanoDerecho() != null) {
            this.getNodoHermanoDerecho().setNodoHermanoIzquierdo(newRNode);
        }
        this.setNodoHermanoDerecho(newRNode);

        /* Empujar hacia arriba una clave para el nodo interno principal */
        return this.getPadre().subirClave(claveArriba, this, newRNode);
    }

    protected abstract Nodo<TKey> dividir();

    protected abstract Nodo<TKey> subirClave(TKey clave, Nodo<TKey> nodoHijoIzquierda, Nodo<TKey> nodoHijoDerecha);

    /* Los códigos siguientes se utilizan para respaldar la operación de eliminación */
    public boolean nodoBajo() {
        return this.getNumClaves() < (this.claves.length / 2);
    }

    public boolean puedePrestarUnaLlave() {
        return this.getNumClaves() > (this.claves.length / 2);
    }

    public Nodo<TKey> getNodoHermanoIzquierdo() {
        if (this.nodoHermanoIzquierdo != null && this.nodoHermanoIzquierdo.getPadre() == this.getPadre()) {
            return this.nodoHermanoIzquierdo;
        }
        return null;
    }

    public void setNodoHermanoIzquierdo(Nodo<TKey> hermano) {
        this.nodoHermanoIzquierdo = hermano;
    }

    public Nodo<TKey> getNodoHermanoDerecho() {
        if (this.nodoHermanoDerecho != null && this.nodoHermanoDerecho.getPadre() == this.getPadre()) {
            return this.nodoHermanoDerecho;
        }
        return null;
    }

    public void setNodoHermanoDerecho(Nodo<TKey> hermano) {
        this.nodoHermanoDerecho = hermano;
    }

    public Nodo<TKey> tratarNodoBajo() {
        if (this.getPadre() == null) {
            return null;
        }

        /* Intentar de pedir prestada una clave a un hermano */
        Nodo<TKey> hermanoIzquierda = this.getNodoHermanoIzquierdo();
        if (hermanoIzquierda != null && hermanoIzquierda.puedePrestarUnaLlave()) {
            this.getPadre().procesoTransferirHijos(this, hermanoIzquierda, hermanoIzquierda.getNumClaves() - 1);
            return null;
        }

        Nodo<TKey> hermanoDerehco = this.getNodoHermanoDerecho();
        if (hermanoDerehco != null && hermanoDerehco.puedePrestarUnaLlave()) {
            this.getPadre().procesoTransferirHijos(this, hermanoDerehco, 0);
            return null;
        }

        /* No se puede pedir prestada una llave a ningún hermano, luego hacer la fusión con el hermano*/
        if (hermanoIzquierda != null) {
            return this.getPadre().procesoFusionarHijos(hermanoIzquierda, this);
        } else {
            return this.getPadre().procesoFusionarHijos(this, hermanoDerehco);
        }
    }

    protected abstract void procesoTransferirHijos(Nodo<TKey> borrower, Nodo<TKey> lender, int borrowIndex);

    protected abstract Nodo<TKey> procesoFusionarHijos(Nodo<TKey> hijoIzquierdo, Nodo<TKey> hijoDerecho);

    protected abstract void fusionConHermano(TKey sinkKey, Nodo<TKey> hermanoDerecho);

    protected abstract TKey transferenciaDeHermano(TKey sinkKey, Nodo<TKey> hermano, int borrowIndex);
}
