/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import agents.LARVAFirstAgent;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import tools.emojis;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class PlainAgent extends LARVAFirstAgent {

    boolean _exit;
    String message = "", tabs = "";
    ACLMessage _inbox, _outbox;
    int latencyms = 10;

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
        state = Status.WAITING;
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
