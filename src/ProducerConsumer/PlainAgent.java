/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import agents.LARVAFirstAgent;
import static crypto.Keygen.getWordo;
import glossary.Dictionary;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import tools.emojis;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class PlainAgent extends LARVAFirstAgent {

    Dictionary d;
    boolean _exit;
    String message = "", tabs = "", receiver, stopper = "STOP", controller="Trinity";
    ACLMessage _inbox, _outbox;
    int latencyms = 2000;

    enum Status {
        PROCESSING, PREPARING, RECEIVING, SENDING, WAITING, EXIT
    };
    Status state;

    // Scheduling
    int maxTime = 100, clock = 0;
    int nmessages = 0, countClock = 0, maxClock = 3;

    @Override
    public void setup() {
        super.setup();
        saveTime();
        Info(tabs + "Booting");
        d = new Dictionary();
        d.load("config/EN.words");
        logger.onTabular();
        state = Status.WAITING;
    }

    public String findFirstWord() {
        String wordo;
        ArrayList<String> words;
        do {
            wordo = getWordo(4);
            words = d.completeWord(wordo, 25);
        } while (words.size() == 0);
        return words.get((int) (Math.random() * words.size()));
    }

    public String findNextWord(String word) {
        ArrayList<String> words;
        int n = 3;
        do {
            try {
                words = d.completeWord(word.substring(word.length() - n), 10);
                if (words.size() > 0) {
                    return words.get((int) (Math.random() * words.size()));
                }
            } catch (Exception ex) {

            }
            n--;
        } while (true);
    }

    public void saveTime() {
        try {
            PrintWriter p = new PrintWriter(new FileOutputStream("consumer.txt"));
            p.println(clock);
            p.close();
        } catch (Exception ex) {
            System.err.println("Exception:: " + ex.toString());
        }
    }

    public void readTime() {
        int read = -1;
        try {
            Scanner input = new Scanner(new FileInputStream("producer.txt"));
            read = input.nextInt();
            input.close();
            if (read >= clock) {
                clock = read;
            } else {
                clock--;
            }
        } catch (Exception ex) {
            System.err.println("Exception:: " + ex.toString());
        }
    }

    public String doProgress(int value, int max) {
        String res = "";
        for (int i = 1; i <= max; i++) {
            if (i < value) {
                res += emojis.BLACKSQUARE;
            } else {
                res += emojis.WHITESQUARE;
            }
        }
        res += "" + value + "/" + max;
        return res;
    }

    protected void mark() {
        if (state == Status.RECEIVING) {
            Info(tabs + doProgress(countClock, this.maxClock) + "[" + nmessages + "]");
        } else if (state == Status.PREPARING) {
            Info(tabs + doProgress(countClock, this.maxClock) + (countClock == maxClock ? "[" + nmessages + "]" : ""));
            this.doWait((int) (latencyms * (1 + Math.random() - 0.5)));
        } else if (state == Status.PROCESSING) {
            Info(tabs + doProgress(countClock, this.maxClock));
            this.doWait((int) (latencyms * (1 + Math.random() - 0.5)));
        } else if (state == Status.EXIT) {
            Info(tabs + doProgress(countClock, this.maxClock) + " X");
        } else if (state == Status.WAITING) {
            Info(tabs + doProgress(countClock, this.maxClock) + " W");
        }
    }

}
