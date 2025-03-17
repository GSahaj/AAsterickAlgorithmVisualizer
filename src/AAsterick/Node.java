package AAsterick;

import java.util.Objects;

class Node implements Comparable<Node>{
    int x, y;
    double g, h;
    Node parent;

    Node(int x, int y){
        this.x = x;
        this.y = y;
    }

    double f(){
        return g+h;
    }

    @Override
    public int compareTo(Node other){
        return Double.compare(this.f(), other.f());
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        Node node = (Node) object;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }
}