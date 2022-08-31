/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DialogicalParameter;

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
        this.DFAddMyServices(new String[]{"WORDPLAYER"});
        this.activateSequenceDiagrams();
        if (sd.getOwner().equals(getLocalName())) {
            Info("I am the owner");
            tTotalWait_ms += 5000;
        }
        nIter = tTotalWait_ms / tWait_ms;
        Modes.clear();
        Modes.add(MODE.POLITE);
        Modes.add(MODE.SINGLECID);
//        Modes.add(MODE.RECURSIVE);
//        Modes.add(MODE.DEADLINES);
        this.logger.offEcho();
        if (Modes.contains(MODE.SINGLECID)) {
            CID = getHexaKey();
        }
        this.logger.offEcho();
        selectPartners();
    }

    @Override
    public void Execute() {
//        this.LARVAcheckOpenDialogs();
//        Info("Expected waiting time " + (nIter * this.tWait_ms) + "ms");
//        Info(this.DM.toString());
        ACLMessage aux = sendNewWord();
        if (aux != null) {
            request = aux;
        }
        processMyAnswers(request);
        processUnexpected();
        if (checkExit()) {
            doExit();
        }
    }

    public void selectPartners() {
        partners = this.getRivals(nPlayers);
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }

    public ACLMessage sendNewWord() {
        if (nMessages <= 0) {
            return null;
        }
        if (Modes.contains(MODE.RECURSIVE) && request != null) {
            return null;
        }
        wordsent = dict.findFirstWord();
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
            if (Modes.contains(MODE.SINGLECID)) {
                request.setConversationId(CID);
            } else {
                request.setConversationId(crypto.Keygen.getHexaKey());
            }
            request.setReplyWith(wordsent);
            if (tDeadline_s > 0 && Modes.contains(MODE.DEADLINES)) {
                request.setReplyByDate(TimeHandler.nextSecs(tDeadline_s).toDate());
            }
            this.LARVADialogue(request);
            nMessages--;
            return request;
        } else {
            Info("No rivals to play with");
            return null;
        }
    }

    public void processUnexpected() {
        if (this.LARVAhasUnexpectedRequests()) {
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
                    if (Modes.contains(MODE.RECURSIVE)) {
                        if (nMessages > 0) {
                            respondTo(m);
                            nMessages--;
                        }
                    } else {
                        respondTo(m);
                    }
                } else {
                    this.NotUnderstood(m);
                }
            }
        }
    }

    public void respondTo(ACLMessage m) {
        wordreceived = dict.findNextWord(m.getContent());
        outbox = this.LARVAreplySender(m);
        if (Modes.contains(MODE.RECURSIVE)) {
            outbox.setPerformative(ACLMessage.QUERY_IF);
            request = outbox;
        } else {
            outbox.setPerformative(ACLMessage.INFORM);
            request = null;
        }
        outbox.setContent(wordreceived);
        outbox.setReplyWith(wordreceived);
        this.LARVADialogue(outbox);
    }

    public void processMyAnswers(ACLMessage sent) {
        if (sent != null
                && this.LARVAgetDialogueStatus(sent) == Utterance.Status.CLOSED) {
//                && (this.LARVAgetDialogueStatus(sent) == Utterance.Status.COMPLETED
//                || this.LARVAgetDialogueStatus(sent) == Utterance.Status.OVERDUE)) {
            Info("Processing my answers");
            received = this.LARVAqueryAnswersTo(sent);
            Info("Received " + received.length + " answers to " + wordsent + ":");
            for (int i = 0; i < received.length; i++) {
                Info(ACLMessageTools.fancyWriteACLM(received[i]));
                if (received[i].getPerformative() == ACLMessage.INFORM
                        || received[i].getPerformative() == ACLMessage.QUERY_IF) {
                    if (!this.dict.findWord(received[i].getContent())) {
                        incidences.add("Unkown word " + received[i].getContent());
                    }
                    if (dict.checkWords(sent.getContent(), received[i].getContent()) < 0) {
                        incidences.add("Word received " + received[i].getContent() + " does not match " + sent.getContent());
                    }
                } else {
                    if (received[i].getPerformative() != ACLMessage.NOT_UNDERSTOOD) {
                        this.NotUnderstood(received[i]);
                        incidences.add("Received a bad performative");
                    }
                }
//                this.LARVAcloseUtterance(sent);
            }
            if (this.LARVAgetDialogueStatus(sent) == Utterance.Status.OVERDUE) {
                incidences.add("Missed answers to my sent " + sent.getContent());
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
