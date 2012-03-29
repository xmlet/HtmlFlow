package htmlflow;

public interface HtmlWriter<T>{
	String write(int depth, T model);
}
