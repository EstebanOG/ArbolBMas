package arbolBMas;

class NodoHoja<TKey extends Comparable<TKey>, TValue> extends Nodo<TKey> {

    protected final static int ordenHojas = 4;
    private Object[] valores;

    public NodoHoja() {
        this.claves = new Object[ordenHojas + 1];
        this.valores = new Object[ordenHojas + 1];
    }

    @SuppressWarnings("unchecked")
    public TValue getValor(int index) {
        return (TValue) this.valores[index];
    }

    public void setValor(int index, TValue value) {
        this.valores[index] = value;
    }

    @Override
    public TipoNodoArbol getTipoNodo() {
        return TipoNodoArbol.NodoHoja;
    }

    @Override
    public int buscar(TKey key) {
        for (int i = 0; i < this.getNumClaves(); ++i) {
            int cmp = this.getClave(i).compareTo(key);
            if (cmp == 0) {
                return i;
            } else if (cmp > 0) {
                return -1;
            }
        }

        return -1;
    }

    /* Los códigos siguientes se utilizan para respaldar la operación de inserción*/
    public void insertarClave(TKey clave, TValue valor) {
        int index = 0;
        while (index < this.getNumClaves() && this.getClave(index).compareTo(clave) < 0) {
            ++index;
        }
        this.insertAt(index, clave, valor);
    }

    private void insertAt(int index, TKey clave, TValue valor) {
        /* Mover espacio para la nueva llave */
        for (int i = this.getNumClaves() - 1; i >= index; --i) {
            this.setClave(i + 1, this.getClave(i));
            this.setValor(i + 1, this.getValor(i));
        }

        /* Insertar nueva clave y valor */
        this.setClave(index, clave);
        this.setValor(index, valor);
        ++this.numClaves;
    }

    /**
     * Cuando divide un nodo hoja, la clave central se mantiene en el nuevo nodo
     * y se empuja al nodo padre.
     */
    @Override
    protected Nodo<TKey> dividir() {
        int indiceMedio = this.getNumClaves() / 2;

        NodoHoja<TKey, TValue> newRNode = new NodoHoja<TKey, TValue>();
        for (int i = indiceMedio; i < this.getNumClaves(); ++i) {
            newRNode.setClave(i - indiceMedio, this.getClave(i));
            newRNode.setValor(i - indiceMedio, this.getValor(i));
            this.setClave(i, null);
            this.setValor(i, null);
        }
        newRNode.numClaves = this.getNumClaves() - indiceMedio;
        this.numClaves = indiceMedio;

        return newRNode;
    }

    @Override
    protected Nodo<TKey> subirClave(TKey key, Nodo<TKey> leftChild, Nodo<TKey> rightNode) {
        throw new UnsupportedOperationException();
    }

    /* Los códigos siguientes se utilizan para respaldar la operación de eliminación */
    public boolean eliminar(TKey key) {
        int index = this.buscar(key);
        if (index == -1) {
            return false;
        }

        this.eliminarAt(index);
        return true;
    }

    private void eliminarAt(int index) {
        int i = index;
        for (i = index; i < this.getNumClaves() - 1; ++i) {
            this.setClave(i, this.getClave(i + 1));
            this.setValor(i, this.getValor(i + 1));
        }
        this.setClave(i, null);
        this.setValor(i, null);
        --this.numClaves;
    }

    @Override
    protected void procesoTransferirHijos(Nodo<TKey> borrower, Nodo<TKey> lender, int borrowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Nodo<TKey> procesoFusionarHijos(Nodo<TKey> leftChild, Nodo<TKey> rightChild) {
        throw new UnsupportedOperationException();
    }

    /**
     * Tenga en cuenta que se abandonará la clave hundida del padre.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void fusionConHermano(TKey sinkKey, Nodo<TKey> rightSibling) {
        NodoHoja<TKey, TValue> siblingLeaf = (NodoHoja<TKey, TValue>) rightSibling;

        int j = this.getNumClaves();
        for (int i = 0; i < siblingLeaf.getNumClaves(); ++i) {
            this.setClave(j + i, siblingLeaf.getClave(i));
            this.setValor(j + i, siblingLeaf.getValor(i));
        }
        this.numClaves += siblingLeaf.getNumClaves();

        this.setNodoHermanoDerecho(siblingLeaf.nodoHermanoDerecho);
        if (siblingLeaf.nodoHermanoDerecho != null) {
            siblingLeaf.nodoHermanoDerecho.setNodoHermanoIzquierdo(this);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TKey transferenciaDeHermano(TKey sinkKey, Nodo<TKey> sibling, int borrowIndex) {
        NodoHoja<TKey, TValue> siblingNode = (NodoHoja<TKey, TValue>) sibling;

        this.insertarClave(siblingNode.getClave(borrowIndex), siblingNode.getValor(borrowIndex));
        siblingNode.eliminarAt(borrowIndex);

        return borrowIndex == 0 ? sibling.getClave(0) : this.getClave(0);
    }
}
