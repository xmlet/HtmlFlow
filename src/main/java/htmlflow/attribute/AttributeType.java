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
  HREF,
  ID,
  LANG,
  LINK,
  REL,
  SPELLCHECK,
  STYLE,
  TABINDEX,
  TITLE,
  TRANSLATE,
    TYPE, ACTION, METHOD, ENCTYPE,
    ;
  
  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}