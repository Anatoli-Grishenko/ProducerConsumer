///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package visualplainproducerconsumer;
//
//import static ConsoleAnsi.ConsoleAnsi.black;
//import static ConsoleAnsi.ConsoleAnsi.blue;
//import static ConsoleAnsi.ConsoleAnsi.defBackground;
//import static ConsoleAnsi.ConsoleAnsi.defColor;
//import static ConsoleAnsi.ConsoleAnsi.defCursorXY;
//import static ConsoleAnsi.ConsoleAnsi.defText;
//import static ConsoleAnsi.ConsoleAnsi.gray;
//import static ConsoleAnsi.ConsoleAnsi.green;
//import static ConsoleAnsi.ConsoleAnsi.lightblue;
//import static ConsoleAnsi.ConsoleAnsi.lightgreen;
//import static ConsoleAnsi.ConsoleAnsi.red;
//import static ConsoleAnsi.ConsoleAnsi.white;
//import PlainAgent.PlainAgent;
//import jade.core.AID;
//import jade.lang.acl.ACLMessage;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintWriter;
//import java.util.Scanner;
//
///**
// *
// * @author lcv
// */
//public class RealConsumer extends PlainAgent {
//
//    boolean _exit;
//    String _consumerName = "Smith", message = "";
//    ACLMessage _inbox;
//
//    enum Status {
//        PROCESSING, RECEIVING, WAITING, EXIT
//    };
//    Status state;
//
//    // Scheduling
//    int row = 3, color = defColor(0, 0, 0.7), maxTime = 100, clock = 1;
//    int nmessages = 0, countClock = 1, maxClock = 3;
//
//    @Override
//    public void setup() {
//        super.setup();
//        saveTime();
//        System.out.print(defText(white) + defBackground(green)
//                + defCursorXY(1, row) + getLocalName()
//                + defText(white) + defBackground(black));
//
//        // Draw the temporal grid
//        for (int y = 0; y <= 3; y++) {
//            for (int x = 1; x <= maxTime; x++) {
//                if (x % 10 == 0) {
//                    System.out.println(defText(gray) + defBackground(white) + defCursorXY(10 + x - 1, y + 1) + "|");
//                } else if (x % 10 == 5) {
//                    System.out.println(defText(gray) + defBackground(white) + defCursorXY(10 + x - 1, y + 1) + "+");
//                } else {
//                    System.out.println(defText(gray) + defBackground(white) + defCursorXY(10 + x - 1, y + 1) + "·");
//                }
//
//            }
//        }
//        System.out.print(defText(white) + defBackground(green)
//                + defCursorXY(1, row) + getLocalName() + defText(white) + defBackground(black));
//        state = Status.WAITING;
//        _exit = false;
//    }
//
//    @Override
//    public void plainExecute() {
//        switch (state) {
//            case WAITING:
//                mark();
//                state = Status.RECEIVING;
//                break;
//            case RECEIVING:
//                _inbox = this.blockingReceive();
//                    readTime();
//                if (_inbox != null) {
//                    countClock = 1;
//                    if (_inbox.getContent().equals("STOP")) {
//                        state = Status.EXIT;
//                        mark();
//                    } else {
//                        nmessages = Integer.parseInt(_inbox.getContent());
//                        mark();
//                        state = Status.PROCESSING;
//                    }
//                    saveTime();
//                } else {
//                    state = Status.WAITING;
//                }
//                break;
//            case PROCESSING:
//                if (this.countClock <= maxClock) {
//                    mark();
//                    saveTime();
//                    countClock++;
//                    doSleep(500);
//                } else {
//                    state = Status.WAITING;
//                }
//
//                break;
//            case EXIT:
//                _exit = true;
//                break;
//        }
//
//    }
//
//    @Override
//    public boolean canExit() {
//        return _exit;
//    }
//
//  protected void mark() {
//        if (state == Status.RECEIVING) {
//
//            System.out.print(defText(white) + defBackground(red)
//                    + defCursorXY(9 + clock, row)
//                    + nmessages + defText(white) + defBackground(black));
//        } else if (state == Status.PROCESSING) {
//            System.out.print(defText(white) + defBackground(color)
//                    + defCursorXY(9 + clock, row)
//                    + countClock + defText(white) + defBackground(black));
//        } else if (state == Status.EXIT) {
//            System.out.print(defText(white) + defBackground(red)
//                    + defCursorXY(9 + clock, row)
//                    + "X" + defText(white) + defBackground(black));
//        } else if (state == Status.WAITING) {
//            System.out.print(defText(white) + defBackground(red)
//                    + defCursorXY(9 + clock, row)
//                    + "W" + defText(white) + defBackground(black));
//        }
//
//        clock++;
//    }
//
//    public void saveTime() {
//        try {
//            PrintWriter p = new PrintWriter(new FileOutputStream("consumer.txt"));
//            p.println(clock);
//            p.close();
//        } catch (Exception ex) {
//            System.err.println("Exception:: " + ex.toString());
//        }
//    }
//
//     public void readTime() {
//        int read = -1;
//        try {
//            Scanner input = new Scanner(new FileInputStream("producer.txt"));
//            read = input.nextInt();
//            input.close();
//            if (read > clock) {
//                clock = read;
//            }
////            else
//                clock--;
//        } catch (Exception ex) {
//            System.err.println("Exception:: " + ex.toString());
//        }
//
//    }
//}
