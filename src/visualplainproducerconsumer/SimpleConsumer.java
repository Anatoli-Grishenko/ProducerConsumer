/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualplainproducerconsumer;

/**
 *
 * @author lcv
 */
public class SimpleConsumer extends PlainAgent {

    @Override
    public void setup() {
        super.setup();
        this.maxClock = 5;
        this.countClock=0;
        state = Status.WAITING;
        this.doNotExit();
    }

    @Override
    public void Execute() {
        switch (state) {
            case WAITING:
                mark();
                state = Status.RECEIVING;
                break;
            case RECEIVING:
                _inbox = this.LARVAblockingReceive();
                if (_inbox != null) {
                    readTime();
                    countClock = 1;
                    if (_inbox.getContent().equals("STOP")) {
                        state = Status.EXIT;
                        mark();
                    } else {
                        nmessages = Integer.parseInt(_inbox.getContent());
                        mark();
                        state = Status.PROCESSING;
                    }
                    saveTime();
                } else {
                    state = Status.WAITING;
                }
                break;
            case PROCESSING:
                if (this.countClock <= maxClock) {
                    mark();
                    saveTime();
                    countClock++;
                } else {
                    state = Status.WAITING;
                }

                break;
            case EXIT:
                doExit();
                break;
        }
        this.clock++;
    }

    @Override
    public void takeDown() {
        this.saveSequenceDiagram(getLocalName() + ".seqd");
        super.takeDown();
    }
}
