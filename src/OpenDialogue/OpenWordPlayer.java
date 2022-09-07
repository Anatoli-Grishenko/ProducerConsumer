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
        //
        selectPartners();
    }

    @Override
    public void Execute() {
        Info("New cycle of execution");
        Info(this.DM.toString());
        if (nMessages > 0) {
            sendNewWord();
            nMessages--;
        }
        processMyAnswers();
        processRequests();
        if (checkExit()) {
            doExit();
        }
    }

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

    public void selectPartners() {
        Info("Configuring partners");
        partners = this.getRivals(nPlayers);
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }

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

    public void processRequests() {
        received = this.getExtRequests();
        Info("Processing " + received.size() + " requests");
        for (ACLMessage m : received) {
            if (m.getContent().startsWith(wordStopper)) {
                urgentExit = true;
                return;
            }
        }
        for (ACLMessage m : received) {
            if (Modes.contains(MODE.RECURSIVE)) {
                if (nMessages > 0) {
                    respondTo(m);
                    nMessages--;
                }
            } else {
                respondTo(m);
            }
        }
    }

    public void processMyAnswers() {
        received = this.getAllDueUtterances();
        Info("Processing " + received.size() + " new due messages");
        for (ACLMessage m : received) {
            for (ACLMessage mans : this.getAnswersTo(m)) {
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

    public boolean checkExit() {
        Info("Checking exit");
        if (urgentExit) {
            return true;
        }
//        this.checkOpenUtterances();
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
        } else {
            this.getSequenceDiagram();
            if (!this.hasOpenUtterances() && nMessages <= 0) {
                Info("No more utterances nor messages to send. Waiting " + (nIter * this.tWait_ms) + "ms to quit.");
                exit = true;
            } else {
                Info("New utterances arrived. Resuming.");
                exit = false;
                nIter = tTotalWait_ms / tWait_ms;
            }
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
