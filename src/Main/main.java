/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import SequentialDialogue.WordStarter;
import SequentialDialogue.WordFollower;
import CentralizedDialogue.WordProducer;
import CentralizedDialogue.WordConsumer;
import CentralizedDialogue.ProductionController;
import OpenDialogue.OWordFollower;
import OpenDialogue.OWordStarter;
import OpenDialogue.OpenWordPlayer;
import OpenDialogue.POpenCheater;
import OpenDialogue.POpenWordPlayer;
import OpenDialogue.PBlockingWordPlayer;
import appboot.JADEBoot;
import appboot.LARVABoot;
import static crypto.Keygen.getHexaKey;

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
//        main2();
//        main3();
//        main4();
        main5();
//        main6();
    }

    public static void main1() {
        JADEBoot _console;
        _console = new JADEBoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo"+suffix, WordFollower.class);
        _console.launchAgent("Smith"+suffix, WordStarter.class);
        _console.WaitToShutDown();

    }

    public static void main2() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo"+suffix, WordFollower.class);
        _console.launchAgent("Smith"+suffix, WordStarter.class);
        _console.WaitToShutDown();

    }

    public static void main3() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Trinity"+suffix, ProductionController.class);
        _console.launchAgent("Neo"+suffix, WordConsumer.class);
        _console.launchAgent("Smith"+suffix, WordProducer.class);
        _console.WaitToShutDown();

    }

    public static void main4() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo"+suffix, OWordFollower.class);
        _console.launchAgent("Smith"+suffix, OWordStarter.class);
        _console.WaitToShutDown();

    }

    public static void main5() {
        LARVABoot _console;
        int nAgents=3;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        for (int i = 0; i < nAgents; i++) {
            _console.launchAgent(suffix + i, OpenWordPlayer.class);
        }
        _console.WaitToShutDown();

    }

    public static void main6() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        int nblocking = 1, nopen = 1, ncheat = 1;
        //
        for (int i = 0; i < nopen; i++) {
            _console.launchAgent(suffix + i, POpenWordPlayer.class);
        }
        for (int i = 0; i < nblocking; i++) {
            _console.launchAgent(suffix+"B" + i, PBlockingWordPlayer.class);
        }
        for (int i = 0; i < ncheat; i++) {
            _console.launchAgent(suffix+"C" + i, POpenCheater.class);
        }
        _console.WaitToShutDown();

    }

}
