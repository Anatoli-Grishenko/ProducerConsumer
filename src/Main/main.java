/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Boot.LARVABoot;
import static Crypto.LKeygen.getHexaKey;
import SequentialDialogue.WordFollower;
import SequentialDialogue.WordStarter;


/**
 *
 * @author lcv
 */
public class main {
    static String suffix=getHexaKey(4);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        main1();
        main2();
//        main3();
//        main4();
//        main5();
//        main6();
    }

 
    public static void main2() {
        LARVABoot console;
        console = new LARVABoot();
//        _console.Boot("localhost", 1099);
        console.boot();
        console.loadAgent("Neo-"+suffix, WordFollower.class);
        console.loadAgent("Smith-"+suffix, WordStarter.class);
        console.shutDown();

    }

//    public static void main3() {
//        LARVABoot _console;
//        _console = new LARVABoot();
////        _console.Boot("localhost", 1099);
//        _console.Boot();
//        _console.launchAgent("Trinity-"+suffix, ProductionController.class);
//        _console.launchAgent("Neo-"+suffix, WordConsumer.class);
//        _console.launchAgent("Smith-"+suffix, WordProducer.class);
//        _console.WaitToShutDown();
//
//    }
//
//    public static void main4() {
//        LARVABoot _console;
//        _console = new LARVABoot();
//        _console.Boot("localhost", 1099);
////        _console.Boot("isg2.ugr.es", 1099);
//        _console.launchAgent("Neo-"+suffix, OWordFollower.class);
//        _console.launchAgent("Smith-"+suffix, OWordStarter.class);
//        _console.WaitToShutDown();
//
//    }
//
//    public static void main5() {
//        LARVABoot _console;
//        int nAgents=2;
//        _console = new LARVABoot();
//        _console.Boot("localhost", 1099);
//        _console.Boot("isg2.ugr.es", 1099);
//        for (int i = 0; i < nAgents; i++) {
//            _console.launchAgent(suffix+"-" + i, OpenWordPlayer.class);
//        }
//        _console.WaitToShutDown();
//
//    }
//
//    public static void main6() {
//        LARVABoot _console;
//        _console = new LARVABoot();
//        _console.Boot("localhost", 1099);
////        _console.Boot("isg2.ugr.es", 1099);
//        int nblocking = 1, nopen = 1, ncheat = 0;
//        //
//        for (int i = 0; i < nopen; i++) {
//            _console.launchAgent(suffix + "-"+i, POpenWordPlayer.class);
//        }
//        for (int i = 0; i < nblocking; i++) {
//            _console.launchAgent(suffix+"-B" + i, PBlockingWordPlayer.class);
//        }
//        for (int i = 0; i < ncheat; i++) {
//            _console.launchAgent(suffix+"-C" + i, POpenCheater.class);
//        }
//        _console.WaitToShutDown();
//
//    }

}
