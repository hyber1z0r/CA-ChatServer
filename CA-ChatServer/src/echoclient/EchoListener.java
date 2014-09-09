package echoclient;


/**
 *
 * @author jakobgaardandersen
 */
public interface EchoListener {

    void messageArrived(String data);
}
