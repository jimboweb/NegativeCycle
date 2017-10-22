import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class NegativeCycle {
    int maxEdgeWeight = 20;
    
    //NAIVE ALGORITHM:
    //for each node, find every path to itself.
    //if any are negative, stop. 
    
    //this unit test does not work because 
    // there might be negative paths where not
    // all the parts are negative. 
    private void unitTest(){
        Random rnd = new Random();
        int trials = 100;
        for(int t=0;t<trials;t++){
            boolean containsNc = false;//rnd.nextBoolean();
            int m = rnd.nextInt(10)+3; //999
            Node[] nodes = new Node[m];
            for(int i=0; i<m; i++)
                nodes[i] = new Node(i);
            int n = Math.max(rnd.nextInt(triangular(m)), 3);
            for(int i=0;i<n;i++){
                int startNode = rnd.nextInt(m);
                int endNode;
                int edgeWeight;
                do{
                    endNode = rnd.nextInt(m);
                    edgeWeight = rnd.nextInt(maxEdgeWeight*2) - maxEdgeWeight;
                } while(startNode == endNode ||
                        nodes[startNode].adj.contains(endNode));
                if(edgeWeight<0 && !containsNc){
                    if(nodes[startNode].prevNeg)
                        edgeWeight*=-1;
                    else 
                        nodes[startNode].prevNeg = true;
                    
                    for(int c : nodes[endNode].cost){
                        if(c<0){
                            edgeWeight*=-1;
                            break;
                        }
                    }
                } 
                nodes[startNode].adj.add(endNode);
                nodes[startNode].cost.add(edgeWeight);
                
            }
            if(containsNc){
                int startNc = rnd.nextInt(m);
                addNegCycle(nodes, startNc, startNc);
            }
            nodes[0].dist = 0;
            int hnc = negativeCycle(nodes);
            int nnc = naiveNegCycle(nodes);
            if(hnc==nnc){
                System.out.print(".");
            } else {
                System.out.println("different return");
                System.out.printf("naive answer is %d efficient answer is %d %n", nnc, hnc);
                System.out.printf("%d %d%n", m, n);
                for(Node nd:nodes){
                    for(int a=0; a<nd.adj.size();a++){
                        System.out.printf("%d %d %d%n", nd.index + 1, nd.adj.get(a) + 1, nd.cost.get(a));
                    }
                }
            }
            continue;
        }
        
    }
    
    private void addNegCycle(Node[] nodes, int node, int startNc){
        Random rnd = new Random();
        int nextNode;
        do{
            nextNode = rnd.nextInt(nodes.length);
        } while(nextNode == nodes[node].index);
        int nnInd = nodes[node].adj.indexOf(nextNode);
        if(nnInd != -1){
            if(nodes[node].cost.get(nnInd) > 0)
                nodes[node].cost.set(nnInd, nodes[node].cost.get(nnInd) * -1);
        } else {
            nodes[node].adj.add(nextNode);
            nodes[node].cost.add(rnd.nextInt(maxEdgeWeight)*-1);
        }
        if(nextNode != startNc)
            addNegCycle(nodes, nextNode, startNc);
     }
    
    private int naiveNegCycle(Node[] nodes){
        int negativeCycle = 0;
        boolean[] discovered = new boolean[nodes.length];
        for(int i=0;i<discovered.length; i++)
            discovered[i] = false;
        for(Node n : nodes){
            int d = dfsCycle(nodes, n.index, n.index, 0, 0, discovered);
            if(d<0)
                return 1;
        }
        return negativeCycle;
    }
    
    private int dfsCycle(Node[] nodes, int currentNode, int startNode, int distance, int depth, boolean[] discovered){
        if(currentNode == startNode && depth>0)
            return distance;
        
        //if depth>1000 or so print the array
        if(!discovered[currentNode]){
            discovered[currentNode] = true;
            for(int i= 0; i < nodes[currentNode].adj.size(); i++){
                Node n = nodes[nodes[currentNode].adj.get(i)];
                int cost = nodes[currentNode].cost.get(i);
                int d = 0;
                try{
                    d = dfsCycle(nodes, n.index, startNode, distance + cost, depth + 1, discovered);
                } catch (StackOverflowError e){
                    System.out.println(e.getMessage());
                }
                if(d<Integer.MAX_VALUE){
                    return d;
                }
            }
        }
        discovered[currentNode] = false;
        return Integer.MAX_VALUE;
    }
    
    private int negativeCycle(Node[] nodes){
        int negativeCycle = 0;
        
        // it's because I set nodes[0] to 0 but not all 
        // the others. so when it starts from nodes[8] it
        // doesn't continue on the conditional on line 139
        for(int j=0;j<=nodes.length; j++){
            for(Node u:nodes){
                //if(u.dist<Integer.MAX_VALUE){
                    // I need to replace the last line with a 
                    // procedure that re-initializes the distance of
                    // all nodes to infinity except u, which is 1
                    for(int i=0; i<u.adj.size();i++){
                        Node endNode = nodes[u.adj.get(i)];
                        if(endNode.dist>u.dist + u.cost.get(i)){
                            endNode.dist = u.dist + u.cost.get(i);
                            endNode.prev = u.index;
                            if(j==nodes.length){
                                negativeCycle = 1;
                                break;
                            }
                        }
                    }
                //}
            }
        } 
        return negativeCycle;
    }

    private void normalRun(){
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Node[] nodes = new Node[n];
        for(int nd = 0; nd < n; nd++)
            nodes[nd] = new Node(nd);
        for (int i = 0; i < m; i++) {
            int x, y, w;
            x = scanner.nextInt();
            y = scanner.nextInt();
            w = scanner.nextInt();
            nodes[x - 1].adj.add(y - 1);
            nodes[x - 1].cost.add(w);
        }
        nodes[0].dist = 0;
        System.out.println(negativeCycle(nodes));


    }

    private void naiveRun(){
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Node[] nodes = new Node[n];
        for(int nd = 0; nd < n; nd++)
            nodes[nd] = new Node(nd);
        for (int i = 0; i < m; i++) {
            int x, y, w;
            x = scanner.nextInt();
            y = scanner.nextInt();
            w = scanner.nextInt();
            nodes[x - 1].adj.add(y - 1);
            nodes[x - 1].cost.add(w);
        }
        //nodes[0].dist = 0;
        System.out.println(naiveNegCycle(nodes));


    }
    
    
    public static void main(String[] args) {
        NegativeCycle nc = new NegativeCycle();
        nc.normalRun();
        //nc.unitTest();
        //nc.naiveRun();
    }
    
    private static class Node{
        ArrayList<Integer> adj;
        ArrayList<Integer> cost;
        int dist = 0;
        Integer prev = null;
        int index;
        boolean prevNeg = false;
        public Node(int index){
            this.index = index;
            adj = new ArrayList<>();
            cost = new ArrayList<>();
        }
    }
    private static int triangular(int n){
        int tri = 0;
        for(int i=1; i<n; i++){
            tri = tri + i;
        }
        return tri;
    }


}

