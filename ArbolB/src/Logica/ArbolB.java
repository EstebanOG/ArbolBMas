package Logica;

import java.util.Stack;



/**
 * Class ArbolB
 * @author tnguyen
 * Description: ArbolB implementation
 */
public class ArbolB<K extends Comparable, V>
{
    public final static int     REBALANCE_FOR_LEAF_NODE         =   1;
    public final static int     REBALANCE_FOR_INTERNAL_NODE     =   2;

    private Nodo<K, V> mRoot = null;
    private long  mSize = 0L;
    private Nodo<K, V> mIntermediateInternalNode = null;
    private int mNodeIdx = 0;
    private final Stack<StackInfo> mStackTracer = new Stack<StackInfo>();



    //
    // Get the root node
    //
    public Nodo<K, V> getRootNode() {
        return mRoot;
    }


    //
    // The total number of nodes in the tree
    //
    public long size() {
        return mSize;
    }


    //
    // Clear all the entries in the tree
    //
    public void clear() {
        mSize = 0L;
        mRoot = null;
    }


    //
    // Create a node with default values
    //
    private Nodo<K, V> createNode() {
        Nodo<K, V> nodo;
        nodo = new Nodo();
        nodo.mEsHoja = true;
        nodo.mActualKeyNum = 0;
        return nodo;
    }


    //
    // Search value for a specified key of the tree
    //
    public V search(K key) {
        Nodo<K, V> currentNode = mRoot;
        KeyValue<K, V> currentKey;
        int i, numberOfKeys;

        while (currentNode != null) {
            numberOfKeys = currentNode.mActualKeyNum;
            i = 0;
            currentKey = currentNode.mKeys[i];
            while ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) > 0)) {
                ++i;
                if (i < numberOfKeys) {
                    currentKey = currentNode.mKeys[i];
                }
                else {
                    --i;
                    break;
                }
            }

            if ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) == 0)) {
                return currentKey.mValue;
            }

            // We don't need it
            /*
            if (currentNode.mIsLeaf) {
                return null;
            }
            */

            if (key.compareTo(currentKey.mKey) > 0) {
                currentNode = Nodo.getRightChildAtIndex(currentNode, i);
            }
            else {
                currentNode = Nodo.getLeftChildAtIndex(currentNode, i);
            }
        }

        return null;
    }


    //
    // Insertar la llave y su valor en el árbol
    //
    public ArbolB insertar(K key, V value) {
        if (mRoot == null) {
            mRoot = createNode();
        }

        ++mSize;
        if (mRoot.mActualKeyNum == Nodo.numMaxLlaves) {
            // Dividir si la raiz está llena
            Nodo<K, V> nodo = createNode();
            nodo.mEsHoja = false;
            nodo.mChildren[0] = mRoot;
            mRoot = nodo;
            splitNode(mRoot, 0, nodo.mChildren[0]);
        }

        insertKeyAtNode(mRoot, key, value);
        return this;
    }


    //
    // Insert key and its value to the specified root
    //
    private void insertKeyAtNode(Nodo rootNode, K key, V value) {
        int i;
        int currentKeyNum = rootNode.mActualKeyNum;

        if (rootNode.mEsHoja) {
            if (rootNode.mActualKeyNum == 0) {
                // Empty root
                rootNode.mKeys[0] = new KeyValue<K, V>(key, value);
                ++(rootNode.mActualKeyNum);
                return;
            }

            // Verifica si la clave especificada no existe en el nodo
            for (i = 0; i < rootNode.mActualKeyNum; ++i) {
                if (key.compareTo(rootNode.mKeys[i].mKey) == 0) {
                    // Busca la clave existente
                    rootNode.mKeys[i].mValue = value;
                    --mSize;
                    return;
                }
            }

            i = currentKeyNum - 1;
            KeyValue<K, V> existingKeyVal = rootNode.mKeys[i];
            while ((i > -1) && (key.compareTo(existingKeyVal.mKey) < 0)) {
                rootNode.mKeys[i + 1] = existingKeyVal;
                --i;
                if (i > -1) {
                    existingKeyVal = rootNode.mKeys[i];
                }
            }

            i = i + 1;
            rootNode.mKeys[i] = new KeyValue<K, V>(key, value);

            ++(rootNode.mActualKeyNum);
            return;
        }

        // This is an internal node (i.e: not a leaf node)
        // So let find the child node where the key is supposed to belong
        i = 0;
        int numberOfKeys = rootNode.mActualKeyNum;
        
        KeyValue<K, V> currentKey = rootNode.mKeys[i];
        System.out.println(key+"="+rootNode.mKeys[i]);
        while ((i < numberOfKeys) &&(key.compareTo(currentKey.mKey) > 0)) {
            ++i;
            if (i < numberOfKeys) {
                currentKey = rootNode.mKeys[i];
            }
            else {
                --i;
                break;
            }
        }

        if ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) == 0)) {
            // The key already existed so replace its value and done with it
            currentKey.mValue = value;
            --mSize;
            return;
        }

        Nodo<K, V> nodo;
        if (key.compareTo(currentKey.mKey) > 0) {
            nodo = Nodo.getRightChildAtIndex(rootNode, i);
            i = i + 1;
        }
        else {
            if ((i - 1 >= 0) && (key.compareTo(rootNode.mKeys[i - 1].mKey) > 0)) {
                nodo = Nodo.getRightChildAtIndex(rootNode, i - 1);
            }
            else {
                nodo = Nodo.getLeftChildAtIndex(rootNode, i);
            }
        }

        if (nodo.mActualKeyNum == Nodo.numMaxLlaves) {
            // If the child node is a full node then handle it by splitting out
            // then insert key starting at the root node after splitting node
            splitNode(rootNode, i, nodo);
            insertKeyAtNode(rootNode, key, value);
            return;
        }

        insertKeyAtNode(nodo, key, value);
    }


    //
    // Split a child with respect to its parent at a specified node
    //
    private void splitNode(Nodo nodoPadre, int nodeIdx, Nodo nodo) {
        int i;

        Nodo<K, V> newNode = createNode();

        newNode.mEsHoja = nodo.mEsHoja;

        // Since the node is full,
        // new node must share LOWER_BOUND_KEYNUM (aka t - 1) keys from the node
        newNode.mActualKeyNum = Nodo.numMinLlaves;


        // Copy right half of the keys from the node to the new node
        for (i = 0; i < Nodo.numMinLlaves; ++i) {
            newNode.mKeys[i] = nodo.mKeys[i + Nodo.orden];
            nodo.mKeys[i + Nodo.orden] = null;
        }

        // If the node is an internal node (not a leaf),
        // copy the its child pointers at the half right as well
        if (!nodo.mEsHoja) {
            for (i = 0; i < Nodo.orden; ++i) {
                newNode.mChildren[i] = nodo.mChildren[i + Nodo.orden];
                nodo.mChildren[i + Nodo.orden] = null;
            }
        }

        // The node at this point should have LOWER_BOUND_KEYNUM (aka min degree - 1) keys at this point.
        // We will move its right-most key to its parent node later.
        nodo.mActualKeyNum = Nodo.numMinLlaves;

        // Do the right shift for relevant child pointers of the parent node
        // so that we can put the new node as its new child pointer
        for (i = nodoPadre.mActualKeyNum; i > nodeIdx; --i) {
            nodoPadre.mChildren[i + 1] = nodoPadre.mChildren[i];
            nodoPadre.mChildren[i] = null;
        }
        nodoPadre.mChildren[nodeIdx + 1] = newNode;

        // Do the right shift all the keys of the parent node the right side of the node index as well
        // so that we will have a slot for move a median key from the splitted node
        for (i = nodoPadre.mActualKeyNum - 1; i >= nodeIdx; --i) {
            nodoPadre.mKeys[i + 1] = nodoPadre.mKeys[i];
            nodoPadre.mKeys[i] = null;
        }
        nodoPadre.mKeys[nodeIdx] = nodo.mKeys[Nodo.numMinLlaves];
        nodo.mKeys[Nodo.numMinLlaves] = null;
        ++(nodoPadre.mActualKeyNum);
    }


    //
    // Find the predecessor node for a specified node
    //
    private Nodo<K, V> findPredecessor(Nodo<K, V> nodo, int nodeIdx) {
        if (nodo.mEsHoja) {
            return nodo;
        }

        Nodo<K, V> predecessorNode;
        if (nodeIdx > -1) {
            predecessorNode = Nodo.getLeftChildAtIndex(nodo, nodeIdx);
            if (predecessorNode != null) {
                mIntermediateInternalNode = nodo;
                mNodeIdx = nodeIdx;
                nodo = findPredecessor(predecessorNode, -1);
            }

            return nodo;
        }

        predecessorNode = Nodo.getRightChildAtIndex(nodo, nodo.mActualKeyNum - 1);
        if (predecessorNode != null) {
            mIntermediateInternalNode = nodo;
            mNodeIdx = nodo.mActualKeyNum;
            nodo = findPredecessorForNode(predecessorNode, -1);
        }

        return nodo;
    }


    //
    // Find predecessor node of a specified node
    //
    private Nodo<K, V> findPredecessorForNode(Nodo<K, V> nodo, int keyIdx) {
        Nodo<K, V> predecessorNode;
        Nodo<K, V> originalNode = nodo;
        if (keyIdx > -1) {
            predecessorNode = Nodo.getLeftChildAtIndex(nodo, keyIdx);
            if (predecessorNode != null) {
                nodo = findPredecessorForNode(predecessorNode, -1);
                rebalanceTreeAtNode(originalNode, predecessorNode, keyIdx, REBALANCE_FOR_LEAF_NODE);
            }

            return nodo;
        }

        predecessorNode = Nodo.getRightChildAtIndex(nodo, nodo.mActualKeyNum - 1);
        if (predecessorNode != null) {
            nodo = findPredecessorForNode(predecessorNode, -1);
            rebalanceTreeAtNode(originalNode, predecessorNode, keyIdx, REBALANCE_FOR_LEAF_NODE);
        }

        return nodo;
    }


    //
    // Do the left rotation
    //
    private void performLeftRotation(Nodo<K, V> nodo, int nodeIdx, Nodo<K, V> parentNode, Nodo<K, V> rightSiblingNode) {
        int parentKeyIdx = nodeIdx;

        /*
        if (nodeIdx >= parentNode.mCurrentKeyNum) {
            // This shouldn't happen
            parentKeyIdx = nodeIdx - 1;
        }
        */

        // Move the parent key and relevant child to the deficient node
        nodo.mKeys[nodo.mActualKeyNum] = parentNode.mKeys[parentKeyIdx];
        nodo.mChildren[nodo.mActualKeyNum + 1] = rightSiblingNode.mChildren[0];
        ++(nodo.mActualKeyNum);

        // Move the leftmost key of the right sibling and relevant child pointer to the parent node
        parentNode.mKeys[parentKeyIdx] = rightSiblingNode.mKeys[0];
        --(rightSiblingNode.mActualKeyNum);
        // Shift all keys and children of the right sibling to its left
        for (int i = 0; i < rightSiblingNode.mActualKeyNum; ++i) {
            rightSiblingNode.mKeys[i] = rightSiblingNode.mKeys[i + 1];
            rightSiblingNode.mChildren[i] = rightSiblingNode.mChildren[i + 1];
        }
        rightSiblingNode.mChildren[rightSiblingNode.mActualKeyNum] = rightSiblingNode.mChildren[rightSiblingNode.mActualKeyNum + 1];
        rightSiblingNode.mChildren[rightSiblingNode.mActualKeyNum + 1] = null;
    }


    //
    // Do the right rotation
    //
    private void performRightRotation(Nodo<K, V> nodo, int nodeIdx, Nodo<K, V> parentNode, Nodo<K, V> leftSiblingNode) {
        int parentKeyIdx = nodeIdx;
        if (nodeIdx >= parentNode.mActualKeyNum) {
            // This shouldn't happen
            parentKeyIdx = nodeIdx - 1;
        }

        // Shift all keys and children of the deficient node to the right
        // So that there will be available left slot for insertion
        nodo.mChildren[nodo.mActualKeyNum + 1] = nodo.mChildren[nodo.mActualKeyNum];
        for (int i = nodo.mActualKeyNum - 1; i >= 0; --i) {
            nodo.mKeys[i + 1] = nodo.mKeys[i];
            nodo.mChildren[i + 1] = nodo.mChildren[i];
        }

        // Move the parent key and relevant child to the deficient node
        nodo.mKeys[0] = parentNode.mKeys[parentKeyIdx];
        nodo.mChildren[0] = leftSiblingNode.mChildren[leftSiblingNode.mActualKeyNum];
        ++(nodo.mActualKeyNum);

        // Move the leftmost key of the right sibling and relevant child pointer to the parent node
        parentNode.mKeys[parentKeyIdx] = leftSiblingNode.mKeys[leftSiblingNode.mActualKeyNum - 1];
        leftSiblingNode.mChildren[leftSiblingNode.mActualKeyNum] = null;
        --(leftSiblingNode.mActualKeyNum);
    }


    //
    // Do a left sibling merge
    // Return true if it should continue further
    // Return false if it is done
    //
    private boolean performMergeWithLeftSibling(Nodo<K, V> nodo, int nodeIdx, Nodo<K, V> parentNode, Nodo<K, V> leftSiblingNode) {
        if (nodeIdx == parentNode.mActualKeyNum) {
            // For the case that the node index can be the right most
            nodeIdx = nodeIdx - 1;
        }

        // Here we need to determine the parent node's index based on child node's index (nodeIdx)
        if (nodeIdx > 0) {
            if (leftSiblingNode.mKeys[leftSiblingNode.mActualKeyNum - 1].mKey.compareTo(parentNode.mKeys[nodeIdx - 1].mKey) < 0) {
                nodeIdx = nodeIdx - 1;
            }
        }

        // Copy the parent key to the node (on the left)
        leftSiblingNode.mKeys[leftSiblingNode.mActualKeyNum] = parentNode.mKeys[nodeIdx];
        ++(leftSiblingNode.mActualKeyNum);

        // Copy keys and children of the node to the left sibling node
        for (int i = 0; i < nodo.mActualKeyNum; ++i) {
            leftSiblingNode.mKeys[leftSiblingNode.mActualKeyNum + i] = nodo.mKeys[i];
            leftSiblingNode.mChildren[leftSiblingNode.mActualKeyNum + i] = nodo.mChildren[i];
            nodo.mKeys[i] = null;
        }
        leftSiblingNode.mActualKeyNum += nodo.mActualKeyNum;
        leftSiblingNode.mChildren[leftSiblingNode.mActualKeyNum] = nodo.mChildren[nodo.mActualKeyNum];
        nodo.mActualKeyNum = 0;  // Abandon the node

        // Shift all relevant keys and children of the parent node to the left
        // since it lost one of its keys and children (by moving it to the child node)
        int i;
        for (i = nodeIdx; i < parentNode.mActualKeyNum - 1; ++i) {
            parentNode.mKeys[i] = parentNode.mKeys[i + 1];
            parentNode.mChildren[i + 1] = parentNode.mChildren[i + 2];
        }
        parentNode.mKeys[i] = null;
        parentNode.mChildren[parentNode.mActualKeyNum] = null;
        --(parentNode.mActualKeyNum);

        // Make sure the parent point to the correct child after the merge
        parentNode.mChildren[nodeIdx] = leftSiblingNode;

        if ((parentNode == mRoot) && (parentNode.mActualKeyNum == 0)) {
            // Root node is updated.  It should be done
            mRoot = leftSiblingNode;
            return false;
        }

        return true;
    }


    //
    // Do the right sibling merge
    // Return true if it should continue further
    // Return false if it is done
    //
    private boolean performMergeWithRightSibling(Nodo<K, V> nodo, int nodeIdx, Nodo<K, V> parentNode, Nodo<K, V> rightSiblingNode) {
        // Copy the parent key to right-most slot of the node
        nodo.mKeys[nodo.mActualKeyNum] = parentNode.mKeys[nodeIdx];
        ++(nodo.mActualKeyNum);

        // Copy keys and children of the right sibling to the node
        for (int i = 0; i < rightSiblingNode.mActualKeyNum; ++i) {
            nodo.mKeys[nodo.mActualKeyNum + i] = rightSiblingNode.mKeys[i];
            nodo.mChildren[nodo.mActualKeyNum + i] = rightSiblingNode.mChildren[i];
        }
        nodo.mActualKeyNum += rightSiblingNode.mActualKeyNum;
        nodo.mChildren[nodo.mActualKeyNum] = rightSiblingNode.mChildren[rightSiblingNode.mActualKeyNum];
        rightSiblingNode.mActualKeyNum = 0;  // Abandon the sibling node

        // Shift all relevant keys and children of the parent node to the left
        // since it lost one of its keys and children (by moving it to the child node)
        int i;
        for (i = nodeIdx; i < parentNode.mActualKeyNum - 1; ++i) {
            parentNode.mKeys[i] = parentNode.mKeys[i + 1];
            parentNode.mChildren[i + 1] = parentNode.mChildren[i + 2];
        }
        parentNode.mKeys[i] = null;
        parentNode.mChildren[parentNode.mActualKeyNum] = null;
        --(parentNode.mActualKeyNum);

        // Make sure the parent point to the correct child after the merge
        parentNode.mChildren[nodeIdx] = nodo;

        if ((parentNode == mRoot) && (parentNode.mActualKeyNum == 0)) {
            // Root node is updated.  It should be done
            mRoot = nodo;
            return false;
        }

        return true;
    }


    //
    // Search the specified key within a node
    // Return index of the keys if it finds
    // Return -1 otherwise
    //
    private int searchKey(Nodo<K, V> nodo, K key) {
        for (int i = 0; i < nodo.mActualKeyNum; ++i) {
            if (key.compareTo(nodo.mKeys[i].mKey) == 0) {
                return i;
            }
            else if (key.compareTo(nodo.mKeys[i].mKey) < 0) {
                return -1;
            }
        }

        return -1;
    }


    //
    // List all the items in the tree
    //
    public void list(Iterator<K, V> iterImpl) {
        if (mSize < 1) {
            return;
        }

        if (iterImpl == null) {
            return;
        }

        listEntriesInOrder(mRoot, iterImpl);
    }


    //
    // Recursively loop to the tree and list out the keys and their values
    // Return true if it should continues listing out futher
    // Return false if it is done
    //
    private boolean listEntriesInOrder(Nodo<K, V> treeNode, Iterator<K, V> iterImpl) {
        if ((treeNode == null) ||
            (treeNode.mActualKeyNum == 0)) {
            return false;
        }

        boolean bStatus;
        KeyValue<K, V> keyVal;
        int currentKeyNum = treeNode.mActualKeyNum;
        for (int i = 0; i < currentKeyNum; ++i) {
            listEntriesInOrder(Nodo.getLeftChildAtIndex(treeNode, i), iterImpl);

            keyVal = treeNode.mKeys[i];
            bStatus = iterImpl.item(keyVal.mKey, keyVal.mValue);
            if (!bStatus) {
                return false;
            }

            if (i == currentKeyNum - 1) {
                listEntriesInOrder(Nodo.getRightChildAtIndex(treeNode, i), iterImpl);
            }
        }

        return true;
    }


    //
    // Delete a key from the tree
    // Return value if it finds the key and delete it
    // Return null if it cannot find the key
    //
    public V delete(K key) {
        mIntermediateInternalNode = null;
        KeyValue<K, V> keyVal = deleteKey(null, mRoot, key, 0);
        if (keyVal == null) {
            return null;
        }
        --mSize;
        return keyVal.mValue;
    }


    //
    // Delete a key from a tree node
    //
    private KeyValue<K, V> deleteKey(Nodo<K, V> parentNode, Nodo<K, V> nodo, K key, int nodeIdx) {
        int i;
        int nIdx;
        KeyValue<K, V> retVal;

        if (nodo == null) {
            // The tree is empty
            return null;
        }

        if (nodo.mEsHoja) {
            nIdx = searchKey(nodo, key);
            if (nIdx < 0) {
                // Can't find the specified key
                return null;
            }

            retVal = nodo.mKeys[nIdx];

            if ((nodo.mActualKeyNum > Nodo.numMinLlaves) || (parentNode == null)) {
                // Remove it from the node
                for (i = nIdx; i < nodo.mActualKeyNum - 1; ++i) {
                    nodo.mKeys[i] = nodo.mKeys[i + 1];
                }
                nodo.mKeys[i] = null;
                --(nodo.mActualKeyNum);

                if (nodo.mActualKeyNum == 0) {
                    // nodo is actually the root node
                    mRoot = null;
                }

                return retVal;
            }

            // Find the left sibling
            Nodo<K, V> rightSibling;
            Nodo<K, V> leftSibling = Nodo.getLeftSiblingAtIndex(parentNode, nodeIdx);
            if ((leftSibling != null) && (leftSibling.mActualKeyNum > Nodo.numMinLlaves)) {
                // Remove the key and borrow a key from the left sibling
                moveLeftLeafSiblingKeyWithKeyRemoval(nodo, nodeIdx, nIdx, parentNode, leftSibling);
            }
            else {
                rightSibling = Nodo.getRightSiblingAtIndex(parentNode, nodeIdx);
                if ((rightSibling != null) && (rightSibling.mActualKeyNum > Nodo.numMinLlaves)) {
                    // Remove a key and borrow a key the right sibling
                    moveRightLeafSiblingKeyWithKeyRemoval(nodo, nodeIdx, nIdx, parentNode, rightSibling);
                }
                else {
                    // Merge to its sibling
                    boolean isRebalanceNeeded = false;
                    boolean bStatus;
                    if (leftSibling != null) {
                        // Merge with the left sibling
                        bStatus = doLeafSiblingMergeWithKeyRemoval(nodo, nodeIdx, nIdx, parentNode, leftSibling, false);
                        if (!bStatus) {
                            isRebalanceNeeded = false;
                        }
                        else if (parentNode.mActualKeyNum < Nodo.numMinLlaves) {
                            // Need to rebalance the tree
                            isRebalanceNeeded = true;
                        }
                    }
                    else {
                        // Merge with the right sibling
                        bStatus = doLeafSiblingMergeWithKeyRemoval(nodo, nodeIdx, nIdx, parentNode, rightSibling, true);
                        if (!bStatus) {
                            isRebalanceNeeded = false;
                        }
                        else if (parentNode.mActualKeyNum < Nodo.numMinLlaves) {
                            // Need to rebalance the tree
                            isRebalanceNeeded = true;
                        }
                    }

                    if (isRebalanceNeeded && (mRoot != null)) {
                        rebalanceTree(mRoot, parentNode, parentNode.mKeys[0].mKey);
                    }
                }
            }

            return retVal;  // Done with handling for the leaf node
        }

        //
        // At this point the node is an internal node
        //

        nIdx = searchKey(nodo, key);
        if (nIdx >= 0) {
            // We found the key in the internal node

            // Find its predecessor
            mIntermediateInternalNode = nodo;
            mNodeIdx = nIdx;
            Nodo<K, V> predecessorNode =  findPredecessor(nodo, nIdx);
            KeyValue<K, V> predecessorKey = predecessorNode.mKeys[predecessorNode.mActualKeyNum - 1];

            // Swap the data of the deleted key and its predecessor (in the leaf node)
            KeyValue<K, V> deletedKey = nodo.mKeys[nIdx];
            nodo.mKeys[nIdx] = predecessorKey;
            predecessorNode.mKeys[predecessorNode.mActualKeyNum - 1] = deletedKey;

            // mIntermediateNode is done in findPrecessor
            return deleteKey(mIntermediateInternalNode, predecessorNode, deletedKey.mKey, mNodeIdx);
        }

        //
        // Find the child subtree (node) that contains the key
        //
        i = 0;
        KeyValue<K, V> currentKey = nodo.mKeys[0];
        while ((i < nodo.mActualKeyNum) && (key.compareTo(currentKey.mKey) > 0)) {
            ++i;
            if (i < nodo.mActualKeyNum) {
                currentKey = nodo.mKeys[i];
            }
            else {
                --i;
                break;
            }
        }

        Nodo<K, V> childNode;
        if (key.compareTo(currentKey.mKey) > 0) {
            childNode = Nodo.getRightChildAtIndex(nodo, i);
            if (childNode.mKeys[0].mKey.compareTo(nodo.mKeys[nodo.mActualKeyNum - 1].mKey) > 0) {
                // The right-most side of the node
                i = i + 1;
            }
        }
        else {
            childNode = Nodo.getLeftChildAtIndex(nodo, i);
        }

        return deleteKey(nodo, childNode, key, i);
    }


    //
    // Remove the specified key and move a key from the right leaf sibling to the node
    // Note: The node and its sibling must be leaves
    //
    private void moveRightLeafSiblingKeyWithKeyRemoval(Nodo<K, V> nodo,
                                                       int nodeIdx,
                                                       int keyIdx,
                                                       Nodo<K, V> parentNode,
                                                       Nodo<K, V> rightSiblingNode) {
        // Shift to the right where the key is deleted
        for (int i = keyIdx; i < nodo.mActualKeyNum - 1; ++i) {
            nodo.mKeys[i] = nodo.mKeys[i + 1];
        }

        nodo.mKeys[nodo.mActualKeyNum - 1] = parentNode.mKeys[nodeIdx];
        parentNode.mKeys[nodeIdx] = rightSiblingNode.mKeys[0];

        for (int i = 0; i < rightSiblingNode.mActualKeyNum - 1; ++i) {
            rightSiblingNode.mKeys[i] = rightSiblingNode.mKeys[i + 1];
        }

        --(rightSiblingNode.mActualKeyNum);
    }


    //
    // Remove the specified key and move a key from the left leaf sibling to the node
    // Note: The node and its sibling must be leaves
    //
    private void moveLeftLeafSiblingKeyWithKeyRemoval(Nodo<K, V> nodo,
                                                      int nodeIdx,
                                                      int keyIdx,
                                                      Nodo<K, V> parentNode,
                                                      Nodo<K, V> leftSiblingNode) {
        // Use the parent key on the left side of the node
        nodeIdx = nodeIdx - 1;

        // Shift to the right to where the key will be deleted 
        for (int i = keyIdx; i > 0; --i) {
            nodo.mKeys[i] = nodo.mKeys[i - 1];
        }

        nodo.mKeys[0] = parentNode.mKeys[nodeIdx];
        parentNode.mKeys[nodeIdx] = leftSiblingNode.mKeys[leftSiblingNode.mActualKeyNum - 1];
        --(leftSiblingNode.mActualKeyNum);
    }


    //
    // Do the leaf sibling merge
    // Return true if we need to perform futher re-balancing action
    // Return false if we reach and update the root hence we don't need to go futher for re-balancing the tree
    //
    private boolean doLeafSiblingMergeWithKeyRemoval(Nodo<K, V> nodo,
                                                     int nodeIdx,
                                                     int keyIdx,
                                                     Nodo<K, V> parentNode,
                                                     Nodo<K, V> siblingNode,
                                                     boolean isRightSibling) {
        int i;

        if (nodeIdx == parentNode.mActualKeyNum) {
            // Case node index can be the right most
            nodeIdx = nodeIdx - 1;
        }

        if (isRightSibling) {
            // Shift the remained keys of the node to the left to remove the key
            for (i = keyIdx; i < nodo.mActualKeyNum - 1; ++i) {
                nodo.mKeys[i] = nodo.mKeys[i + 1];
            }
            nodo.mKeys[i] = parentNode.mKeys[nodeIdx];
        }
        else {
            // Here we need to determine the parent node id based on child node id (nodeIdx)
            if (nodeIdx > 0) {
                if (siblingNode.mKeys[siblingNode.mActualKeyNum - 1].mKey.compareTo(parentNode.mKeys[nodeIdx - 1].mKey) < 0) {
                    nodeIdx = nodeIdx - 1;
                }
            }

            siblingNode.mKeys[siblingNode.mActualKeyNum] = parentNode.mKeys[nodeIdx];
            // siblingNode.mKeys[siblingNode.mCurrentKeyNum] = parentNode.mKeys[0];
            ++(siblingNode.mActualKeyNum);

            // Shift the remained keys of the node to the left to remove the key
            for (i = keyIdx; i < nodo.mActualKeyNum - 1; ++i) {
                nodo.mKeys[i] = nodo.mKeys[i + 1];
            }
            nodo.mKeys[i] = null;
            --(nodo.mActualKeyNum);
        }

        if (isRightSibling) {
            for (i = 0; i < siblingNode.mActualKeyNum; ++i) {
                nodo.mKeys[nodo.mActualKeyNum + i] = siblingNode.mKeys[i];
                siblingNode.mKeys[i] = null;
            }
            nodo.mActualKeyNum += siblingNode.mActualKeyNum;
        }
        else {
            for (i = 0; i < nodo.mActualKeyNum; ++i) {
                siblingNode.mKeys[siblingNode.mActualKeyNum + i] = nodo.mKeys[i];
                nodo.mKeys[i] = null;
            }
            siblingNode.mActualKeyNum += nodo.mActualKeyNum;
            nodo.mKeys[nodo.mActualKeyNum] = null;
        }

        // Shift the parent keys accordingly after the merge of child nodes
        for (i = nodeIdx; i < parentNode.mActualKeyNum - 1; ++i) {
            parentNode.mKeys[i] = parentNode.mKeys[i + 1];
            parentNode.mChildren[i + 1] = parentNode.mChildren[i + 2];
        }
        parentNode.mKeys[i] = null;
        parentNode.mChildren[parentNode.mActualKeyNum] = null;
        --(parentNode.mActualKeyNum);

        if (isRightSibling) {
            parentNode.mChildren[nodeIdx] = nodo;
        }
        else {
            parentNode.mChildren[nodeIdx] = siblingNode;
        }

        if ((mRoot == parentNode) && (mRoot.mActualKeyNum == 0)) {
            // Only root left
            mRoot = parentNode.mChildren[nodeIdx];
            mRoot.mEsHoja = true;
            return false;  // Root has been changed, we don't need to go futher
        }

        return true;
    }


    //
    // Re-balance the tree at a specified node
    // Params:
    // parentNode = the parent node of the node needs to be re-balanced
    // nodo = the node needs to be re-balanced
    // nodeIdx = the index of the parent node's child array where the node belongs
    // balanceType = either REBALANCE_FOR_LEAF_NODE or REBALANCE_FOR_INTERNAL_NODE
    //   REBALANCE_FOR_LEAF_NODE: the node is a leaf
    //   REBALANCE_FOR_INTERNAL_NODE: the node is an internal node
    // Return:
    // true if it needs to continue rebalancing further
    // false if further rebalancing is no longer needed
    //
    private boolean rebalanceTreeAtNode(Nodo<K, V> parentNode, Nodo<K, V> nodo, int nodeIdx, int balanceType) {
        if (balanceType == REBALANCE_FOR_LEAF_NODE) {
            if ((nodo == null) || (nodo == mRoot)) {
                return false;
            }
        }
        else if (balanceType == REBALANCE_FOR_INTERNAL_NODE) {
            if (parentNode == null) {
                // Root node
                return false;
            }
        }

        if (nodo.mActualKeyNum >= Nodo.numMinLlaves) {
            // The node doesn't need to rebalance
            return false;
        }

        Nodo<K, V> rightSiblingNode;
        Nodo<K, V> leftSiblingNode = Nodo.getLeftSiblingAtIndex(parentNode, nodeIdx);
        if ((leftSiblingNode != null) && (leftSiblingNode.mActualKeyNum > Nodo.numMinLlaves)) {
            // Do right rotate
            performRightRotation(nodo, nodeIdx, parentNode, leftSiblingNode);
        }
        else {
            rightSiblingNode = Nodo.getRightSiblingAtIndex(parentNode, nodeIdx);
            if ((rightSiblingNode != null) && (rightSiblingNode.mActualKeyNum > Nodo.numMinLlaves)) {
                // Do left rotate
                performLeftRotation(nodo, nodeIdx, parentNode, rightSiblingNode);
            }
            else {
                // Merge the node with one of the siblings
                boolean bStatus;
                if (leftSiblingNode != null) {
                    bStatus = performMergeWithLeftSibling(nodo, nodeIdx, parentNode, leftSiblingNode);
                }
                else {
                    bStatus = performMergeWithRightSibling(nodo, nodeIdx, parentNode, rightSiblingNode);
                }

                if (!bStatus) {
                    return false;
                }
            }
        }

        return true;
    }


    //
    // Re-balance the tree upward from the lower node to the upper node
    //
    private void rebalanceTree(Nodo<K, V> upperNode, Nodo<K, V> lowerNode, K key) {
        mStackTracer.clear();
        mStackTracer.add(new StackInfo(null, upperNode, 0));

        //
        // Find the child subtree (node) that contains the key
        //
        Nodo<K, V> parentNode, childNode;
        KeyValue<K, V> currentKey;
        int i;
        parentNode = upperNode;
        while ((parentNode != lowerNode) && !parentNode.mEsHoja) {
            currentKey = parentNode.mKeys[0];
            i = 0;
            while ((i < parentNode.mActualKeyNum) && (key.compareTo(currentKey.mKey) > 0)) {
                ++i;
                if (i < parentNode.mActualKeyNum) {
                    currentKey = parentNode.mKeys[i];
                }
                else {
                    --i;
                    break;
                }
            }

            if (key.compareTo(currentKey.mKey) > 0) {
                childNode = Nodo.getRightChildAtIndex(parentNode, i);
                if (childNode.mKeys[0].mKey.compareTo(parentNode.mKeys[parentNode.mActualKeyNum - 1].mKey) > 0) {
                    // The right-most side of the node
                    i = i + 1;
                }
            }
            else {
                childNode = Nodo.getLeftChildAtIndex(parentNode, i);
            }

            if (childNode == null) {
                break;
            }

            if (key.compareTo(currentKey.mKey) == 0) {
                break;
            }

            mStackTracer.add(new StackInfo(parentNode, childNode, i));
            parentNode = childNode;
        }

        boolean bStatus;
        StackInfo stackInfo;
        while (!mStackTracer.isEmpty()) {
            stackInfo = mStackTracer.pop();
            if ((stackInfo != null) && !stackInfo.mNode.mEsHoja) {
                bStatus = rebalanceTreeAtNode(stackInfo.mParent,
                                              stackInfo.mNode,
                                              stackInfo.mNodeIdx,
                                              REBALANCE_FOR_INTERNAL_NODE);
                if (!bStatus) {
                    break;
                }
            }
        }
    }


    /**
     * Inner class StackInfo for tracing-back purpose
     * Structure contains parent node and node index
     */
    public class StackInfo {
        public Nodo<K, V> mParent = null;
        public Nodo<K, V> mNode = null;
        public int mNodeIdx = -1;

        public StackInfo(Nodo<K, V> parent, Nodo<K, V> node, int nodeIdx) {
            mParent = parent;
            mNode = node;
            mNodeIdx = nodeIdx;
        }
    }
}
