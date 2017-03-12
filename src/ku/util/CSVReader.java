package ku.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The class that use for read the CSV file and return it as arrays of the
 * String per line.
 * 
 * @author Triwith Mutitakul
 *
 */
public class CSVReader implements Iterator<String[]> {
	private InputStream input;
	private char delim = ',';
	private BufferedReader breader;
	private String line = "";

	/**
	 * The constructor that use when the user call CSVReader by input is
	 * InputStream.
	 * 
	 * @param input
	 *            the file that user input.
	 */
	public CSVReader(InputStream input) {
		this.input = input;
	}

	/**
	 * The constructor that use when the user call CSVReader by input is File
	 * name or URL.
	 * 
	 * @param name
	 *            is the name of the file.
	 */
	public CSVReader(String name) {
		String URLPATTERN = "^\\w\\w+://\\S+";
		if (name.matches(URLPATTERN)) {
			try {
				input = new URL(name).openStream();
				breader = new BufferedReader(new InputStreamReader(input));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				input = new FileInputStream(name);
				breader = new BufferedReader(new InputStreamReader(input));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The method is use for close the file that user input.
	 */
	public void close() {
		try {
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * The method that use for set the delimeter.
	 * 
	 * @param delim
	 *            the char that user want to set it to delimeter.
	 */
	public void setDelimeter(char delim) {
		this.delim = delim;
	}

	/**
	 * The method that return the delimeter.
	 * 
	 * @return the delimmeter
	 */
	public char getDelimeter() {
		return this.delim;
	}

	/**
	 * The method that use for check the CSV file.return true if it has another
	 * line.
	 * 
	 * @return true if it has another line.return false if it has no line to
	 *         read.
	 */
	@Override
	public boolean hasNext() {
		try {
			while (line.equals("") || line.startsWith("#")) {
				line = breader.readLine();
				if (line == null) {
					return false;
				}
				line = line.trim();
			}
			return true;
		} catch (IOException e) {
			return false;
		}

	}

	/**
	 * The method that use for return the next line of the CSV file in the
	 * arrays of String.
	 * 
	 * @return the array of the words in line.
	 */
	@Override
	public String[] next() {
		if (hasNext()) {
			List<String> temp = new ArrayList<>();
			StringBuilder word = new StringBuilder();
			int countQuot = 0;
			boolean continuos = true;
			for (int x = 0; x < line.length(); x++) {
				char cursor = line.charAt(x);
				if (cursor == '"' && countQuot <= 2) {
					// find have quotes case.
					word.append(cursor);
					countQuot += 1;
					continuos = false;
					if (countQuot == 2) {
						countQuot = 0;
						continuos = true;
					}
				} else if (x == line.length() - 1) {
					// end of line case.
					if (cursor != this.delim) {
						// if it end with another char not delimeter.
						word.append(cursor);
						temp.add(word.toString());
						word = new StringBuilder();
					} else {
						// if it end with delimeter
						temp.add(word.toString());
						temp.add("");
						word = new StringBuilder();
					}
				} else if (cursor == this.delim && continuos) {
					// check and add char in quote case.
					temp.add(word.toString());
					word = new StringBuilder();
				} else {
					// normal case.
					word.append(line.charAt(x));
				}

			}
			String[] boxLine = new String[temp.size()];
			for (int x = 0; x < temp.size(); x++) {
				temp.set(x, temp.get(x).trim().replace("\"", ""));
				boxLine[x] = temp.get(x);
			}
			line = "";
			return boxLine;
		} else {
			throw new NoSuchElementException();
		}

	}

	@Override
	public void remove() {
	}

}
