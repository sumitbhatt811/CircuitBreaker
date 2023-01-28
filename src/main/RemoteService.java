public interface RemoteService {

    //Fetch response from remote service.
    String call() throws RemoteServiceException;
}