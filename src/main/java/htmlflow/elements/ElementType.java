/*
 * MIT License
 *
 * Copyright (c) 2015-16, Mikael KROK
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package htmlflow.elements;


/**
 * @author Mikael KROK
 *         created on 8-10-2015
 */
public enum ElementType {
    A,
    ABBR,
    ACRONYM,
    ADDRESS,
    AREA,
    B,
    BASE,
    BDO,
    BIG,
    BLOCKQUOTE,
    BODY,
    BR,
    BUTTON,
    CAPTION,
    CITE,
    CODE,
    COL,
    COLGROUP,
    DD,
    DEL,
    DFN,
    DIV,
    DL,
    DOCTYPE,
    DT,
    EM,
    FIELDSET,
    FORM,
    H1, H2, H3, H4, H5, H6,
    HEAD,
    HTML,
    HR,
    I,
    IMG,
    INPUT,
    INS,
    KBD,
    LABEL,
    LEGEND,
    LI,
    LINK,
    MAP,
    META,
    NOSCRIPT,
    OBJECT,
    OL,
    OPTGROUP,
    OPTION,
    P,
    PARAM,
    PRE,
    Q,
    SAMP,
    SCRIPT,
    SELECT,
    SMALL,
    SPAN,
    STRONG,
    STYLE,
    SUB,
    SUP,
    TABLE,
    TBODY,
    TD,
    TEXTAREA,
    TFOOT,
    TH,
    THEAD,
    TITLE,
    TR,
    TT,
    UL,
    VAR
    ;
  
  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}