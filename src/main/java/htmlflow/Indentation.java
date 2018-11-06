/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
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

import static htmlflow.Tags.FINISH_TAG;

/**
 * @author Luís Duarte created on 9-09-2018
 */
class Indentation {

    private static final int MAX_TABS = 1000;
    private static final char NEWLINE = '\n';
    private static final char TAB = '\t';
    private static final String[] tabs = createTabs(MAX_TABS);
    private static final String[] closedTabs = createClosedTabs(MAX_TABS);

    private Indentation(){ }

    private static String[] createTabs(int tabsMax){
        String[] tabs = new String[tabsMax];

        for (int i = 0; i < tabsMax; i++) {
            char[] newTab = new char[i + 1];
            newTab[0] = NEWLINE;

            tabs[i] = new String(newTab).replace('\0', TAB);
        }

        return tabs;
    }

    private static String[] createClosedTabs(int tabsMax){
        String[] closedTabs = new String[tabsMax];

        for (int i = 0; i < tabsMax; i++) {

            char[] newClosedTab = new char[i + 2];
            newClosedTab[0] = FINISH_TAG;
            newClosedTab[1] = NEWLINE;

            closedTabs[i] = new String(newClosedTab).replace('\0', TAB);
        }

        return closedTabs;
    }

    public static String tabs(int depth){
        return tabs[depth];
    }

    public static String closedTabs(int depth) {
        return closedTabs[depth];
    }
}
