package com.profitsoft.statistics.io;

import com.profitsoft.statistics.util.Attribute;
import com.profitsoft.statistics.util.XmlEscaper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

/**
 * Writes aggregated statistics to an XML file sorted by count descending.
 */
public class StatisticsXmlWriter {

  public Path write(Path outputFile, Attribute attribute, Map<String, Long> statistics) {
    try (java.io.BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
      writer.write("<statistics attribute=\"");
      writer.write(XmlEscaper.escape(attribute.inputName()));
      writer.write("\">\n");

      statistics.entrySet().stream()
          .sorted(Comparator
              .<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed()
              .thenComparing(Map.Entry::getKey))
          .forEach(entry -> writeItem(writer, entry));

      writer.write("</statistics>\n");
      return outputFile;
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write statistics to " + outputFile, e);
    }
  }

  private void writeItem(java.io.Writer writer, Map.Entry<String, Long> entry) {
    try {
      writer.write("  <item>\n");
      writer.write("    <value>");
      writer.write(XmlEscaper.escape(entry.getKey()));
      writer.write("</value>\n");
      writer.write("    <count>");
      writer.write(String.valueOf(entry.getValue()));
      writer.write("</count>\n");
      writer.write("  </item>\n");
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write item: " + entry, e);
    }
  }
}
