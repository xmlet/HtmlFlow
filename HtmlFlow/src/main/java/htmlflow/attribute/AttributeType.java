package htmlflow.attribute;

/**
 * 
 * @author Mikael KROK 
 *
 */
public enum AttributeType {
  ACCESSKEY,
  CLASS,
  CONTENTEDITABLE,
  CONTEXTMENU,
  DATA,
  DIR,
  DRAGGABLE,
  DROPZONE,
  HIDDEN,
  ID,
  LANG,
  SPELLCHECK,
  STYLE,
  TABINDEX,
  TITLE,
  TRANSLATE,
    ;
  
  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}