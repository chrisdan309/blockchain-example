package Nodo;

// import ServerPackage.CONSTANTS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static Nodo.SHA1.findMerkleRoot;

class Nodo {

    private final double[] sums = new double[40];
    private TCPNodo tcpNodo;
    public String name;
    public int nodeID;
    public Block newBlock;

    public static void main(String[] args) {
        Nodo client = new Nodo();
        client.start();
    }

    void start() {
        new Thread(
                () -> {
                    tcpNodo = new TCPNodo("localhost", this::receiveFromClient);
                    tcpNodo.run();
                }
        ).start();
    }

    void receiveFromClient(String input) throws FileNotFoundException {
        // Inician los mineros
        System.out.println("Mensaje recibido: " + input);
        String[] parts = input.split("-");
        name = parts[0];
        nodeID = Integer.parseInt(parts[1]);
        String fileName = "Archivos/Chain.txt";
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String previousHash = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] parts2 = line.split(":");
                String findHashWord = parts2[0];
                if (findHashWord.equals("Hash")) {
                    previousHash = parts2[1];
                    break;
                }
            }
            fr.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        fileName = "Archivos/blockchaincuentas.txt";
        fr = new FileReader(fileName);
        br = new BufferedReader(fr);
        String merkleTreeRoot = "";
        try {
            while ((line = br.readLine()) != null) {
                List<String> transactions = new ArrayList<>();
                transactions.add(line);
                merkleTreeRoot = findMerkleRoot(transactions);
            }
            fr.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        newBlock = new Block(previousHash, merkleTreeRoot, 3);

        // Aqui demora

        newBlock.generateHash();

        sendToClient("Recibe-"+nodeID+":"+newBlock.blockHash+":"+newBlock.timeExecution+":"+newBlock.merkleRoot);




        /*
        String[] parts = input.split("/");nonce
        String vectorMessage = parts[0];
        String centroidMessage = parts[1];

        // separar vectorMessage y almacenar en un arreglo de point
        String[] vectorParts = vectorMessage.split(" ");
        Point[] points = separarPuntos(vectorParts);

        String[] centroidParts = centroidMessage.split(" ");
        Point[] centroids = separarPuntos(centroidParts);
        for (Point point : points) {
            System.out.println(point);
        }

        for (Point centroid : centroids) {
            System.out.println(centroid);
        }
        process(points, centroids);*/


        
        /*
        String[] parts = input.split("/");
        String vectorMessage = parts[0];
        String centroidMessage = parts[1];

        // separar vectorMessage y almacenar en un arreglo de point
        String[] vectorParts = vectorMessage.split(" ");
        Point[] points = separarPuntos(vectorParts);

        String[] centroidParts = centroidMessage.split(" ");
        Point[] centroids = separarPuntos(centroidParts);
        for (Point point : points) {
            System.out.println(point);
        }

        for (Point centroid : centroids) {
            System.out.println(centroid);
        }
        process(points, centroids);*/
    }



    void sendToClient(String message) {
        if (tcpNodo != null) {
            tcpNodo.sendMessage(message);
        }
    }

/*    void process(Point[] points, Point[] centroids) {
        KMeansAlgorithm kMeansAlgorithm = new KMeansAlgorithm(points, centroids);
        kMeansAlgorithm.asignarPuntos();
        kMeansAlgorithm.actualizarCentroides();

        String message = "Resultado vector ";
        for (Point point : points) {
            message += point.name + "(" + point.x + "," + point.y + ")-" + point.cluster + ", ";
        }
        message += "/Resultado centroide ";
        for (Point centroid : centroids) {
            message += centroid.name + "(" + centroid.x + "," + centroid.y + ")-" + centroid.puntos + ", ";
        }

        System.out.println(message);
        System.out.println("-----------------------------------------");
        sendToClient(message);
    }*/
}
