package arbolBMas;



/**
 * Class KeyValue
 * @author tnguyen
 */
public class KeyValue<K extends Comparable>//, V>
{
    public K mKey;
//    public V mValue;

//    public KeyValue(K key, V value) {
//        mKey = key;
//        mValue = value;
//    }
    public KeyValue(K key) {
        mKey = key;
    }
}
