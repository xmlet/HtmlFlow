package htmlflow.attribute;

/**
 * 
 * @author Mikael KROK 
 * 
 * abstract way of handling an html attribute
 */
public abstract class AbstractAttribute implements Attribute {
  
  private String value;
  
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  public abstract String getName();

  @Override
  public String getAttribute() {
      if(this.getValue() != null){
          return " "+this.getName()+"=\""+this.getValue()+"\"";
      }
      return "";
  }
}
