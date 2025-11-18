package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;

import javax.swing.SwingUtilities;
import java.io.*;
import java.util.*;

/**
 * Main application class for the Clevis vector graphics tool.
 * Handles command-line interface, command parsing, and logging.
 * Implements REQ1 (logging to HTML and TXT files) and REQ15 (quit command).
 */
public class Application {

    private static List<String> commandLog = new ArrayList<>();
    private static int commandIndex = 0;

    /**
     * Main entry point for the Clevis application.
     * Parses command-line arguments for log file paths and optional GUI mode.
     *
     * @param args Command-line arguments: -html <htmlFile> -txt <txtFile> [-gui]
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // Check if GUI mode is requested (BON1)
        boolean useGui = false;
        for (String arg : args) {
            if ("-gui".equalsIgnoreCase(arg)) {
                useGui = true;
                break;
            }
        }

        // Parse command-line arguments for log file paths (REQ1)
        if (args.length < 4) {
            System.out.println("Usage: java hk.edu.polyu.comp.comp2021.clevis.Application -html <htmlFile> -txt <txtFile> [-gui]");
            return;
        }

        String htmlPath = null;
        String txtPath = null;

        // Parse arguments
        for (int i = 0; i < args.length - 1; i++) {
            if ("-html".equalsIgnoreCase(args[i])) {
                htmlPath = args[i + 1];
            } else if ("-txt".equalsIgnoreCase(args[i])) {
                txtPath = args[i + 1];
            }
        }

        if (htmlPath == null || txtPath == null) {
            System.out.println("Error: Both -html and -txt arguments are required.");
            System.out.println("Usage: java hk.edu.polyu.comp.comp2021.clevis.Application -html <htmlFile> -txt <txtFile> [-gui]");
            return;
        }

        // Initialize the Clevis model
        Clevis clevis = new Clevis();
        ViewerFrame viewer = null;

        // Initialize GUI if requested (BON1)
        if (useGui) {
            ViewerFrame vf = new ViewerFrame(clevis);
            viewer = vf;
            final ViewerFrame finalViewer = viewer;
            SwingUtilities.invokeLater(() -> finalViewer.setVisible(true));
        }

        // Display available commands
        System.out.println("=== Clevis Vector Graphics Tool ===");
        System.out.println("Available commands:");
        System.out.println("  rectangle <name> <x> <y> <width> <height>");
        System.out.println("  line <name> <x1> <y1> <x2> <y2>");
        System.out.println("  circle <name> <cx> <cy> <radius>");
        System.out.println("  square <name> <x> <y> <sideLength>");
        System.out.println("  group <groupName> <shape1> <shape2> ...");
        System.out.println("  ungroup <groupName>");
        System.out.println("  delete <shapeName>");
        System.out.println("  boundingbox <shapeName>");
        System.out.println("  move <shapeName> <dx> <dy>");
        System.out.println("  shapeAt <x> <y>");
        System.out.println("  intersect <shape1> <shape2>");
        System.out.println("  list <shapeName>");
        System.out.println("  listAll");
        System.out.println("  quit");
        System.out.println("=====================================\n");

        final ViewerFrame finalViewer = viewer;

        // Main command processing loop
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            System.out.print("> ");

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    System.out.print("> ");
                    continue;
                }

                // Log the command (REQ1)
                commandIndex++;
                commandLog.add(line);

                // Parse and execute command
                String[] tokens = line.split("\\s+");
                String command = tokens[0].toLowerCase(Locale.ROOT);

                try {
                    boolean shouldRefreshGui = false;

                    switch (command) {
                        case "rectangle": // REQ2
                            if (tokens.length != 6) {
                                System.out.println("Error: Usage: rectangle <name> <x> <y> <width> <height>");
                                break;
                            }
                            try {
                                String name = tokens[1];
                                double x = Double.parseDouble(tokens[2]);
                                double y = Double.parseDouble(tokens[3]);
                                double w = Double.parseDouble(tokens[4]);
                                double h = Double.parseDouble(tokens[5]);
                                clevis.rectangle(name, x, y, w, h);
                                System.out.println("Rectangle '" + name + "' created successfully.");
                                shouldRefreshGui = true;
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid number format. Please enter valid numeric values.");
                            }
                            break;

                        case "line": // REQ3
                            if (tokens.length != 6) {
                                System.out.println("Error: Usage: line <name> <x1> <y1> <x2> <y2>");
                                break;
                            }
                            try {
                                String name = tokens[1];
                                double x1 = Double.parseDouble(tokens[2]);
                                double y1 = Double.parseDouble(tokens[3]);
                                double x2 = Double.parseDouble(tokens[4]);
                                double y2 = Double.parseDouble(tokens[5]);
                                clevis.line(name, x1, y1, x2, y2);
                                System.out.println("Line '" + name + "' created successfully.");
                                shouldRefreshGui = true;
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid number format. Please enter valid numeric values.");
                            }
                            break;

                        case "circle": // REQ4
                            if (tokens.length != 5) {
                                System.out.println("Error: Usage: circle <name> <cx> <cy> <radius>");
                                break;
                            }
                            try {
                                String name = tokens[1];
                                double cx = Double.parseDouble(tokens[2]);
                                double cy = Double.parseDouble(tokens[3]);
                                double r = Double.parseDouble(tokens[4]);
                                clevis.circle(name, cx, cy, r);
                                System.out.println("Circle '" + name + "' created successfully.");
                                shouldRefreshGui = true;
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid number format. Please enter valid numeric values.");
                            }
                            break;

                        case "square": // REQ5
                            if (tokens.length != 5) {
                                System.out.println("Error: Usage: square <name> <x> <y> <sideLength>");
                                break;
                            }
                            try {
                                String name = tokens[1];
                                double x = Double.parseDouble(tokens[2]);
                                double y = Double.parseDouble(tokens[3]);
                                double s = Double.parseDouble(tokens[4]);
                                clevis.square(name, x, y, s);
                                System.out.println("Square '" + name + "' created successfully.");
                                shouldRefreshGui = true;
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid number format. Please enter valid numeric values.");
                            }
                            break;

                        case "group": // REQ6
                            if (tokens.length < 3) {
                                System.out.println("Error: Usage: group <groupName> <shape1> <shape2> ...");
                                break;
                            }
                            try {
                                String groupName = tokens[1];
                                List<String> shapeNames = new ArrayList<>();
                                for (int i = 2; i < tokens.length; i++) {
                                    shapeNames.add(tokens[i]);
                                }
                                clevis.group(groupName, shapeNames);
                                System.out.println("Group '" + groupName + "' created successfully.");
                                shouldRefreshGui = true;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "ungroup": // REQ7
                            if (tokens.length != 2) {
                                System.out.println("Error: Usage: ungroup <groupName>");
                                break;
                            }
                            try {
                                String groupName = tokens[1];
                                clevis.ungroup(groupName);
                                System.out.println("Group '" + groupName + "' ungrouped successfully.");
                                shouldRefreshGui = true;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "delete": // REQ8
                            if (tokens.length != 2) {
                                System.out.println("Error: Usage: delete <shapeName>");
                                break;
                            }
                            try {
                                String shapeName = tokens[1];
                                clevis.deleteShape(shapeName);
                                System.out.println("Shape '" + shapeName + "' deleted successfully.");
                                shouldRefreshGui = true;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "boundingbox": // REQ9
                            if (tokens.length != 2) {
                                System.out.println("Error: Usage: boundingbox <shapeName>");
                                break;
                            }
                            try {
                                String shapeName = tokens[1];
                                BoundingBox bbox = clevis.boundingBox(shapeName);
                                System.out.println(bbox.toString());
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "move": // REQ10
                            if (tokens.length != 4) {
                                System.out.println("Error: Usage: move <shapeName> <dx> <dy>");
                                break;
                            }
                            try {
                                String shapeName = tokens[1];
                                double dx = Double.parseDouble(tokens[2]);
                                double dy = Double.parseDouble(tokens[3]);
                                clevis.move(shapeName, dx, dy);
                                System.out.printf("Shape '%s' moved by (%.2f, %.2f).\n", shapeName, dx, dy);
                                shouldRefreshGui = true;
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid number format. Please enter valid numeric values.");
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "shapeat": // REQ11
                            if (tokens.length != 3) {
                                System.out.println("Error: Usage: shapeAt <x> <y>");
                                break;
                            }
                            try {
                                double x = Double.parseDouble(tokens[1]);
                                double y = Double.parseDouble(tokens[2]);
                                String foundShape = clevis.shapeAt(x, y);
                                if (foundShape != null) {
                                    System.out.println("Shape at (" + x + ", " + y + "): " + foundShape);
                                } else {
                                    System.out.println("No shape found at (" + x + ", " + y + ")");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid number format. Please enter valid numeric values.");
                            }
                            break;

                        case "intersect": // REQ12
                            if (tokens.length != 3) {
                                System.out.println("Error: Usage: intersect <shape1> <shape2>");
                                break;
                            }
                            try {
                                String shape1 = tokens[1];
                                String shape2 = tokens[2];
                                boolean doIntersect = clevis.intersect(shape1, shape2);
                                System.out.println("Shapes '" + shape1 + "' and '" + shape2 + "' " +
                                        (doIntersect ? "intersect" : "do not intersect") + ".");
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "list": // REQ13
                            if (tokens.length != 2) {
                                System.out.println("Error: Usage: list <shapeName>");
                                break;
                            }
                            try {
                                String shapeName = tokens[1];
                                String info = clevis.list(shapeName);
                                System.out.println(info);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "listall": // REQ14
                            if (tokens.length != 1) {
                                System.out.println("Error: Usage: listAll");
                                break;
                            }
                            String listing = clevis.listAll();
                            if (listing.isEmpty()) {
                                System.out.println("No shapes have been created yet.");
                            } else {
                                System.out.println(listing);
                            }
                            break;

                        case "quit": // REQ15
                            System.out.println("Saving logs and exiting...");
                            saveLogs(htmlPath, txtPath);
                            System.out.println("Thank you for using Clevis. Goodbye!");
                            if (finalViewer != null) {
                                finalViewer.dispose();
                            }
                            return;

                        default:
                            System.out.println("Error: Unknown command '" + command + "'. Type a valid command.");
                    }

                    // Refresh GUI if needed (BON1)
                    if (shouldRefreshGui && finalViewer != null) {
                        SwingUtilities.invokeLater(finalViewer::refresh);
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error: An unexpected error occurred: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.print("> ");
            }
        } catch (IOException ioe) {
            System.err.println("I/O Error: " + ioe.getMessage());
            ioe.printStackTrace();
        } finally {
            // Ensure logs are saved even if there's an error
            saveLogs(htmlPath, txtPath);
        }
    }

    /**
     * Saves command logs to both HTML and TXT files (REQ1).
     *
     * @param htmlPath Path to the HTML log file
     * @param txtPath Path to the TXT log file
     */
    private static void saveLogs(String htmlPath, String txtPath) {
        saveTxtLog(txtPath);
        saveHtmlLog(htmlPath);
    }

    /**
     * Saves command log to a plain text file (REQ1).
     * Each line contains one command in execution order.
     *
     * @param txtPath Path to the TXT log file
     */
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

    /**
     * Saves command log to an HTML file (REQ1).
     * Commands are recorded in a table with operation index and command columns.
     *
     * @param htmlPath Path to the HTML log file
     */
    private static void saveHtmlLog(String htmlPath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(htmlPath))) {
            pw.println("<!DOCTYPE html>");
            pw.println("<html>");
            pw.println("<head>");
            pw.println("    <meta charset=\"UTF-8\">");
            pw.println("    <title>Clevis Command Log</title>");
            pw.println("    <style>");
            pw.println("        body { font-family: Arial, sans-serif; margin: 20px; }");
            pw.println("        h2 { color: #333; }");
            pw.println("        table { border-collapse: collapse; width: 100%; max-width: 800px; }");
            pw.println("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            pw.println("        th { background-color: #4CAF50; color: white; }");
            pw.println("        tr:nth-child(even) { background-color: #f2f2f2; }");
            pw.println("    </style>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("    <h2>Clevis Session Command Log</h2>");
            pw.println("    <table>");
            pw.println("        <tr><th>Operation Index</th><th>Operation Command</th></tr>");

            for (int i = 0; i < commandLog.size(); i++) {
                pw.printf("        <tr><td>%d</td><td>%s</td></tr>%n",
                        i + 1, escapeHtml(commandLog.get(i)));
            }

            pw.println("    </table>");
            pw.println("</body>");
            pw.println("</html>");

            System.out.println("HTML log saved to: " + htmlPath);
        } catch (IOException e) {
            System.err.println("Error saving HTML log: " + e.getMessage());
        }
    }

    /**
     * Escapes HTML special characters to prevent HTML injection.
     *
     * @param text The text to escape
     * @return HTML-safe text
     */
    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
