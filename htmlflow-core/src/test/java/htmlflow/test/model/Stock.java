/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt)
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

package htmlflow.test.model;

import java.util.ArrayList;
import java.util.List;

public class Stock {

    private int index;

    private String name;

    private String name2;

    private String url;

    private String symbol;

    private double price;

    private double change;

    private double ratio;

    public Stock(int index, String name, String name2, String url, String symbol, double price, double change, double ratio) {
        this.index = index;
        this.name = name;
        this.name2 = name2;
        this.url = url;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.ratio = ratio;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return this.name;
    }

    public String getName2() {
        return this.name2;
    }

    public String getUrl() {
        return this.url;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public double getPrice() {
        return this.price;
    }

    public double getChange() {
        return this.change;
    }

    public double getRatio() {
        return this.ratio;
    }

    public static List<Stock> dummy3Items() {
        List<Stock> items = new ArrayList<Stock>();
        items.add(new Stock(1, "Adobe Systems", "Adobe Systems Inc.", "http://www.adobe.com", "ADBE", 39.26, 0.13, 0.33));
        items.add(new Stock(2, "Advanced Micro Devices", "Advanced Micro Devices Inc.", "http://www.amd.com", "AMD",
                16.22, 0.17, 1.06));
        items.add(new Stock(3, "Amazon.com", "Amazon.com Inc", "http://www.amazon.com", "AMZN", 36.85, -0.23, -0.62));
        return items;
    }

    public static List<Stock> other3Items() {
        List<Stock> items = new ArrayList<Stock>();
        items.add(new Stock(8, "Dell", "Dell Corp.", "http://www.dell.com/", "DELL", 23.73, -0.42, -1.74));
        items.add(new Stock(9, "eBay", "eBay Inc.", "http://www.ebay.com", "EBAY", 31.65, -0.8, -2.47));
        items.add(new Stock(10, "Google", "Google Inc.", "http://www.google.com", "GOOG", 495.84, 7.75, 1.59));
        return items;
    }

    public static List<Stock> dummy5Items() {
        List<Stock> items = new ArrayList<Stock>();
        items.add(new Stock(7, "Cisco Systems", "Cisco Systems Inc.", "http://www.cisco.com", "CSCO", 26.35, 0.13, 0.5));
        items.add(new Stock(8, "Dell", "Dell Corp.", "http://www.dell.com/", "DELL", 23.73, -0.42, -1.74));
        items.add(new Stock(9, "eBay", "eBay Inc.", "http://www.ebay.com", "EBAY", 31.65, -0.8, -2.47));
        items.add(new Stock(10, "Google", "Google Inc.", "http://www.google.com", "GOOG", 495.84, 7.75, 1.59));
        items.add(new Stock(11, "Hewlett-Packard", "Hewlett-Packard Co.", "http://www.hp.com", "HPQ", 41.69, -0.02, -0.05));
        return items;
    }
}
