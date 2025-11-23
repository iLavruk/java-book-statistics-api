package com.profitsoft.statistics;

import com.profitsoft.statistics.io.BookJsonParser;
import com.profitsoft.statistics.io.StatisticsXmlWriter;
import com.profitsoft.statistics.service.StatisticsCalculator;
import com.profitsoft.statistics.service.StatisticsService;
import com.profitsoft.statistics.util.Attribute;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {

  public static void main(String[] args) {
    if (args.length < 2 || args.length > 3) {
      printUsage();
      System.exit(1);
    }

    Path directory = Paths.get(args[0]);
    Attribute attribute = Attribute.fromInput(args[1])
        .orElseThrow(() -> new IllegalArgumentException("Unsupported attribute: " + args[1]));
    int threads = parseThreadCount(args.length == 3 ? args[2] : null);

    StatisticsService service = new StatisticsService(
        new StatisticsCalculator(new BookJsonParser()),
        new StatisticsXmlWriter()
    );

    long start = System.currentTimeMillis();
    Path output = service.generateStatistics(directory, attribute, threads);
    long duration = System.currentTimeMillis() - start;

    System.out.println("Statistics saved to: " + output.toAbsolutePath());
    System.out.println("Processed with " + threads + " thread(s) in " + duration + " ms");
  }

  private static void printUsage() {
    System.err.println("Usage: java -jar <jar-file> <directory> <attribute> [threads]");
    System.err.println("Supported attributes: author | year_published | genres");
    System.err.println("Examples:");
    System.err.println("  java -jar app.jar data author");
    System.err.println("  java -jar app.jar data genres 4");
  }

  private static int parseThreadCount(String value) {
    if (value == null || value.isBlank()) {
      return Math.max(1, Runtime.getRuntime().availableProcessors());
    }
    try {
      int parsed = Integer.parseInt(value);
      if (parsed < 1) {
        throw new IllegalArgumentException("Thread count must be >= 1");
      }
      return parsed;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Thread count must be an integer", e);
    }
  }
}
