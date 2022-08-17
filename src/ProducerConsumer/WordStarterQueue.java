/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import data.Transform;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class WordStarterQueue extends ProdConsAgent {

    String word = "";
    String Conversations[] = new String[]{"PLAY", "SERIOUS", "NONE"};
    String convID;

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Who is the receiver of the first word
        receiver = "Neo";
        // Randomly generate first word
        word = this.findFirstWord();
        convID = Transform.outOf(Conversations);
    }

    @Override
    public void Execute() {
        // Generate first ACL message and send it to receiver
        outbox = new ACLMessage();
        outbox.setSender(getAID());
        outbox.addReceiver(new AID(receiver, AID.ISLOCALNAME));
        outbox.setContent(word);
        outbox.setConversationId(convID);
        // this.send(outbox) would also work,  
        // but it does not keep record of sent/received messages 
        // nor allow (auto) sequence diagrams        
        this.LARVAsend(outbox);
        // If it sent "STOP", then ends
        if (word.equals(stopper)) {
            doExit();
        } else {
            // Waits for the answer   
            if (convID.equals("PLAY")) {
                Info("Plays : " + word);
                inbox = this.LARVAblockingReceive();
                Info("Gets: " + inbox.getContent());
                if (word.equals(inbox.getContent())) {
                    word = stopper;
                } else {
                    word = this.findNextWord(inbox.getContent());
                }
            } else {
                Info("Says : " + word);
                this.LARVAwait(500);
                // Randomly interrupts the game and stops.
                if (Math.random() > 0.9) {
                    word = stopper;
                    convID = "PLAY";
                } else {
                    word = this.findFirstWord();
                    convID = Transform.outOf(Conversations);
                }
            }
        }
    }

    @Override
    public void takeDown() {
        // At the end, it automatically generates the sequence diagram
        this.saveSequenceDiagram("./" + getLocalName() + ".seqd");
        super.takeDown();

    }

}
