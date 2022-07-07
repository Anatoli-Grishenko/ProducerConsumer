/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class WordProducer extends ProdConsAgent {

    String word;

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Who is the receiver of the first word
        receiver = "Neo";
        // Minimum time to wait (ms) before sending the next word
        latencyms = 100;
        word = "";
        logger.offEcho();
        // Randomly generate first word
        word = this.findFirstWord();
    }

    @Override
    public void Execute() {
        // Generate first ACL message and send it to receiver
        outbox = new ACLMessage();
        outbox.setSender(getAID());
        outbox.addReceiver(new AID(receiver, AID.ISLOCALNAME));
        outbox.addReceiver(new AID(controller, AID.ISLOCALNAME));
        outbox.setContent("+" + word);
        // this.send(outbox) would also work,  
        // but it does not keep record of sent/received messages 
        // nor allow (auto) sequence diagrams        
        this.LARVAsend(outbox);
        Info("Says : " + word);
        this.saveSequenceDiagram("tmp.seqd");
        // If it sent "STOP", then ends
        if (word.equals(stopper)) {
            doExit();
        } else {
            // Wait for the buffer to be free
            do {
                inbox = this.LARVAblockingReceive();
            } while (!inbox.getContent().startsWith("ACCEPT") &&
                    !inbox.getContent().startsWith("RESUME"));
            // Waits for the next word            
            this.clock = latencyms + (int) (Math.random() * latencyms);
            Info("Wating " + clock + " ms to the next word");
            this.LARVAwait(clock);
            // Randomly interrupts the game and stops.
            // If it receives the same word that was set, it stops
            if (Math.random() > 0.8 && this.getNCycles()>6) {
                word = stopper;
            } else {
                word = this.findFirstWord();
            }
        }
    }
}
