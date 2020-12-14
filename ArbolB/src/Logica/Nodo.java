package Logica;



/**
 * Class Nodo
 * @author tnguyen
 */
public class Nodo<K extends Comparable, V>
{
    public final static int MIN_DEGREE          =   2;
    public final static int LOWER_BOUND_KEYNUM  =   MIN_DEGREE-1;
    public final static int UPPER_BOUND_KEYNUM  =   (MIN_DEGREE * 2) - 1;

    public boolean mIsLeaf;
    public int mCurrentKeyNum;
    public KeyValue<K, V> mKeys[];
    public Nodo mChildren[];


    public Nodo() {
        mIsLeaf = true;
        mCurrentKeyNum = 0;
        mKeys = new KeyValue[UPPER_BOUND_KEYNUM];
        mChildren = new Nodo[UPPER_BOUND_KEYNUM + 1];
    }


    protected static Nodo getChildNodeAtIndex(Nodo nodo, int keyIdx, int nDirection) {
        if (nodo.mIsLeaf) {
            return null;
        }

        keyIdx += nDirection;
        if ((keyIdx < 0) || (keyIdx > nodo.mCurrentKeyNum)) {
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
