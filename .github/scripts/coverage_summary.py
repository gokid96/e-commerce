#!/usr/bin/env python3
from __future__ import annotations

import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
AGGREGATE_REPORT = REPO_ROOT / "build/reports/jacoco/aggregate/jacoco.xml"
MODULE_REPORT_GLOB = "*/*/build/reports/jacoco/test/jacocoTestReport.xml"

COUNTERS = {
    "LINE": "라인",
    "BRANCH": "분기",
    "METHOD": "메서드",
    "CLASS": "클래스",
}


def read_counters(report: Path) -> dict[str, tuple[int, int]]:
    root = ET.parse(report).getroot()
    counters: dict[str, tuple[int, int]] = {}

    for counter in root.findall("counter"):
        covered = int(counter.get("covered", 0))
        missed = int(counter.get("missed", 0))
        counters[counter.get("type", "")] = (covered, covered + missed)

    return counters


def percentage(covered: int, total: int) -> str:
    if total == 0:
        return "-"
    return f"{covered / total * 100:.1f}%"


def module_name(report: Path) -> str:
    relative = report.relative_to(REPO_ROOT)
    return ":".join(relative.parts[:2])


def render() -> list[str]:
    lines: list[str] = ["## 테스트 커버리지", ""]

    if not AGGREGATE_REPORT.exists():
        lines.append(f"집계 리포트를 찾을 수 없습니다: `{AGGREGATE_REPORT.relative_to(REPO_ROOT)}`")
        return lines

    aggregate = read_counters(AGGREGATE_REPORT)

    lines.append("| 구분 | 커버 | 전체 | 비율 |")
    lines.append("|---|---:|---:|---:|")
    for counter_type, label in COUNTERS.items():
        covered, total = aggregate.get(counter_type, (0, 0))
        lines.append(f"| {label} | {covered} | {total} | {percentage(covered, total)} |")

    module_reports = sorted(REPO_ROOT.glob(MODULE_REPORT_GLOB))
    if module_reports:
        lines += ["", "### 모듈별 라인 커버리지", "", "| 모듈 | 커버 | 전체 | 비율 |", "|---|---:|---:|---:|"]
        for report in module_reports:
            covered, total = read_counters(report).get("LINE", (0, 0))
            lines.append(f"| `{module_name(report)}` | {covered} | {total} | {percentage(covered, total)} |")

    lines += ["", "> 테스트가 없는 모듈도 집계에 포함되므로, 전체 비율은 실제 커버리지를 반영한다."]
    return lines


def main() -> int:
    summary = "\n".join(render()) + "\n"
    print(summary)

    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as file:
            file.write(summary)

    return 0


if __name__ == "__main__":
    sys.exit(main())
