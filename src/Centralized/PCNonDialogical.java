/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Centralized;

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
public class PCNonDialogical extends LARVAFirstAgent {

    protected enum MODE {
        POLITE, DEADLINES, DELAYANSWERS, URGENCY, BLOCKING, CHEAT,
        MISTAKES, JUSTANSWER, MANUAL
    };
    protected ArrayList<MODE> Modes;
    protected Dictionary dict;
    protected String message = "", tabs = "", receiver, stopper = "STOP", controller = "Trinity";
    protected ACLMessage _inbox, _outbox;
    protected int tDeadline_s = 60, tWait_ms = 500, tTotalWait_ms = 2000, tLatency_ms = tTotalWait_ms + 5000;
    protected int nPlayers = 2, nMessages = 1, nIter, nPoints = 0;
    protected String wordsent = "", wordreceived = "", wordStopper = "ALTO";
    protected ACLMessage sent[], received[], unexpected[], request, emergency;
    protected String partner = "", partners[];
    protected ArrayList<String> lReceived = new ArrayList(), lSent = new ArrayList(), incidences = new ArrayList();
    protected boolean bSents = false, bReceived = false, exit = false, urgentExit = false, urgentSent = false;
    protected static String cheater = "";

    // Scheduling
    int maxTime = 100, clock = 0;

    @Override
    public void setup() {
        super.setup();
        dict = new Dictionary();
        dict.load("config/ES.words");
        logger.onEcho();
        logger.onTabular();
        this.activateSequenceDiagrams();
    }

}
