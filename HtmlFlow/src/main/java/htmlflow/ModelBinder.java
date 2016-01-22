package htmlflow;

import java.io.PrintStream;

@FunctionalInterface
public interface ModelBinder<T>{
	void bind(PrintStream out, T model);
}
