/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

import appboot.LARVABoot;
import console.Console;
import static console.Console.white;


/**
 *
 * @author lcv
 */
public class PlainProducerConsumer {

    static LARVABoot _app;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        _app = new LARVABoot();
//       _app.Boot();

        VisualSimpleProducerConsumer();

        _app.WaitToShutDown();
    }

    public static void VisualSimpleProducerConsumer() {
        _app.loadAgent("Smith", SimpleConsumer.class);
        _app.loadAgent("Neo", SimpleProducer.class);
    }

    public static void VisualRealProducerConsumer() {
        Console console = new Console("Producer-Consumer", 120, 20);
        console.clearScreen().setText(white).captureStdInOut().setCursorOff();

        _app.launchAgent("Smith    ", RealConsumer.class);
        _app.launchAgent("Neo      ", RealProducer.class);
        console.waitToClose();
    }

}
