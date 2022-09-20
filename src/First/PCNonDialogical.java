/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package First;

import agents.LARVAFirstAgent;
import glossary.Dictionary;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import messaging.ACLMessageTools;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class PCNonDialogical extends LARVAFirstAgent {

    protected enum MODE {
        POLITE, DEADLINES, DELAYANSWERS, URGENCY, BLOCKING, CHEAT,
        MISTAKES, JUSTANSWER, MANUAL, RECURSIVE, SINGLECID
    };

    protected ArrayList<MODE> Modes;
    protected Dictionary dict;
    protected String message = "", tabs = "", receiver, stopper = "STOP", controller;
    protected ACLMessage _inbox, _outbox, lastRequest;
    protected int tDeadline_s = 60, tWait_ms = 500, tTotalWait_ms = 2000, tLatency_ms = tTotalWait_ms + 5000;
    protected int nPlayers = 2, nMessages = 1, nIter, nPoints = 0, nAnswers;
    protected String wordsent = "", wordreceived = "", wordStopper = "ALTO";
    protected ACLMessage sent[], received[], unexpected[], request, emergency;
    protected ArrayList<ACLMessage> alInbox, alUnexpected, alIgnored;
    protected String partner = "", partners[];
    protected ArrayList<String> lReceived = new ArrayList(), lSent = new ArrayList(), incidences = new ArrayList();
    protected boolean bSents = false, bReceived = false, exit = false, urgentExit = false, urgentSent = false;
    protected static String cheater = "";

    // Scheduling
    protected int maxTime = 100, clock = 0;

    @Override
    public void setup() {
        super.setup();
        dict = new Dictionary();
        dict.load("config/ES.words");
        logger.onEcho();
        logger.onTabular();
//        this.activateSequenceDiagrams();
        alInbox = new ArrayList();
        alUnexpected = new ArrayList();
        alIgnored = new ArrayList();
        controller="Trinity"+getLocalName().substring(getLocalName().length()-4,getLocalName().length());
    }

//    public void checkAllMessages(ACLMessage request) {
//        ACLMessage received;
//        received = super.LARVAreceive(); //super.LARVAblockingReceive(5);
//        while (received != null) {
//            if (request != null) {
//                if (ACLMessageTools.isAnswerTo(received, request)) {
//                    alInbox.add(received);
//                } else {
//                    alUnexpected.add(received);
//                }
//            } else {
//                alUnexpected.add(received);
//            }
//            received = super.LARVAreceive(); //super.LARVAblockingReceive(5);
//        }
//
//    }

//    @Override
//    public void LARVAsend(ACLMessage msg) {
//        super.LARVAsend(msg);
//        lastRequest = msg;
//        nAnswers = ACLMessageTools.getAllReceivers(msg).split(",").length;
//        alInbox.clear();
//    }
//
//    @Override
//    public ACLMessage LARVAblockingReceive() {
//        checkAllMessages(lastRequest);
//        while (alInbox.size() == 0) {
//            this.LARVAwait(100);
//            checkAllMessages(lastRequest);
//        }
//        ACLMessage res = alInbox.get(0);
//        lastRequest = null;
//        return res;
//    }

}
