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
  public String printAttribute() {
      if(this.getValue() != null){
          return " "+this.getName()+"=\""+this.getValue()+"\"";
      }
      return "";
  }
}
