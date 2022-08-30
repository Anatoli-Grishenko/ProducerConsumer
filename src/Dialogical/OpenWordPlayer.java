/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogical;

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
        this.DFAddMyServices(new String[]{"WORDPLAYER"});
        this.activateSequenceDiagrams();
        if (sd.getOwner().equals(getLocalName())) {
            Info("I am the owner");
            tTotalWait_ms += 5000;
        }
        nIter = tTotalWait_ms / tWait_ms;
        Modes.clear();
        Modes.add(MODE.POLITE);
//        Modes.add(MODE.DEADLINES);
//        this.logger.offEcho();
    }

    @Override
    public void Execute() {
        openTurn();
    }

    public void selectPartners() {
        partners = this.getNextPlayer(nPlayers);       
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }
    
    public void sendNewWord() {
        if (nMessages <= 0) {
            return;
        }
        selectPartners();
        wordsent = this.findFirstWord();
        Info("Starting a new thread: " + wordsent);
        request = new ACLMessage(ACLMessage.QUERY_IF);
        request.setSender(getAID());
        for (String p : partners) {
            if (this.AMSIsConnected(p)) {
                request.addReceiver(new AID(p, AID.ISLOCALNAME));
            }
        }
        if (ACLMessageTools.getAllReceivers(request).length() > 0) {
            request.setContent(wordsent);
            request.setReplyWith(wordsent);
            request.setConversationId(crypto.Keygen.getHexaKey());
            if (tDeadline_s > 0 && Modes.contains(MODE.DEADLINES)) {
                request.setReplyByDate(TimeHandler.nextSecs(tDeadline_s).toDate());
            }
            this.LARVADialogue(request);
        } else {
            Info("Too late to play");
        }
    }

    public void processUnexpected() {
        nIter = tTotalWait_ms / tWait_ms;
        Info("Checking for unexpected request");
        Info("Processing unexpected");
        received = this.LARVAqueryUnexpectedRequests();
        for (ACLMessage m : received) {
            if (m.getContent().startsWith(wordStopper)) {
                urgentExit = true;
                incidences.add("Requested to stop");
                return;
            }
        }
        for (ACLMessage m : received) {
            if (m.getPerformative() == ACLMessage.QUERY_IF) {
                wordreceived = this.findNextWord(m.getContent());
                outbox = this.LARVAreplySender(m);
                outbox.setPerformative(ACLMessage.INFORM);
                outbox.setContent(wordreceived);
                outbox.setReplyWith(wordreceived);
                this.LARVADialogue(outbox);
                this.LARVAcloseDialogue(m);
            } else {
                this.NotUnderstood(m);
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
                    if (!this.d.findWord(received[i].getContent())) {
                        incidences.add("Unkown word " + received[i].getContent());
                    }
                    if (this.checkWords(request.getContent(), received[i].getContent()) < 0) {
                        incidences.add("Word received " + received[i].getContent() + " does not match " + request.getContent());
                    }
                } else {
                    if (received[i].getPerformative() != ACLMessage.NOT_UNDERSTOOD) {
                        this.NotUnderstood(received[i]);
                        incidences.add("Received a bad performative");
                    }
                }
            }
            if (this.LARVAgetDialogueStatus(request) == Utterance.Status.OVERDUE) {
                incidences.add("Missed answers to my request " + request.getContent());
            } else {
                Info("Closing conversation about " + request.getContent());
            }
            this.LARVAcloseDialogue(request);
            if (nMessages > 0) {
                sendNewWord();
                nMessages--;
            }
        }
    }

    public boolean checkExit() {
        if (urgentExit) {
            return true;
        }
//        this.LARVAcheckOpenDialogs();
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

    public void NotUnderstood(ACLMessage m) {
        outbox = this.LARVAreplySender(m);
        outbox.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        outbox.setContent("");
        this.LARVADialogue(outbox);
    }

    public void openTurn() {
//        this.LARVAcheckOpenDialogs();
//        Info("Expected waiting time " + (nIter * this.tWait_ms) + "ms");
//        Info(this.DM.toString());
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

    @Override
    public void ignoreMessage(ACLMessage m
    ) {
        incidences.add("Ignored message " + m.getContent());
    }

    @Override
    public void takeDown() {
//        Message("Achieved " + nPoints + " pts");
        // At the end, it automatically generates the sequence diagram
        if (incidences.size() == 0) {
            Info("\n%%%%%%%%%%%%%%%%%%%%\nEverything went well\n%%%%%%%%%%%%%%%%%%%%\n");
        } else {
            Info("\n%%%%%%%%%%%%%%%%%%%%\nIncidences detected: " + incidences.toString() + "\n%%%%%%%%%%%%%%%%%%%%\n");
            Message("Indicences:\n" + incidences.toString());
        }
        this.saveSequenceDiagram("./" + getLocalName() + ".seqd");
        super.takeDown();

    }

}
