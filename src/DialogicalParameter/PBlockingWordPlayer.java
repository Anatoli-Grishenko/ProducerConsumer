/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DialogicalParameter;

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
        request=null;
    }

    @Override
    public void Execute() {
        this.sendNewWord();
        processUnexpected();
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
            request.setReplyWith(wordsent);
            if (Modes.contains(MODE.SINGLECID)) {
                request.setConversationId(CID);
            } else {
                request.setConversationId(crypto.Keygen.getHexaKey());
            }
            if (tDeadline_s > 0 && Modes.contains(MODE.DEADLINES)) {
                request.setReplyByDate(TimeHandler.nextSecs(tDeadline_s).toDate());
            }
            received = this.LARVAblockingDialogue(request);
            nMessages--;
            Info("Processing my answers");
            received = this.LARVAqueryAnswersTo(request);
            Info("Received " + received.length + " answers to " + wordsent + ":");
            for (int i = 0; i < received.length; i++) {
                Info(ACLMessageTools.fancyWriteACLM(received[i]));
                if (received[i].getPerformative() == ACLMessage.INFORM) {
                    if (!this.dict.findWord(received[i].getContent())) {
                        incidences.add("Unkown word " + received[i].getContent());
                    }
                    if (dict.checkWords(request.getContent(), received[i].getContent()) < 0) {
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
//            this.LARVAcloseUtterance(request);
//            if (nMessages > 0) {
//                sendNewWord();
//                nMessages--;
//            }
            return request;
        }
        return null;
    }
}
//    @Override
//    public void setup() {
//        // Setup higher classes
//        super.setup();
//        this.DFAddMyServices(new String[]{"WORDPLAYER"});
//        this.activateSequenceDiagrams();
////        this.logger.offEcho();
////        this.openRemote();
//    }
//
//    @Override
//    public void Execute() {
//        blockingTurn();
//    }
//
//    public void blockingTurn() {
//        this.LARVAcheckOpenDialogs();
////        if (this.LARVAhasOpenDialogs()) {
////            nIter = tTotalWait_ms / tWait_ms;
////        }
//
////        Info("OPEN D: " + this.LARVAsizeOpenDialogues()
////                + " = " + this.LARVAsizeUnexpectedRequests() + "-UR"
////                + " + " + this.LARVASizePendingRequests() + "-PR"
////        );
////        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
//
//        if (wordsent.length() == 0) {
//            partners = this.getNextPlayer(nPlayers);
//            nMessages = nPlayers;
//            wordsent = this.findFirstWord();
//            Info("I will play with " + Transform.toArrayList(partners).toString());
//            Info("Starting a new thread: " + wordsent);
//            request = new ACLMessage(ACLMessage.QUERY_IF);
//            request.setSender(getAID());
//            for (String p : partners) {
//                request.addReceiver(new AID(p, AID.ISLOCALNAME));
//            }
//            request.setContent(wordsent);
//            request.setReplyWith(wordsent);
//            request.setConversationId(crypto.Keygen.getHexaKey());
//            received = this.LARVAblockingDialogue(request);
//            if (received.length != nPlayers) {
//                Alert(" I sent " + nPlayers + " but received " + received.length);
//            }
//            Info("Received " + received.length + " answers to " + wordsent + ":");
//            for (int i = 0; i < received.length; i++) {
//                Info("\t" + received[i].getContent() + "\tfrom " + received[i].getSender().getLocalName());
//            }
//            check(request);
//        } else if (this.LARVAsizeUnexpectedRequests() > 0) {
//            Info("Checking for other request");
//            Info("Processing unexpected");
//            received = this.LARVAqueryUnexpectedRequests();
//            for (ACLMessage m : received) {
//                check(m);
//                if (m.getPerformative() == ACLMessage.QUERY_IF) {
//                    wordreceived = this.findNextWord(m.getContent());
//                    Info("Answering to " + m.getContent() + " from " + m.getSender().getLocalName());
//                    outbox = this.LARVAreplySender(m);
//                    outbox.setPerformative(ACLMessage.INFORM);
//                    outbox.setContent(wordreceived);
//                    outbox.setReplyWith(wordreceived);
//                    this.LARVADialogue(outbox);
//                    check(outbox);
//                }
//            }
//        }
//        this.LARVAcheckOpenDialogs();
//        if (!this.LARVAhasOpenDialogs() && nIter > 0) {
//            LARVAwait(tWait_ms);
//            nIter--;
//        }
//        if (nIter == 0 && !this.LARVAhasOpenDialogs()) {
//            doExit();
//        }
//
//    }
//
//    @Override
//    public void takeDown() {
//        Message("Achieved " + nPoints + " pts");
//        // At the end, it automatically generates the sequence diagram
//        super.takeDown();
//
//    }
//
//    public void check(ACLMessage msg) {
//        if (msg.getSender().getLocalName().equals(getLocalName())) {
//            if (msg.getPerformative() == ACLMessage.QUERY_IF && ACLMessageTools.getAllReceivers(msg).split(",").length == nPlayers) {
//                bSents = true;
//                Info("Enviadas peticiones");
//                nPoints += 3;
//            } else if (msg.getPerformative() == ACLMessage.INFORM) {
//                Info("Respondiendo a otras peticiones");
//                int p = checkWords(msg.getInReplyTo(), msg.getContent());
//                if (p > 0) {
//                    lSent.add(msg.getContent());
//                    nPoints += p;
//                } else {
//                    nPoints--;
//                }
////                }
//            }
//        } else {
//            if (msg.getPerformative() == ACLMessage.QUERY_IF) {
//                lReceived.add(msg.getReplyWith());
//            } else if (msg.getPerformative() == ACLMessage.INFORM) {
//            }
//
//        }
////        if (inbox == null && nMessages == nPlayers) {
////            if (lReceived.size() > 0 && lSent.size() == 0) {
////                this.Message("ok");
////            }
////            doExit();
////        }
//    }
//}
//
////    public void SingleTurn() {
////        Info("OPEN D: " + this.LARVAsizeOpenDialogues()
////                + " = " + this.LARVAsizeUnexpectedRequests() + "-UR"
////                + " + " + this.LARVASizeNewAnswers() + "-NA"
////                + " + " + this.LARVASizePendingRequests() + "-PR"
////        );
////        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
////
////        if (this.LARVAhasOpenDialogs()) {
////            if (this.LARVAhasNewAnswers()) {
////                Info("New answers received");
////                received = this.LARVAqueryNewAnswers();
////                Info("Received " + received.length + " answers");
////                if (received.length > 0) {
////                    outbox = received[0].createReply();
////                    outbox.setPerformative(ACLMessage.INFORM);
////                    word = this.findNextWord(received[0].getContent());
////                    outbox.setReplyWith(word);
////                    outbox.setContent(word);
////                    this.LARVAsend(outbox);
////
////                }
////            } else if (this.LARVAhasUnexpectedRequests()) {
////                unexpected = this.LARVAqueryUnexpectedRequests();
////                Info("Unexpected " + unexpected.length + " unexpected");
////                if (unexpected.length > 0) {
////                    outbox = unexpected[0].createReply();
////                    outbox.setPerformative(ACLMessage.INFORM);
////                    word = this.findNextWord(unexpected[0].getContent());
////                    outbox.setReplyWith(word);
////                    outbox.setContent(word);
////                    this.LARVAsend(outbox);
////                }
////            }
////        } else if (word.length() == 0) {
////            partner = this.getNextPlayer(1)[0];
////            word = this.findFirstWord();
////            Info("I will play with " + partner);
////            Info("Starting a new thread: " + word);
////            // If it sent "STOP", then en ds
////            if (word.equals(stopper)) {
////                doExit();
////            }
////            outbox = new ACLMessage(ACLMessage.QUERY_IF);
////            outbox.setSender(getAID());
////            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
////            outbox.setContent(word);
////            outbox.setReplyWith(outbox.getContent());
////            outbox.setConversationId(crypto.Keygen.getHexaKey());
////            this.LARVAsend(outbox);
////        } else {
////            Info("Waiting");
////            LARVAwait(500);
////        }
////    }
////
////    public void MultipleTurn() {
////        this.LARVAhasOpenDialogs();
//////        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
////        received = this.LARVAqueryNewAnswers();
////        unexpected = this.LARVAqueryUnexpectedRequests();
////
////        if (received.length > 0) {
////            Info("Received " + received.length + " answers");
////            if (received.length > 0) {
////                outbox = received[0].createReply();
////                outbox.setPerformative(ACLMessage.QUERY_IF);
////                word = this.findNextWord(received[0].getContent());
////                outbox.setReplyWith(word);
////                outbox.setContent(word);
////                this.LARVAsend(outbox);
////
////            }
////        } else if (unexpected.length > 0) {
////            Info("Unexpected " + unexpected.length + " unexpected");
////            if (unexpected.length > 0) {
////                outbox = unexpected[0].createReply();
////                outbox.setPerformative(ACLMessage.QUERY_IF);
////                word = this.findNextWord(unexpected[0].getContent());
////                outbox.setReplyWith(word);
////                outbox.setContent(word);
////                this.LARVAsend(outbox);
////            }
////        } else if (word.length() == 0) {
////            partner = this.getNextPlayer(1)[0];
////            word = this.findFirstWord();
////            Info("I will play with " + partner);
////            Info("Starting a new thread: " + word);
////            // If it sent "STOP", then en ds
////            if (word.equals(stopper)) {
////                doExit();
////            }
////            outbox = new ACLMessage(ACLMessage.QUERY_IF);
////            outbox.setSender(getAID());
////            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
////            outbox.setContent(word);
////            outbox.setReplyWith(outbox.getContent());
////            outbox.setConversationId(crypto.Keygen.getHexaKey());
////            this.LARVAsend(outbox);
////        } else {
////            Info("Waiting");
////            LARVAwait(500);
////        }
////    }
////
////    public void TimedMultipleTurn() {
////        this.LARVAhasOpenDialogs();
//////        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
////        received = this.LARVAqueryNewAnswers();
////        unexpected = this.LARVAqueryUnexpectedRequests();
////
////        if (received.length > 0) {
////            Info("Received " + received.length + " answers");
////            if (received.length > 0) {
////                outbox = received[0].createReply();
////                outbox.setPerformative(ACLMessage.QUERY_IF);
////                word = this.findNextWord(received[0].getContent());
////                outbox.setReplyWith(word);
////                outbox.setContent(word);
////                this.LARVAsend(outbox);
////
////            }
////        } else if (unexpected.length > 0) {
////            Info("Unexpected " + unexpected.length + " unexpected");
////            if (unexpected.length > 0) {
////                outbox = unexpected[0].createReply();
////                outbox.setPerformative(ACLMessage.QUERY_IF);
////                word = this.findNextWord(unexpected[0].getContent());
////                outbox.setReplyWith(word);
////                outbox.setContent(word);
////                this.LARVAsend(outbox);
////            }
////        } else if (word.length() == 0) {
////            partner = this.getNextPlayer(1)[0];
////            word = this.findFirstWord();
////            Info("I will play with " + partner);
////            Info("Starting a new thread: " + word);
////            // If it sent "STOP", then en ds
////            if (word.equals(stopper)) {
////                doExit();
////            }
////            outbox = new ACLMessage(ACLMessage.QUERY_IF);
////            outbox.setSender(getAID());
////            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
////            outbox.setContent(word);
////            outbox.setReplyWith(outbox.getContent());
////            outbox.setConversationId(crypto.Keygen.getHexaKey());
////            this.LARVAsend(outbox);
////        } else {
////            Info("Waiting");
////            LARVAwait(500);
////        }
////    }
