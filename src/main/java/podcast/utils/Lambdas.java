package podcast.utils;

import com.couchbase.client.java.error.CASMismatchException;
import rx.Observable;
import java.util.concurrent.TimeUnit;

public class Lambdas {

  /** One argument, observable interface */
  public static interface OneArgObservableInterface {
    Observable operation(Observable<? extends Throwable> a);
  }

  /** Retry a request to Couchbase */
  public static OneArgObservableInterface retry = (attempts) ->
    attempts.flatMap(n -> {
      if (!(n.getCause() instanceof CASMismatchException)) {
        return Observable.just(n.getCause());
      }
      return Observable.timer(1, TimeUnit.SECONDS);
    });
}
