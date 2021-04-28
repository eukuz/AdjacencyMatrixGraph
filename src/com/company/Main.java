package com.company;
//Eugene Kuzyakhmetov BS20-02
import java.util.*;

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

        }
    }
    /**
     * Works with Dijkstra's algorithm, with previous nodes in each path being saved
     * such that the shortest path to a certain node can be restored.
     *
     * @param from Vertex from
     * @param to Vertex to
     * @param bandwidth
     */
//    static void Dijkstra(Vertex<Integer> from, Vertex<Integer> to, int bandwidth) {
//        PriorityQueue<Vertex<Integer>> pQ = new PriorityQueue(graph.vertexList.size, from);
//        from.element = 0;
//        pQ.add(from);
//
//        ArrayList<Vertex> path = new ArrayList<>();
//        int minWidth = Integer.MAX_VALUE;
//        Vertex c = to;
//
//        while (!pQ.isEmpty()) {
//            Vertex<Integer> v = pQ.poll();
//            if (v.element < Integer.MAX_VALUE) {
//                for (Node<Edge<Integer,Tuple>> tempE = graph.edgesFrom(v).first; tempE != null; tempE = tempE.next){
//                    Edge<Integer,Tuple> edge = tempE.value;
//                    if (edge.to.element > v.element + edge.weight.length && edge.weight.bandwidth >= bandwidth) {
//                        pQ.remove(edge.to);
//                        edge.to.element = edge.weight.length + v.element;
//                        pQ.add(edge.to);
//                        edge.to.parent = v;
//                    }
//                }
//            }
//        }
//
//        if (to.element != Integer.MAX_VALUE) {
//            path.add(c);
//            while (c != from) {
//                if (minWidth > graph.adjacencyMatrix[c.parent.index][c.index].weight.bandwidth)
//                    minWidth = graph.adjacencyMatrix[c.parent.index][c.index].weight.bandwidth;
//                path.add(c.parent);
//                c = c.parent;
//            }
//            Collections.reverse(path);
//            System.out.println(path.size() + " " + to.element + " " + minWidth);
//            String output = String.valueOf(path.get(0).index);
//            for (int i = 1; i < path.size(); i++) output += " " + path.get(i).index;
//            System.out.println(output);
//        }
//        else System.out.println("IMPOSSIBLE");
//    }
}

/**
 * Abstract data type describing Graphs
 *
 * @param <V> generic param for a Vertexes of the graph
 * @param <W> generic param for a Edges of the graph
 */
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

/**
 * Class implementing GraphADT via Adjacency Matrix concept
 * @param <V> generic param for a Vertexes of the graph
 * @param <W> generic param for a Edges of the graph
 */
class AdjacencyMatrixGraph<V extends Comparable , W extends Comparable> implements GraphADT {

    DoublyLinkedList<Vertex<V>> vertexList = new DoublyLinkedList<>();
    DoublyLinkedList<Edge<V, W>> edgeList = new DoublyLinkedList<>();
    Edge<V, W>[][] adjacencyMatrix = new Edge[2][2];

    /**
     * Prints the whole information of the current state of a graph
     */
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

    /**
     * Recursively traverses forward a path, determines cycles
     * @param stack stores the path
     * @param vertex Vertex
     * @return Is there a cycle
     */
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
        }
        stack.Remove(stack.FindNode(vertex));
        return false;
    }

    /**
     * Forms a list of cycled vertices in there any
     * @return Cycled list of vertices
     */
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

    /**
     * Transposes the graph
     */
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

    /**
     * Resizes the adjacency matrix by the doubling strategy
     */
    void DoubleAdjacencyMatrix() {
        Edge<V, W>[][] newAM = new Edge[adjacencyMatrix.length * 2][adjacencyMatrix.length * 2];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix.length; j++) {
                newAM[i][j] = adjacencyMatrix[i][j];
            }
        }
        adjacencyMatrix = newAM;
    }

    /**
     * Adds a vertex to the graph with a given value
     * @param value Value of a new vertex
     * @return Formed vertex
     */
    @Override
    public Vertex addVertex(Comparable value) {
        Vertex<Comparable> vertex = new Vertex<>(value, null, vertexList.size);
        vertex.position = vertexList.Add(vertexList.size, vertex);
        if (adjacencyMatrix.length <= vertex.index) DoubleAdjacencyMatrix();
        return vertex;
    }

    /**
     * Removes given vertex and its incident Edges
     * @param v Vertex to be excluded from the graph
     */
    @Override
    public void removeVertex(Vertex v) {
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
    }

    /**
     * Add an edge with given params
     * @param from Vertex from
     * @param to Vertex to
     * @param weight Weight of a new Edge
     * @return Formed Edge
     */
    @Override
    public Edge addEdge(Vertex from, Vertex to, Comparable weight) {
        if (adjacencyMatrix[from.index][to.index] != null)
            return null;
        else {
            Edge<V, W> edge = new Edge<>(from, to, null, weight);
            edge.position = edgeList.Add(edgeList.size, edge);
            adjacencyMatrix[from.index][to.index] = edge;
            return edge;
        }
    }

    /**
     * Remove the given edge from the graph
     * @param e
     */
    @Override
    public void removeEdge(Edge e) throws IllegalArgumentException {
        edgeList.Remove(e.position);
        adjacencyMatrix[e.from.index][e.to.index] = null;
    }

    /**
     * Forms the set of edges that are from the given vertex
     * @param v Vertex from which the list of outgoing edges is formed
     * @return set of edges that are from the given vertex
     */
    @Override
    public DoublyLinkedList<Edge<V, W>> edgesFrom(Vertex v) {
        DoublyLinkedList<Edge<V, W>> edgesFrom = new DoublyLinkedList<>();
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[v.index][i] != null) edgesFrom.Add(edgesFrom.size, adjacencyMatrix[v.index][i]);
        }
        return edgesFrom;
    }

    /**
     * Forms the set of edges that go into the given vertex
     * @param v Vertex from which the list of incoming edges is formed
     * @return set of edges that go into the given vertex
     */
    @Override
    public DoublyLinkedList<Edge<V, W>> edgesTo(Vertex v) {
        DoublyLinkedList<Edge<V, W>> edgesTo = new DoublyLinkedList<>();
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[i][v.index] != null) edgesTo.Add(edgesTo.size, adjacencyMatrix[v.index][i]);
        }
        return edgesTo;
    }

    /**
     * Finds a vertex with a given value in the graph
     * @param value of a Vertex to be find
     * @return Vertex with a given value
     */
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

    /**
     * Find an edge in the graph coming from one vertex with a given value to another
     * @param valueFrom of a Vertex from which comes the edge to be found
     * @param valueTo of a Vertex to which comes the edge to be found
     * @return Found Edge
     */
    @Override
    public Edge<V, W> findEdge(Comparable valueFrom, Comparable valueTo) {
        // TODO maybe add an exception iff null
        return adjacencyMatrix[findVertex(valueFrom).index][findVertex(valueTo).index];
    }

    /**
     * Checks whether given vertices have an edge between
     * @param v Vertex from there supposed to be an edge
     * @param u Vertex to there supposed to be an edge
     * @return true if found, false otherwise
     */
    @Override
    public boolean hasEdge(Vertex v, Vertex u) {
        return adjacencyMatrix[v.index][u.index] != null;
    }
}

/**
 * Class representing vertices of a graph
 * @param <T> generic param of a stored value in a Vertex
 */
class Vertex<T extends Comparable> implements Comparable<Vertex<T>>, Comparator<Vertex<T>> {
    T element;
    Node position;
    int index;
    Vertex<T> parent;

    /**
     * Vertex Constructor
     * @param element value to be stored
     * @param position place in the DoublyLinkedList
     * @param index index of the node in the graph
     */
    public Vertex(T element, Node position, int index) {
        this.element = element;
        this.position = position;
        this.index = index;
    }

    /**
     * Converts a vertex to a string format
     * @return string representation of a Vertex
     */
    @Override
    public String toString() {
        return "Vertex{" +
                "element=" + element +
                ", index=" + index +
                '}';
    }

    /**
     * Compares Vertex with the another one, needed for the interface Comparable
     * @param v Vertex to be compared with
     * @return -1 if smaller, 0 if equal, 1 if bigger
     */
    @Override
    public int compareTo(Vertex v) {
        return this.element.compareTo(v.element);
    }

    /**
     * Compares Vertex with the another one, needed for the interface Comparable
     * @param v1 1st vertex to be compared
     * @param v2 2nd vertex to be compared
     * @return -1 if smaller, 0 if equal, 1 if bigger
     */
    @Override
    public int compare(Vertex<T> v1, Vertex<T> v2) {
        return Integer.compare((int)v1.element, (int)v2.element);
    }
}

/**
 * Auxiliary class for packing two parameters into generic of edges
 */
class Tuple implements Comparable {
    int length;
    int bandwidth;

    /**
     * Tuple Constructor
     * @param length in meters
     * @param bandwidth
     */
    public Tuple(Integer length, Integer bandwidth) {
        this.length = length;
        this.bandwidth = bandwidth;
    }

    /**
     * Redundant method to maintain the Comparable interface
     * @param o param to be compared with
     * @return
     */
    @Override
    public int compareTo(Object o) {
        return 0;
    }

}

/**
 * Class representing edges in a graph
 * @param <V> generic for vertices
 * @param <W> generic for edges
 */
class Edge<V extends Comparable, W extends Comparable> implements Comparable<Edge<V, W>> {
    Vertex<V> from;
    Vertex<V> to;
    Node position;
    W weight;

    /**
     * Constructor for edges
     * @param from Vertex from
     * @param to Vertex to
     * @param position place in the DoublyLinkedList
     * @param weight value of the edge
     */
    public Edge(Vertex<V> from, Vertex<V> to, Node position, W weight) {
        this.from = from;
        this.to = to;
        this.position = position;
        this.weight = weight;
    }

    /**
     * Converts Edge to a string format
     * @return string representation of the Edge
     */
    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", weight=" + weight +
                '}';
    }

    /**
     * Compares the edge with another one
     * @param e edge to be compared with
     * @return @return -1 if smaller, 0 if equal, 1 if bigger
     */
    @Override
    public int compareTo(Edge<V, W> e) {
        return this.weight.compareTo(e.weight);
    }
}

/**
 * Implementing ListADT via Doubly Linked List concept
 * @param <T> generic for nodes
 */
class DoublyLinkedList<T extends Comparable> implements ListADT {
    Node<T> first, last;
    int size;

    /**
     * Get size of the DLL
     * @return size of the DLL
     */
    @Override
    public int GetSize() {
        return size;
    }

    /**
     * Check whether DLL is empty
     * @return true if DLL is empty, false otherwise
     */
    @Override
    public boolean IsEmpty() {
        return size == 0;
    }

    /**
     * Get a value of a node with the index provided
     * @param i index of the node on the DLL
     * @return value of the node by index
     */
    @Override
    public Comparable Get(int i) {
        return getNode(i).value;
    }

    /**
     * Get node by index
     * @param i index of the node on the DLL
     * @return node by index
     * @throws IndexOutOfBoundsException if the index is out of bounds of the DLL
     */
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

    /**
     * Prints the values of the DLL to the console
     */
    public void Print() {
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(getNode(i).value.toString() + " ");
        }
        System.out.println();
    }

    /**
     * Update the value by index
     * @param i index
     * @param value new value
     * @return old value
     * @throws IndexOutOfBoundsException if the index is out of bounds of the DLL
     */
    @Override
    public Comparable Set(int i, Comparable value)throws IndexOutOfBoundsException {
        if (i < 0 || i > size) throw new IndexOutOfBoundsException();
        Comparable oldValue;
        Node temp = getNode(i);
        oldValue = temp.value;
        temp.value = value;
        return oldValue;
    }

    /**
     * Insert the value by index
     * @param i index
     * @param value new value
     * @return formed node
     * @throws IndexOutOfBoundsException if the index is out of bounds of the DLL
     */
    @Override
    public Node Add(int i, Comparable value) throws IndexOutOfBoundsException {
        if (i < 0 || i > size) throw new IndexOutOfBoundsException();
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

    /**
     * Removes the value by index
     * @param i index
     * @return removed value
     * @throws IndexOutOfBoundsException if the index is out of bounds of the DLL
     */
    @Override
    public Comparable Remove(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i > size) throw new IndexOutOfBoundsException();
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

    /**
     * Checks whether the value in th DLL
     * @param value to be checked
     * @return true if found, false otherwise
     */
    public boolean Contains(Comparable value) {
        Node temp = first;
        for (int i = 0; i < size; i++) {
            if (temp.value == value) return true;
            temp = temp.next;
        }
        return false;
    }

    /**
     * Find node by value
     * @param value for a node to be found
     * @return Found node or null if not found
     */
    public Node FindNode(Comparable value) {
        if (last.value.equals(value)) return last;
        Node temp = first;
        for (int i = 0; i < size; i++) {
            if (temp.value.equals(value)) return temp;
            temp = temp.next;
        }
        return null;
    }


    /**
     * Remove node from the DLL
     * @param node to be removed
     * @return removed value
     */
    public Comparable Remove(Node node) {
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

/**
 * Class representing Nodes in the DLL
 * @param <T> generic value of the node
 */
class Node<T extends Comparable> {
    public T value;
    public Node next, previous;

    /**
     * Constructor of the Node
     * @param value value of new node
     * @param next link to the previous node
     * @param previous link to the next node
     */
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

/**
 * Interface representing List abstract data type
 * @param <T> generic value of nodes
 */
interface ListADT<T extends Comparable & Comparator> {
    int GetSize();

    boolean IsEmpty();

    T Get(int i) throws IndexOutOfBoundsException;

    T Set(int i, T value) throws IndexOutOfBoundsException;

    Node Add(int i, T value) throws IndexOutOfBoundsException;

    T Remove(int i) throws IndexOutOfBoundsException;
}