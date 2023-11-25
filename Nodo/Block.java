package Nodo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Block {
    String blockHash;
    String previousHash;
    LocalDateTime dateTime;
    List<String> transactions;
    String nonce;
    String merkleRoot;
    int difficulty;
    long timeExecution;

    public Block(String previousHash, String merkleRoot, int difficulty) {
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.difficulty = difficulty;
    }

    public void generateHash() {
        String input = previousHash + merkleRoot + nonce;
        String[] aux = SHA1.sha1WithDifficulty(input, difficulty);
        blockHash = aux[0];
        timeExecution = Long.parseLong(aux[1]);
    }








}
