package uk.ac.core.supervisor.client;

import uk.ac.core.common.exceptions.CHARSException;

/**
 *
 * @author lucasanastasiou
 */
public class App {

    public static void main(String... args) {
        SupervisorClient sc = new SupervisorClient("http://localhost:9001/supervisor");
        try {
            sc.sendHarvestRepositoryRequest(1);
        } catch (CHARSException ex) {
            System.out.println("caught exception");
            ex.printStackTrace();
        }
    }
}
