/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import appboot.JADEBoot;
import appboot.LARVABoot;
import console.Console;
import static console.Console.white;

/**
 *
 * @author lcv
 */
public class PlainProducerConsumer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        TextSimpleProducerConsumer();
//        VisualSimpleProducerConsumer1();
        VisualSimpleProducerConsumer2();
//        VisualRealProducerConsumer();
    }

    public static void TextSimpleProducerConsumer() {
        JADEBoot _console;
        _console = new JADEBoot();
        _console.Boot("localhost", 1099);
        _console.launchAgent("Smith", SimpleConsumer.class);
        _console.launchAgent("Neo", SimpleProducer.class);
        _console.WaitAndShutDown();
    }

    public static void VisualSimpleProducerConsumer1() {
        LARVABoot _gui;
        _gui = new LARVABoot();
        _gui.Boot("localhost", 1099);
        _gui.launchAgent("Smith", SimpleConsumer.class);
        _gui.launchAgent("Neo", SimpleProducer.class);
        _gui.WaitToShutDown();
    }

    public static void VisualSimpleProducerConsumer2() {
        LARVABoot _gui;
        _gui = new LARVABoot();
        _gui.Boot("localhost", 1099);
        _gui.loadAgent("Smith", SimpleConsumer.class);
        _gui.loadAgent("Neo", SimpleProducer.class);
        _gui.WaitToShutDown();
    }

    public static void VisualRealProducerConsumer() {
        Console terminal = new Console("Producer-Consumer", 120, 20);
        JADEBoot _console;
        _console = new JADEBoot();
        _console.Boot("localhost", 1099);
        terminal.clearScreen().setText(white).captureStdInOut().setCursorOff();
        _console.launchAgent("Smith", RealConsumer.class);
        _console.launchAgent("Neo", RealProducer.class);
        _console.WaitAndShutDown();
    }
}
