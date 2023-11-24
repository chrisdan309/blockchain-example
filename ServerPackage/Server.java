package ServerPackage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    private TCPServer tcpServer;
    public int clientCount = 0;
    public static ArrayList<Client> clients;
    public static int numberQuery;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el número de clientes (c): ");
        int clientCount = scanner.nextInt();

        Server server = new Server(clientCount);
        server.start();
    }

    public Server(int clientCount) {
        numberQuery = 1;
        this.clientCount = clientCount;
        clients = new ArrayList<>();
        for (int i = 0; i < clientCount; i++) {
            Client aux = new Client();
            int integerPart = (int) (1000 + Math.random() * 9000);
            int firstDecimalPart = (int) (Math.random() * 10);
            int secondDecimalPart = (int) (Math.random() * 10);
            String balance = (integerPart + "." + firstDecimalPart + secondDecimalPart);
            aux.idAccount = (i + 1);
            aux.balance = balance;
            clients.add(aux);
        }
    }

    void start() {
        new Thread(() -> {
            tcpServer = new TCPServer(message -> {
                synchronized (this) {
                    receiveFromServer(message);
                }
            });
            tcpServer.run();
        }).start();

        System.out.print("1. Generar transacciones (v): Escribir Generar-v\n");
        System.out.println("2. Salir");

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        String fileName = "Archivos/cuentas.txt";
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            System.out.println("Escribiendo en el archivo " + fileName);

            for (Client client: clients) {
                bufferedWriter.write(client.idAccount + ": " + client.balance);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /* Imprimir el estado de las cuentas
        for (int i = 0; i < clientCount; i++) {
            System.out.println("Cuenta " + clients[i].idAccount + ": " + clients[i].balance);
        }
        */



        listening();

        tcpServer.stopServer();
        System.exit(0);

    }

    String[] clientResponses = new String[20];
    boolean isVector = false;
    boolean isCentroide = false;
    String vectorMessage = "";
    String centroideMessage = "";

    // Recibe los mensajes de los clientes (Final)
    void receiveFromServer(String message) {
        if (message != null && !message.isEmpty()) {
            // Sumar la cantidad de cluster 1, 2, 3, etc
            // Update centroides
            // Si diferencia entre centroides es menor a 0.1, detener el programa
            // Sino, enviar mensaje a los clientes

            System.out.println("Mensaje recibido: " + message);

            String[] parts = message.split("/");
            String vectorMessage = parts[0];
            String centroidMessage = parts[1];
            String[] partesCentroides = centroidMessage.split(" ");
            int contadorCentroides = 0;
            for (String parte : partesCentroides) {
                if (parte.contains("(")) {
                    contadorCentroides++;
                }
            }
            if (message.trim().contains("Resultado")) {

                // Update Centroides
                clientResponses[clientCount] = message;
                clientCount++;

                // Llegaron todos los mensajes
                if (clientCount == this.tcpServer.nodeCount) {
                    return;
                }else {
                    return;
                }
            }
        }
    }


    // Envia los mensajes a los cliente
    void sendToServer(String message) {
        if (message != null) {
            if (message.trim().contains("Generar")) {
                // Enviar mensaje a los clientes
                tcpServer.sendMessageToTCPServer(message);
            } else {
                System.out.println("El mensaje no contiene la palabra 'enviar'");
            }
        }
    }

    void listening() {
        Scanner scanner = new Scanner(System.in);
        String command;


        do {
            command = scanner.nextLine();
            sendToServer(command);
        } while (!command.equals("salir"));
    }
}
