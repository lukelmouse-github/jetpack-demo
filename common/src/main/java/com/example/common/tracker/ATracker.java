package com.example.common.tracker;

import com.example.common.log.ALog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // 🔴 累加功能相关的数据结构
        private final Map<String, Long> accumulatedDurations; // 存储累加的总时间
        private final Map<String, Long> accumulateStartTimes; // 存储每个累加stage的开始时间
        private final Set<String> accumulatedStages; // 标记哪些stage是累加类型的

        // 🆕 新增：调用次数统计
        private final Map<String, Integer> accumulatedCounts; // 存储每个stage的调用次数

        // 🆕 新增：纯调用次数统计（不涉及时间测量）
        private final Map<String, Integer> callCounts; // 存储纯调用次数

        private TrackerSession(String tag) {
            this.startTag = tag;
            this.startTimeNanos = System.nanoTime();
            this.lastTimestampNanos = this.startTimeNanos;
            this.stageTimestampsNanos = new LinkedHashMap<>();
            this.stageOrder = new ArrayList<>();
            this.nextStageIndex = 1;

            // 🔴 初始化累加相关的数据结构
            this.accumulatedDurations = new LinkedHashMap<>();
            this.accumulateStartTimes = new LinkedHashMap<>();
            this.accumulatedStages = new HashSet<>();

            // 🆕 初始化调用次数统计
            this.accumulatedCounts = new LinkedHashMap<>();

            // 🆕 初始化纯调用次数统计
            this.callCounts = new LinkedHashMap<>();

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

        // 🔴 开始累加计时
        public void startAccumulate(String stage) {
            if (stage == null || stage.trim().isEmpty()) {
                log("ATracker: Accumulate stage name cannot be null or empty.");
                return;
            }

            long currentTimeNanos = System.nanoTime();
            accumulateStartTimes.put(stage, currentTimeNanos);
            accumulatedStages.add(stage);
            this.lastTimestampNanos = currentTimeNanos;

            // 🆕 增加调用次数计数 - 🔧 修复API兼容性
            Integer currentCount = accumulatedCounts.get(stage);
            if (currentCount == null) {
                currentCount = 0;
            }
            accumulatedCounts.put(stage, currentCount + 1);

//            log(String.format("ATracker: Started timing for accumulate stage '%s' (call #%d)", stage, currentCount + 1));
        }

        // 🔴 结束累加计时并累加到总时间
        public void endAccumulate(String stage) {
            if (stage == null || stage.trim().isEmpty()) {
                log("ATracker: Accumulate stage name cannot be null or empty.");
                return;
            }

            if (!accumulateStartTimes.containsKey(stage)) {
                log(String.format("ATracker: No active timing found for accumulate stage '%s'. Call startAccumulate() first.", stage));
                return;
            }

            long currentTimeNanos = System.nanoTime();
            long startTime = accumulateStartTimes.get(stage);
            long duration = currentTimeNanos - startTime;

            // 🔧 修复API兼容性
            Long totalDurationLong = accumulatedDurations.get(stage);
            long totalDuration = (totalDurationLong != null ? totalDurationLong : 0L) + duration;
            accumulatedDurations.put(stage, totalDuration);
            accumulateStartTimes.remove(stage);

            this.lastTimestampNanos = currentTimeNanos;

//            Integer callCountInteger = accumulatedCounts.get(stage);
//            int callCount = callCountInteger != null ? callCountInteger : 0;
//            double averageDuration = callCount > 0 ? (totalDuration / 1_000_000.0) / callCount : 0.0;
//            log(String.format("ATracker: Added %.3f ms to stage '%s' (Total: %.3f ms, Calls: %d, Avg: %.3f ms/call)",
//                    duration / 1_000_000.0, stage, totalDuration / 1_000_000.0, callCount, averageDuration));
        }

        // 🆕 新增：纯调用次数统计方法
        public void incrementCallCount(String operation) {
            if (operation == null || operation.trim().isEmpty()) {
                log("ATracker: Operation name cannot be null or empty.");
                return;
            }

            // 🔧 修复API兼容性
            Integer currentCount = callCounts.get(operation);
            if (currentCount == null) {
                currentCount = 0;
            }
            callCounts.put(operation, currentCount + 1);

//            log(String.format("ATracker: Incremented call count for '%s' to %d", operation, currentCount + 1));
        }

        // 🆕 新增：获取纯调用次数
        public int getCallCount(String operation) {
            Integer count = callCounts.get(operation);
            return count != null ? count : 0;
        }

        // 🔴 获取累加stage的总时间 - 🔧 修复API兼容性
        public double getAccumulatedTime(String stage) {
            Long duration = accumulatedDurations.get(stage);
            return (duration != null ? duration : 0L) / 1_000_000.0;
        }

        // 🆕 新增：获取累加stage的调用次数 - 🔧 修复API兼容性
        public int getAccumulatedCount(String stage) {
            Integer count = accumulatedCounts.get(stage);
            return count != null ? count : 0;
        }

        // 🆕 新增：获取累加stage的平均耗时 - 🔧 修复API兼容性
        public double getAccumulatedAverageTime(String stage) {
            Long totalDurationLong = accumulatedDurations.get(stage);
            long totalDuration = totalDurationLong != null ? totalDurationLong : 0L;
            Integer countInteger = accumulatedCounts.get(stage);
            int count = countInteger != null ? countInteger : 0;
            return count > 0 ? (totalDuration / 1_000_000.0) / count : 0.0;
        }

        public void end() {
            end(null); // 调用带有默认最终阶段名称的 end 方法
        }

        public void end(String finalStageName) {
            long endTimeNanos = System.nanoTime();
            String actualFinalStageName = (finalStageName == null || finalStageName.trim().isEmpty()) ? "End" : finalStageName;

            // 🔴 结束所有未完成的累加计时
            for (Map.Entry<String, Long> entry : accumulateStartTimes.entrySet()) {
                String stage = entry.getKey();
                long startTime = entry.getValue();
                long duration = endTimeNanos - startTime;

                // 🔧 修复API兼容性
                Long totalDurationLong = accumulatedDurations.get(stage);
                long totalDuration = (totalDurationLong != null ? totalDurationLong : 0L) + duration;
                accumulatedDurations.put(stage, totalDuration);

                Integer callCountInteger = accumulatedCounts.get(stage);
                int callCount = callCountInteger != null ? callCountInteger : 0;
                double averageDuration = callCount > 0 ? (totalDuration / 1_000_000.0) / callCount : 0.0;
                log(String.format("ATracker: Auto-completed accumulate stage '%s' with %.3f ms (Total: %.3f ms, Calls: %d, Avg: %.3f ms/call)",
                        stage, duration / 1_000_000.0, totalDuration / 1_000_000.0, callCount, averageDuration));
            }
            accumulateStartTimes.clear();

            if (stageOrder.isEmpty()) {
                log("ATracker: No stages were recorded before end().");
                return;
            }

            // 处理最终阶段逻辑（保持原有逻辑）
            if (actualFinalStageName != null && !actualFinalStageName.equals(stageOrder.get(stageOrder.size() - 1))) {
                if (stageTimestampsNanos.containsKey(actualFinalStageName)) {
                    recordStage(actualFinalStageName, endTimeNanos);
                } else {
                    recordStage(actualFinalStageName, endTimeNanos);
                }
            } else if (actualFinalStageName != null && actualFinalStageName.equals(stageOrder.get(stageOrder.size() - 1))) {
                this.stageTimestampsNanos.put(actualFinalStageName, endTimeNanos);
            }

            long totalDurationNanos = endTimeNanos - startTimeNanos;
            if (totalDurationNanos <= 0) {
                log("ATracker: Total duration is zero or negative. No time report will be generated.");
                return;
            }

            log("ATracker Report for: " + startTag);
            log("--------------------------------------------------");
            log(String.format("Total time: %.3f ms", totalDurationNanos / 1_000_000.0));

            // 🔴 如果有累加的stage，先输出累加统计（🆕 包含调用次数和平均耗时）
            if (!accumulatedDurations.isEmpty()) {
                log("--------------------------------------------------");
                log("Accumulated Stages Summary:");
                for (Map.Entry<String, Long> entry : accumulatedDurations.entrySet()) {
                    String stage = entry.getKey();
                    long durationNanos = entry.getValue();

                    // 🔧 修复API兼容性
                    Integer countInteger = accumulatedCounts.get(stage);
                    int count = countInteger != null ? countInteger : 0;
                    double averageDuration = count > 0 ? (durationNanos / 1_000_000.0) / count : 0.0;
                    double percentage = ((double) durationNanos / totalDurationNanos) * 100.0;

                    // 🆕 新的输出格式：总耗时 - 调用次数 - 平均调用时长 (百分比)
                    log(String.format("- %s: %.3f ms - %d calls - %.3f ms/call (%.2f%%)",
                            stage, durationNanos / 1_000_000.0, count, averageDuration, percentage));
                }
            }

            // 🆕 新增：输出纯调用次数统计
            if (!callCounts.isEmpty()) {
                log("--------------------------------------------------");
                log("Call Count Statistics:");
                for (Map.Entry<String, Integer> entry : callCounts.entrySet()) {
                    String operation = entry.getKey();
                    int count = entry.getValue();
                    log(String.format("- %s: %d calls", operation, count));
                }
            }

            log("--------------------------------------------------");
            log("Stage Details:");

            long previousStageTimeNanos = startTimeNanos;
            String firstStageKey = stageOrder.get(0);

            for (int i = 0; i < stageOrder.size(); i++) {
                String currentStageKey = stageOrder.get(i);
                long currentStageTimestampNanos = stageTimestampsNanos.get(currentStageKey);
                long stageDurationNanos;

                if (i == 0) {
                    if (stageOrder.size() == 1) {
                        stageDurationNanos = endTimeNanos - startTimeNanos;
                    } else {
                        previousStageTimeNanos = currentStageTimestampNanos;
                        continue;
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

            // 计算从最后一个记录的 stage 到 end() 的时间（保持原有逻辑）
            if (previousStageTimeNanos < endTimeNanos && stageOrder.size() > 0) {
                long durationFromLastDotToEndNanos = endTimeNanos - previousStageTimeNanos;
                if (durationFromLastDotToEndNanos > 0) {
                    double percentage = ((double) durationFromLastDotToEndNanos / totalDurationNanos) * 100.0;
                    String lastRecordedStageName = stageOrder.get(stageOrder.size() - 1);
                    boolean alreadyProcessedAsFinalStage = (finalStageName != null && stageOrder.get(stageOrder.size()-1).equals(finalStageName) && stageTimestampsNanos.get(finalStageName) == endTimeNanos);

                    if(!alreadyProcessedAsFinalStage){
                        log(String.format("- Stage: '%s' (from '%s')",
                                (finalStageName != null && !finalStageName.trim().isEmpty() && !stageOrder.contains(finalStageName)) ? finalStageName : "Finalizing",
                                lastRecordedStageName));
                        log(String.format("  Duration: %.3f ms (%.2f%%)", durationFromLastDotToEndNanos / 1_000_000.0, percentage));
                    }
                }
            }

            log("--------------------------------------------------");
        }
    }
}