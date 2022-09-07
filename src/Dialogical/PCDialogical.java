/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogical;

import agents.LARVADialogicalAgent;
import agents.LARVAFirstAgent;
import static crypto.Keygen.getWordo;
import data.OleConfig;
import glossary.Dictionary;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import swing.OleDialog;
import tools.emojis;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class PCDialogical extends LARVADialogicalAgent {

    protected enum MODE { // Game modes
        POLITE,     // Agent always answer. Otherwise it keeps muted and does not answer to anything
        DEADLINES,  // Include deadlines in the answers
        DELAYANSWERS, // It takes some random time to answer. Many times too long
        URGENCY,    // It suddenly sends an STOP message
        BLOCKING,   // It uses blockingDialogues only
        CHEAT,      // It cheats, that is, it sends bad messages to other
        MISTAKES,   // It introduces mistaken (not)chained words
        JUSTANSWER, // It does not send new words, only answers to requests
        MANUAL,     // It asks the user to introduce the words instead of automatically finding them
        RECURSIVE,  // It can respond qith nested QUERY-IF. If not, only INFORM is allowed in the answer
        SINGLECID, // Uses the same ConversationId always. Otherwise, it changes from one word to another
        HIDDEN  // It does not register as WORDPLAYER but try to cheat by sending unexpected messages to others
    };
    protected ArrayList<MODE> 
            Modes=new ArrayList(); // List of configuration modes out of the above ones
    protected Dictionary 
            dict; // The dictionary
    protected int 
            tDeadline_s = 30, // Max seconds to wait for answers
            tWait_ms = 500, // Miliseconds to wait in each iteration
            tTotalWait_ms = 2000, // Max accumulated miliseconds to wait for
            tLatency_ms = tTotalWait_ms + 5000; // Wait time at the end to get, possibly, lazy messages
    protected int 
            nPlayers = 2, // How many agents are used rivals to one agent
            nMessages = 1, // Number of starting words sent to other agent
            nIter, 
            nPoints = 0;
    protected String wordSent = "", wordreceived = "", wordStopper = "ALTO";
    protected ACLMessage request, emergency;
    protected ArrayList<ACLMessage> received, unexpected, ignored;
    protected String partner = "", partners[], CID;
    protected ArrayList<String> lReceived = new ArrayList(), lSent = new ArrayList(), incidences = new ArrayList();
    protected boolean bSents = false, bReceived = false, exit = false, urgentExit = false, urgentSent = false;
    protected static String cheater = "";

   @Override
    public void setup() {
        super.setup();
        Info("Booting");
        dict = new Dictionary();
        dict.load("config/ES.words");
        logger.onEcho();
        logger.onTabular();
        if (sd.getOwner().equals(getLocalName())) {
            Info("I am the owner");
            tTotalWait_ms += 5000;
        }
        this.activateSequenceDiagrams();
        nIter = tTotalWait_ms / tWait_ms;
    }

    public String[] getRivals(int n) {
        ArrayList<String> Players;
        ArrayList<String> next = new ArrayList();
        String mynext;
        for (int i = 0; i < n; i++) {
            Players = this.DFGetAllProvidersOf("WORDPLAYER");
            mynext = Players.get((int) (Math.random() * Players.size()));
            while (mynext.equals(getLocalName()) || next.contains(mynext)) {
                this.LARVAwait(100);
                Players = this.DFGetAllProvidersOf("WORDPLAYER");
                mynext = Players.get((int) (Math.random() * Players.size()));
            }
            next.add(mynext);
        }
        return next.toArray(new String[next.size()]);
    }

    public boolean rollDice(double threshold) {
        return Math.random() > threshold;
    }
}

//    public String[] getNextPlayerSwing(int n) {
//        ArrayList<String> Players;
//        OleConfig ocfg = new OleConfig();
//        String configFile = "/resources/config/MarioBrawl.conf";
//        ocfg.loadFile(configFile);
//        OleDialog odPlayers = new OleDialog(null, "Select players");
//        Players = new ArrayList();
//        if (odPlayers.run(ocfg)) {
//            ocfg = odPlayers.getResult();
//            for (String sfield : ocfg.getFieldList()) {
//                if (ocfg.getBoolean(sfield)) {
//                    Players.add(title);
//                }
//            }
//            return Players.toArray(new String[Players.size()]);
//        } else {
//            doExit();
//        }
//        return null;
//    }
//
