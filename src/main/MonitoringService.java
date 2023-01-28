public class MonitoringService {

    private final CircuitBreaker delayedService;

    private final CircuitBreaker quickService;

    public MonitoringService(CircuitBreaker delayedService, CircuitBreaker quickService) {
        this.delayedService = delayedService;
        this.quickService = quickService;
    }

    //Assumption: Local service won't fail, no need to wrap it in a circuit breaker logic
    public String localResourceResponse() {
        return "Local Service is working";
    }

    /**
     * Fetch response from the delayed service (with some simulated startup time).
     *
     * @return response string
     */
    public String delayedServiceResponse() {
        try {
            return this.delayedService.attemptRequest();
        } catch (RemoteServiceException e) {
            return e.getMessage();
        }
    }

    /**
     * Fetches response from a healthy service without any failure.
     *
     * @return response string
     */
    public String quickServiceResponse() {
        try {
            return this.quickService.attemptRequest();
        } catch (RemoteServiceException e) {
            return e.getMessage();
        }
    }
}