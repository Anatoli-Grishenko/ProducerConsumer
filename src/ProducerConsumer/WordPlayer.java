/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import data.Transform;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import messaging.ACLMessageTools;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class WordPlayer extends ProdConsAgent {

    int nPlayers = 3, nMessages, nIter = 3*nPlayers, nPoints=0;
    String word = "";
    ACLMessage sent[], received[], unexpected[], request;
    String partner = "", partners[];
    ArrayList<String> lReceived = new ArrayList(), lSent = new ArrayList();
    boolean bSents = false, bReceived = false;

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        this.DFAddMyServices(new String[]{"WORDPLAYER"});
        this.activateSequenceDiagrams();
        this.logger.offEcho();
//        this.openRemote();
    }

    @Override
    public void Execute() {
        blockingTurn();
    }

    public void blockingTurn() {
        this.LARVAcheckOpenDialogs();
//        Info("OPEN D: " + this.LARVAsizeOpenDialogues()
//                + " = " + this.LARVAsizeUnexpectedRequests() + "-UR"
//                + " + " + this.LARVASizePendingRequests() + "-PR"
//        );
//        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());

        if (word.length() == 0) {
            partners = this.getNextPlayer(nPlayers);
            nMessages = nPlayers;
            word = this.findFirstWord();
            Info("I will play with " + Transform.toArrayList(partners).toString());
            Info("Starting a new thread: " + word);
            request = new ACLMessage(ACLMessage.QUERY_IF);
            request.setSender(getAID());
            for (String p : partners) {
                request.addReceiver(new AID(p, AID.ISLOCALNAME));
            }
            request.setContent(word);
            request.setReplyWith(word);
            request.setConversationId(crypto.Keygen.getHexaKey());
            this.LARVADialogue(request);
            check(request);
        } else {
            Info("Checking for unexpected request");
            if (this.LARVAsizeUnexpectedRequests() > 0) {
                Info("processing unexpected");
                received = this.LARVAqueryUnexpectedRequests();
                for (ACLMessage m : received) {
                    check(m);
                    if (m.getPerformative() == ACLMessage.QUERY_IF) {
                        word = this.findNextWord(m.getContent());
                        outbox = this.LARVAcreateSingleReply(m);
                        outbox.setPerformative(ACLMessage.INFORM);
                        outbox.setContent(word);
                        outbox.setReplyWith(word);
                        this.LARVADialogue(outbox);
                        check(outbox);
                    }
                }
            } else if (this.LARVASizeAnswersTo(request) == nPlayers) {
                Info("processing answers");
                this.LARVAqueryAnswersTo(request);
                request = null;
            } else {
                LARVAwait(1000);
            }
        }
        this.LARVAcheckOpenDialogs();
        if (bSents && lReceived.size() == lSent.size() && nMessages == nPlayers && this.LARVAsizeOpenDialogues() == 0) {
            Info("Mission cumplida");
            this.LARVAwait(500);
            nIter--;
            if (nIter == 0) {
                this.LARVAexit = true;
            }
        }
//        Info("Sents: " + bSents + " QIF:" + lReceived.size() + " INF:" + (lReceived.size() - lSent.size()));
//        if (this.LARVAsizeOpenDialogues()==0) {
//            Info("No more conversations left");
//            this.LARVAexit=true;
//        }
    }

    @Override
    public void takeDown() {
        Message("Achieved "+nPoints+" pts");   
        // At the end, it automatically generates the sequence diagram
        this.saveSequenceDiagram("./" + getLocalName() + ".seqd");
        super.takeDown();

    }

    public String[] getNextPlayer(int n) {
        ArrayList<String> Players;
        ArrayList<String> next = new ArrayList();
        String mynext;
        for (int i = 0; i < n; i++) {
            Players = this.DFGetAllProvidersOf("WORDPLAYER");
            mynext = Players.get((int) (Math.random() * Players.size()));
            while (mynext.equals(getLocalName()) || next.contains(mynext)) {
                this.LARVAwait(1000);
                Players = this.DFGetAllProvidersOf("WORDPLAYER");
                mynext = Players.get((int) (Math.random() * Players.size()));
            }
            next.add(mynext);
        }
        return next.toArray(new String[next.size()]);
    }

    public void check(ACLMessage msg) {
        if (msg.getSender().getLocalName().equals(getLocalName())) {
            if (msg.getPerformative() == ACLMessage.QUERY_IF && ACLMessageTools.getAllReceivers(msg).split(",").length == nPlayers) {
                bSents = true;
                Info("Enviadas peticiones");
                nPoints+=3;
            } else if (msg.getPerformative() == ACLMessage.INFORM) {
                Info("Respondiendo a otras peticiones");
                int p = checkWords(msg.getInReplyTo(), msg.getContent());
                if (p > 0) {
                    lSent.add(msg.getContent());
                    nPoints +=p;
                }
                else
                    nPoints--;
//                }
            }
        } else {
            if (msg.getPerformative() == ACLMessage.QUERY_IF) {
                lReceived.add(msg.getReplyWith());
            } else if (msg.getPerformative() == ACLMessage.INFORM) {
            }

        }
//        if (inbox == null && nMessages == nPlayers) {
//            if (lReceived.size() > 0 && lSent.size() == 0) {
//                this.Message("ok");
//            }
//            doExit();
//        }
    }
}

//    public void SingleTurn() {
//        Info("OPEN D: " + this.LARVAsizeOpenDialogues()
//                + " = " + this.LARVAsizeUnexpectedRequests() + "-UR"
//                + " + " + this.LARVASizeNewAnswers() + "-NA"
//                + " + " + this.LARVASizePendingRequests() + "-PR"
//        );
//        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
//
//        if (this.LARVAhasOpenDialogs()) {
//            if (this.LARVAhasNewAnswers()) {
//                Info("New answers received");
//                received = this.LARVAqueryNewAnswers();
//                Info("Received " + received.length + " answers");
//                if (received.length > 0) {
//                    outbox = received[0].createReply();
//                    outbox.setPerformative(ACLMessage.INFORM);
//                    word = this.findNextWord(received[0].getContent());
//                    outbox.setReplyWith(word);
//                    outbox.setContent(word);
//                    this.LARVAsend(outbox);
//
//                }
//            } else if (this.LARVAhasUnexpectedRequests()) {
//                unexpected = this.LARVAqueryUnexpectedRequests();
//                Info("Unexpected " + unexpected.length + " unexpected");
//                if (unexpected.length > 0) {
//                    outbox = unexpected[0].createReply();
//                    outbox.setPerformative(ACLMessage.INFORM);
//                    word = this.findNextWord(unexpected[0].getContent());
//                    outbox.setReplyWith(word);
//                    outbox.setContent(word);
//                    this.LARVAsend(outbox);
//                }
//            }
//        } else if (word.length() == 0) {
//            partner = this.getNextPlayer(1)[0];
//            word = this.findFirstWord();
//            Info("I will play with " + partner);
//            Info("Starting a new thread: " + word);
//            // If it sent "STOP", then en ds
//            if (word.equals(stopper)) {
//                doExit();
//            }
//            outbox = new ACLMessage(ACLMessage.QUERY_IF);
//            outbox.setSender(getAID());
//            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
//            outbox.setContent(word);
//            outbox.setReplyWith(outbox.getContent());
//            outbox.setConversationId(crypto.Keygen.getHexaKey());
//            this.LARVAsend(outbox);
//        } else {
//            Info("Waiting");
//            LARVAwait(500);
//        }
//    }
//
//    public void MultipleTurn() {
//        this.LARVAhasOpenDialogs();
////        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
//        received = this.LARVAqueryNewAnswers();
//        unexpected = this.LARVAqueryUnexpectedRequests();
//
//        if (received.length > 0) {
//            Info("Received " + received.length + " answers");
//            if (received.length > 0) {
//                outbox = received[0].createReply();
//                outbox.setPerformative(ACLMessage.QUERY_IF);
//                word = this.findNextWord(received[0].getContent());
//                outbox.setReplyWith(word);
//                outbox.setContent(word);
//                this.LARVAsend(outbox);
//
//            }
//        } else if (unexpected.length > 0) {
//            Info("Unexpected " + unexpected.length + " unexpected");
//            if (unexpected.length > 0) {
//                outbox = unexpected[0].createReply();
//                outbox.setPerformative(ACLMessage.QUERY_IF);
//                word = this.findNextWord(unexpected[0].getContent());
//                outbox.setReplyWith(word);
//                outbox.setContent(word);
//                this.LARVAsend(outbox);
//            }
//        } else if (word.length() == 0) {
//            partner = this.getNextPlayer(1)[0];
//            word = this.findFirstWord();
//            Info("I will play with " + partner);
//            Info("Starting a new thread: " + word);
//            // If it sent "STOP", then en ds
//            if (word.equals(stopper)) {
//                doExit();
//            }
//            outbox = new ACLMessage(ACLMessage.QUERY_IF);
//            outbox.setSender(getAID());
//            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
//            outbox.setContent(word);
//            outbox.setReplyWith(outbox.getContent());
//            outbox.setConversationId(crypto.Keygen.getHexaKey());
//            this.LARVAsend(outbox);
//        } else {
//            Info("Waiting");
//            LARVAwait(500);
//        }
//    }
//
//    public void TimedMultipleTurn() {
//        this.LARVAhasOpenDialogs();
////        Info("DIALOGUE STATUS:" + this.LARVAqueryOpenDialogues().length + " pending answers\n" + DM.toString());
//        received = this.LARVAqueryNewAnswers();
//        unexpected = this.LARVAqueryUnexpectedRequests();
//
//        if (received.length > 0) {
//            Info("Received " + received.length + " answers");
//            if (received.length > 0) {
//                outbox = received[0].createReply();
//                outbox.setPerformative(ACLMessage.QUERY_IF);
//                word = this.findNextWord(received[0].getContent());
//                outbox.setReplyWith(word);
//                outbox.setContent(word);
//                this.LARVAsend(outbox);
//
//            }
//        } else if (unexpected.length > 0) {
//            Info("Unexpected " + unexpected.length + " unexpected");
//            if (unexpected.length > 0) {
//                outbox = unexpected[0].createReply();
//                outbox.setPerformative(ACLMessage.QUERY_IF);
//                word = this.findNextWord(unexpected[0].getContent());
//                outbox.setReplyWith(word);
//                outbox.setContent(word);
//                this.LARVAsend(outbox);
//            }
//        } else if (word.length() == 0) {
//            partner = this.getNextPlayer(1)[0];
//            word = this.findFirstWord();
//            Info("I will play with " + partner);
//            Info("Starting a new thread: " + word);
//            // If it sent "STOP", then en ds
//            if (word.equals(stopper)) {
//                doExit();
//            }
//            outbox = new ACLMessage(ACLMessage.QUERY_IF);
//            outbox.setSender(getAID());
//            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
//            outbox.setContent(word);
//            outbox.setReplyWith(outbox.getContent());
//            outbox.setConversationId(crypto.Keygen.getHexaKey());
//            this.LARVAsend(outbox);
//        } else {
//            Info("Waiting");
//            LARVAwait(500);
//        }
//    }
