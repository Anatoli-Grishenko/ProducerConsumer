/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenDialogue;

import Dialogical.PCDialogical;
import static crypto.Keygen.getHexaKey;
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
public class POpenWordPlayer extends PCDialogical {

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        if (!Modes.contains(MODE.HIDDEN)) {
            this.DFAddMyServices(new String[]{"WORDPLAYER"});
        } else {
            this.LARVAwait(50);
        }
        this.activateSequenceDiagrams();
        if (sd.getOwner().equals(getLocalName())) {
            Info("I am the owner");
            tTotalWait_ms += 5000;
        }
        nIter = tTotalWait_ms / tWait_ms;
        Modes.add(MODE.POLITE);
//        Modes.add(MODE.SINGLECID);
//        Modes.add(MODE.RECURSIVE);
//        Modes.add(MODE.DEADLINES);
        this.logger.offEcho();
        if (Modes.contains(MODE.SINGLECID)) {
            CID = getHexaKey();
        }
//        this.logger.offEcho();
        logger.onEcho();
        selectPartners();
    }

    @Override
    public void Execute() {
//        this.checkOpenUtterances();
//        Info("Expected waiting time " + (nIter * this.tWait_ms) + "ms. " + nMessages + " messages left");
        Info("Executing");
        Info(this.toString());
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

    public void selectPartners() {
        Info("Configuring partners");
        partners = this.getRivals(nPlayers);
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }

    public ACLMessage sendNewWord() {
        if (Modes.contains(MODE.RECURSIVE) && request != null) {
            return null;
        }
        wordSent = dict.findFirstWord();
        Info("Starting a new thread: " + wordSent);
        request = new ACLMessage(ACLMessage.QUERY_IF);
        request.setSender(getAID());
        for (String p : partners) {
            request.addReceiver(new AID(p, AID.ISLOCALNAME));
        }
        if (ACLMessageTools.getAllReceivers(request).length() > 0) {
            request.setContent(wordSent);
            if (Modes.contains(MODE.SINGLECID)) {
                request.setConversationId(CID);
            } else {
                request.setConversationId(crypto.Keygen.getHexaKey());
            }
            request.setReplyWith(wordSent);
            if (tDeadline_s > 0 && Modes.contains(MODE.DEADLINES)) {
                request.setReplyByDate(TimeHandler.nextSecs(tDeadline_s).toDate());
            }
            this.Dialogue(request);
            nMessages--;
            return request;
        } else {
            Info("No rivals to play with");
            return null;
        }
    }

    public void processRequests() {
        received = this.getExtRequests();
        Info("Processing " + received.size() + " requests");
        for (ACLMessage m : received) {
            if (m.getContent().startsWith(wordStopper)) {
                urgentExit = true;
                incidences.add("Requested to stop");
                return;
            }
        }
        for (ACLMessage m : received) {
            if (m.getPerformative() == ACLMessage.QUERY_IF) {
                if (Modes.contains(MODE.RECURSIVE)) {
//                        if (nMessages > 0) {
                    respondTo(m);
                    nMessages--;
//                        }
                } else {
                    respondTo(m);
                }
            } else {
                this.NotUnderstood(m);
            }
        }
    }

    public void respondTo(ACLMessage m) {
        wordreceived = dict.findNextWord(m.getContent());
        outbox = m.createReply(); //this.LARVAreplySender(m);
        if (Modes.contains(MODE.RECURSIVE)) {
            outbox.setPerformative(ACLMessage.QUERY_IF);
            request = outbox;
        } else {
            outbox.setPerformative(ACLMessage.INFORM);
            request = null;
        }
        outbox.setContent(wordreceived);
        outbox.setReplyWith(wordreceived);
        this.Dialogue(outbox);
    }

    public void processMyAnswers() {
        received = this.getAllDueUtterances();
        Info("Processing " + received.size() + " new due messages");
        for (ACLMessage m : received) {
            for (ACLMessage mans : this.getAnswersTo(m)) {
//                Info(ACLMessageTools.fancyWriteACLM(mans));
                if (mans.getPerformative() == ACLMessage.INFORM
                        || mans.getPerformative() == ACLMessage.QUERY_IF) {
                    if (!this.dict.findWord(mans.getContent())) {
                        incidences.add("Unkown word " + mans.getContent());
                    }
                    if (dict.checkWords(m.getContent(), mans.getContent()) < 0) {
                        incidences.add("Word received " + mans.getContent() + " does not match " + m.getContent());
                    }
                } else {
                    if (mans.getPerformative() != ACLMessage.NOT_UNDERSTOOD) {
                        this.NotUnderstood(mans);
                        incidences.add("Received a bad performative");
                    }
                }
            }
            if (this.getUtteranceStatus(m) == Utterance.Status.OVERDUE) {
                incidences.add("Missed some answers to my sent " + request.getContent());
            }
            this.closeUtterance(m);
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

    public void NotUnderstood(ACLMessage m) {
        outbox = m.createReply(); //this.LARVAreplySender(m);
        outbox.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        outbox.setContent("");
        this.Dialogue(outbox);
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
