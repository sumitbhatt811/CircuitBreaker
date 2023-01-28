import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

public class App {

    /**
     * Program entry point.
     *
     * @param args command line args
     */
    public static void main(String[] args) {

        long serverStartTime = System.nanoTime();

        DelayedRemoteService delayedService = new DelayedRemoteService(serverStartTime, 5);
        DefaultCircuitBreaker delayedServiceCircuitBreaker = new DefaultCircuitBreaker(delayedService, 3000, 2,
                2000 * 1000 * 1000);

        QuickRemoteService quickService = new QuickRemoteService();
        DefaultCircuitBreaker quickServiceCircuitBreaker = new DefaultCircuitBreaker(quickService, 3000, 2,
                2000 * 1000 * 1000);

        //Create an object of monitoring service which makes both local and remote calls
        MonitoringService monitoringService = new MonitoringService(delayedServiceCircuitBreaker,
                quickServiceCircuitBreaker);

        //Fetch response from local resource
        LOGGER.info(monitoringService.localResourceResponse());

        //Fetch response from delayed service 2 times, to meet the failure threshold
        LOGGER.info(monitoringService.delayedServiceResponse());
        LOGGER.info(monitoringService.delayedServiceResponse());

        //Fetch current state of delayed service circuit breaker after crossing failure threshold limit
        //which is OPEN now
        LOGGER.info(delayedServiceCircuitBreaker.getState());

        //Meanwhile, the delayed service is down, fetch response from the healthy quick service
        LOGGER.info(monitoringService.quickServiceResponse());
        LOGGER.info(quickServiceCircuitBreaker.getState());

        //Wait for the delayed service to become responsive
        try {
            LOGGER.info("Waiting for delayed service to become responsive");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check the state of delayed circuit breaker, should be HALF_OPEN
        LOGGER.info(delayedServiceCircuitBreaker.getState());

        //Fetch response from delayed service, which should be healthy by now
        LOGGER.info(monitoringService.delayedServiceResponse());
        //As successful response is fetched, it should be CLOSED again.
        LOGGER.info(delayedServiceCircuitBreaker.getState());
    }
}