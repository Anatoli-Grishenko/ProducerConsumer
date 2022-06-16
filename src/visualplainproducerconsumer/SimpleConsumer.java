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
import static console.Console.defColor;
import static console.Console.defCursorXY;
import static console.Console.defText;
import static console.Console.gray;
import static console.Console.green;
import static console.Console.red;
import static console.Console.white;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import tools.emojis;

/**
 *
 * @author lcv
 */
public class SimpleConsumer extends PlainAgent {

    @Override
    public void setup() {
        super.setup();
        this.maxClock = 6;
        latencyms = 10;
        state = Status.WAITING;
        exit = false;
    }

    @Override
    public void Execute() {
        switch (state) {
            case WAITING:
                mark();
                state = Status.RECEIVING;
                break;
            case RECEIVING:
                _inbox = this.LARVAblockingReceive();
                if (_inbox != null) {
                    readTime();
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
                } else {
                    state = Status.WAITING;
                }

                break;
            case EXIT:
                doExit();
                break;
        }
    }

    @Override
    public void takeDown() {
        this.saveSequenceDiagram(getLocalName() + ".seqd");
        super.takeDown();
    }
}
