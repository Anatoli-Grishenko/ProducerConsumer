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
public class WordFollower extends PCNonDialogical {

    String word = "";

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Do not need to know the receiver, just answer
        //receiver = "Smith";
    }

    @Override
    public void Execute() {
        // It starts listening to new messages
        inbox = this.LARVAblockingReceive();
        word = inbox.getContent();
        Info("Gets: " + word);
        // If it is STOP, the stops the llop and terminate
        if (word.equals(wordStopper)) {
            doExit();
        } else {
        // Otherwise, find the chained word and continue
            word = dict.findNextWord(word);
            // Does not need to buld a new message. Instead, it
            // answers to the previous one
            outbox = inbox.createReply();
            outbox.setContent(word);
            this.LARVAsend(outbox);
            Info("Says : " + word);
        }

    }
    
    @Override
    public void takeDown() {
        super.takeDown();
        Info("SEQUENCE DIAGRAM:\n"+this.getSequenceDiagram());
    }

    
}
