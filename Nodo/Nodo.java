package Nodo;

// import ServerPackage.CONSTANTS;

class Nodo {

    private final double[] sums = new double[40];
    private TCPNodo tcpNodo;
    public String name;
    public int nodeID;

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

    void receiveFromClient(String input) {
        System.out.println("Mensaje recibido: " + input);
        String[] parts = input.split("-");
        name = parts[0];
        nodeID = Integer.parseInt(parts[1]);
        
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
