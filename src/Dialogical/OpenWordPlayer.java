/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogical;

import Dialogical.PCDialogical;
import data.Transform;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import messaging.ACLMessageTools;
import messaging.Utterance;
import tools.TimeHandler;

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
    }

    @Override
    public void Execute() {
        if (nMessages > 0) {
            sendNewWord();
            nMessages--;
        }
        processMyAnswers();
        if (this.LARVAhasUnexpectedRequests()) {
            processUnexpected();
        }
        if (checkExit()) {
            doExit();
        }
    }

    public void respondTo(ACLMessage m) {
        wordreceived = dict.findNextWord(m.getContent());
        outbox = this.LARVAreplySender(m);
        outbox.setPerformative(ACLMessage.INFORM);
        outbox.setContent(wordreceived);
        outbox.setReplyWith(wordreceived);
        this.LARVADialogue(outbox);
    }

    public void selectPartners() {
        partners = this.getRivals(nPlayers);
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }

    public ACLMessage sendNewWord() {
        if (nMessages <= 0) {
            return null;
        }
        selectPartners();
        wordsent = dict.findFirstWord();
        Info("Starting a new thread: " + wordsent);
        request = new ACLMessage(ACLMessage.QUERY_IF);
        request.setSender(getAID());
        for (String p : partners) {
            request.addReceiver(new AID(p, AID.ISLOCALNAME));
        }
        request.setContent(wordsent);
        request.setReplyWith(wordsent);
        request.setConversationId(crypto.Keygen.getHexaKey());
        this.LARVADialogue(request);
        nMessages--;
        return request;
    }

    public void processUnexpected() {
        nIter = tTotalWait_ms / tWait_ms;
        Info("Checking for unexpected request");
        Info("Processing unexpected");
        received = this.LARVAqueryUnexpectedRequests();
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
        if (request != null
                && (this.LARVAgetDialogueStatus(request) == Utterance.Status.COMPLETED
                || this.LARVAgetDialogueStatus(request) == Utterance.Status.OVERDUE)) {
            Info("Processing my answers");
            received = this.LARVAqueryAnswersTo(request);
            Info("Received " + received.length + " answers to " + wordsent + ":");
            for (int i = 0; i < received.length; i++) {
                Info(ACLMessageTools.fancyWriteACLM(received[i]));
                if (received[i].getPerformative() == ACLMessage.INFORM) {
                    if (!this.dict.findWord(received[i].getContent())) {
                        Info("Unkown word " + received[i].getContent());
                    }
                    if (dict.checkWords(request.getContent(), received[i].getContent()) < 0) {
                        Info("Word received " + received[i].getContent() + " does not match " + request.getContent());
                    }
                }
            }
        }
    }

    public boolean checkExit() {
        if (urgentExit) {
            return true;
        }
        if (sd.getOwner().equals(getLocalName())) {
            this.getSequenceDiagram();
            if (this.DFGetAllProvidersOf("WORDPLAYER").size() < 2) {
                return true;
            } else {
                LARVAwait(tWait_ms);
                return false;
            }
        } else {
            this.getSequenceDiagram();
            if (!this.LARVAhasOpenDialogs() || nMessages == 0) {
                exit = true;
            } else {
                exit = false;
                nIter = tTotalWait_ms / tWait_ms;
            }
            if (exit) {
                if (nIter > 0) {
                    LARVAwait(tWait_ms);
                    nIter--;
                }
                if (nIter == 0) {
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
