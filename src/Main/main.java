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
import crypto.Keygen;

/**
 *
 * @author lcv
 */
public class main {

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
        _console.launchAgent("Neo", WordFollower.class);
        _console.launchAgent("Smith", WordStarter.class);
        _console.WaitToShutDown();

    }

    public static void main2() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo", WordFollower.class);
        _console.launchAgent("Smith", WordStarter.class);
        _console.WaitToShutDown();

    }

    public static void main3() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Trinity", ProductionController.class);
        _console.launchAgent("Neo", WordConsumer.class);
        _console.launchAgent("Smith", WordProducer.class);
        _console.WaitToShutDown();

    }

    public static void main4() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo", OWordFollower.class);
        _console.launchAgent("Smith", OWordStarter.class);
        _console.WaitToShutDown();

    }

    public static void main5() {
        LARVABoot _console;
        int nAgents=3;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        for (int i = 0; i < nAgents; i++) {
            _console.launchAgent("" + i, OpenWordPlayer.class);
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
            _console.launchAgent("" + i, POpenWordPlayer.class);
        }
        for (int i = 0; i < nblocking; i++) {
            _console.launchAgent("B" + i, PBlockingWordPlayer.class);
        }
        for (int i = 0; i < ncheat; i++) {
            _console.launchAgent("C" + i, POpenCheater.class);
        }
        _console.WaitToShutDown();

    }

}
