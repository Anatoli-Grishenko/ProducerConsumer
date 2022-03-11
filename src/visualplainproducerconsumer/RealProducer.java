///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package visualplainproducerconsumer;
//
//import static ConsoleAnsi.ConsoleAnsi.black;
//import static ConsoleAnsi.ConsoleAnsi.defBackground;
//import static ConsoleAnsi.ConsoleAnsi.defCursorXY;
//import static ConsoleAnsi.ConsoleAnsi.defText;
//import static ConsoleAnsi.ConsoleAnsi.gray;
//import static ConsoleAnsi.ConsoleAnsi.green;
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
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author lcv
// */
//public class RealProducer extends PlainAgent {
//
//    boolean _exit;
//    String _consumerName = "Smith", message = "";
//    ACLMessage _outbox;
//
//    enum Status {
//        PREPARING, SENDING, EXIT
//    };
//    Status state;
//
//    // Scheduling
//    int row = 2, color = green, maxTime = 100, clock = 1;
//    int nmessages = 0, countClock = 1, maxClock = 6;
//
//    @Override
//    public void setup() {
//        super.setup();
//        System.out.print(defText(white) + defBackground(green)
//                + defCursorXY(1, row) + getLocalName()
//                + defText(white) + defBackground(black));
//        state = Status.PREPARING;
//        saveTime();
//        _exit = false;
//    }
//
//    @Override
//    public void plainExecute() {
//        switch (state) {
//            case PREPARING:
//                if (countClock <= maxClock) {
//                    saveTime();
//                    mark();
//                    countClock++;
//                    doSleep(500);
//                } else {
//                    state = Status.SENDING;
//                }
//                break;
//            case SENDING:
//                message = (Math.random() < 0.95 && clock < maxTime / 2
//                        ? "" + nmessages : "STOP");
//                _outbox = new ACLMessage();
//                _outbox.setSender(this.getAID());
//                _outbox.addReceiver(new AID(_consumerName, AID.ISLOCALNAME));
//                _outbox.setContent(message);
//                this.send(_outbox);
//                mark();
//                saveTime();
//                nmessages++;
//                countClock = 1;
//                if (message.equals("STOP")) {
//                    state = Status.EXIT;
//                } else {
//                    state = Status.PREPARING;
//
//                }
//                break;
//            case EXIT:
////                clock++;
//                saveTime();
//                _exit = true;
//                break;
//        }
//    }
//
//    @Override
//    public boolean canExit() {
//        return _exit;
//    }
//
//    protected void mark() {
//        if (state == Status.SENDING) {
//            if (message.equals("STOP")) {
//                System.out.print(defText(white) + defBackground(red)
//                        + defCursorXY(9 + clock, row)
//                        + "X" + defText(white) + defBackground(black));
//            } else {
//                System.out.print(defText(white) + defBackground(red)
//                        + defCursorXY(9 + clock, row)
//                        + nmessages + defText(white) + defBackground(black));
//            }
//        } else if (state == Status.PREPARING) {
//            System.out.print(defText(black) + defBackground(color)
//                    + defCursorXY(9 + clock, row)
//                    + countClock + defText(white) + defBackground(black));
//        } else if (state == Status.EXIT) {
//            System.out.print(defText(black) + defBackground(color)
//                    + defCursorXY(9 + clock, row)
//                    + countClock + defText(white) + defBackground(black));
//        }
//
//        clock++;
//    }
//
//    public void saveTime() {
//        try {
//            PrintWriter p = new PrintWriter(new FileOutputStream("producer.txt"));
//            p.println(clock);
//            p.close();
//        } catch (FileNotFoundException ex) {
//        }
//    }
//
//    public void readTime() {
//        try {
//            Scanner input = new Scanner(new FileInputStream("consumer.txt"));
//            clock = input.nextInt();
//            input.close();
//        } catch (FileNotFoundException ex) {
//        }
//    }
//}
