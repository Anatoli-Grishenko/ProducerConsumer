/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DialogicalParameter;

import DialogicalParameter.POpenWordPlayer;
import data.Transform;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import messaging.ACLMessageTools;
import messaging.Utterance;
import tools.TimeHandler;

/**
 *
 * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
 */
public class POpenCheater extends POpenWordPlayer {

    @Override
    public void setup() {
        // Setup higher classes
        super.setup();
        Modes.add(MODE.JUSTANSWER);
//         Modes.add(MODE.RECURSIVE);
        Modes.remove(MODE.POLITE);
//        Modes.add(MODE.MISTAKES);
//        Modes.add(MODE.CHEAT);
//        Modes.add(MODE.URGENCY);
//        Modes.add(MODE.DELAYANSWERS);
//        Modes.add(MODE.MANUAL);
//        Modes.add(MODE.DEADLINES);
        if (Modes.contains(MODE.JUSTANSWER)) {
            nMessages = 0;
//            this.tTotalWait_ms+=5000;
        }
//        this.logger.offEcho();
        this.logger.onEcho();
    }

    @Override
    public void selectPartners() {
        partners = getRivals(nPlayers);
        Info("I will play with " + Transform.toArrayList(partners).toString());
    }

    public void sendStop() {
        if (urgentSent) {
            return;
        }
        wordsent = wordStopper;
        emergency = new ACLMessage(ACLMessage.REQUEST);
        emergency.setSender(getAID());
        for (String p : this.DFGetAllProvidersOf("WORDPLAYER")) {
            if (this.AMSIsConnected(p) && !p.equals(getLocalName())) {
                emergency.addReceiver(new AID(p, AID.ISLOCALNAME));
            }
        }
        if (ACLMessageTools.getAllReceivers(emergency).length() > 0) {
            emergency.setContent(wordsent);
            emergency.setReplyWith(wordsent);
            emergency.setInReplyTo(wordsent);
            emergency.setConversationId(crypto.Keygen.getHexaKey());
            this.LARVADialogue(emergency);
            urgentSent = true;
            Modes.remove(MODE.URGENCY);
            urgentExit = true;
            incidences.add("Asked to stop");
        }
    }

    @Override
    public void processUnexpected() {
        if (this.LARVAhasUnexpectedRequests()) {
            nIter = tTotalWait_ms / tWait_ms;
            Info("Checking for unexpected request");
            Info("Processing unexpected");
            received = this.LARVAqueryUnexpectedRequests();
            for (ACLMessage m : received) {
                if (m.getPerformative() == ACLMessage.QUERY_IF) {
                    if (Modes.contains(MODE.DELAYANSWERS) && rollDice(0.8)) {
                        int milis = (int) (Math.random() * tLatency_ms);
                        this.LARVAwait(milis);
                        incidences.add("Answered " + milis + "ms late");
                    }
                    if (Modes.contains(MODE.POLITE)
                            || rollDice(1)) {
                        if (Modes.contains(MODE.CHEAT) && rollDice(0.8)) {
                            wordreceived = "1234";
                            incidences.add("Sent a bad answer");
                        } else if (Modes.contains(MODE.MANUAL)) {
                            String aux = this.inputLine("Please type an answer for " + m.getSender().getLocalName() + ": \n\n" + m.getContent() + "\n\n");
                            if (aux != null) {
                                wordreceived = aux.toUpperCase();
                            } else {
                                wordreceived = "XXX";
                            }
                        } else {
                            wordreceived = dict.findNextWord(m.getContent());
                        }
                        outbox = this.LARVAreplySender(m);
                        if (Modes.contains(MODE.RECURSIVE)) {
                            outbox.setPerformative(ACLMessage.QUERY_IF);
                        } else {
                            outbox.setPerformative(ACLMessage.INFORM);
                        }
                        outbox.setContent(wordreceived);
                        outbox.setReplyWith(wordreceived);
                        if (Modes.contains(MODE.MISTAKES)) {
                            if (rollDice(0.8)) {
                                outbox.setConversationId("XXXX");
                                incidences.add("Sent a bad CID");
                            } else if (rollDice(0.8)) {
                                outbox.setInReplyTo("XXXX");
                                incidences.add("Sent a bad IRT");
                            } else if (rollDice(0.8)) {
                                outbox.setPerformative(ACLMessage.CONFIRM);
                                incidences.add("Sent a bad PERF");
                            }
                        }
                        this.LARVADialogue(outbox);
//                    this.LARVAcloseUtterance(m);
                    } else {
                        this.LARVAcloseUtterance(m);
                        incidences.add("I did not answer");
//                    this.LARVAcloseUtterance(m);
                    }
                } else {
                    this.NotUnderstood(m);
//                this.LARVAcloseUtterance(m);
                }
            }
        }
    }

    @Override
    public void Execute() {
        super.Execute();
        if (Modes.contains(MODE.URGENCY) && rollDice(0.9)) {
            sendStop();
        }
    }

}
