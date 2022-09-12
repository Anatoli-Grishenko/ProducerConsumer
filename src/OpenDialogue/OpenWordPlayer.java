/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenDialogue;

import Dialogical.PCDialogical;
import data.Transform;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.Arrays;
import messaging.ACLMessageTools;
import messaging.Utterance;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class OpenWordPlayer extends PCDialogical {

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        // Register DF with my new service so others could find me
        this.DFAddMyServices(new String[]{"WORDPLAYER"});
        // Máx number of turns of sending/receiving words
        nMessages = 1;
        // Number of rivals per player
        nPlayers = 2;
        // Look up in the DF for available partners in my session to select them as rivals
        selectPartners();
    }

    // Main execution loop
    @Override
    public void Execute() {
        Info("New cycle of execution");
        // show the schema of the Dialogue
        // Info(this.DM.toString());
        
        // Send the first word to my rivals
        if (nMessages > 0) {
            sendNewWord();
            nMessages--;
        }
        // Get the answers to my previous messages
        processMyAnswers();
        
        // Respond to external requests from any other partner, not necessarilly a selected rival
        processRequests();
        
        // If no more communication acts are open, wait a little for any
        // lazy partner, and close the agent.
        if (checkExit()) {
            doExit();
        }
    }

    // Given a message m coming from elsewhere, I simply respond to it.
    public void respondTo(ACLMessage m) {
        Info("Responding to " + m.getSender().getLocalName());
        wordreceived = dict.findNextWord(m.getContent());
        outbox = m.createReply();
        outbox.setPerformative(ACLMessage.INFORM);
        outbox.setContent(wordreceived);
        outbox.setReplyWith(wordreceived);
        this.Dialogue(outbox);
        this.closeUtterance(m);
    }

    // Look up in the DF other partners in the same session who could
    // act as my rivals.
    public void selectPartners() {
        Info("Configuring partners");
        partners = this.getRivals(nPlayers);
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }

    // First round. To all my rivals, I send the first word to them
    // Please reember that only nMessages new messages can be sent
    public ACLMessage sendNewWord() {
        if (nMessages <= 0) {
            return null;
        }
        wordSent = dict.findFirstWord();
        Info("Starting a new thread: " + wordSent + " with " + Arrays.toString(this.partners));
        request = new ACLMessage(ACLMessage.QUERY_IF);
        request.setSender(getAID());
        for (String p : partners) {
            request.addReceiver(new AID(p, AID.ISLOCALNAME));
        }
        request.setContent(wordSent);
        request.setReplyWith(wordSent);
        request.setConversationId(crypto.Keygen.getHexaKey());
        this.Dialogue(request);
        nMessages--;
        return request;
    }

    // At any time, the requests coming fro other agents do not interrupt
    // existing dialogues. They are stored in separated queues which migh be
    // Attended at any moment
    public void processRequests() {
        received = this.getExtRequests();
        Info("Processing " + received.size() + " requests");
        // First check for urgent request
        for (ACLMessage m : received) {
            if (m.getContent().startsWith(wordStopper)) {
                urgentExit = true;
                return;
            }
        }
        // The check the remaining of the queue to respond to them
        for (ACLMessage m : received) {
            if (Modes.contains(MODE.RECURSIVE)) {
                if (nMessages > 0) {
                    // Important. Just by answering these pending request
                    // they are no further a "pending request" and they simply dissapear
                    // from this list
                    respondTo(m);
                    nMessages--;
                }
            } else {
                respondTo(m);
            }
        }
    }

    // Now it is time to process the answers to all messages sent,
    // In chronologica order.
    public void processMyAnswers() {
        received = this.getAllDueUtterances();
        Info("Processing " + received.size() + " new due messages");
        for (ACLMessage m : received) {
            for (ACLMessage mans : this.getAnswersTo(m)) {
                // If I receive an INFORM, then I assume that I can close that dialogue (utterance)
                if (mans.getPerformative() == ACLMessage.INFORM) {
                    if (!this.dict.findWord(mans.getContent())) {
                        Info("Unkown word " + mans.getContent());
                    }
                    if (dict.checkWords(m.getContent(), mans.getContent()) < 0) {
                        incidences.add("Word received " + mans.getContent()
                                + " does not match " + m.getContent());
                    }
                    this.closeUtterance(m);
                }
            }
        }
    }

    // Let see whether or not I can terminate
    public boolean checkExit() {
        Info("Checking exit");
        // If I received a request for urgent leave, then exit
        if (urgentExit) {
            return true;
        }
        
        // This if would not be necessary in most cases. Only one agent is allowed to paint
        // the Sequence Diagram char in the GUI. It just wait until all other agents are done to
        // terminate and, therefore, draw any possibly "late communication".
        // Only for drawing purposes of the sequence diagrams
        if (sd.getOwner().equals(getLocalName())) {
            this.getSequenceDiagram();
            if (this.DFGetAllProvidersOf("WORDPLAYER").size() < 2) {
                Info("No more speakers left. Quit.");
                return true;
            } else {
                Info("There are speakers left. Hold on.");
                LARVAwait(tWait_ms);
                return false;
            }
        } else { // This is the general case valid for all agents
            // Again only for drawing the Sequence diagram. Comment this line in most of the cases
            this.getSequenceDiagram();
            // If there are no open dialogues coming, and I spent my nMessages messages, the exit
            if (!this.hasOpenUtterances() && nMessages <= 0) {
                Info("No more utterances nor messages to send. Waiting " + (nIter * this.tWait_ms) + "ms to quit.");
                exit = true;
            } else {
                Info("New utterances arrived. Resuming.");
                exit = false;
                nIter = tTotalWait_ms / tWait_ms;
            }
            // If the decision is exit, then wait a little, step by step
            // before closing the agent
            if (exit) {
                if (nIter > 0) {
                    Info("Waiting " + (nIter * this.tWait_ms) + "ms to quit.");
                    LARVAwait(tWait_ms);
                    nIter--;
                }
                if (nIter == 0) {
                    Info("Stop waiting. Quit now.");
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void takeDown() {
        this.saveSequenceDiagram("./" + getLocalName() + ".seqd");
        super.takeDown();

    }

}
