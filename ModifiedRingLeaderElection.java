import java.util.concurrent.CountDownLatch;

class Node implements Runnable {
    private int id;
    private Node next;
    private boolean isLeader;
    private boolean hasToken;
    private CountDownLatch latch;
    public Node(int id, CountDownLatch latch) {
        this.id = id;
        this.isLeader = false;
        this.hasToken = false;
        this.latch = latch;
    }
    public void setNext(Node next) {
        this.next = next;
    }
    public void receiveToken() {
        this.hasToken = true;
    }
    public void electLeader() {
        if (id == 1) {
            System.out.println("Node " + id + " is starting the election process...");
            hasToken = true;
        }

        while (!isLeader) {
            if (hasToken) {
                if (id == 5) {
                    System.out.println("Node " + id + " is the leader.");
                    isLeader = true;
                    latch.countDown(); // Signal that the leader is elected
                    break;
                } else {
                    System.out.println("Node " + id + " received token, passing it to Node " + next.id);
                    next.receiveToken();
                    hasToken = false;
                }
            }
        }
    }
    @Override
    public void run() {
        electLeader();
    }
}
public class ModifiedRingLeaderElection {
    public static void main(String[] args) {
        final int numNodes = 5;
        CountDownLatch latch = new CountDownLatch(1);
        Node[] nodes = new Node[numNodes];
        Thread[] threads = new Thread[numNodes];
        for (int i = 0; i < numNodes; i++) {
            nodes[i] = new Node(i + 1, latch);
        }
        for (int i = 0; i < numNodes; i++) {
            nodes[i].setNext(nodes[(i + 1) % numNodes]);
        }
        for (int i = 0; i < numNodes; i++) {
            threads[i] = new Thread(nodes[i]);
            threads[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Terminating all nodes");
        System.exit(0);
    }
}
