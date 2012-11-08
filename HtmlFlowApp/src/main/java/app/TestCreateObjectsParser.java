package app;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

interface Expression<T>{
	T parse() throws Exception;
}
class ExpNew implements Expression<Object>{
	public Object parse(){return null;}
}
class ExpList implements Expression<Object>{
	public Object parse(){return null;}
}
class ExpLiteral implements Expression<Object>{
	final String arg;
	public Object parse() throws Exception{
		int idx = arg.indexOf(':');
		if(idx <= 0) throw new IllegalArgumentException("Constant expression without : character!");
		String className = arg.substring(0, idx);
		Class c = Class.forName(className); 
		Method m = c.getMethod("valueOf", String.class);
		return m.invoke(null, arg.substring(idx + 1));	
	}
	public ExpLiteral(String arg) {
		this.arg = arg;
	}	
}

class ExpRefObject implements Expression<Object>{
	public ExpRefObject(String string) {}
	public Object parse(){return null;}
}
class ExpClassName implements Expression<String>{
	public ExpClassName(String string) {}
	public String parse(){return null;}
}

public class TestCreateObjectsParser {
	private Map<Long, Object> objsCache = new HashMap<Long, Object>();
	private Stack<Expression> eval = new Stack<Expression>();
	/*=====================================================
	=======================================================*/
	static boolean isLiteral(String arg){
		return arg.contains(":");
	}
	static boolean isObject(String arg){
		return arg.startsWith("hash:");
	}
	static boolean isNewObject(String arg){
		return arg.equals("new");
	}
	static boolean isNewList(String arg){
		return arg.equals("list");
	}

	/*=====================================================
	=======================================================*/
	Object parseObjects(String []args) {
		Object res = null;
		for (int i = 0; i < args.length; i++) {
			if(isLiteral(args[i])){
				eval.push(new ExpLiteral(args[i]));
			}else if(isObject(args[i])){
				eval.push(new ExpRefObject(args[i]));
			} else if(isNewObject(args[i])){
				eval.push(new ExpNew());
			} else if(isNewList(args[i])){
				eval.push(new ExpList());
			} else {
				eval.push(new ExpClassName(args[i]));
			}
		}
		return res;
	}
}
