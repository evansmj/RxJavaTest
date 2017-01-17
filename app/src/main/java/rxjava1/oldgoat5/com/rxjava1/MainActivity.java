package rxjava1.oldgoat5.com.rxjava1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/*********************************************************************
 * http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/
 *********************************************************************/
public class MainActivity extends AppCompatActivity {

    private TextView consoleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        printHelloWorld();
        printHelloWorldSimpleCode();
        printHelloWorldByChainingMethodCalls();
        printHelloWorldWithLambdas();

        //Transformations using Operators
        appendWithMap();
        appendWithMapWithLambdas();

        //map() doesn't have to emit items of the same type as the source Observable!
        printHashWithMap();
        printHashWithMapWithLambdas();

        //Key idea #1, Observable and Subscriber can do anything.

        //Your Observable could be a database query, the Subscriber taking the results and
        // displaying them on the screen. Your Observable could be a click on the screen,
        // the Subscriber reacting to it. Your Observable could be a stream of bytes read from
        // the internet, the Subscriber could write it to the disk.

        //Key idea #2, Observable and Subscriber are independent of the transformation steps in
        //between them.

        /*************************************************************
         * Part 2: Messing with more operators.
         *************************************************************/
        consoleTextView.append("\n\n Part 2: More Operators.\n\n");

        //Observable.from() takes a collection of items and emits each of them one at a time.
        printListWithFrom();

        //Observable.flatMap() takes the emissions of one Observable and returns the emissions of
        //another Observable to take its place.

        //Key idea 3, the new Observable is what the Subscribed sees.  It doesn't receive
        //a List<String>, it gets a series of individual Strings as returned by Observable.from().


        /*************************************************************
         * Part 3: Error Handling and Concurrency.
         *************************************************************/
        consoleTextView.append("\n\nPart 3: Error Handling and Concurrency");

        printComplete();
        printWithException(); //onError() will be called ANYTIME an Exception is thrown.


    }

    private void initViews() {
        consoleTextView = (TextView) findViewById(R.id.main_activity_console_text_view);
    }

    private void printHelloWorld() {

        Observable<String> observable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("Hello, world!");
                        subscriber.onCompleted();
                    }
                }
        ); //this emits "Hello, World!" then completes.

        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onNext(String s) {
                consoleTextView.setText(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        //now that we have the observable and the subscriber, hook them up with subscribe()

        observable.subscribe(subscriber); // "subscribe given subscriber to this observable"
    }

    /*****************************************************************
     * Use RxJava shortcuts to simplify printing "Hello, World!".
     *****************************************************************/
    private void printHelloWorldSimpleCode() {

        //Observable.just() just emits a single item and then completes, like the previous code does
        Observable<String> observable = Observable.just("\nHello, World! (using simple code)");

        //In this case we don't care about Subscriber.onError() or Subscriber.onComplete(), so
        //instead we can use a simpler class called Action1 to define what to do onNext().
        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                consoleTextView.append(s);
            }
        };


        //Actions can define each part of a Subscriber.  If you do Observable.subscribe(), you can
        //have 1, 2, or 3 Action parameters take the place of onNext(), onError(), and onComplete().

        observable.subscribe(onNextAction); //or observable.subscribe(onNextAction, onErrorAction,
        // onCompleteAction);
    }

    private void printHelloWorldByChainingMethodCalls() {
        Observable.just("\nHello, World! (By chaining)")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        consoleTextView.append(s);
                    }
                });
    }

    private void printHelloWorldWithLambdas() {
        Observable.just("\nHello, World! (With Lambdas)")
                .subscribe(s -> consoleTextView.append(s));
    }

    private void appendWithMap() {
        Observable.just("\nHello, World! (Append with Map)")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + "- string appended";
                    }
                })
                .subscribe(s -> consoleTextView.append(s));
    }

    private void appendWithMapWithLambdas() {
        Observable.just("\nHello, World! (Append with Map with lambdas)")
                .map(s -> s + " - string appended").subscribe(s -> consoleTextView.append(s));
    }

    private void printHashWithMap() {
        consoleTextView.append("\nHello, World! (print hash with map) ");
        Observable.just("\nHello, World! (print hash with map) ")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .subscribe(i -> consoleTextView.append("\n" + Integer.toString(i)));
    }

    private void printHashWithMapWithLambdas() {
        consoleTextView.append("\nHello, World! (print hash with map with lambdas)");
        Observable.just("\nHello, World! (print hash with map with lambdas)")
                .map(s -> "\n" + s.hashCode())
                .subscribe(s -> consoleTextView.append(s));
    }

    //Part 2 methods

    /*****************************************************************
     * Takes a collection and emits each item one at a time.
     *****************************************************************/
    private void printListWithFrom() {
        consoleTextView.append("Print list using Observable.from() (from a collection, emit 1 at " +
                "a time): ");
        Observable.from(new String[]{"url1 ", "url2 ", "url3 "})
                .subscribe(url -> consoleTextView.append(url));
    }

    private void printComplete() {
        Observable.just("\nHello, World! (with Complete)")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        consoleTextView.append("\nCompleted.");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        consoleTextView.append(s);
                    }
                });
    }

    private void printWithException() {
        consoleTextView.append("\nHello, World! (with Exception)");
        final int[] counter = {0};
        Observable.from(new String[]{"\nHello, World! (with Exception)", "", " ", "", "", "", ""})
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        consoleTextView.append("\nCompleted.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        consoleTextView.append(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        if (counter[0] == 5) {
                            throw new RuntimeException("\nEXCEPTION HAS BEEN THROWN");
                        } else {
                            consoleTextView.append("\ncounting...");
                            counter[0]++;
                        }
                    }
                });
    }

}
