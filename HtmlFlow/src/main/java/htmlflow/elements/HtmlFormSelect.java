package htmlflow.elements;

import htmlflow.HtmlWriter;

import static htmlflow.HtmlWriterComposite.println;
import static htmlflow.HtmlWriterComposite.tabs;

public class HtmlFormSelect implements HtmlWriter<Object>{
	final String name;
	final String[] options;
	public HtmlFormSelect(String name, String...options) {
		this.name = name;
		this.options = options;
	}
	@Override
	public String write(int depth, Object model) {
		String res = println("<select name=\""+ name+ "\">") + tabs(++depth);
		for (String op : options) {
			res += println("<option>" + op + "</option>") + tabs(depth);
		}
		return res + tabs(--depth) + println("</select>") + tabs(depth);
	}
}
