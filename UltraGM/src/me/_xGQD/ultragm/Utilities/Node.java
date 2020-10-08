package me._xGQD.ultragm.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author w w w. j a v a g i s t s . c o m
 *
 */
public class Node<K, V> {

    private V data = null;
    private K key = null;

    private List<Node<K, V>> children = new ArrayList<>();

    private Node<K, V> parent = null;

    public Node(K key, V data) {
        this.data = data;
        this.key = key;
    }

    public Node<K, V> addChild(Node<K, V> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }
    public Node<K, V> getChild(K key){
        for(Node<K, V> child : children){
            if(child.getKey().equals(key)){
                return child;
            }
        }
        return null;
    }
    public Pair<Integer, Node<K, V>> getNodeFromPath(int start, K[] path){
        String key = "";
        int i = 0;
        if(path.length == start){
            return new Pair<Integer, Node<K, V>>(start, this);
        }else{
            Node<K, V> child = this.getChild(path[start]);
            if(child != null) {
                return (child.getNodeFromPath(start + 1, path));
            }else{
                return new Pair<Integer, Node<K, V>>(start, this);
            }
        }
    }
    public void addChildren(List<Node<K, V>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<Node<K, V>> getChildren() {
        return children;
    }

    public V getData() {
        return data;
    }
    public K getKey(){
        return key;
    }

    public void setData(V data) {
        this.data = data;
    }

    public void setParent(Node<K, V> parent) {
        this.parent = parent;
    }

    public Node<K, V> getParent() {
        return parent;
    }

}