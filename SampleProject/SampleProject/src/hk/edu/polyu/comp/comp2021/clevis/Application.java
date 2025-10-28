package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;

// import tools for safe user input reading and consistent number format
import java.io.BufferedReader;   // to read user input line by line
import java.io.IOException;      // to handle input/output exceptions
import java.io.InputStreamReader;// converts byte stream (System.in) to characters
import java.util.Locale;         // ensures decimal point format is consistent

public class Application {

   public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Clevis clevis = new Clevis();
        System.out.println("Commands: rectangle | line | quit");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] t = line.split("\\s+");
                String cmd = t[0].toLowerCase(Locale.ROOT);
                try {
                    switch (cmd) {
                        case "rectangle": {
                            if (t.length != 6) { System.out.println("Usage: rectangle n x y w h"); break; }
                            String n = t[1];
                            double x = Double.parseDouble(t[2]);
                            double y = Double.parseDouble(t[3]);
                            double w = Double.parseDouble(t[4]);
                            double h = Double.parseDouble(t[5]);
                            clevis.rectangle(n, x, y, w, h);
                            System.out.println("OK rectangle " + n);
                            break;
                        }
                        case "line": {
                            if (t.length != 6) { System.out.println("Usage: line n x1 y1 x2 y2"); break; }
                            String n = t[1];
                            double x1 = Double.parseDouble(t[2]);
                            double y1 = Double.parseDouble(t[3]);
                            double x2 = Double.parseDouble(t[4]);
                            double y2 = Double.parseDouble(t[5]);
                            clevis.line(n, x1, y1, x2, y2);
                            System.out.println("OK line " + n);
                            break;
                        }

                        case "quit": System.out.println("Bye,See you."); return;
                        default: System.out.println("Unknown command: " + cmd);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException ioe) {
            System.err.println("I/O: " + ioe.getMessage());
        }
    }
}
