package cn.heshiqian.hotpothttp.core.addtion;

import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public final class ConfigTree {

    private ArrayList<ConfigNode> linkes = new ArrayList<>();
    private HashMap<String,Integer> posHashMap=new HashMap<>();

    private String nodeName;

    public ConfigTree(String nodeName) {
        this.nodeName = nodeName;
    }

    public void addNode(ConfigNode configNode){
        linkes.add(configNode);
    }

    public static class ConfigNode<T>{
        private String nodeName;
        private T nodeValue;
        private ArrayList<ConfigNode> nodes = new ArrayList<>();

        public ConfigNode(String nodeName,T nodeValue) {
            this.nodeName = nodeName;
            this.nodeValue = nodeValue;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public void setNodeValue(T nodeValue) {
            this.nodeValue = nodeValue;
        }

        public T getNodeValue() {
            return nodeValue;
        }

        public void nextNode(ArrayList<ConfigNode> nodes){
            this.nodes=nodes;
        }

        public void addNextNode(ConfigNode configNode){
            nodes.add(configNode);
        }
    }

}
