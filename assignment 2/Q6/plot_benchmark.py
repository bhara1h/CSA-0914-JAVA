import csv
from pathlib import Path

import matplotlib.pyplot as plt


project = Path(__file__).resolve().parents[1]
csv_path = project / "outputs" / "Q6-benchmark.csv"
image_path = project / "outputs" / "Q6-benchmark.png"

with csv_path.open(newline="", encoding="utf-8") as source:
    rows = list(csv.DictReader(source))

implementations = ["PriorityQueue", "Sorted LinkedList", "Five FIFO queues"]
sizes = sorted({int(row["patients"]) for row in rows})
colors = ["#2563eb", "#f97316", "#16a34a"]
fig, axes = plt.subplots(1, 2, figsize=(12, 4.8), sharex=True)

for axis, metric, title in (
    (axes[0], "admission_ms", "Admission"),
    (axes[1], "removal_ms", "Removal"),
):
    width = 0.24
    for index, (implementation, color) in enumerate(zip(implementations, colors)):
        values = []
        for size in sizes:
            samples = [
                float(row[metric]) for row in rows
                if row["implementation"] == implementation and int(row["patients"]) == size
            ]
            values.append(sum(samples) / len(samples))
        positions = [size_index + (index - 1) * width for size_index in range(len(sizes))]
        axis.bar(positions, values, width=width, label=implementation, color=color)
    axis.set_title(title)
    axis.set_ylabel("Average milliseconds per full workload")
    axis.set_xticks(range(len(sizes)), [f"{size:,}" for size in sizes])
    axis.set_xlabel("Patients admitted and removed (peak residency: 256)")
    axis.grid(axis="y", alpha=0.25)

axes[0].legend(frameon=False, fontsize=8)
fig.suptitle("Priority-queue alternatives — 7-run mean after 3 warm-ups")
fig.tight_layout()
fig.savefig(image_path, dpi=160)
print(f"Saved {image_path}")
