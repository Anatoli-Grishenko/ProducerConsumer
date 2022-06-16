/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import static console.Console.black;
import static console.Console.defBackground;
import static console.Console.defColor;
import static console.Console.defCursorXY;
import static console.Console.defText;
import static console.Console.defclearScreen;
import static console.Console.gray;
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

/**
 *
 * @author lcv
 */
public class RealConsumer extends PlainAgent {

    String _consumerName = "Smith";

    // Scheduling
    int row = 3, color = defColor(0, 0, 0.7);

    @Override
    public void setup() {
        super.setup();
        logger.offEcho();
        maxClock=10;
        System.out.println(defclearScreen());
        this.doNotExit();
        saveTime();
        System.out.print(defText(white) + defBackground(green)
                + defCursorXY(1, row) + getLocalName()
                + defText(white) + defBackground(black));

        // Draw the temporal grid
        for (int y = 0; y <= 3; y++) {
            for (int x = 1; x <= maxTime; x++) {
                if (x % 10 == 0) {
                    System.out.println(defText(gray) + defBackground(white) + defCursorXY(10 + x - 1, y + 1) + "|");
                } else if (x % 10 == 5) {
                    System.out.println(defText(gray) + defBackground(white) + defCursorXY(10 + x - 1, y + 1) + "+");
                } else {
                    System.out.println(defText(gray) + defBackground(white) + defCursorXY(10 + x - 1, y + 1) + "·");
                }

            }
        }
        System.out.print(defText(white) + defBackground(green)
                + defCursorXY(1, row) + getLocalName() + defText(white) + defBackground(black));
        state = Status.WAITING;
        this.doNotExit();
    }

    @Override
    public void Execute() {
        switch (state) {
            case WAITING:
                mark();
                state = Status.RECEIVING;
                break;
            case RECEIVING:
                _inbox = this.blockingReceive();
                readTime();
                if (_inbox != null) {
                    countClock = 1;
                    if (_inbox.getContent().equals("STOP")) {
                        state = Status.EXIT;
                        mark();
                    } else {
                        nmessages = Integer.parseInt(_inbox.getContent());
                        mark();
                        state = Status.PROCESSING;
                    }
                    saveTime();
                } else {
                    state = Status.WAITING;
                }
                break;
            case PROCESSING:
                if (this.countClock <= maxClock) {
                    mark();
                    saveTime();
                    countClock++;
                    doWait(500);
                } else {
                    state = Status.WAITING;
                }

                break;
            case EXIT:
                this.doExit();
                break;
        }

    }

    protected void mark() {
        if (state == Status.RECEIVING) {
            System.out.print(defText(white) + defBackground(red)
                    + defCursorXY(9 + clock, row)
                    + nmessages + defText(white) + defBackground(black));
        } else if (state == Status.PROCESSING) {
            System.out.print(defText(white) + defBackground(color)
                    + defCursorXY(9 + clock, row)
                    + countClock + defText(white) + defBackground(black));
        } else if (state == Status.EXIT) {
            System.out.print(defText(white) + defBackground(red)
                    + defCursorXY(9 + clock, row)
                    + "X" + defText(white) + defBackground(black));
        } else if (state == Status.WAITING) {
            System.out.print(defText(white) + defBackground(red)
                    + defCursorXY(9 + clock, row)
                    + "W" + defText(white) + defBackground(black));
        }

        clock++;
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
            if (read > clock) {
                clock = read;
            }
//            else
            clock--;
        } catch (Exception ex) {
            System.err.println("Exception:: " + ex.toString());
        }

    }
}
