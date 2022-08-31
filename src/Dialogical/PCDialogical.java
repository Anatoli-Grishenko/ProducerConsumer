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

    protected enum MODE {
        POLITE, DEADLINES, DELAYANSWERS, URGENCY, BLOCKING, CHEAT,
        MISTAKES, JUSTANSWER, MANUAL, RECURSIVE, SINGLECID
    };
    protected ArrayList<MODE> Modes;
    protected Dictionary dict;
    protected ACLMessage _inbox, _outbox;
    protected int tDeadline_s = 60, tWait_ms = 500, tTotalWait_ms = 2000, tLatency_ms = tTotalWait_ms + 5000;
    protected int nPlayers = 2, nMessages = 1, nIter, nPoints = 0;
    protected String wordsent = "", wordreceived = "", wordStopper = "ALTO";
    protected ACLMessage sent[], received[], unexpected[], request, emergency;
    protected String partner = "", partners[], CID;
    protected ArrayList<String> lReceived = new ArrayList(), lSent = new ArrayList(), incidences = new ArrayList();
    protected boolean bSents = false, bReceived = false, exit = false, urgentExit = false, urgentSent = false;
    protected static String cheater = "";

    // Scheduling
    int maxTime = 100, clock = 0;

    @Override
    public void setup() {
        super.setup();
        Info("Booting");
        dict = new Dictionary();
        dict.load("config/ES.words");
        logger.onTabular();
        Modes = new ArrayList();
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
