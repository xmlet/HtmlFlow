package htmlflow;

import java.io.PrintStream;

@FunctionalInterface
public interface ModelBinder<T, V>{
	V bind(T model);
}
