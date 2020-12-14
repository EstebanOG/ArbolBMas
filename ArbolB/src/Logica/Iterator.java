package Logica;



/**
 * Interface Iterator
 * @author tnguyen
 */
public interface Iterator<K extends Comparable, V> {
    public boolean item(K key, V value);
}
