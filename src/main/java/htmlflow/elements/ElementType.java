/*
 * Copyright (c) 2016, Mikael KROK
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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