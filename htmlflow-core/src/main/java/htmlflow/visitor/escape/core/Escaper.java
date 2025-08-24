/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package htmlflow.visitor.escape.core;

/**
 * An abstract class for escaping text to make it safe for HTML output.
 *
 * <p>This class provides a method to escape a given string, which may involve replacing certain
 * characters with their escaped equivalents.
 *
 * <p>Derived and adapted from Guava's Escaper class: <a
 * href="https://github.com/google/guava/blob/master/guava/src/com/google/common/escape/Escaper.java">guava</a>
 *
 * <p>Modified by Arthur Oliveira on 04-08-2025
 *
 * @author Arthur Oliveira
 * @author The Guava Authors
 */
public abstract class Escaper {

    protected Escaper() {}

    /**
     * Escapes the given text to make it safe for HTML output.
     *
     * <p>This method may treat input characters differently depending on the specific escaper
     * implementation.
     *
     * @param code the literal text to escape
     * @return the escaped text form of {@code text}
     */
    public abstract String escape(String code);
}
