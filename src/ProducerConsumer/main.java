/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import Sequential.WordStarter;
import Sequential.WordFollower;
import Centralized.WordProducer;
import Centralized.WordConsumer;
import Centralized.ProductionController;
import Dialogical.OpenWordPlayer;
import DialogicalParameter.POpenCheater;
import DialogicalParameter.POpenWordPlayer;
import DialogicalParameter.PBlockingWordPlayer;
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
//        sequentialTTY();
//        sequentialX();
//        centralizedX();
//        pureDialogicalX();
        hybridDialogicalX();
    }

    public static void sequentialTTY() {
        JADEBoot _console;
        _console = new JADEBoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo", WordFollower.class);
        _console.launchAgent("Smith", WordStarter.class);
        _console.WaitToShutDown();

    }

    public static void sequentialX() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo", WordFollower.class);
        _console.launchAgent("Smith", WordStarter.class);
        _console.WaitToShutDown();

    }

    public static void centralizedX() {
        LARVABoot _console;
        _console = new LARVABoot(LARVABoot.LIGHT);
        _console.Boot("localhost", 1099);
        _console.launchAgent("Trinity", ProductionController.class);
        _console.launchAgent("Neo", WordConsumer.class);
        _console.launchAgent("Smith", WordProducer.class);
        _console.WaitToShutDown();

    }

    public static void pureDialogicalX() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        for (int i = 0; i < 3; i++) {
            _console.launchAgent("" + i, OpenWordPlayer.class);
        }
        _console.WaitToShutDown();

    }

    public static void hybridDialogicalX() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        int nblocking=1, nopen=1, ncheat=1;
        //
        for (int i = 0; i < nopen; i++) {
            _console.launchAgent("" + i, POpenWordPlayer.class);
        }
        for (int i = 0; i < nblocking; i++) {
            _console.launchAgent("B"+i, PBlockingWordPlayer.class);
        }
        for (int i = 0; i < ncheat; i++) {
            _console.launchAgent("C"+i, POpenCheater.class);
        }
        _console.WaitToShutDown();

    }

}
