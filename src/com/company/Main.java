package com.company;

import java.util.Scanner;

public class Main {
    static AdjacencyMatrixGraph<String, Integer> graph = new AdjacencyMatrixGraph();

    public static void main(String[] args) {
        ParseInput();
    }

    public static void ParseInput() {
        Scanner sc = new Scanner(System.in);
        int it = 1;
        while (sc.hasNext()) {

            String[] input = sc.nextLine().split(" ");
            switch (input[0]) {
                case "ADD_VERTEX":
                    graph.addVertex(input[1]);
                    break;
                case "REMOVE_VERTEX":
                    graph.removeVertex(graph.findVertex(input[1]));
                    break;
                case "ADD_EDGE":
                    graph.addEdge(graph.findVertex(input[1]), graph.findVertex(input[2]), Integer.parseInt(input[3]));
                    break;
                case "REMOVE_EDGE":
                    graph.removeEdge(graph.findEdge(input[1], input[2]));
                    break;
                case "HAS_EDGE":
                    System.out.println(
                            graph.hasEdge(graph.findVertex(input[1]), graph.findVertex(input[2])) ? "TRUE" : "FALSE");
                    break;
                case "TRANSPOSE":
                    graph.Transpose();
                    break;
                case "IS_ACYCLIC":
                    DoublyLinkedList<Vertex> out = graph.CheckAcyclicity();
                    if (out == null) System.out.println("ACYCLIC");
                    else {
                        int weight = 0;
                        String output = "";

                        for (int i = 0; i < out.size; i++) {
                            Vertex from = (Vertex) out.Get(i);

                            Vertex to = (Vertex) out.Get((i + 1) % out.size);
                            output += " " + from.element.toString();
                            Edge<String, Integer> e = graph.adjacencyMatrix[from.index][to.index];
                            weight += e.weight;
                        }
                        System.out.println(weight + output);
                        break;
                    }
            }
//            graph.Print();
//            System.out.println(it++);
        }
    }
}

interface GraphADT<V extends Comparable, W extends Comparable> {
    Vertex addVertex(V value);

    void removeVertex(Vertex v) throws IllegalArgumentException;

    Edge<V, W> addEdge(Vertex from, Vertex to, W weight) throws IllegalArgumentException;

    void removeEdge(Edge<V, W> e) throws IllegalArgumentException;

    DoublyLinkedList<Edge<V, W>> edgesFrom(Vertex v);

    DoublyLinkedList<Edge<V, W>> edgesTo(Vertex v);

    Vertex findVertex(V value);

    Edge<V, W> findEdge(V valueFrom, V valueTo);

    boolean hasEdge(Vertex v, Vertex u);
}

class AdjacencyMatrixGraph<V extends Comparable, W extends Comparable> implements GraphADT {

    DoublyLinkedList<Vertex<V>> vertexList = new DoublyLinkedList<>();
    DoublyLinkedList<Edge<V, W>> edgeList = new DoublyLinkedList<>();
    Edge<V, W>[][] adjacencyMatrix = new Edge[2][2];

    public void Print() {
        String vertices = "", edges = "", aMatrix = "";
        Node ver = vertexList.first, ed = edgeList.first;

        for (int i = 0; i < vertexList.size; i++) {
            vertices += ((Vertex) ver.value).toString() + " ";
            ver = ver.next;
        }

        for (int i = 0; i < edgeList.size; i++) {
            edges += ((Edge<V, W>) ed.value).toString() + " ";
            ed = ed.next;
        }

        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix.length; j++) {
                aMatrix += adjacencyMatrix[i][j] == null ? "\t∅" : "\t" + adjacencyMatrix[i][j].weight;
            }
            aMatrix += "\n";
        }

        System.out.println(vertices + "\n" + edges + "\n" + aMatrix);
    }

    Boolean DiscoverCycles(DoublyLinkedList<Vertex> stack, Vertex vertex) {
        if (stack.Contains(vertex)) {
            while (!stack.Get(0).equals(vertex))
                stack.Remove(0);
            return true;
        }

        stack.Add(stack.size, vertex);
        for (Node tempE = edgesFrom(vertex).first; tempE != null; tempE = tempE.next) {
            Edge<V, W> e = (Edge<V, W>) tempE.value;
            if (DiscoverCycles(stack, e.to)) return true;
//            else stack.Remove(stack.FindNode(e.to));
        }
        stack.Remove(stack.FindNode(vertex));
        return false;
    }

    DoublyLinkedList<Vertex> CheckAcyclicity() {
        for (Node tempV = vertexList.first; tempV != null; tempV = tempV.next) {
            DoublyLinkedList<Vertex> stack = new DoublyLinkedList<>();
            stack.Add(stack.size, tempV.value);

            DoublyLinkedList<Vertex> verticesFrom = new DoublyLinkedList<>();
            for (Node tempE = edgesFrom((Vertex) tempV.value).first; tempE != null; tempE = tempE.next)
                verticesFrom.Add(verticesFrom.size, ((Edge<V, W>) tempE.value).to);

            for (Node tempVF = verticesFrom.first; tempVF != null; tempVF = tempVF.next)
                if (DiscoverCycles(stack, (Vertex) tempVF.value))
                    return stack;
        }
        return null;
    }

    public void Transpose() {
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = i; j < adjacencyMatrix.length; j++) {
                Vertex tempV;
                if (adjacencyMatrix[i][j] != null) {

                    tempV = adjacencyMatrix[i][j].from; //switch
                    adjacencyMatrix[i][j].from = adjacencyMatrix[i][j].to;
                    adjacencyMatrix[i][j].to = tempV;
                }

                if (adjacencyMatrix[j][i] != null) {
                    tempV = adjacencyMatrix[j][i].from;
                    adjacencyMatrix[j][i].from = adjacencyMatrix[j][i].to;
                    adjacencyMatrix[j][i].to = tempV;
                }

                Edge<V, W> tempE = adjacencyMatrix[i][j];
                adjacencyMatrix[i][j] = adjacencyMatrix[j][i];
                adjacencyMatrix[j][i] = tempE;

            }
        }
    }

    void DoubleAdjacencyMatrix() {
        Edge<V, W>[][] newAM = new Edge[adjacencyMatrix.length * 2][adjacencyMatrix.length * 2];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix.length; j++) {
                newAM[i][j] = adjacencyMatrix[i][j];
            }
        }
        adjacencyMatrix = newAM;
    }

    @Override
    public Vertex addVertex(Comparable value) {
        Vertex<Comparable> vertex = new Vertex<>(value, null, vertexList.size);
        vertex.position = vertexList.Add(vertexList.size, vertex);
        if (adjacencyMatrix.length <= vertex.index) DoubleAdjacencyMatrix();
        return vertex;
    }

    @Override
    public void removeVertex(Vertex v) throws IllegalArgumentException {
        //  TODO  throw new IllegalArgumentException("There's no such a vertex in the Graph");

        Node temp = vertexList.first;
        for (int i = 0; i < vertexList.size; i++) {
            if (((Vertex) temp.value).index > v.index) ((Vertex) temp.value).index--;
            temp = temp.next;
        }
        vertexList.Remove(v.position);

        for (int i = 0; i < adjacencyMatrix.length; i++) { //delete edges in the row from the list
            if (adjacencyMatrix[i][v.index] != null) edgeList.Remove(adjacencyMatrix[i][v.index].position);
        }
        for (int j = 0; j < adjacencyMatrix.length - 1; j++) {//delete edges in the column from the list
            if (adjacencyMatrix[v.index][j] != null) edgeList.Remove(adjacencyMatrix[v.index][j].position);
        }

        if (v.position != vertexList.last) {
            if (v.position != vertexList.first) {
                for (int j = v.index; j < adjacencyMatrix.length - 1; j++) { //move up the part under the deleted by y
                    for (int i = 0; i < v.index; i++) {
                        adjacencyMatrix[i][j] = adjacencyMatrix[i][j + 1];
                    }
                }
                for (int i = v.index; i < adjacencyMatrix.length - 1; i++) { //move left the part right the deleted by x
                    for (int j = 0; j < v.index; j++) {
                        adjacencyMatrix[i][j] = adjacencyMatrix[i + 1][j];
                    }
                }
            }
            for (int i = v.index; i < adjacencyMatrix.length - 1; i++) { //move the part under and right the deleted by diagonal
                for (int j = v.index; j < adjacencyMatrix.length - 1; j++) {
                    adjacencyMatrix[i][j] = adjacencyMatrix[i + 1][j + 1];
                }
            }
        }
        for (int i = 0; i < adjacencyMatrix.length; i++) { //delete the last row
            adjacencyMatrix[i][adjacencyMatrix.length - 1] = null;
        }
        for (int j = 0; j < adjacencyMatrix.length - 1; j++) { //delete the last column
            adjacencyMatrix[adjacencyMatrix.length - 1][j] = null;
        }
        //Print();
    }

    @Override
    public Edge addEdge(Vertex from, Vertex to, Comparable weight) throws IllegalArgumentException {
        if (adjacencyMatrix[from.index][to.index] != null)
            return null;
        else {
            Edge<V, W> edge = new Edge<>(from, to, null, weight);
            edge.position = edgeList.Add(edgeList.size, edge);
            adjacencyMatrix[from.index][to.index] = edge;
            return edge;
        }
    }

//    @Override
//    public Edge<V,W> addEdge(Vertex from, Vertex to, W weight) throws IllegalArgumentException {
////        if(adjacencyMatrix.length<= from.index || adjacencyMatrix.length<=to.index)
////            throw new IndexOutOfBoundsException();
//
//    }

    @Override
    public void removeEdge(Edge e) throws IllegalArgumentException {
        //  TODO  throw new IllegalArgumentException("There's no such an edge in the Graph");
        edgeList.Remove(e.position);
        adjacencyMatrix[e.from.index][e.to.index] = null;
    }

    @Override
    public DoublyLinkedList<Edge<V, W>> edgesFrom(Vertex v) {
        DoublyLinkedList<Edge<V, W>> edgesFrom = new DoublyLinkedList<>();
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[v.index][i] != null) edgesFrom.Add(edgesFrom.size, adjacencyMatrix[v.index][i]);
        }
        return edgesFrom;
    }

    @Override
    public DoublyLinkedList<Edge<V, W>> edgesTo(Vertex v) {
        DoublyLinkedList<Edge<V, W>> edgesTo = new DoublyLinkedList<>();
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[i][v.index] != null) edgesTo.Add(edgesTo.size, adjacencyMatrix[v.index][i]);
        }
        return edgesTo;
    }

    @Override
    public Vertex findVertex(Comparable value) {
        Node node = vertexList.first;
        for (int i = 0; i < vertexList.size; i++) {
            if (((Vertex) node.value).element.equals(value))
                return (Vertex) node.value;
            node = node.next;
        }
        return null;
    }

    @Override
    public Edge<V, W> findEdge(Comparable valueFrom, Comparable valueTo) {
        // TODO maybe add an exception iff null
        return adjacencyMatrix[findVertex(valueFrom).index][findVertex(valueTo).index];
    }

    @Override
    public boolean hasEdge(Vertex v, Vertex u) {
        return adjacencyMatrix[v.index][u.index] != null;
    }
}

class Vertex<T extends Comparable> implements Comparable<Vertex<T>> {
    T element;
    Node position;
    int index;

    public Vertex(T element, Node position, int index) {
        this.element = element;
        this.position = position;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "element=" + element +
                ", index=" + index +
                '}';
    }

    @Override
    public int compareTo(Vertex v) {
        return this.element.compareTo(v.element);
    }
}

class Edge<V extends Comparable, W extends Comparable> implements Comparable<Edge<V, W>> {
    Vertex<V> from;
    Vertex<V> to;
    Node position;
    W weight;

    public Edge(Vertex<V> from, Vertex<V> to, Node position, W weight) {
        this.from = from;
        this.to = to;
        this.position = position;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int compareTo(Edge<V, W> e) {
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
        } else if (i == size) last = node = new Node(value, null, last);
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

    public boolean Contains(Comparable value) {
        Node temp = first;
        for (int i = 0; i < size; i++) {
            if (temp.value == value) return true;
            temp = temp.next;
        }
        return false;
    }

    public int FindIndex(Node node) {
        if (node.next == null) return size - 1;
        Node temp = first;
        for (int i = 0; i < size; i++) {
            if (temp == node) return i;
            temp = temp.next;
        }
        return -1;
    }

    public Node FindNode(Comparable value) {
        if (last.value.equals(value)) return last;
        Node temp = first;
        for (int i = 0; i < size; i++) {
            if (temp.value.equals(value)) return temp;
            temp = temp.next;
        }
        return null;
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
//Eugene Kuzyakhmetov
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