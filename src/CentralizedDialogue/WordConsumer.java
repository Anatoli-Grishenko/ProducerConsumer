/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CentralizedDialogue;

import First.PCNonDialogical;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class WordConsumer extends PCNonDialogical {

    String word = "";

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Do not need to know the receiver, just answer
        //receiver = "Smith";
        // Minimum time to wait (ms) to process the word
        tLatency_ms = 1000;
        logger.offEcho();
    }

    @Override
    public void Execute() {
        Info("Waiting for a new word");
        // It starts listening to new messages
        inbox = this.LARVAblockingReceive();
        word = inbox.getContent().substring(1);
        Info("Received "+word);
        // If it is STOP, the stops the llop and terminate
        if (word.equals(stopper)) {
            doExit();
        } else {
            // Informs Controller that it is done
            word =dict.findNextWord(word);
            Info ("Answered "+word);
            outbox = new ACLMessage();
            outbox.setSender(getAID());
            outbox.addReceiver(new AID(controller, AID.ISLOCALNAME));
            outbox.setContent("-"+word);
            // this.send(outbox) would also work,  
            // but it does not keep record of sent/received messages 
            // nor allow (auto) sequence diagrams        
            this.LARVAsend(outbox);
            clock = tLatency_ms + (int) (Math.random() * tLatency_ms);
            Info("Wating " + clock + " ms before the next word");
            LARVAwait(clock);

        }

    }

}
