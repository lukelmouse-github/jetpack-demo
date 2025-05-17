package com.example.common.tracker;

import com.example.common.log.ALog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ATracker {

    // 内部日志方法，方便切换日志实现
    private static void log(String message) {
        ALog.e(message);
    }

    public static TrackerSession start(String tag) {
        return new TrackerSession(tag);
    }

    public static class TrackerSession {
        private final String startTag;
        private final long startTimeNanos;
        private final Map<String, Long> stageTimestampsNanos; // 使用 LinkedHashMap 保持插入顺序
        private final List<String> stageOrder;
        private long lastTimestampNanos;
        private int nextStageIndex;

        private TrackerSession(String tag) {
            this.startTag = tag;
            this.startTimeNanos = System.nanoTime();
            this.lastTimestampNanos = this.startTimeNanos;
            this.stageTimestampsNanos = new LinkedHashMap<>();
            this.stageOrder = new ArrayList<>();
            this.nextStageIndex = 1;

            // 记录初始点
            recordStage(tag, this.startTimeNanos);
        }

        private void recordStage(String stage, long timestampNanos) {
            if (stageTimestampsNanos.containsKey(stage)) {
                // 如果stage已存在，添加一个后缀使其唯一，例如 stage_1, stage_2
                int count = 1;
                String newStage;
                do {
                    newStage = stage + "_" + count++;
                } while (stageTimestampsNanos.containsKey(newStage));
                stage = newStage;
                log("ATracker: Stage name '" + (stage.substring(0, stage.lastIndexOf("_"))) + "' was already used. Renaming to '" + stage + "'.");
            }
            this.stageTimestampsNanos.put(stage, timestampNanos);
            this.stageOrder.add(stage);
        }

        public void dot() {
            String stageName = "Stage_" + nextStageIndex++;
            dot(stageName);
        }

        public void dot(String stage) {
            if (stage == null || stage.trim().isEmpty()) {
                log("ATracker: Stage name cannot be null or empty. Using default stage name: Stage_" + nextStageIndex);
                dot(); // 调用无参版本生成默认名称
                return;
            }
            long currentTimeNanos = System.nanoTime();
            recordStage(stage, currentTimeNanos);
            this.lastTimestampNanos = currentTimeNanos;
        }

        public void end() {
            end(null); // 调用带有默认最终阶段名称的 end 方法
        }

        public void end(String finalStageName) {
            long endTimeNanos = System.nanoTime();
            String actualFinalStageName = (finalStageName == null || finalStageName.trim().isEmpty()) ? "End" : finalStageName;

            // 如果最后一个点不是通过 dot(finalStageName) 添加的，或者没有调用过 dot，
            // 并且 stageOrder 的最后一个元素不是 startTag (意味着至少有一次 dot 调用)，
            // 我们需要将最后一段耗时归于一个隐式的阶段，或者如果 finalStageName 提供了，就用它。
            // 但更合理的做法是，end() 本身代表一个结束点，其耗时不直接归属于某个“阶段”，
            // 而是用来计算最后一个已记录阶段到end()的耗时。
            // 为了简化和明确，我们认为 end() 仅仅是结束计时并输出结果。
            // 如果用户想标记最后一个阶段，应该在 end() 之前调用 dot(finalStageName)。
            // 我们这里将 end() 视为一个时间戳，用于计算最后一个 dot() 到 end() 的时间（如果适用）。
            // 但更符合用户预期的可能是，end() 传入的 stage 就是最后一个阶段的标记。

            if (stageOrder.isEmpty()) {
                log("ATracker: No stages were recorded before end().");
                return;
            }

            // 如果 finalStageName 不为空，且与最后一个记录的 stage 不同，则添加这个 finalStageName
            // 这通常意味着用户想用 end("final operation") 来标记最后的操作并结束。
            if (actualFinalStageName != null && !actualFinalStageName.equals(stageOrder.get(stageOrder.size() - 1))) {
                // 检查是否已经存在同名的 stage
                if (stageTimestampsNanos.containsKey(actualFinalStageName)) {
                    // 如果 finalStageName 已作为 dot 被记录，我们不应重复添加，而是用 end 的时间戳更新它
                    // 但这会使逻辑复杂。更简单的处理是，如果提供了 finalStageName，就认为这是最后一个阶段的标记点。
                    // 或者，如果 finalStageName 旨在命名从最后一个 dot 到 end 的时间段，则应在之前 dot()。
                    // 当前设计：如果 end(stage) 的 stage 与最后一个 dot 的 stage 不同，则记录一个新的点。
                    recordStage(actualFinalStageName, endTimeNanos);
                } else {
                    recordStage(actualFinalStageName, endTimeNanos);
                }
            } else if (actualFinalStageName != null && actualFinalStageName.equals(stageOrder.get(stageOrder.size() - 1))) {
                // 如果 end(stage) 的 stage 和最后一个 dot 的 stage 相同，
                // 更新该 stage 的时间戳为 endTimeNanos，以确保其为最新的时间。
                // 这在用户调用 dot("X") 后紧接着调用 end("X") 时有用。
                this.stageTimestampsNanos.put(actualFinalStageName, endTimeNanos);
            } else {
                // 如果 finalStageName 为 null，且 stageOrder 不止一个元素 (即至少有一次 dot 调用)
                // 或者 stageOrder 只有一个元素 (即只有 startTag)，
                // 我们需要一个最终点来计算从最后一个记录点到结束的时间。
                // 如果没有调用过 dot，那么从 startTag 到 end 的时间就是总时间。
                // 如果调用过 dot，那么从最后一个 dot 到 end 的时间也需要计算。
                // 为了简化，我们假设 end() 的调用时间就是整个追踪的结束时间。
                // 如果 stageOrder 的最后一个不是 "End"（或用户提供的 finalStageName），
                // 并且最后一个记录的时间戳不是 endTimeNanos，则添加一个最终标记点。
                if (!stageOrder.get(stageOrder.size() -1).equals("End_Marker_For_Calculation")) {
                    // 确保最后一个时间戳是 endTimeNanos，用于计算
                    // 这里不直接添加 "End" stage 到 stageOrder，因为计算逻辑会处理
                }
            }


            long totalDurationNanos = endTimeNanos - startTimeNanos;
            if (totalDurationNanos <= 0) {
                log("ATracker: Total duration is zero or negative. No time report will be generated.");
                return;
            }

            log("ATracker Report for: " + startTag);
            log("--------------------------------------------------");
            log(String.format("Total time: %.3f ms", totalDurationNanos / 1_000_000.0));
            log("--------------------------------------------------");
            log("Stage Details:");

            long previousStageTimeNanos = startTimeNanos;
            String firstStageKey = stageOrder.get(0);

            for (int i = 0; i < stageOrder.size(); i++) {
                String currentStageKey = stageOrder.get(i);
                long currentStageTimestampNanos = stageTimestampsNanos.get(currentStageKey);
                long stageDurationNanos;

                if (i == 0) { // 第一个 stage (通常是 startTag)
                    // 如果只有一个 stage (即 startTag)，并且 end() 被调用，它的持续时间是到 endTimeNanos
                    // 但通常 startTag 本身不耗时，它是一个起点。
                    // 我们计算的是从这个点到下一个点的时间。
                    // 所以第一个列出的 "阶段" 应该是从 startTag 到第一个 dot() 的时间。
                    // 或者，如果 startTag 就是唯一的阶段，那么它的耗时是从 startTimeNanos 到 endTimeNanos。
                    // 为了统一，我们将 startTag 视为一个瞬时点，其后的第一个 dot() 定义了第一个真正的耗时阶段。

                    // 如果 stageOrder 只有一个元素，它是 startTag。
                    // 这意味着没有调用 dot()，或者 end(startTag) 被调用。
                    // 这种情况下，从 startTag 到 endTimeNanos 的耗时就是这个 "阶段" 的耗时。
                    if (stageOrder.size() == 1) {
                        stageDurationNanos = endTimeNanos - startTimeNanos;
                    } else {
                        // 如果有多个 stage，startTag 本身不展示耗时，而是作为后续阶段的起点。
                        // 或者我们可以认为从 startTag 到第一个 dot 的阶段。
                        // 当前设计：第一个打印的阶段是从 startTag 到第一个 dot()。
                        // 因此，在循环的下一次迭代中处理第一个实际的耗时阶段。
                        // 我们在这里可以打印 startTag 的时间戳作为参考。
                        // log(String.format("- %s (Timestamp: %.3f ms from start)", currentStageKey, (currentStageTimestampNanos - startTimeNanos) / 1_000_000.0));
                        previousStageTimeNanos = currentStageTimestampNanos; // 更新 previous 为当前 stage 的时间戳
                        continue; // 第一个 stage (startTag) 仅作为起点，不计算耗时（除非它是唯一的 stage）
                    }
                } else {
                    stageDurationNanos = currentStageTimestampNanos - previousStageTimeNanos;
                }


                double percentage = ((double) stageDurationNanos / totalDurationNanos) * 100.0;
                String previousStageName = stageOrder.get(i-1);
                log(String.format("- Stage: '%s' (from '%s')", currentStageKey, previousStageName));
                log(String.format("  Duration: %.3f ms (%.2f%%)", stageDurationNanos / 1_000_000.0, percentage));

                previousStageTimeNanos = currentStageTimestampNanos;
            }

            // 计算从最后一个记录的 stage 到 end() 的时间
            // 这种情况发生在用户调用了 dot() 但没有用 end(stageName) 来标记最后一个操作，或者 end() 时没有提供 stageName
            if (previousStageTimeNanos < endTimeNanos && stageOrder.size() > 0) {
                long durationFromLastDotToEndNanos = endTimeNanos - previousStageTimeNanos;
                if (durationFromLastDotToEndNanos > 0) { // 只有当有实际耗时时才打印
                    double percentage = ((double) durationFromLastDotToEndNanos / totalDurationNanos) * 100.0;
                    String lastRecordedStageName = stageOrder.get(stageOrder.size() - 1);
                    // 如果 end() 时提供了 finalStageName 且该 stage 已经被上面的循环处理，则不应重复计算。
                    // 这种情况主要针对 end() 没有 finalStageName，或者 finalStageName 与最后一个 dot 不同且未被dot添加。
                    boolean alreadyProcessedAsFinalStage = (finalStageName != null && stageOrder.get(stageOrder.size()-1).equals(finalStageName) && stageTimestampsNanos.get(finalStageName) == endTimeNanos);

                    if(!alreadyProcessedAsFinalStage){
                        log(String.format("- Stage: '%s' (from '%s')",
                                (finalStageName != null && !finalStageName.trim().isEmpty() && !stageOrder.contains(finalStageName)) ? finalStageName : "Finalizing", // 如果提供了新的finalStageName且未被记录，则使用它
                                lastRecordedStageName));
                        log(String.format("  Duration: %.3f ms (%.2f%%)", durationFromLastDotToEndNanos / 1_000_000.0, percentage));
                    }
                }
            }


            log("--------------------------------------------------");
        }
    }
}