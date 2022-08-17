///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ProducerConsumer;
//
//import jade.core.AID;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//import messaging.ACLMessageTools;
//import messaging.MessageBox;
//import messaging.MessageBox.BoxQueue;
//
///**
// *
// * @author Anatoli Grishenko <Anatoli.Grishenko@gmail.com>
// */
//public class WordFollowerQueue extends ProdConsAgent {
//
//    String word = "";
//    int milis = 500;
//
//    @Override
//    public void setup() {
//        // Setup higher classes
//        super.setup();
////        this.activateMessageQueue();
//        // Do not need to know the receiver, just answer
//        //receiver = "Smith";
//    }
//
//    @Override
//    public void Execute() {
//       // It starts listening to new messages
//        Info("Executing " + queueSize() + " messages in queue");
//        inbox = blockingPop(BoxQueue.CONVERSATIONID, "PLAY");
////        inbox = this.LARVAblockingReceive(MessageTemplate.MatchConversationId("PLAY"), milis);
////        inbox = blockingPop(BoxQueue.CONVERSATIONID, "PLAY");
//        word = inbox.getContent();
//        Info("Gets: " + word);
//        // If it is STOP, the stops the llop and terminate
//        if (word.equals(stopper)) {
//            doExit();
//        } else {
//            // Otherwise, find the chained word and continue
//            word = this.findNextWord(word);
//            // Does not need to buld a new message. Instead, it
//            // answers to the previous one
//            outbox = inbox.createReply();
//            outbox.setContent(word);
//            this.LARVAsend(outbox);
//            Info("Says : " + word);
//        }
//    }
//
//    @Override
//    protected void checkBackgroundACLMessages() {
//        if (this.queueSize() > 0) {
//            Info("Ignoring message " + ACLMessageTools.fancyWriteACLM(Pop(), false));
//        }
//    }
//
//}
