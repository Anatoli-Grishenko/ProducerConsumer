/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import static console.Console.black;
import static console.Console.defBackground;
import static console.Console.defCursorXY;
import static console.Console.defText;
import static console.Console.defclearScreen;
import static console.Console.green;
import static console.Console.red;
import static console.Console.white;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lcv
 */
public class RealProducer extends PlainAgent {

    String _consumerName = "Smith";

    // Scheduling
    int row = 2, color = green;

    @Override
    public void setup() {
        super.setup();
        logger.offEcho();
        maxClock=5;
        System.out.println(defclearScreen());
        this.doNotExit();
        System.out.print(defText(white) + defBackground(green)
                + defCursorXY(1, row) + getLocalName()
                + defText(white) + defBackground(black));
        state = Status.PREPARING;
        saveTime();
    }

    @Override
    public void Execute() {
        switch (state) {
            case PREPARING:
                if (countClock <= maxClock) {
                    saveTime();
                    mark();
                    countClock++;
                    this.doWait(500);
                } else {
                    state = Status.SENDING;
                }
                break;
            case SENDING:
                message = (Math.random() < 0.95 && clock < maxTime / 2
                        ? "" + nmessages : "STOP");
                _outbox = new ACLMessage();
                _outbox.setSender(this.getAID());
                _outbox.addReceiver(new AID(_consumerName, AID.ISLOCALNAME));
                _outbox.setContent(message);
                this.send(_outbox);
                mark();
                saveTime();
                nmessages++;
                countClock = 1;
                if (message.equals("STOP")) {
                    state = Status.EXIT;
                } else {
                    state = Status.PREPARING;

                }
                break;
            case EXIT:
//                clock++;
                saveTime();
                doExit();
                break;
        }
    }

    protected void mark() {
        if (state == Status.SENDING) {
            if (message.equals("STOP")) {
                System.out.print(defText(white) + defBackground(red)
                        + defCursorXY(9 + clock, row)
                        + "X" + defText(white) + defBackground(black));
            } else {
                System.out.print(defText(white) + defBackground(red)
                        + defCursorXY(9 + clock, row)
                        + nmessages + defText(white) + defBackground(black));
            }
        } else if (state == Status.PREPARING) {
            System.out.print(defText(black) + defBackground(color)
                    + defCursorXY(9 + clock, row)
                    + countClock + defText(white) + defBackground(black));
        } else if (state == Status.EXIT) {
            System.out.print(defText(black) + defBackground(color)
                    + defCursorXY(9 + clock, row)
                    + countClock + defText(white) + defBackground(black));
        }

        clock++;
    }

    public void saveTime() {
        try {
            PrintWriter p = new PrintWriter(new FileOutputStream("producer.txt"));
            p.println(clock);
            p.close();
        } catch (FileNotFoundException ex) {
        }
    }

    public void readTime() {
        try {
            Scanner input = new Scanner(new FileInputStream("consumer.txt"));
            clock = input.nextInt();
            input.close();
        } catch (FileNotFoundException ex) {
        }
    }
}
