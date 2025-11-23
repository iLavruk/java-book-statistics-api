package com.profitsoft.statistics.service;

import com.profitsoft.statistics.io.StatisticsXmlWriter;
import com.profitsoft.statistics.util.Attribute;
import java.nio.file.Path;
import java.util.Map;

/**
 * High-level service that orchestrates calculation and persistence.
 */
public class StatisticsService {

  private final StatisticsCalculator calculator;
  private final StatisticsXmlWriter writer;

  public StatisticsService(StatisticsCalculator calculator, StatisticsXmlWriter writer) {
    this.calculator = calculator;
    this.writer = writer;
  }

  public Path generateStatistics(Path directory, Attribute attribute, int threadCount) {
    Map<String, Long> statistics = calculator.calculate(directory, attribute, threadCount);
    Path outputFile = directory.resolve("statistics_by_" + attribute.inputName() + ".xml");
    return writer.write(outputFile, attribute, statistics);
  }
}
