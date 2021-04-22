package com.company;

public class Main {

    public static void main(String[] args) {
        // write your code here
    }
}

interface GraphADT <T extends Comparable>{
    Vertex addVertex(T value);
    void removeVertex(Vertex v) throws IllegalArgumentException;
    Edge addEdge(Vertex from, Vertex to, T weight) throws IllegalArgumentException;
    void removeEdge(Edge e) throws IllegalArgumentException;
    Edge[] edgesFrom(Vertex v);
    Edge[] edgesTo(Vertex v);
    Vertex findVertex(T value);
    Edge findEdge(T valueFrom, T valueTo);
    Edge hasEdge(Vertex v, Vertex u);
}

class AdjacencyMatrixGraph <T extends Comparable> implements GraphADT{

    DoublyLinkedList<Vertex<T>> vertexList;
    DoublyLinkedList<Edge<T>> edgeList;
    Edge[][] adjacencyMatrix;

    void DoubleAdjacencyMatrix(){
        Edge[][] newAM = new Edge[adjacencyMatrix.length*2][adjacencyMatrix.length*2];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix.length; j++) {
                newAM[i][j] = adjacencyMatrix[i][j];
            }
        }
        adjacencyMatrix = newAM;
    }
    @Override
    public Vertex addVertex(Comparable value) {
        Vertex<Comparable> vertex= new Vertex<>(value, null, vertexList.size);
        vertex.position = vertexList.Add(vertexList.size,vertex);
        if(adjacencyMatrix.length< vertex.index) DoubleAdjacencyMatrix();
        return vertex;
    }

    @Override
    public void removeVertex(Vertex v) throws IllegalArgumentException {
     //  TODO  throw new IllegalArgumentException("There's no such a vertex in the Graph");
    }

    @Override
    public Edge addEdge(Vertex from, Vertex to, Comparable weight) throws IllegalArgumentException {
//        if(adjacencyMatrix.length<= from.index || adjacencyMatrix.length<=to.index)
//            throw new IndexOutOfBoundsException();
        if(adjacencyMatrix[from.index][to.index]!= null)
            throw new IllegalArgumentException("There's such an Edge already");
        else {
            Edge<Comparable> edge = new Edge<>(from,to,null, weight);
            edge.position = edgeList.Add(edgeList.size, edge);
            adjacencyMatrix[from.index][to.index] = edge;
            return edge;
        }
    }

    @Override
    public void removeEdge(Edge e) throws IllegalArgumentException {
        //  TODO  throw new IllegalArgumentException("There's no such an edge in the Graph");
        edgeList.Remove(e.position);
        adjacencyMatrix[e.from.index][e.to.index] = null;
    }

    @Override
    public Edge[] edgesFrom(Vertex v) {
        return new Edge[0];
    }

    @Override
    public Edge[] edgesTo(Vertex v) {
        return new Edge[0];
    }

    @Override
    public Vertex findVertex(Comparable value) {
        return null;
    }

    @Override
    public Edge findEdge(Comparable valueFrom, Comparable valueTo) {
        return null;
    }

    @Override
    public Edge hasEdge(Vertex v, Vertex u) {
        return null;
    }
}


class Vertex <T extends Comparable> implements Comparable<Vertex<T>> {
    T element;
    Node position;
    int index;
    public Vertex(T element, Node position, int index) {
        this.element = element;
        this.position = position;
        this.index = index;
    }
    @Override
    public int compareTo(Vertex v) {
        return this.element.compareTo(v.element);
    }
}
class Edge <T extends Comparable> implements Comparable<Edge> {
    Vertex<T> from;
    Vertex<T> to;
    Node position;
    T weight;

    public Edge(Vertex<T> from, Vertex<T> to, Node position, T weight) {
        this.from = from;
        this.to = to;
        this.position = position;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge e) {
        return this.weight.compareTo(e.weight);
    }
}


class DoublyLinkedList<T extends Comparable> implements ListADT {
    Node<T> first, last;
    int size;

    @Override
    public int GetSize() {
        return size;
    }

    @Override
    public boolean IsEmpty() {
        return size == 0;
    }

    @Override
    public Comparable Get(int i) throws IndexOutOfBoundsException {
        return getNode(i).value;
    }

    private Node getNode(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i > size) throw new IndexOutOfBoundsException();
        Node temp;
        if (i < size / 2) {
            temp = first;
            for (int j = 0; j < i; j++) {
                temp = temp.next;
            }
        } else {
            temp = last;
            for (int j = size - 1; j > i; j--) {
                temp = temp.previous;
            }
        }
        return temp;
    }

    public void Print() {
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(getNode(i).value.toString() + " ");
        }
        System.out.println();
    }

    @Override
    public Comparable Set(int i, Comparable value) throws IndexOutOfBoundsException {
        Comparable oldValue;
        Node temp = getNode(i);
        oldValue = temp.value;
        temp.value = value;
        return oldValue;
    }

    @Override
    public Node Add(int i, Comparable value) throws IndexOutOfBoundsException {
        Node node = null;
        if (i == 0) {
            first = node = new Node(value, first, null);
            if (size == 0) last = first;
        } else if (i == size) last = new Node(value, null, last);
        else {
            Node previous = getNode(i), next = previous.next;
            node = new Node(value, previous, next);
        }
        size++;
        return node;
    }

    @Override
    public Comparable Remove(int i) throws IndexOutOfBoundsException {
        Node removedNode = getNode(i);
        if (size == 1) first = last = null;
        else if (i == 0) first = first.next;
        else if (i == size - 1) last = last.previous;
        else {
            Node previous = removedNode.previous;
            previous.next = removedNode.next;
            removedNode.next.previous = previous;
        }
        size--;
        return removedNode.value;
    }

    public int FindIndex(Node node) {
        if(node.next==null) return size-1;
        Node temp = first;
        for (int i = 0; i < size-1; i++) {
            if(temp == node) return i;
            temp = temp.next;
        }
        return -1;
    }
    public Comparable Remove(Node node) throws IndexOutOfBoundsException {
        if (size == 1) first = last = null;
        else if (node == first) first = first.next;
        else if (node == last) last = last.previous;
        else {
            Node previous = node.previous;
            previous.next = node.next;
            node.next.previous = previous;
        }
        size--;
        return node.value;
    }
}

class Node<T extends Comparable> {
    public T value;
    public Node next, previous;

    public Node(T value, Node next, Node previous) {
        this.value = value;
        if (previous != null) {
            this.previous = previous;
            previous.next = this;
        }
        if (next != null) {
            this.next = next;
            next.previous = this;
        }
    }
}

interface ListADT<T extends Comparable> {
    int GetSize();

    boolean IsEmpty();

    T Get(int i) throws IndexOutOfBoundsException;

    T Set(int i, T value) throws IndexOutOfBoundsException;

    Node Add(int i, T value) throws IndexOutOfBoundsException;

    T Remove(int i) throws IndexOutOfBoundsException;
}
