package ServerPackage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static ServerPackage.Server.clients;
import static ServerPackage.Server.numberQuery;

public class TCPServer {

    protected int nodeCount = 0;
    public static final int SERVER_PORT = CONSTANTS.PORT;
    private final OnMessageReceived messageListener;
    private final TCPServerThread[] connectecNodes = new TCPServerThread[10];
    private boolean generateQuerys = false;
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
    public OnMessageReceived getMessageListener() {
        return this.messageListener;
    }

    String centroidMessage = "";
    String vectorMessage = "";
    public void sendMessageToTCPServer(String message) {
        if(message.contains("Generar")) {
            generateQuerys = true;
        }


        if(generateQuerys) {
            int v = Integer.parseInt(message.split("-")[1]);
            List<Thread> clientThreads = new ArrayList<>();

            for (Client c : clients) {
                Thread t = new Thread(() -> {
                    c.generateQuery(v);
                });
                t.start();
                clientThreads.add(t);
            }

            for (Thread t : clientThreads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String fileName = "Archivos/blockchaincuentas.txt";
            try{
                FileWriter fileWriter = new FileWriter(fileName);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                System.out.println("Escribiendo en el archivo " + fileName);

                for (Client client: clients) {
                    Queue<String> q = client.querys;
                    while(!q.isEmpty()) {
                        String query = q.poll();
                        if(isValid(query)){
                            bufferedWriter.write(numberQuery + "-" + query);
                            numberQuery++;
                            bufferedWriter.newLine();
                        }
                    }
                }
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            /*for (Client c : clients) {
                for (String query : c.querys) {
                    System.out.println(query);
                }
            }*/
            generateQuerys = false;

            System.out.println("Generando copias para los nodos");


            copyFile("cuentas.txt");
            copyFile("blockchaincuentas.txt");
            // Copia cuentas.txt en el directorio Copias

            

            for (int i = 1; i <= nodeCount; i++) {
                if(i == 1) connectecNodes[i].sendMessage("Master node-1");
                else connectecNodes[i].sendMessage("Slave node-" + i);
            }
        }

        // clientCount = 6;
        /*if (clientCount == 0) {
            System.out.println("No hay clientes conectados");
            System.exit(0);
        }
        String[] parts = message.split("/");
        vectorMessage = parts[0];
        centroidMessage = parts[1];
        String[] partesVector = vectorMessage.split(" ");
        int contadorVectores = 0;
        for (String parte : partesVector) {
            if (parte.contains("(")) {
                contadorVectores++;
            }
        }

        int elementosPorParte = contadorVectores / clientCount;
        int elementosExtras = contadorVectores % clientCount;

        int indice = 1;
        // Repartición de vectores
        for (int parte = 1; parte <= clientCount; parte++) {
            System.out.print("Enviado al cliente " + parte + ": ");
            int elementosEnEstaParte = elementosPorParte;

            if (elementosExtras > 0) {
                elementosEnEstaParte++;
                elementosExtras--;
            }
            StringBuilder cadenaConPuntos = new StringBuilder("enviar vector ");
            for (int i = 0; i < elementosEnEstaParte; i++) {
                if (indice <= contadorVectores) {
                    String aux = "a" + indice + "(";
                    for (String cad : partesVector) {
                        if (cad.contains(aux)) {
                            cadenaConPuntos.append(cad).append(" ");
                        }
                    }
                    // Ingresa cada punto del vector
                    indice++;
                }
            }
            String cadenaAEnviar = cadenaConPuntos.append("/").append(centroidMessage).toString();
            System.out.print(cadenaAEnviar);
            connectedClients[parte].sendMessage(message);
            System.out.println();
        }

         */
    }

    private boolean isValid(String query){
        char typeQuery = query.charAt(1);
        String[] parts = query.split("-");
        if(typeQuery == 'L') {
            double mount = getMount(parts[1]);
        }
        else if(typeQuery == 'A') {
            return updateTransaction(parts[1]);
        }
        return true;
    }

    double getMount(String stringAccountID){
        int accountID = Integer.parseInt(stringAccountID);
        double mount = -1;
        String fileName = "Archivos/cuentas.txt";
        try{

            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null) {
                int accountAux = Integer.parseInt(line.split(":")[0]);
                if (accountID == accountAux) {
                    mount = Double.parseDouble(line.split(":")[1]);
                    return mount;
                }
            }

            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return mount;
    }

    boolean updateTransaction(String query){
        String[] parts = query.split(";");
        int idAccountSend = Integer.parseInt(parts[0]);
        int idAccountReceive = Integer.parseInt(parts[1]);
        double mountToTransfer = Integer.parseInt(parts[2]);
        double mountidAccountSend = getMount(parts[0]);

        if (mountidAccountSend < mountToTransfer) {
            return false;
        }


        // Actualizar el txt
        String fileName = "Archivos/cuentas.txt";
        List<String> lines = readFileContent(fileName);

        for (int i = 0; i < lines.size(); i++) {
            String[] partsLine = lines.get(i).split(":");
            int accountID = Integer.parseInt(partsLine[0]);
            if (accountID == idAccountSend) {
                double mount = Double.parseDouble(partsLine[1]);
                mount -= mountToTransfer;
                lines.set(i, accountID + ": " + mount);
            }
            else if (accountID == idAccountReceive) {
                double mount = Double.parseDouble(partsLine[1]);
                mount += mountToTransfer;
                lines.set(i, accountID + ": " + mount);
            }
        }
        writeFileContent(fileName, lines);



        /*String fileName = "Archivos/cuentas.txt";
        try{

            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null) {
                int accountAux = Integer.parseInt(line.split(":")[0]);
                if (accountID == accountAux) {
                    mount = Double.parseDouble(line.split(":")[1]);
                }
            }

            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        return true;
    }

    private List<String> readFileContent(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null) {
                lines.add(line);
            }

            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private void writeFileContent(String fileName, List<String> lines) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        System.out.println(SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            while (true) {
                Socket client = serverSocket.accept();
                nodeCount++;
                System.out.println("Clientes totales: " + nodeCount);
                connectecNodes[nodeCount] = new TCPServerThread(client, this, nodeCount, connectecNodes);
                connectecNodes[nodeCount].start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void stopServer() {
        for (int i = 1; i <= nodeCount; i++) {
            connectecNodes[i].stopClient();
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    void copyFile(String filename) {
        String fileNameOriginal = "Archivos/" + filename;
        for (int i = 1; i <= nodeCount; i++) {
            String fileNameCopy = "Archivos/Copias/" + i + filename;
            try{
                FileReader fr = new FileReader(fileNameOriginal);
                FileWriter fw =  new FileWriter(fileNameCopy);

                char []buffer = new char[1024];
                int length;

                while((length = fr.read(buffer)) != -1) {
                    fw.write(buffer, 0, length);
                }
                fw.close();
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

