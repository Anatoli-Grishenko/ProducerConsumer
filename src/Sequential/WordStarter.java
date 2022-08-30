/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sequential;

import Centralized.PCNonDialogical;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class WordStarter extends PCNonDialogical {

    String word = "";

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Who is the receiver of the first word
        receiver = "Neo";
        // Randomly generate first word
        word = dict.findFirstWord();
    }

    @Override
    public void Execute() {
        // Generate first ACL message and send it to receiver
        outbox = new ACLMessage();
        outbox.setSender(getAID());
        outbox.addReceiver(new AID(receiver, AID.ISLOCALNAME));
        outbox.setContent(word);
        // this.send(outbox) would also work,  
        // but it does not keep record of sent/received messages 
        // nor allow (auto) sequence diagrams        
        this.LARVAsend(outbox);
        Info("Says : " + word);
        // If it sent "STOP", then ends
        if (word.equals(wordStopper)) {
            doExit();
        } else {
            // Waits for the answer            
            inbox = this.LARVAblockingReceive();
            Info("Gets: " + inbox.getContent());
            // Randomly interrupts the game and stops.
            // If it receives the same word that was set, it stops
            if (Math.random() > 0.8 || inbox.getContent().equals(word)) {
                word = wordStopper;
            } else {
                word = dict.findNextWord(inbox.getContent());
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
