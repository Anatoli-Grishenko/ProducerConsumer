/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenDialogue;

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
public class PBlockingWordPlayer extends POpenWordPlayer {

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        request = null;
    }

    @Override
    public void Execute() {
        this.sendNewWord();
        processRequests();
        if (checkExit()) {
            doExit();
        }
    }

    @Override
    public ACLMessage sendNewWord() {
        if (nMessages <= 0) {
            return null;
        }
        selectPartners();
        wordSent = dict.findFirstWord();
        Info("Starting a new thread: " + wordSent);
        request = new ACLMessage(ACLMessage.QUERY_IF);
        request.setSender(getAID());
        for (String p : partners) {
            if (this.AMSIsConnected(p)) {
                request.addReceiver(new AID(p, AID.ISLOCALNAME));
            }
        }
        if (ACLMessageTools.getAllReceivers(request).length() > 0) {
            request.setContent(wordSent);
            request.setReplyWith(wordSent);
            if (Modes.contains(MODE.SINGLECID)) {
                request.setConversationId(CID);
            } else {
                request.setConversationId(crypto.Keygen.getHexaKey());
            }
            if (tDeadline_s > 0 && Modes.contains(MODE.DEADLINES)) {
                request.setReplyByDate(TimeHandler.nextSecs(tDeadline_s).toDate());
            }
            received = this.blockingDialogue(request);
            nMessages--;
            Info("Processing my answers");
            received = this.getAnswersTo(request);
            Info("Received " + received.size() + " answers to " + wordSent + ":");
            for (int i = 0; i < received.size(); i++) {
//                Info(ACLMessageTools.fancyWriteACLM(received.get(i)));
                if (received.get(i).getPerformative() == ACLMessage.INFORM) {
                    if (!this.dict.findWord(received.get(i).getContent())) {
                        incidences.add("Unkown word " + received.get(i).getContent());
                    }
                    if (dict.checkWords(request.getContent(), received.get(i).getContent()) < 0) {
                        incidences.add("Word received " + received.get(i).getContent() + " does not match " + request.getContent());
                    }
                } else {
                    if (received.get(i).getPerformative() != ACLMessage.NOT_UNDERSTOOD) {
                        this.NotUnderstood(received.get(i));
                        incidences.add("Received a bad performative");
                    }
                }
            }
            if (this.getUtteranceStatus(request) == Utterance.Status.OVERDUE) {
                incidences.add("Missed answers to my request " + request.getContent());
            } else {
                Info("Closing conversation about " + request.getContent());
            }
            this.closeUtterance(request);
            return request;
        }
        return null;
    }
}
