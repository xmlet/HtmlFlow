/*
 * Copyright (c) 2016,  Mikael KROK
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

package htmlflow;

/**
 *  Allows an element to define its own class, id attribute and any generic attribute
 *
 *  @param <T> The type derived of HtmlSelector
 *
 *  @author Mikael KROK
 *  created on 07-10-2015
 */
public interface HtmlSelector<T>{
    /**
     * Get the class attribute if the object has one
     * @return
     */
    String getClassAttribute();

    /**
     * Get the id attribute if the object has one
	 */
	String getIdAttribute();

    /**
     * Set the class attribute
     * @return
     */
    T classAttr(String classAttribute);

    /**
     * Set the id attribute
     */
	T idAttr(String idAttribute);
}
