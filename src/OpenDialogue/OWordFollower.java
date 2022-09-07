/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenDialogue;

import Dialogical.PCDialogical;
import First.PCNonDialogical;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class OWordFollower extends PCDialogical {
    
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
        Info(this.toString());
        // It starts listening to new messages
        if (inbox == null) {
            inbox = this.blockingDialogue().get(0);
        } 
        word = inbox.getContent();
        Info("Gets: " + word);
        // If it is STOP, the stops then terminate
        if (word.equals(wordStopper)) {
            doExit();
        } else {
            // Otherwise, find the chained word and continue
            word = dict.findNextWord(word);
            // Does not need to buld a new message. Instead, it
            // answers to the previous one
            outbox = inbox.createReply();
            outbox.setPerformative(ACLMessage.QUERY_IF);
            outbox.setContent(word);
            outbox.setConversationId("UNIQUE");
            outbox.setReplyWith(word);
            inbox = this.blockingDialogue(outbox).get(0);
            Info("Says : " + word);
        }
        
    }
    
    @Override
    public void takeDown() {
        super.takeDown();
        Info("SEQUENCE DIAGRAM:\n" + this.getSequenceDiagram());
    }
    
}
