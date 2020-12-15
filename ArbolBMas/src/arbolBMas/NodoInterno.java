package arbolBMas;

class NodoInterno<TKey extends Comparable<TKey>> extends Nodo<TKey> {

    protected final static int ordenInterno = 4;
    protected Object[] hijos;

    public NodoInterno() {
        this.claves = new Object[ordenInterno + 1];
        this.hijos = new Object[ordenInterno + 2];
    }

    @SuppressWarnings("unchecked")
    public Nodo<TKey> getHijo(int index) {
        return (Nodo<TKey>) this.hijos[index];
    }

    public void setHijo(int index, Nodo<TKey> hijo) {
        this.hijos[index] = hijo;
        if (hijo != null) {
            hijo.setPadre(this);
        }
    }

    @Override
    public TipoNodoArbol getTipoNodo() {
        return TipoNodoArbol.NodoInterno;
    }

    @Override
    public int buscar(TKey key) {
        int index = 0;
        for (index = 0; index < this.getNumClaves(); ++index) {
            int cmp = this.getClave(index).compareTo(key);
            if (cmp == 0) {
                return index + 1;
            } else if (cmp > 0) {
                return index;
            }
        }

        return index;
    }

    /* Los códigos siguientes se utilizan para respaldar la operación de insertar */
    private void insertAt(int index, TKey key, Nodo<TKey> leftChild, Nodo<TKey> rightChild) {
        /* Mover espacio para la nueva llave */
        for (int i = this.getNumClaves() + 1; i > index; --i) {
            this.setHijo(i, this.getHijo(i - 1));
        }
        for (int i = this.getNumClaves(); i > index; --i) {
            this.setClave(i, this.getClave(i - 1));
        }

        /* Insertar nueva clave */
        this.setClave(index, key);
        this.setHijo(index, leftChild);
        this.setHijo(index + 1, rightChild);
        this.numClaves += 1;
    }

    /**
     * When splits a internal node, the middle key is kicked out and be pushed
     * to parent node.
     */
    @Override
    protected Nodo<TKey> dividir() {
        int midIndex = this.getNumClaves() / 2;

        NodoInterno<TKey> newRNode = new NodoInterno<TKey>();
        for (int i = midIndex + 1; i < this.getNumClaves(); ++i) {
            newRNode.setClave(i - midIndex - 1, this.getClave(i));
            this.setClave(i, null);
        }
        for (int i = midIndex + 1; i <= this.getNumClaves(); ++i) {
            newRNode.setHijo(i - midIndex - 1, this.getHijo(i));
            newRNode.getHijo(i - midIndex - 1).setPadre(newRNode);
            this.setHijo(i, null);
        }
        this.setClave(midIndex, null);
        newRNode.numClaves = this.getNumClaves() - midIndex - 1;
        this.numClaves = midIndex;

        return newRNode;
    }

    @Override
    protected Nodo<TKey> subirClave(TKey key, Nodo<TKey> leftChild, Nodo<TKey> rightNode) {
        // find the target position of the new key
        int index = this.buscar(key);

        // insert the new key
        this.insertAt(index, key, leftChild, rightNode);

        // check whether current node need to be split
        if (this.nodoDesbordado()) {
            return this.tratarNodoDesbordado();
        } else {
            return this.getPadre() == null ? this : null;
        }
    }

    /* The codes below are used to support delete operation */
    private void deleteAt(int index) {
        int i = 0;
        for (i = index; i < this.getNumClaves() - 1; ++i) {
            this.setClave(i, this.getClave(i + 1));
            this.setHijo(i + 1, this.getHijo(i + 2));
        }
        this.setClave(i, null);
        this.setHijo(i + 1, null);
        --this.numClaves;
    }

    @Override
    protected void procesoTransferirHijos(Nodo<TKey> borrower, Nodo<TKey> lender, int borrowIndex) {
        int borrowerChildIndex = 0;
        while (borrowerChildIndex < this.getNumClaves() + 1 && this.getHijo(borrowerChildIndex) != borrower) {
            ++borrowerChildIndex;
        }

        if (borrowIndex == 0) {
            // borrow a key from right sibling
            TKey upKey = borrower.transferenciaDeHermano(this.getClave(borrowerChildIndex), lender, borrowIndex);
            this.setClave(borrowerChildIndex, upKey);
        } else {
            // borrow a key from left sibling
            TKey upKey = borrower.transferenciaDeHermano(this.getClave(borrowerChildIndex - 1), lender, borrowIndex);
            this.setClave(borrowerChildIndex - 1, upKey);
        }
    }

    @Override
    protected Nodo<TKey> procesoFusionarHijos(Nodo<TKey> leftChild, Nodo<TKey> rightChild) {
        int index = 0;
        while (index < this.getNumClaves() && this.getHijo(index) != leftChild) {
            ++index;
        }
        TKey sinkKey = this.getClave(index);

        // merge two children and the sink key into the left child node
        leftChild.fusionConHermano(sinkKey, rightChild);

        // remove the sink key, keep the left child and abandon the right child
        this.deleteAt(index);

        // check whether need to propagate borrow or fusion to parent
        if (this.nodoBajo()) {
            if (this.getPadre() == null) {
                // current node is root, only remove keys or delete the whole root node
                if (this.getNumClaves() == 0) {
                    leftChild.setPadre(null);
                    return leftChild;
                } else {
                    return null;
                }
            }

            return this.tratarNodoBajo();
        }

        return null;
    }

    @Override
    protected void fusionConHermano(TKey sinkKey, Nodo<TKey> rightSibling) {
        NodoInterno<TKey> rightSiblingNode = (NodoInterno<TKey>) rightSibling;

        int j = this.getNumClaves();
        this.setClave(j++, sinkKey);

        for (int i = 0; i < rightSiblingNode.getNumClaves(); ++i) {
            this.setClave(j + i, rightSiblingNode.getClave(i));
        }
        for (int i = 0; i < rightSiblingNode.getNumClaves() + 1; ++i) {
            this.setHijo(j + i, rightSiblingNode.getHijo(i));
        }
        this.numClaves += 1 + rightSiblingNode.getNumClaves();

        this.setNodoHermanoDerecho(rightSiblingNode.nodoHermanoDerecho);
        if (rightSiblingNode.nodoHermanoDerecho != null) {
            rightSiblingNode.nodoHermanoDerecho.setNodoHermanoIzquierdo(this);
        }
    }

    @Override
    protected TKey transferenciaDeHermano(TKey sinkKey, Nodo<TKey> sibling, int borrowIndex) {
        NodoInterno<TKey> siblingNode = (NodoInterno<TKey>) sibling;

        TKey upKey = null;
        if (borrowIndex == 0) {
            // borrow the first key from right sibling, append it to tail
            int index = this.getNumClaves();
            this.setClave(index, sinkKey);
            this.setHijo(index + 1, siblingNode.getHijo(borrowIndex));
            this.numClaves += 1;

            upKey = siblingNode.getClave(0);
            siblingNode.deleteAt(borrowIndex);
        } else {
            // borrow the last key from left sibling, insert it to head
            this.insertAt(0, sinkKey, siblingNode.getHijo(borrowIndex + 1), this.getHijo(0));
            upKey = siblingNode.getClave(borrowIndex);
            siblingNode.deleteAt(borrowIndex);
        }

        return upKey;
    }
}
