package htmlflow.attribute;

/**
 * 
 * @author Mikael KROK 
 *
 */
public class AttrGeneric extends AbstractAttribute {

  private String name;
  
  public AttrGeneric(String name, String value) {
    this.name = name;
    setValue(value);
  }
  
  @Override
  public String getName() {
    return name;
  }
}
