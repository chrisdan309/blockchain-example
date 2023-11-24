package Nodo;

import java.time.LocalDateTime;

public class Block {
    String previousHash;
    LocalDateTime dateTime;
    String transactions;
    String nonce;
    String merkleRoot;
    int difficulty;

    public Block(String previousHash, String transactions, String nonce, int difficulty) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.nonce = nonce;
        this.difficulty = difficulty;
    }

}
