import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.random;
import static java.util.stream.Collectors.joining;

import java.lang.Integer;

public class WithRecursion {

    private static Consumer<Runnable> TIMER = (c) -> {
        long cumulative = 0;
        for (int i = 0; i < 102; i++) {
            long start = System.nanoTime();
            c.run();
            long stop = System.nanoTime();
            if (i > 1) {
                cumulative += ((stop - start));
            }
        }
        System.out.println(cumulative / 100/1000);
    };

    public static void main(String[] args) {
        Node<Double> node0 = new Node<>("0", random() * 1_000_000);
        Node<Double> node1 = new Node<>("1", random() * 1_000_000);
        Node<Double> node2 = new Node<>("2", random() * 1_000_000);
        Node<Double> node3 = new Node<>("3", random() * 1_000_000);
        Node<Double> node4 = new Node<>("4", random() * 1_000_000);
        Node<Double> node5 = new Node<>("5", random() * 1_000_000);
        Node<Double> node6 = new Node<>("6", random() * 1_000_000);
        Node<Double> node7 = new Node<>("7", random() * 1_000_000);
        Node<Double> node8 = new Node<>("8", random() * 1_000_000);
        Node<Double> node9 = new Node<>("9", random() * 1_000_000);
        Node<Double> node10 = new Node<>("10", random() * 1_000_000);
        Node<Double> node11 = new Node<>("11", random() * 1_000_000);
        Node<Double> node12 = new Node<>("12", random() * 1_000_000);
        Node<Double> node13 = new Node<>("13", random() * 1_000_000);

        node0.addVertex(node2, 5);
        node0.addVertex(node1, 1);
        node1.addVertex(node0, 3);
        node1.addVertex(node4, 8);
        node1.addVertex(node5, 3);
        node2.addVertex(node3, 10);
        node3.addVertex(node1, 7);
        node3.addVertex(node5, 4);
        node4.addVertex(node5, 6);
        node5.addVertex(node8, 11);
        node5.addVertex(node6, 1);
        node6.addVertex(node8, 3);
        node6.addVertex(node7, 4);
        node6.addVertex(node12, 4);
        node8.addVertex(node9, 9);
        node8.addVertex(node7, 5);
        node9.addVertex(node13, 3);
        node9.addVertex(node10, 5);
        node10.addVertex(node7, 7);
        node10.addVertex(node11, 2);
        node11.addVertex(node12, 3);
        node13.addVertex(node10, 5);
        node13.addVertex(node12, 3);

        TIMER.accept(() -> node0.searchForShortestPath(node12));
        System.out.println(node0.searchForShortestPath(node12));

    }


    public static class Node<T> {
        String id;
        T data;
        LinkedList<Vertex<T>> vertices = new LinkedList<>();

        public Node(String id, T data) {
            this.id = id;
            this.data = data;
        }

        void addVertex(Node<T> destinationNode, int cost) {
            Vertex<T> vertex = new Vertex<>();
            vertex.destinationNode = destinationNode;
            vertex.cost = cost;
            vertices.add(vertex);
        }

        public Node<Integer> searchForShortestPath(Node<T> finalDestination) {
            Node<Integer> pathAndCost = new Node<>(this.id, MAX_VALUE);
            if (this.id.equals(finalDestination.id)) {
                pathAndCost.data = 0;
                return pathAndCost;
            }
            LinkedList<Node<T>> paths = new LinkedList<>();
            paths.add(this);
            searchForShortestPath(this, finalDestination, paths, new HashSet<>(), pathAndCost, 0);
            return pathAndCost;
        }

        private void searchForShortestPath(Node<T> start,
                                           Node<T> finalDestination,
                                           LinkedList<Node<T>> visitedNodes,
                                           HashSet<String> visitedNodesHashset,
                                           Node<Integer> results,
                                           int currentPathCost) {
            while (true) {
                for (Vertex<T> n :
                        start.vertices) {

                    // protection against cyclic connections, otherwise our program will be stuck forever
                    if (visitedNodesHashset.contains(n.destinationNode.id)) {
                        continue;
                    }

                    /*
                     the objective here if we already have calculated a cost of the path to our destination
                     and the current cost of the current examined path is larger , even if we didn't reach our
                     target there is no point continuing
                      */
                    else if (currentPathCost > results.data) {
                        continue;
                    }

                    visitedNodes.addLast(n.destinationNode);
                    visitedNodesHashset.add(n.destinationNode.id);
                    currentPathCost += n.cost;

                    if (n.destinationNode.id.equals(finalDestination.id)) {
                        if (results.data > currentPathCost) {
                            results.id = fromListToString(visitedNodes);
                            results.data = currentPathCost;
                        }
                        visitedNodesHashset.remove(visitedNodes.removeLast().id);
                        currentPathCost -= n.cost;
                        continue;
                    }

                    searchForShortestPath(n.destinationNode,
                            finalDestination,
                            visitedNodes,
                            visitedNodesHashset,
                            results,
                            currentPathCost);

                    visitedNodesHashset.remove(visitedNodes.pop().id);
                    currentPathCost -= n.cost;
                }
                break;
            }
        }

        private String fromListToString(List<Node<T>> stringList) {
            return stringList.stream().map(n -> n.id).collect(joining("→"));
        }

        @Override
        public String toString() {
            return "Path: " + id + ";Cost: " + data;
        }


    }

    public static class Vertex<T> {
        Node<T> destinationNode;

        int cost = 1;

    }

}