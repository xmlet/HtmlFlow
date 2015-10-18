package htmlflow.attribute;

/**
 * 
 * @author Mikael KROK 
 *
 */
public interface Attribute {
  
  void setValue(String value);

  String getValue();
  
  String getName();

  String printAttribute();

}
