package app;

import htmlflow.HtmlView;
import htmlflow.ModelBinder;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;
import static java.lang.System.in;

import model.Priority;
import model.Status;
import model.Task;

public class Program {
	public static void main(String[] args) throws IOException {

		TestTaskToHtml.testTasks();
		//
		// run command shell
		//
		/*
		Scanner cin = new Scanner(in);
		out.println("****** Commmand shell application *****");
		while(true){
			out.print("> ");
			out.flush();
			String inLine = cin.nextLine();
			if(inLine.startsWith("new ")){

			}
		}
		 */
	}
}
