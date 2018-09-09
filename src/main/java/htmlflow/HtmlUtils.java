package htmlflow;

import static htmlflow.HtmlTags.FINISH_TAG;

class HtmlUtils {

    private static final char NEWLINE = '\n';

    static String[] createTabs(int tabsMax){
        String[] tabs = new String[tabsMax];

        for (int i = 0; i < tabsMax; i++) {
            char[] newTab = new char[i + 1];
            newTab[0] = NEWLINE;

            tabs[i] = new String(newTab).replace('\0', '\t');
        }

        return tabs;
    }

    static String[] createClosedTabs(int tabsMax){
        String[] closedTabs = new String[tabsMax];

        for (int i = 0; i < tabsMax; i++) {

            char[] newClosedTab = new char[i + 2];
            newClosedTab[0] = FINISH_TAG;
            newClosedTab[1] = NEWLINE;

            closedTabs[i] = new String(newClosedTab).replace('\0', '\t');
        }

        return closedTabs;
    }
}
