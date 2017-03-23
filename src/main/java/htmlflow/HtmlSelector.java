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
