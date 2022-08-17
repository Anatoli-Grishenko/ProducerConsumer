/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class WordPlayer extends ProdConsAgent {

    String word = "";
    ACLMessage sent[], received[], unexpected[];
    String partner = "";

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        this.DFAddMyServices(new String[]{"WORDPLAYER"});
//        this.openRemote();
        this.activateSequenceDiagrams();
//        this.LARVAwait((int) (Math.random() * 5000 + 500));
    }

    @Override
    public void Execute() {
        if (this.getNCycles() > 15) {
            this.doExit();
        }
        Version1();
    }

    public void Version1() {
        this.LARVAcheckDialogue();
        Info("DIALOGUE STATUS:" + this.LARVAopenConversations().length + " pending answers\n" + DM.toString());
        received = this.LARVAnewAnswers();
        unexpected = this.LARVAunexpectedRequests();

        if (received.length > 0) {
            Info("Received " + received.length + " answers");
            if (received.length > 0) {
                outbox = received[0].createReply();
                outbox.setPerformative(ACLMessage.INFORM);
                word = this.findNextWord(received[0].getContent());
                outbox.setReplyWith(word);
                outbox.setContent(word);
                this.LARVAAnswerDialogue(outbox);

            }
        } else if (unexpected.length > 0) {
            Info("Unexpected " + unexpected.length + " unexpected");
            if (unexpected.length > 0) {
                outbox = unexpected[0].createReply();
                outbox.setPerformative(ACLMessage.INFORM);
                word = this.findNextWord(unexpected[0].getContent());
                outbox.setReplyWith(word);
                outbox.setContent(word);
                this.LARVAAnswerDialogue(outbox);
            }
        } else if (word.length() == 0) {
            partner = this.getNextPlayer();
            word = this.findFirstWord();
            Info("I will play with " + partner);
            Info("Starting a new thread: " + word);
            // If it sent "STOP", then en ds
            if (word.equals(stopper)) {
                doExit();
            }
            outbox = new ACLMessage(ACLMessage.QUERY_IF);
            outbox.setSender(getAID());
            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
            outbox.setContent(word);
            outbox.setReplyWith(outbox.getContent());
            outbox.setConversationId(crypto.Keygen.getHexaKey());
            this.LARVAstartDialogue(outbox);
        } else {
            Info("Waiting");
            LARVAwait(500);
        }
    }

    public void Version2() {
        this.LARVAcheckDialogue();
//        Info("DIALOGUE STATUS:" + this.LARVAopenConversations().length + " pending answers\n" + DM.toString());
        received = this.LARVAnewAnswers();
        unexpected = this.LARVAunexpectedRequests();

        if (received.length > 0) {
            Info("Received " + received.length + " answers");
            if (received.length > 0) {
                outbox = received[0].createReply();
                outbox.setPerformative(ACLMessage.QUERY_IF);
                word = this.findNextWord(received[0].getContent());
                outbox.setReplyWith(word);
                outbox.setContent(word);
                this.LARVAAnswerDialogue(outbox);

            }
        } else if (unexpected.length > 0) {
            Info("Unexpected " + unexpected.length + " unexpected");
            if (unexpected.length > 0) {
                outbox = unexpected[0].createReply();
                outbox.setPerformative(ACLMessage.QUERY_IF);
                word = this.findNextWord(unexpected[0].getContent());
                outbox.setReplyWith(word);
                outbox.setContent(word);
                this.LARVAAnswerDialogue(outbox);
            }
        } else if (word.length() == 0) {
            partner = this.getNextPlayer();
            word = this.findFirstWord();
            Info("I will play with " + partner);
            Info("Starting a new thread: " + word);
            // If it sent "STOP", then en ds
            if (word.equals(stopper)) {
                doExit();
            }
            outbox = new ACLMessage(ACLMessage.QUERY_IF);
            outbox.setSender(getAID());
            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
            outbox.setContent(word);
            outbox.setReplyWith(outbox.getContent());
            outbox.setConversationId(crypto.Keygen.getHexaKey());
            this.LARVAstartDialogue(outbox);
        } else {
            Info("Waiting");
            LARVAwait(500);
        }
    }

    public void VersionX() {
//        this.LARVAwait((int) (Math.random() * 2000 + 500));
        Info("Go!");
//        if (Math.random() > 0.75) {
//            Info("Starting a new thread");
//            outbox = new ACLMessage();
//            outbox.setPerformative(ACLMessage.REQUEST);
//            outbox.setSender(getAID());
//            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
//            outbox.setContent(word);
//            DM.start(outbox);
//        }
//        received = this.LARVAcheckDialogue();
//        Info("DIALOGUE STATUS preanswer: " + received.length + " pending answers\n" + DM.toString());
//        if (received.length > 0) {
//            Info("Answering pending messages");
//            for (ACLMessage msg : received) {
//                outbox = msg.createReply();
//                outbox.setPerformative(ACLMessage.INFORM);
//                word = this.findNextWord(msg.getContent());
//                outbox.setReplyWith(word);
//                outbox.setContent(word);
//                this.LARVAAnswerDialogue(outbox);
//            }
//        }
//        Info("DIALOGUE STATUS postanswer:\n" + DM.toString());
//        if (Math.random() > 0.75) {
//            word = this.findFirstWord();
//            Info("Starting a new thread: " + word);
//            // If it sent "STOP", then ends
//            if (word.equals(stopper)) {
//                doExit();
//            }
//            outbox = new ACLMessage(ACLMessage.QUERY_IF);
//            outbox.setSender(getAID());
//            outbox.addReceiver(new AID(partner, AID.ISLOCALNAME));
//            outbox.setContent(word);
//            outbox.setReplyWith(outbox.getContent());
//            outbox.setConversationId(crypto.Keygen.getHexaKey());
//            received = this.LARVAstartDialogue(outbox);
//            Info("Received " + received.length + " answers");
//            if (received.length > 0) {
//                outbox = received[0].createReply();
//                outbox.setPerformative(ACLMessage.INFORM);
//                word = this.findNextWord(received[0].getContent());
//                outbox.setReplyWith(word);
//                outbox.setContent(word);
//                this.LARVAAnswerDialogue(outbox);
//            }
//        }
    }

    @Override
    public void takeDown() {
        // At the end, it automatically generates the sequence diagram
        this.saveSequenceDiagram("./" + getLocalName() + ".seqd");
        super.takeDown();

    }

    public String getNextPlayer() {
        ArrayList<String> Players;
        String next;
        Players = this.DFGetAllProvidersOf("WORDPLAYER");
        next = Players.get((int) (Math.random() * Players.size()));
        while (next.equals(getLocalName())) {
            this.LARVAwait(1000);
            Players = this.DFGetAllProvidersOf("WORDPLAYER");
            next = Players.get((int) (Math.random() * Players.size()));
        }
        return next;
    }
}
