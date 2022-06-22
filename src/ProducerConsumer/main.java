/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProducerConsumer;

import appboot.JADEBoot;
import appboot.LARVABoot;

/**
 *
 * @author lcv
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        chainedWordsTTY();
//        chainedWordsX();
    sequentialWordsX();
    }

    public static void chainedWordsTTY() {
        JADEBoot _console;
        _console = new JADEBoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo", WordFollower.class);
        _console.launchAgent("Smith", WordStarter.class);
        _console.WaitToShutDown();
        
    }
    
    public static void chainedWordsX() {
        LARVABoot _console;
        _console = new LARVABoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Neo", WordFollower.class);
        _console.launchAgent("Smith", WordStarter.class);
        _console.WaitToShutDown();
        
    }
    
    public static void sequentialWordsX() {
        LARVABoot _console;
        _console = new LARVABoot(LARVABoot.LIGHT);
        _console.Boot("localhost", 1099);
        _console.launchAgent("Trinity", ProductionController.class);
        _console.launchAgent("Neo", WordConsumer.class);
        _console.launchAgent("Smith", WordProducer.class);
        _console.WaitToShutDown();
        
    }
    
}
