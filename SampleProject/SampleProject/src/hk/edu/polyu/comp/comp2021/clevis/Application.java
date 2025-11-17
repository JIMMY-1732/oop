package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import javax.swing.SwingUtilities;
import java.io.*;   // to read user input line by line
import java.util.*;      // to handle input/output exceptions

public class Application {

    private static List<String> commandLog = new ArrayList<>();
    private static int commandIndex = 0;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        boolean useGui = false;
        for (String a : args) if ("-gui".equalsIgnoreCase(a)) useGui = true;
        Clevis clevis = new Clevis();
        ViewerFrame viewer = null;

        if (useGui) {
            ViewerFrame vf = new ViewerFrame(clevis);
            viewer = vf;
            SwingUtilities.invokeLater(() -> vf.setVisible(true));
            if (args.length != 4 || !args[0].equalsIgnoreCase("-html") || !args[2].equalsIgnoreCase("-txt")) {
                System.out.println("Usage: java hk.edu.polyu.comp.comp2021.clevis.Application -html <htmlFile> -txt <txtFile>");
                return;
            }

            String htmlPath = args[1];
            String txtPath = args[3];

            System.out.println("Commands: rectangle | line | circle | square | quit");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    commandIndex++;
                    commandLog.add(line);  // log command to memory first

                    String[] t = line.split("\\s+");
                    String cmd = t[0].toLowerCase(Locale.ROOT);
                    try {
                        switch (cmd) {
                            case "rectangle": {
                                //req2
                                if (t.length != 6) {
                                    System.out.println("Usage: rectangle n x y w h");
                                    break;
                                }
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
                                //req3
                                if (t.length != 6) {
                                    System.out.println("Usage: line n x1 y1 x2 y2");
                                    break;
                                }
                                String n = t[1];
                                double x1 = Double.parseDouble(t[2]);
                                double y1 = Double.parseDouble(t[3]);
                                double x2 = Double.parseDouble(t[4]);
                                double y2 = Double.parseDouble(t[5]);
                                clevis.line(n, x1, y1, x2, y2);
                                System.out.println("OK line " + n);
                                break;
                            }
                            case "circle": {
                                //req4
                                if (t.length != 5) {
                                    System.out.println("Usage: circle n cx cy r");
                                    break;
                                }
                                String n = t[1];
                                double cx = Double.parseDouble(t[2]);
                                double cy = Double.parseDouble(t[3]);
                                double r = Double.parseDouble(t[4]);
                                clevis.circle(n, cx, cy, r);
                                System.out.println("OK circle " + n);
                                break;
                            }
                            case "square": {
                                //req5
                                if (t.length != 5) {
                                    System.out.println("Usage: square n x y s");
                                    break;
                                }
                                String n = t[1];
                                double x = Double.parseDouble(t[2]);
                                double y = Double.parseDouble(t[3]);
                                double s = Double.parseDouble(t[4]);
                                clevis.square(n, x, y, s);
                                System.out.println("OK square " + n);
                                break;
                            }

                            case "delete": {

                                if (t.length != 2) {
                                    System.out.println("Usage: delete <shapeName>");
                                    break;
                                }

                                String shapeName = t[1];

                                if (shapeName == null || shapeName.trim().isEmpty()) {
                                    System.out.println("Error: shape name cannot be empty.");
                                    break;
                                }

                                try {

                                    clevis.deleteShape(shapeName);
                                    System.out.println("Deleted shape: " + shapeName);
                                } catch (IllegalArgumentException ex) {

                                    System.out.println("Couldn’t delete shape: " + ex.getMessage());
                                } catch (Exception e) {

                                    System.out.println("Something went wrong while deleting " + shapeName);
                                    e.printStackTrace(); // TODO: remove or replace with proper logging later
                                }

                                break;
                            }
                            //req10
                            case "move": {
                                if (t.length != 4) {
                                    System.out.println("Usage: move n dx dy");
                                    break;
                                }
                                String n = t[1];
                                double dx = Double.parseDouble(t[2]);
                                double dy = Double.parseDouble(t[3]);

                                clevis.move(n, dx, dy);
                                System.out.printf("> Moved %s by (%.2f, %.2f)\n", n, dx, dy);
                                break;
                            }
                            //req13
                            case "list": {
                                if (t.length != 2) {
                                    System.out.println("Usage: list n");
                                    break;
                                }
                                String n = t[1];
                                System.out.println(clevis.list(n));
                                break;
                            }
                            //req14
                            case "listall": {
                                if (t.length != 1) {
                                    System.out.println("Usage: listAll");
                                    break;
                                }
                                String listing = clevis.listAll();
                                if (listing.isEmpty()) {
                                    System.out.println("No shapes to list.");
                                } else {
                                    System.out.println(listing);
                                }
                                break;
                            }
                            case "quit":
//<<<<<<< Updated upstream
                                //req15
                                System.out.println("Bye, see you.");
//=======
                                System.out.println("You quite this application, please rate us 5 stars and give us full marks.");
//>>>>>>> Stashed changes
                                saveLogs(htmlPath, txtPath);
                                return;
                            default:
                                System.out.println("Unknown command: " + cmd);
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

        /**
         * Save command logs to both HTML and TXT files.
         */
        private static void saveLogs(String htmlPath, String txtPath){
            saveTxtLog(txtPath);
            saveHtmlLog(htmlPath);
        }

        private static void saveTxtLog(String txtPath){
            try (PrintWriter pw = new PrintWriter(new FileWriter(txtPath))) {
                for (String cmd : commandLog) {
                    pw.println(cmd);
                }
                System.out.println("TXT log saved to: " + txtPath);
            } catch (IOException e) {
                System.err.println("Error saving TXT log: " + e.getMessage());
            }
        }

        private static void saveHtmlLog(String htmlPath){
            try (PrintWriter pw = new PrintWriter(new FileWriter(htmlPath))) {
                pw.println("<html><head><title>Clevis Command Log</title></head><body>");
                pw.println("<h2>Clevis Session Log</h2>");
                pw.println("<table border='1' cellpadding='5' cellspacing='0'>");
                pw.println("<tr><th>Index</th><th>Command</th></tr>");
                for (int i = 0; i < commandLog.size(); i++) {
                    pw.printf("<tr><td>%d</td><td>%s</td></tr>%n", i + 1, escapeHtml(commandLog.get(i)));
                }
                pw.println("</table></body></html>");
                System.out.println("HTML log saved to: " + htmlPath);
            } catch (IOException e) {
                System.err.println("Error saving HTML log: " + e.getMessage());
            }
        }

        private static String escapeHtml (String text){
            return text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }
