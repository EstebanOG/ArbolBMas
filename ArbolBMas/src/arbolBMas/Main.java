package arbolBMas;

import static arbolBMas.TipoNodoArbol.NodoHoja;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Toolkit;
// import java.awt.Window;
import java.awt.BorderLayout;
// import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
// import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

public class Main extends JFrame {

    public final static int APP_WIDTH = 1140;
    public final static int APP_HEIGHT = 650;
    public final static int HEIGHT_STEP = 80;
    public final static int NODE_HEIGHT = 30;
    public final static int NODE_DIST = 16;
    public final static int TREE_HEIGHT = 32;

    private final StringBuilder mBuf;
    private final Object[] mObjLists;
    private mxGraph mGraph;
    // private mxGraphComponent mGraphComponent;
    private final JTextField mText, nText;
    private final JButton mAddBt, mRemoveBt;
    private final JButton mAddMoreBt, mRemoveMoreBt;
    private final JButton mClearBt, mSearchKeyBt;
    private final JButton mListBt;
    private final JTextArea mOutputConsole;
    private final ArbolBMas<Integer, String> arbolBMas;

    public Main() {
        super("Arbol B");
//        mTreeTest = new Test();
        arbolBMas = new ArbolBMas<Integer, String>();
        mBuf = new StringBuilder();
        mObjLists = new Object[TREE_HEIGHT];
        mAddBt = new JButton("Insertar");
        mRemoveBt = new JButton("Borrar");
        mAddMoreBt = new JButton("+");
        mRemoveMoreBt = new JButton("-");
        mSearchKeyBt = new JButton("Buscar");
        mClearBt = new JButton("Limpiar");
        mListBt = new JButton("Lista");
        mText = new JTextField("");
        nText = new JTextField("");
        mOutputConsole = new JTextArea(4, 80);

        // mText.addAncestorListener(new RequestFocusListener());
        mSearchKeyBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchButtonPressed();
            }
        });

        mAddBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButtonPressed();
            }
        });

        mRemoveBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeButtonPressed();
            }
        });

        mAddMoreBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMoreButtonPressed();
            }
        });

        mRemoveMoreBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeMoreButtonPressed();
            }
        });

        mListBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listButtonPressed();
            }
        });

        mClearBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearButtonPressed();
            }
        });

        //generateTestData();
    }

    private Integer getInputValue() {
        String strInput = mText.getText().trim();
        int nVal;

        try {
            nVal = Integer.parseInt(strInput);
        } catch (java.lang.Exception ex) {
            return null;
        }

        mText.setFocusable(true);
        return nVal;
    }

    public void searchButtonPressed() {
        Integer in = getInputValue();
        if (in == null) {
            return;
        }

        mText.setText("");
        searchKey(in);
    }

    public void addButtonPressed() {
        Integer in = getInputValue();
        String on = nText.getText().trim();
        if (in == null) {
            return;
        }

        mText.setText("");
        nText.setText("");
        addKey(in, on);
        render();
    }

    public void removeButtonPressed() {
        Integer in = getInputValue();
        if (in == null) {
            return;
        }

        mText.setText("");
        nText.setText("");
//        deleteKey(in);
        render();
    }

    public void addMoreButtonPressed() {
        Integer in = getInputValue();
        if (in == null) {
            return;
        }

//        addKey(in, "");
        in += 1;
        mText.setText(in + "");
        render();
    }

    public void removeMoreButtonPressed() {
        Integer in = getInputValue();
        if (in == null) {
            return;
        }

//        deleteKey(in);
        in -= 1;
        mText.setText(in + "");
        render();
    }

    public void clearButtonPressed() {
//        mTreeTest.getBTree().clear();
        mOutputConsole.setText("");
        render();
    }

//    private Iterator mIter = null;
    public void listButtonPressed() {
//        if (mIter == null) {
//            mIter = new Main.IteratorImpl();
//        }
//
//        mOutputConsole.setText("");
//        mTreeTest.listItems(mIter);
    }

    public void render() {
        mGraph = new mxGraph();
        Object parent = mGraph.getDefaultParent();
        List<Object> pObjList = new ArrayList<Object>();
        List<Object> cObjList = new ArrayList<Object>();
        List<Object> tempObjList;

        for (int i = 0; i < TREE_HEIGHT; ++i) {
            mObjLists[i] = null;
        }

        generateGraphObject(arbolBMas.getRaiz(), 0);
        

        Box hBox = Box.createHorizontalBox();
        hBox.add(new JLabel("   Llave:  "));
        hBox.add(mText);
        hBox.add(nText);
        hBox.add(mAddBt);
        hBox.add(mRemoveBt);
        hBox.add(mAddMoreBt);
        hBox.add(mRemoveMoreBt);
        hBox.add(mSearchKeyBt);
        hBox.add(mListBt);
        hBox.add(mClearBt);

        mGraph.getModel().beginUpdate();

        try {
            int nStartXPos;
            int nStartYPos = 10;
            int cellWidth;
            for (int i = 0; i < mObjLists.length; ++i) {
                cObjList.clear();
                List<KeyData> objList = (List<KeyData>) mObjLists[i];
                if (objList == null) {
                    continue;
                }

                int totalWidth = 0;
                int nCount = 0;
                for (KeyData keyData : objList) {
                    totalWidth += keyData.mKeys.length() * 6;
                    if (nCount > 0) {
                        totalWidth += NODE_DIST;
                    }
                    ++nCount;
                }

                nStartXPos = (APP_WIDTH - totalWidth) / 2;
                if (nStartXPos < 0) {
                    nStartXPos = 0;
                }

                for (KeyData keyData : objList) {
                    int len = keyData.mKeys.length();
                    if (len == 1) {
                        len += 2;
                    }
                    cellWidth = len * 6;
                    Object gObj = mGraph.insertVertex(parent, null, keyData.mKeys, nStartXPos, nStartYPos, cellWidth, 24);
                    cObjList.add(gObj);
                    nStartXPos += (cellWidth + NODE_DIST);
                }

                if (i > 0) {
                    // Conecta los nodos
                    List<KeyData> keyList = (List<KeyData>) mObjLists[i - 1];
                    int j = 0, k = 0;
                    for (Object pObj : pObjList) {
                        KeyData keyData = keyList.get(j);
                        for (int l = 0; l < keyData.mKeyNum + 1; ++l) {
                            mGraph.insertEdge(parent, null, "", pObj, cObjList.get(k));
                            ++k;
                        }
                        ++j;
                    }
                }

                // Cambia color de las páginas
                mGraph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ffffff", cObjList.toArray());

                // Intercambia dos listas de objetos para el siguiente ciclo
                tempObjList = pObjList;
                pObjList = cObjList;
                cObjList = tempObjList;

                nStartYPos += HEIGHT_STEP;
            }
        } finally {
            mGraph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(mGraph);
        getContentPane().removeAll();
        getContentPane().add(hBox, BorderLayout.NORTH);
        getContentPane().add(graphComponent, BorderLayout.CENTER);
        getContentPane().add(new JScrollPane(mOutputConsole), BorderLayout.SOUTH);
        // addClickHandler(graphComponent);
        revalidate();
    }

    public void addClickHandler(mxGraphComponent graphComponent) {
        // mxGraphComponent graphComponent = new mxGraphComponent(mGraph);
        getContentPane().add(graphComponent);
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                /*
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                if (cell != null) {
                    println("cell=" + mGraph.getLabel(cell));
                }
                 */
            }
        });
    }

    public static void centreWindow(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    private void generateGraphObject(Nodo<Integer> nodoArbol, int nivel){
        if ((nodoArbol == null)
                || (nodoArbol.numClaves == 0)) {
            return;
        }

        int numActualClaves = nodoArbol.numClaves;
        //KeyValue<Integer, String> keyVal;
        Object keyVal;
        List<KeyData> keyList = (List<KeyData>) mObjLists[nivel];
        if (keyList == null) {
            keyList = new ArrayList<KeyData>();
            mObjLists[nivel] = keyList;
        }

        mBuf.setLength(0);
        // Renderiza las claves en el nodo
        for (int i = 0; i < numActualClaves; ++i) {
            if (i > 0) {
                mBuf.append(" | ");
            }

            keyVal = nodoArbol.claves[i];
            mBuf.append(keyVal);
            //mBuf.append("(" + keyVal.mValue + ")");
        }

        keyList.add(new KeyData(mBuf.toString(), numActualClaves));

        if (nodoArbol.getTipoNodo()== NodoHoja) {
            return;
        }

        ++nivel;
        for (int i = 0; i < numActualClaves + 1; ++i) {
            NodoInterno<Integer> nodoInterno = (NodoInterno<Integer>) nodoArbol;
            generateGraphObject((Nodo<Integer>) nodoInterno.hijos[i], nivel);
        }
    }

    public void println(String strText) {
        mOutputConsole.append(strText);
        mOutputConsole.append("\n");
    }

    public void searchKey(Integer key) {
//        println("Buscar la llave  = " + key);
////        String strVal = mTreeTest.getBTree().search(key);
//        if (strVal != null) {
//            println("Llave = " + key + " | Valor = " + strVal);
//        } else {
//            println("No hay un valor para la llave  = " + key);
//        }
    }

//    public void deleteKey(Integer key) {
//        String strVal = mTreeTest.getBTree().delete(key);
//        println("Borrar llave = " + key + " | valor = " + strVal);
//    }
//
    public void addKey(Integer key, String value) {
        println("Add key = " + key);
//        mTreeTest.getBTree().insertar(key, value);
        arbolBMas.insertar(key, value);
    }
//
//    public final void generateTestData() {
//        for (int i = 1; i < 42; ++i) {
//            mTreeTest.add(i, " " + i);
//        }
//
//        try {
//            mTreeTest.delete(24);
//            mTreeTest.delete(23);
//            mTreeTest.delete(27);
//        } catch (ABException btex) {
//            btex.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(APP_WIDTH, APP_HEIGHT);
        centreWindow(frame);
        frame.render();
        frame.setVisible(true);
    }

    class KeyData {

        String mKeys = null;
        int mKeyNum = 0;

        KeyData(String keys, int keyNum) {
            mKeys = keys;
            mKeyNum = keyNum;
        }
    }

//    class IteratorImpl<K extends Comparable, V> implements Iterator<K, V> {
//
//        private StringBuilder mBuf = new StringBuilder();
//
//        @Override
//        public boolean item(K key, V value) {
//            mBuf.setLength(0);
//            mBuf.append(key)
//                    .append("  |  Valor = ")
//                    .append(value);
//            println(mBuf.toString());
//            /*
//            if (key.compareTo(30) == 0) {
//                return false;
//            }
//             */
//            return true;
//        }
//    }
}
