/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Centralized;

import console.Console;
import static console.Console.defclearScreen;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class ProductionController extends PCNonDialogical {

    protected final int MAXPROD = 5;
    ArrayList<String> words, produced, consumed;
    String word;
    Console tty;
    ACLMessage producer;
    boolean useTTY = true;

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Setup the queue of words
        words = new ArrayList();
        produced = new ArrayList();
        consumed = new ArrayList();
        logger.offTabular();
        // Use external TTY. Only Linux. Windows turn it false
        if (useTTY) {
            tty = new Console("Enchained words", 60, 80, 10);
            tty.setCursorOff();
        }
        printStatus();
    }

    @Override
    public void Execute() {
        Info("Queue (" + MAXPROD + "):  " + words.toString());
        inbox = LARVAblockingReceive();
        if (inbox.getContent().startsWith("+")) {
            producer = inbox;
            word = producer.getContent().substring(1);
            produced.add(word);
            if (word.equals(stopper)) {
                Info("Stop required");
                doExit();
            }
            Info("Adding " + word);
            words.add(word);
            if (words.size() == MAXPROD) {
                Info("Suspending production");
                outbox = producer.createReply();
                outbox.setContent("SUSPEND " + word);
                this.LARVAsend(outbox);
                produced.add("---");
            } else {
                outbox = producer.createReply();
                outbox.setContent("ACCEPT " + word);
                this.LARVAsend(outbox);

            }
        }
        if (inbox.getContent().startsWith("-")) {
            word = inbox.getContent().substring(1);
            consumed.add(word);
            Info(word + " releases " + words.get(0));
            if (words.size() == MAXPROD / 2) {
                outbox = producer.createReply();
                outbox.setContent("RESUME ");
                this.LARVAsend(outbox);
                Info("Resuming production");
            }
            words.remove(0);
        }
        printStatus();
    }

    public void printStatus() {
        if (useTTY) {
            tty.clearScreen();
            tty.setCursorXY(1, 1);
            tty.println("Producer");
            tty.setCursorXY(20, 1);
            tty.println("Queue (" + MAXPROD + ")");
            tty.setCursorXY(40, 1);
            tty.println("Consumer");
            for (int i = 0; i < produced.size(); i++) {
                tty.setCursorXY(1, 2 + i);
                tty.print("│ " + produced.get(i));
            }
            for (int i = 0; i < this.MAXPROD; i++) {
                tty.setCursorXY(20, 2 + i);
                tty.print("│ ");
                if (i < words.size()) {
                    tty.print(words.get(i));
                }
            }
            for (int i = 0; i < consumed.size(); i++) {
                tty.setCursorXY(40, 2 + i);
                tty.print("│ " + consumed.get(i));
            }
        } else {
            Info("\n\nProducer:\t" + produced.toString()
                    + "\nQueue (" + MAXPROD + "):\t" + words.toString()
                    + "\nConsumer:\t" + consumed.toString() + "\n\n");
        }
    }

    @Override
    public void takeDown() {
        // At the end, it automatically generates the sequence diagram
        if (useTTY) {
            tty.close();
        }
        super.takeDown();

    }

}
