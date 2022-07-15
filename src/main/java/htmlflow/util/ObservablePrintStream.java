package htmlflow.util;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

public class ObservablePrintStream extends PrintStream {
    
    private final PublishSubject<String> subject;
    private final Observable<String> htmlEmitter;
    
    //concurrency issues?
    private StringBuilder bufferTags = new StringBuilder();
    
    public ObservablePrintStream(OutputStream out) {
        super(out);
        
        this.subject = PublishSubject.create();
        this.htmlEmitter = subject
                .doOnSubscribe(__ -> sendOutBuffer())
                .map(s -> s)
                .doFinally(this::resetBuffer);
    }
    
    private void resetBuffer() {
        this.bufferTags = new StringBuilder();
        System.out.println("Observable has ended, resetting buffer");
    }
    
    private void sendOutBuffer() {
        System.out.println("Someone has subscribed to this observable");
    }
    
    @Override
    public void print(char c) {
        final String text = String.valueOf(c);
        this.subject.onNext(text);
        bufferTags.append(text);
        
        super.print(c);
    }
    
    @Override
    public void print(String s) {
        this.subject.onNext(s);
        bufferTags.append(s);
        
        super.print(s);
    }
    
    public String subString(int staticBLockIndex) {
        return bufferTags.substring(staticBLockIndex);
    }
    
    public int length() {
        return bufferTags.length();
    }
    
    public void complete() {
        this.subject.onComplete();
    }
    
    public Observable<String> getHtmlEmitter() {
        return this.htmlEmitter;
    }
}
