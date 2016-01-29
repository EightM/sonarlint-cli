/*
 * SonarLint CLI
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.cli.report;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.IssueListener;

public class IssuesReport {

  public static final int TOO_MANY_ISSUES_THRESHOLD = 1000;
  private String title;
  private Date date;
  private int filesAnalyzed;
  private final ReportSummary summary = new ReportSummary();
  private final Map<Path, ResourceReport> resourceReportsByFilePath = new HashMap<>();
  private final Map<String, String> ruleNameByKey = new HashMap<>();

  IssuesReport() {

  }

  public boolean noIssues() {
    return summary.getTotal().getCountInCurrentAnalysis() == 0;
  }

  public ReportSummary getSummary() {
    return summary;
  }

  public boolean noFiles() {
    return filesAnalyzed == 0;
  }

  public int getFilesAnalyzed() {
    return filesAnalyzed;
  }

  public void setFilesAnalyzed(int num) {
    this.filesAnalyzed = num;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Map<Path, ResourceReport> getResourceReportsByResource() {
    return resourceReportsByFilePath;
  }

  public List<ResourceReport> getResourceReports() {
    return new ArrayList<>(resourceReportsByFilePath.values());
  }

  public List<Path> getResourcesWithReport() {
    return new ArrayList<>(resourceReportsByFilePath.keySet());
  }
  
  public String getRuleName(String ruleKey) {
    return ruleNameByKey.get(ruleKey);
  }
  
  public String issueId(IssueListener.Issue issue) {
    String line = issue.getStartLine() != null ? issue.getStartLine().toString() : "-";
    String path = issue.getFilePath() != null ? issue.getFilePath().toString() : "";
    return issue.getRuleKey() + "R" + path + "L" + line;
  }

  public void addIssue(IssueListener.Issue issue) {
    ruleNameByKey.put(issue.getRuleKey(), issue.getRuleName());
    Path filePath = issue.getFilePath();
    if (filePath == null) {
      // issue on project (no specific file)
      filePath = Paths.get("");
    }
    ResourceReport report = getOrCreate(filePath);
    getSummary().addIssue(issue);

    report.addIssue(issue);
  }

  private ResourceReport getOrCreate(Path filePath) {
    ResourceReport report = resourceReportsByFilePath.get(filePath);
    if (report != null) {
      return report;
    }
    report = new ResourceReport(filePath);
    resourceReportsByFilePath.put(filePath, new ResourceReport(filePath));
    return report;
  }
}
