package Logica;


public class Nodo<K extends Comparable, V>
{
    public final static int orden          =   2;
    public final static int numMinLlaves  =   orden;
    public final static int numMaxLlaves  =   orden * 2;

    public boolean mEsHoja;
    public int mActualKeyNum;
    public KeyValue<K, V> mKeys[];
    public Nodo mChildren[];


    public Nodo() {
        mEsHoja = true;
        mActualKeyNum = 0;
        mKeys = new KeyValue[numMaxLlaves];
        mChildren = new Nodo[numMaxLlaves + 1];
    }


    protected static Nodo getChildNodeAtIndex(Nodo nodo, int keyIdx, int nDirection) {
        if (nodo.mEsHoja) {
            return null;
        }

        keyIdx += nDirection;
        if ((keyIdx < 0) || (keyIdx > nodo.mActualKeyNum)) {
            return null;
        }

        return nodo.mChildren[keyIdx];
    }


    protected static Nodo getLeftChildAtIndex(Nodo nodo, int keyIdx) {
        return getChildNodeAtIndex(nodo, keyIdx, 0);
    }


    protected static Nodo getRightChildAtIndex(Nodo nodo, int keyIdx) {
        return getChildNodeAtIndex(nodo, keyIdx, 1);
    }


    protected static Nodo getLeftSiblingAtIndex(Nodo parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, -1);
    }


    protected static Nodo getRightSiblingAtIndex(Nodo parentNode, int keyIdx) {
        return getChildNodeAtIndex(parentNode, keyIdx, 1);
    }
}
