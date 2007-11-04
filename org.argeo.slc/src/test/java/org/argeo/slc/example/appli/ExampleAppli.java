package org.argeo.slc.example.appli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExampleAppli {
	private int skipFreq = 2;

	public void filter(String[] args) {
		if (args.length < 2) {
			throw new RuntimeException(
					"Not enough arguments. Usage: <inpuit file> <output file>");
		}
		String input = args[0];
		String output = args[1];
		if (args.length > 2) {
			skipFreq = Integer.parseInt(args[2]);
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(input));
			FileWriter out = new FileWriter(output);
			int count = 0;
			String line;
			while ((line = in.readLine()) != null) {
				if (count % skipFreq != 0) {
					out.write(line);
					out.write("\n");
				}
				count++;
			}
			out.close();
			in.close();
		} catch (IOException e) {
			throw new RuntimeException("Appli failed", e);
		}
	}

	public void setSkipFreq(int skipFreq) {
		this.skipFreq = skipFreq;
	}

}
