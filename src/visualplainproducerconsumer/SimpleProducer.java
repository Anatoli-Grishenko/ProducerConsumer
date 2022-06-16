/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

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
        this.countClock=0;
        state = Status.PREPARING;
        this.doNotExit();
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
                message = "" + nmessages;
                message = (Math.random() < 0.95 && clock < maxTime / 2
                        ? "" + nmessages : "STOP");
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
        this.clock++;
    }

}
