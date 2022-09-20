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
public class OWordStarter extends PCDialogical {

    String word = "", receiver;

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Who is the receiver of the first word
        receiver = "Neo"+getLocalName().substring(getLocalName().length()-4,getLocalName().length());
        // Randomly generate first word
        word = dict.findFirstWord();
    }

    @Override
    public void Execute() {
        Info(this.toString());
        // Generate first ACL message and send it to receiver
        Info("Says : " + word);
        // For the first time, prepare to initiate dialogue
        if (inbox == null) {
            outbox = new ACLMessage(ACLMessage.QUERY_IF);
            outbox.setSender(getAID());
            outbox.addReceiver(new AID(receiver, AID.ISLOCALNAME));
            outbox.setConversationId("UNIQUE");
        } else { // Else folllow the dialogue
            outbox = inbox.createReply();
        }
        // Complete the context of the dialogue
        outbox.setContent(word);
        outbox.setReplyWith(word);
        // Select the performative that best adapts to the intention
        if (word.equals(wordStopper)) {
            outbox.setPerformative(ACLMessage.INFORM);
            this.Dialogue(outbox);
            doExit();
        } else {
            outbox.setPerformative(ACLMessage.QUERY_IF);
            inbox = this.blockingDialogue(outbox).get(0);
//            inbox = this.waitAnswersTo(outbox).get(0);
            Info("Gets: " + inbox.getContent());
            if (this.getNCycles() > 1 && 
                    (this.getNCycles() < 5 || (Math.random() > 0.8 || inbox.getContent().equals(word)))) {
                word = wordStopper;
            } else {
                word = dict.findNextWord(inbox.getContent());
            }
        }
    }

    @Override
    public void takeDown() {
        super.takeDown();
        Info("SEQUENCE DIAGRAM:\n" + this.getSequenceDiagram());
    }

}
