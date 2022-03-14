/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import agents.LARVABaseAgent;
import agents.LARVAFirstAgent;
import static console.Console.black;
import static console.Console.defBackground;
import static console.Console.defCursorXY;
import static console.Console.defText;
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
import tools.emojis;

/**
 *
 * @author lcv
 */
public class SimpleProducer extends PlainAgent {

    String _consumerName = "Smith";

    @Override
    public void setup() {
        super.setup();
        tabs = "\t\t\t";
        this.maxClock = 10;
        latencyms = 10;
        state = Status.PREPARING;
        exit = false;
    }

    @Override
    public void Execute() {
        switch (state) {
            case PREPARING:
                if (countClock <= maxClock) {
                    mark();
                    saveTime();
                    countClock++;
                } else {
                    state = Status.SENDING;
                }
                break;
            case SENDING:
                message =  "" + nmessages;
//                message = (Math.random() < 0.95 && clock < maxTime / 2
//                        ? "" + nmessages : "STOP");
                _outbox = new ACLMessage();
                _outbox.setSender(this.getAID());
                _outbox.addReceiver(new AID(_consumerName, AID.ISLOCALNAME));
                _outbox.setContent(message);
                this.LARVAsend(_outbox);
                mark();
                nmessages++;
                countClock = 1;
                if (message.equals("STOP")) {
                    state = Status.EXIT;
                } else {
                    state = Status.PREPARING;
                    saveTime();

                }
                break;
            case EXIT:
                this.doExit();
                break;
        }
    }

}
