package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Entry point for the Clevis application.
 * Supports both CLI and GUI modes depending on the presence of the "-gui" flag.
 */
public class Application {

    private static final List<String> commandLog = new ArrayList<>();

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        ArgsConfig config = ArgsConfig.parse(args);
        if (!config.isValid()) {
            printUsage();
            return;
        }

        Clevis clevis = new Clevis();

        ViewerFrame viewer = null;
        if (config.useGui) {
            viewer = new ViewerFrame(clevis);
            ViewerFrame finalViewer = viewer;
            SwingUtilities.invokeLater(() -> finalViewer.setVisible(true));
        }

        runCommandLoop(clevis, config, viewer);
    }

    private static void runCommandLoop(Clevis clevis, ArgsConfig config, ViewerFrame viewer) {
        System.out.println("Commands: rectangle | line | circle | square | delete | move | list | listall | quit");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                commandLog.add(line);

                String[] tokens = line.split("\\s+");
                String cmd = tokens[0].toLowerCase(Locale.ROOT);

                try {
                    switch (cmd) {
                        case "rectangle":
                            if (tokens.length != 6) {
                                System.out.println("Usage: rectangle n x y w h");
                                break;
                            }
                            clevis.rectangle(
                                    tokens[1],
                                    Double.parseDouble(tokens[2]),
                                    Double.parseDouble(tokens[3]),
                                    Double.parseDouble(tokens[4]),
                                    Double.parseDouble(tokens[5]));
                            System.out.println("OK rectangle " + tokens[1]);
                            refreshViewer(viewer);
                            break;

                        case "line":
                            if (tokens.length != 6) {
                                System.out.println("Usage: line n x1 y1 x2 y2");
                                break;
                            }
                            clevis.line(
                                    tokens[1],
                                    Double.parseDouble(tokens[2]),
                                    Double.parseDouble(tokens[3]),
                                    Double.parseDouble(tokens[4]),
                                    Double.parseDouble(tokens[5]));
                            System.out.println("OK line " + tokens[1]);
                            refreshViewer(viewer);
                            break;

                        case "circle":
                            if (tokens.length != 5) {
                                System.out.println("Usage: circle n cx cy r");
                                break;
                            }
                            clevis.circle(
                                    tokens[1],
                                    Double.parseDouble(tokens[2]),
                                    Double.parseDouble(tokens[3]),
                                    Double.parseDouble(tokens[4]));
                            System.out.println("OK circle " + tokens[1]);
                            refreshViewer(viewer);
                            break;

                        case "square":
                            if (tokens.length != 5) {
                                System.out.println("Usage: square n x y s");
                                break;
                            }
                            clevis.square(
                                    tokens[1],
                                    Double.parseDouble(tokens[2]),
                                    Double.parseDouble(tokens[3]),
                                    Double.parseDouble(tokens[4]));
                            System.out.println("OK square " + tokens[1]);
                            refreshViewer(viewer);
                            break;

                        case "delete":
                            if (tokens.length != 2) {
                                System.out.println("Usage: delete <shapeName>");
                                break;
                            }
                            clevis.deleteShape(tokens[1]);
                            System.out.println("Deleted shape: " + tokens[1]);
                            refreshViewer(viewer);
                            break;

                        case "move":
                            if (tokens.length != 4) {
                                System.out.println("Usage: move n dx dy");
                                break;
                            }
                            double dx = Double.parseDouble(tokens[2]);
                            double dy = Double.parseDouble(tokens[3]);
                            clevis.move(tokens[1], dx, dy);
                            System.out.printf("> Moved %s by (%.2f, %.2f)%n", tokens[1], dx, dy);
                            refreshViewer(viewer);
                            break;

                        case "list":
                            if (tokens.length != 2) {
                                System.out.println("Usage: list n");
                                break;
                            }
                            System.out.println(clevis.list(tokens[1]));
                            break;

                        case "listall":
                            if (tokens.length != 1) {
                                System.out.println("Usage: listall");
                                break;
                            }
                            String listing = clevis.listAll();
                            if (listing.isEmpty()) {
                                System.out.println("No shapes to list.");
                            } else {
                                System.out.println(listing);
                            }
                            break;

                        case "quit":
                            System.out.println("Bye, see you.");
                            if (config.shouldSaveLogs()) {
                                saveLogs(config.htmlPath, config.txtPath);
                            }
                            if (viewer != null) {
                                SwingUtilities.invokeLater(viewer::dispose);
                            }
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

    private static void refreshViewer(ViewerFrame viewer) {
        if (viewer != null) {
            SwingUtilities.invokeLater(viewer::refresh);
        }
    }

    private static void saveLogs(String htmlPath, String txtPath) {
        saveTxtLog(txtPath);
        saveHtmlLog(htmlPath);
    }

    private static void saveTxtLog(String txtPath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(txtPath))) {
            for (String cmd : commandLog) {
                pw.println(cmd);
            }
            System.out.println("TXT log saved to: " + txtPath);
        } catch (IOException e) {
            System.err.println("Error saving TXT log: " + e.getMessage());
        }
    }

    private static void saveHtmlLog(String htmlPath) {
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

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  CLI (no logs):   java -cp out hk.edu.polyu.comp.comp2021.clevis.Application");
        System.out.println("  CLI with logs:   java -cp out hk.edu.polyu.comp.comp2021.clevis.Application -html <htmlFile> -txt <txtFile>");
        System.out.println("  GUI (requires logs):");
        System.out.println("                     java -cp out hk.edu.polyu.comp.comp2021.clevis.Application -gui -html <htmlFile> -txt <txtFile>");
    }

    /**
     * Simple argument holder/validator.
     */
    private static final class ArgsConfig {
        final boolean useGui;
        final String htmlPath;
        final String txtPath;
        private final boolean valid;

        private ArgsConfig(boolean useGui, String htmlPath, String txtPath, boolean valid) {
            this.useGui = useGui;
            this.htmlPath = htmlPath;
            this.txtPath = txtPath;
            this.valid = valid;
        }

        static ArgsConfig parse(String[] args) {
            boolean useGui = false;
            String htmlPath = null;
            String txtPath = null;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg.toLowerCase(Locale.ROOT)) {
                    case "-gui":
                        useGui = true;
                        break;
                    case "-html":
                        if (i + 1 < args.length) {
                            htmlPath = args[++i];
                        } else {
                            return invalid();
                        }
                        break;
                    case "-txt":
                        if (i + 1 < args.length) {
                            txtPath = args[++i];
                        } else {
                            return invalid();
                        }
                        break;
                    default:
                        return invalid();
                }
            }

            // ensure html/txt are both present or both absent
            if ((htmlPath == null) != (txtPath == null)) {
                return invalid();
            }

            boolean logsRequested = htmlPath != null;

            // GUI requires both log files
            if (useGui && !logsRequested) {
                return invalid();
            }

            return new ArgsConfig(useGui, htmlPath, txtPath, true);
        }

        boolean isValid() {
            return valid;
        }

        boolean shouldSaveLogs() {
            return htmlPath != null && txtPath != null;
        }

        private static ArgsConfig invalid() {
            return new ArgsConfig(false, null, null, false);
        }
    }
}