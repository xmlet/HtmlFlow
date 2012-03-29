package htmlflow;

import java.io.PrintStream;

public interface ModelBinder<T>{
	void bind(PrintStream out, T model);
}
