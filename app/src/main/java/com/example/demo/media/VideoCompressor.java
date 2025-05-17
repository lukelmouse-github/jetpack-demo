package com.example.demo.media;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoCompressor {

    private static final String TAG = "VideoCompressor";
    private static final long TIMEOUT_USEC = 25000; // Microseconds (25ms)

    // Mime types
    public static final String CODEC_H264 = "video/avc";
    public static final String CODEC_H265 = "video/hevc";

    // API 21 is needed for MediaCodec.getInput/OutputImage and MediaCodec.releaseOutputBuffer with render timestamp
    // API 18 is needed for MediaMuxer and MediaCodec (Surface input)
    public String compressVideo(@NonNull String srcPath,
                                int bitrateKbps,
                                int fps,
                                @NonNull String codecName, // Use CODEC_H264 or CODEC_H265 constants
                                int targetWidth,
                                int targetHeight,
                                @NonNull String tempPath) throws IOException {

        File inputFile = new File(srcPath);
        if (!inputFile.exists() || !inputFile.canRead()) {
            throw new IOException("Cannot read source file: " + srcPath);
        }

        File outputFile = new File(tempPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Cannot create parent directory for: " + tempPath);
            }
        }

        MediaExtractor videoExtractor = null;
        MediaExtractor audioExtractor = null; // Separate extractor for audio
        MediaCodec videoDecoder = null;
        MediaCodec videoEncoder = null;
        MediaMuxer muxer = null;
        Surface inputSurface = null; // For encoder

        int videoTrackIndexExtractor = -1;
        int audioTrackIndexExtractor = -1;
        int videoTrackIndexMuxer = -1;
        int audioTrackIndexMuxer = -1;

        long totalVideoFramesProcessed = 0;
        long totalAudioFramesProcessed = 0;
        long startTime = System.currentTimeMillis();

        try {
            // 1. Setup MediaExtractors
            videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(srcPath);
            videoTrackIndexExtractor = selectTrack(videoExtractor, "video/");
            if (videoTrackIndexExtractor == -1) {
                throw new IOException("No video track found in " + srcPath);
            }
            MediaFormat inputVideoFormat = videoExtractor.getTrackFormat(videoTrackIndexExtractor);
            videoExtractor.selectTrack(videoTrackIndexExtractor);

            // Log original video properties
            int sourceWidth = inputVideoFormat.containsKey(MediaFormat.KEY_WIDTH) ? inputVideoFormat.getInteger(MediaFormat.KEY_WIDTH) : -1;
            int sourceHeight = inputVideoFormat.containsKey(MediaFormat.KEY_HEIGHT) ? inputVideoFormat.getInteger(MediaFormat.KEY_HEIGHT) : -1;
            int sourceFps = inputVideoFormat.containsKey(MediaFormat.KEY_FRAME_RATE) ? inputVideoFormat.getInteger(MediaFormat.KEY_FRAME_RATE) : -1;
            Log.d(TAG, "Source video: " + sourceWidth + "x" + sourceHeight + " @ " + sourceFps + "fps");
            Log.d(TAG, "Target video: " + targetWidth + "x" + targetHeight + " @ " + fps + "fps, " + bitrateKbps + "kbps, codec: " + codecName);


            audioExtractor = new MediaExtractor(); // Use a separate extractor for audio for easier handling
            audioExtractor.setDataSource(srcPath);
            audioTrackIndexExtractor = selectTrack(audioExtractor, "audio/");
            MediaFormat inputAudioFormat = null;
            if (audioTrackIndexExtractor != -1) {
                inputAudioFormat = audioExtractor.getTrackFormat(audioTrackIndexExtractor);
                audioExtractor.selectTrack(audioTrackIndexExtractor);
                Log.d(TAG, "Source audio found: " + inputAudioFormat.getString(MediaFormat.KEY_MIME));
            } else {
                Log.d(TAG, "No audio track found in " + srcPath);
            }

            // 2. Setup MediaMuxer
            muxer = new MediaMuxer(tempPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            // 3. Setup Video Encoder
            MediaFormat outputVideoFormat = MediaFormat.createVideoFormat(codecName, targetWidth, targetHeight);
            outputVideoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            outputVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrateKbps * 1000); // Bitrate in bps
            outputVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
            outputVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2); // Key frame interval in seconds (1-5 is common)

            // Set advanced parameters for better quality if possible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Try VBR for better quality per bit, fallback to default if not supported
                try {
                    outputVideoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
                } catch (Exception e) {
                    Log.w(TAG, "VBR mode not supported for " + codecName + " on this device, using default.");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API 23
                // Prioritize quality - this is a hint and might not be supported by all encoders
                outputVideoFormat.setInteger(MediaFormat.KEY_PRIORITY, 0); // 0 for process priority, 1 for real-time
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26 for H265 Main10
                if (codecName.equals(CODEC_H265)) {
                    outputVideoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.HEVCProfileMain);
                    // outputVideoFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel4); // Example level
                } else if (codecName.equals(CODEC_H264)) {
                    outputVideoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh); // High profile for better compression
                    // outputVideoFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel4); // Example level
                }
            }


            videoEncoder = MediaCodec.createEncoderByType(codecName);
            videoEncoder.configure(outputVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = videoEncoder.createInputSurface(); // Critical for Surface-based encoding
            videoEncoder.start();

            // 4. Setup Video Decoder (outputs to encoder's inputSurface)
            String inputVideoMimeType = inputVideoFormat.getString(MediaFormat.KEY_MIME);
            videoDecoder = MediaCodec.createDecoderByType(inputVideoMimeType);
            videoDecoder.configure(inputVideoFormat, inputSurface, null, 0);
            videoDecoder.start();


            // Muxer track indices will be set when format is known
            AtomicBoolean muxerStarted = new AtomicBoolean(false);

            // Video Processing Loop
            MediaCodec.BufferInfo videoDecoderBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo videoEncoderBufferInfo = new MediaCodec.BufferInfo();

            boolean videoExtractorDone = false;
            boolean videoDecoderDone = false; // EOS from decoder output
            boolean videoEncoderDone = false; // EOS from encoder output

            // Audio Processing Loop (Passthrough)
            MediaCodec.BufferInfo audioBufferInfo = null;
            ByteBuffer audioByteBuffer = null;
            boolean audioExtractorDone = false;
            if (audioTrackIndexExtractor != -1) {
                audioBufferInfo = new MediaCodec.BufferInfo();
                // Estimate buffer size or use a sufficiently large one
                int maxAudioInputSize = inputAudioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE) ?
                        inputAudioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) : 16 * 1024;
                audioByteBuffer = ByteBuffer.allocateDirect(maxAudioInputSize);
            } else {
                audioExtractorDone = true; // No audio to process
            }


            // Main processing loop:
            while (!videoEncoderDone || !audioExtractorDone) {

                // --- Feed Video Extractor to Decoder ---
                if (!videoExtractorDone) {
                    int decoderInputBufferId = videoDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                    if (decoderInputBufferId >= 0) {
                        ByteBuffer decoderInputBuffer = videoDecoder.getInputBuffer(decoderInputBufferId);
                        if (decoderInputBuffer == null) {
                            throw new IOException("Video Decoder input buffer was null");
                        }
                        int sampleSize = videoExtractor.readSampleData(decoderInputBuffer, 0);
                        long presentationTimeUs = videoExtractor.getSampleTime();

                        if (sampleSize < 0) {
                            Log.d(TAG, "Video Extractor: End of stream.");
                            videoDecoder.queueInputBuffer(decoderInputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            videoExtractorDone = true;
                        } else {
                            videoDecoder.queueInputBuffer(decoderInputBufferId, 0, sampleSize, presentationTimeUs, videoExtractor.getSampleFlags());
                            videoExtractor.advance();
                        }
                    }
                }

                // --- Process Video Decoder Output (which goes to Encoder's Surface) & Encoder Input ---
                // For Surface-based decoding, we don't manually feed the encoder.
                // The decoder renders to the Surface, and the encoder polls it.
                if (!videoDecoderDone) {
                    int decoderOutputBufferId = videoDecoder.dequeueOutputBuffer(videoDecoderBufferInfo, TIMEOUT_USEC);
                    if (decoderOutputBufferId >= 0) {
                        if ((videoDecoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(TAG, "Video Decoder: Output EOS.");
                            videoEncoder.signalEndOfInputStream(); // Signal EOS to encoder
                            videoDecoderDone = true;
                        }
                        // Render to surface. If timestamp adjustment is needed for strict FPS,
                        // use releaseOutputBuffer(decoderOutputBufferId, newPresentationTimeUs) (API 21+)
                        // For now, rely on encoder's frame rate setting.
                        videoDecoder.releaseOutputBuffer(decoderOutputBufferId, true /* render */);
                        if (videoDecoderBufferInfo.size > 0) { // A frame was rendered
                            totalVideoFramesProcessed++;
                        }
                    } else if (decoderOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newDecoderFormat = videoDecoder.getOutputFormat();
                        Log.d(TAG, "Video Decoder: Output format changed to: " + newDecoderFormat);
                    }
                }

                // --- Process Video Encoder Output & Feed to Muxer ---
                if (!videoEncoderDone) {
                    int encoderOutputBufferId = videoEncoder.dequeueOutputBuffer(videoEncoderBufferInfo, TIMEOUT_USEC);
                    if (encoderOutputBufferId >= 0) {
                        ByteBuffer encodedDataBuffer = videoEncoder.getOutputBuffer(encoderOutputBufferId);
                        if (encodedDataBuffer == null) {
                            throw new IOException("Video Encoder output buffer was null");
                        }

                        if ((videoEncoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            // Config buffer, usually handled by muxer when addTrack with format.
                            // But good to log.
                            Log.d(TAG, "Video Encoder: Codec config buffer received. Size: " + videoEncoderBufferInfo.size);
                            // Don't write this as sample data if format was already set for the track.
                            // MediaMuxer handles this internally with the format from addTrack.
                            videoEncoderBufferInfo.size = 0;
                        }

                        if (videoEncoderBufferInfo.size > 0) {
                            if (videoTrackIndexMuxer == -1) {
                                throw new IllegalStateException("Muxer video track not initialized before receiving data.");
                            }
                            encodedDataBuffer.position(videoEncoderBufferInfo.offset);
                            encodedDataBuffer.limit(videoEncoderBufferInfo.offset + videoEncoderBufferInfo.size);
                            muxer.writeSampleData(videoTrackIndexMuxer, encodedDataBuffer, videoEncoderBufferInfo);
                            // Log.v(TAG, "Muxer: Wrote video sample data, size=" + videoEncoderBufferInfo.size + ", pts=" + videoEncoderBufferInfo.presentationTimeUs);
                        }

                        videoEncoder.releaseOutputBuffer(encoderOutputBufferId, false);

                        if ((videoEncoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(TAG, "Video Encoder: Output EOS received.");
                            videoEncoderDone = true;
                        }
                    } else if (encoderOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newEncoderFormat = videoEncoder.getOutputFormat();
                        if (videoTrackIndexMuxer == -1) { // Ensure track is added only once
                            videoTrackIndexMuxer = muxer.addTrack(newEncoderFormat);
                            Log.d(TAG, "Muxer: Added video track with format: " + newEncoderFormat);
                            startMuxerIfTracksReady(muxer, videoTrackIndexMuxer, audioTrackIndexMuxer, audioTrackIndexExtractor != -1, () -> muxerStarted.set(true));
                        }
                    }
                }

                // --- Process Audio Extractor & Feed to Muxer (Passthrough) ---
                if (audioTrackIndexExtractor != -1 && !audioExtractorDone && audioBufferInfo != null && audioByteBuffer != null) {
                    if (audioTrackIndexMuxer == -1) { // Audio track for muxer not added yet
                        // This should ideally be done outside the loop, but if INFO_OUTPUT_FORMAT_CHANGED for video
                        // happens first, we might enter here.
                        audioTrackIndexMuxer = muxer.addTrack(inputAudioFormat);
                        Log.d(TAG, "Muxer: Added audio track with format: " + inputAudioFormat);
                        startMuxerIfTracksReady(muxer, videoTrackIndexMuxer, audioTrackIndexMuxer, true, () -> muxerStarted.set(true));
                    }

                    if (muxerStarted.get()) { // Only write if muxer has started
                        audioByteBuffer.clear();
                        int sampleSize = audioExtractor.readSampleData(audioByteBuffer, 0);
                        if (sampleSize < 0) {
                            Log.d(TAG, "Audio Extractor: End of stream.");
                            audioExtractorDone = true;
                        } else {
                            audioBufferInfo.offset = 0;
                            audioBufferInfo.size = sampleSize;
                            audioBufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
                            int extractorAudioFlags = audioExtractor.getSampleFlags();
                            final int muxerCompatibleAudioFlags; // 声明为 final

                            if ((extractorAudioFlags & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                                muxerCompatibleAudioFlags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                            } else {
                                muxerCompatibleAudioFlags = 0;
                            }
                            // 此处 muxerCompatibleAudioFlags 要么是 0，要么是 MediaCodec.BUFFER_FLAG_KEY_FRAME
                            audioBufferInfo.flags = muxerCompatibleAudioFlags;

                            muxer.writeSampleData(audioTrackIndexMuxer, audioByteBuffer, audioBufferInfo);
                            totalAudioFramesProcessed++;
                            // Log.v(TAG, "Muxer: Wrote audio sample data, size=" + audioBufferInfo.size + ", pts=" + audioBufferInfo.presentationTimeUs);
                            audioExtractor.advance();
                        }
                    }
                } else if (audioTrackIndexExtractor == -1) {
                    audioExtractorDone = true; // No audio track to begin with
                }

                // Check for completion (both video processing and audio passthrough must be done)
                if (videoEncoderDone && audioExtractorDone) {
                    Log.d(TAG, "All processing done, breaking loop.");
                    break;
                }
            }

            long endTime = System.currentTimeMillis();
            Log.d(TAG, "Video/Audio processing finished. Video frames: " + totalVideoFramesProcessed +
                    (audioTrackIndexExtractor != -1 ? ", Audio samples: " + totalAudioFramesProcessed : "") +
                    ". Time: " + (endTime - startTime) + " ms.");
            return tempPath;

        } catch (IllegalStateException e) {
            Log.e(TAG, "MediaCodec IllegalStateException: " + e.getMessage(), e);
            deleteOutputFile(tempPath);
            throw new IOException("MediaCodec error during processing: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Processing error: " + e.getMessage(), e);
            deleteOutputFile(tempPath);
            throw new IOException("Processing failed: " + e.getMessage(), e);
        } finally {
            Log.d(TAG, "Cleaning up resources...");
            try {
                if (videoExtractor != null) {
                    videoExtractor.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error releasing video extractor", e);
            }
            try {
                if (audioExtractor != null) {
                    audioExtractor.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error releasing audio extractor", e);
            }
            try {
                if (videoDecoder != null) {
                    videoDecoder.stop();
                    videoDecoder.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping/releasing video decoder", e);
            }
            try {
                if (videoEncoder != null) {
                    videoEncoder.stop();
                    videoEncoder.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping/releasing video encoder", e);
            }
            if (inputSurface != null) {
                inputSurface.release();
            }
            try {
                if (muxer != null) {
                    // Muxer stop can throw IllegalStateException if not properly started or no data written.
                    // Ensure it was actually started and had tracks.
                    if (videoTrackIndexMuxer != -1 || audioTrackIndexMuxer != -1) { // If any track was potentially added
                        try {
                            muxer.stop();
                        } catch (IllegalStateException ise) {
                            Log.w(TAG, "Muxer failed to stop, possibly not started or no samples written: " + ise.getMessage());
                            // If muxer.stop() fails, the output file might be corrupted.
                            deleteOutputFile(tempPath);
                        }
                    }
                    muxer.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping/releasing muxer", e);
                deleteOutputFile(tempPath);
            }
            Log.d(TAG, "Cleanup complete.");
        }
    }

    private int selectTrack(MediaExtractor extractor, String mimePrefix) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith(mimePrefix)) {
                Log.d(TAG, "Extractor: Found " + mimePrefix + " track at index " + i + " with MIME: " + mime);
                return i;
            }
        }
        Log.w(TAG, "Extractor: No " + mimePrefix + " track found.");
        return -1;
    }

    private interface MuxerStartCallback {
        void onMuxerStarted();
    }

    private void startMuxerIfTracksReady(MediaMuxer muxer, int videoTrackIdxMuxer, int audioTrackIdxMuxer, boolean hasAudioTrack, MuxerStartCallback callback) {
        // Check if muxer has already been started by a previous call (e.g., if audio format came first)
        // This simple check might not be robust if we expect INFO_OUTPUT_FORMAT_CHANGED multiple times,
        // but for typical scenarios (once for video, audio is known upfront), it's okay.
        try {
            // A bit of a hack: trying to call stop() on a non-started muxer or one with no tracks throws.
            // A better way would be to use a boolean flag that's set in this method.
            // For now, let's use the callback to set an external flag.
            // The primary condition is that all *expected* tracks are added.
            if (muxer == null) return; // Should not happen

            boolean videoTrackReady = videoTrackIdxMuxer != -1;
            boolean audioTrackReady = hasAudioTrack ? (audioTrackIdxMuxer != -1) : true; // If no audio, it's "ready"

            if (videoTrackReady && audioTrackReady) {
                Log.d(TAG, "All tracks ready for muxer. Starting muxer.");
                muxer.start();
                callback.onMuxerStarted();
            } else {
                Log.d(TAG, "Muxer not started yet. Video ready: " + videoTrackReady + ", Audio ready: " + audioTrackReady + " (hasAudio: "+hasAudioTrack+")");
            }
        } catch (IllegalStateException e) {
            // This means muxer might have been started already, or other issues.
            Log.w(TAG, "Could not start muxer: " + e.getMessage());
            // If it says "muxer has been started", we can ignore.
            if (e.getMessage() != null && e.getMessage().contains("started")) {
                callback.onMuxerStarted(); // Assume it was started by a concurrent call or previous state.
            }
        }
    }

    private void deleteOutputFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            if (f.delete()) {
                Log.w(TAG, "Deleted potentially corrupt output file: " + path);
            } else {
                Log.e(TAG, "Failed to delete corrupt output file: " + path);
            }
        }
    }
}