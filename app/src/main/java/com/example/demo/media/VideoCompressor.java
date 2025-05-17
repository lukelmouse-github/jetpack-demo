package com.example.demo.media;

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

    public String compressVideo(@NonNull String srcPath,
                                int bitrateKbps,
                                int targetUserFps, // Renamed fps to targetUserFps for clarity
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
        MediaExtractor audioExtractor = null;
        MediaCodec videoDecoder = null;
        MediaCodec videoEncoder = null;
        MediaMuxer muxer = null;
        Surface inputSurface = null;

        int videoTrackIndexExtractor = -1;
        int audioTrackIndexExtractor = -1;
        int videoTrackIndexMuxer = -1;
        int audioTrackIndexMuxer = -1;

        long decodedVideoFrameCount = 0;
        long renderedVideoFrameCount = 0;
        long totalAudioFramesProcessed = 0;
        long startTime = System.currentTimeMillis();

        int actualSourceFps;
        int finalEncoderFps;
        boolean performFrameDroppingLogic;

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

            int sourceWidth = inputVideoFormat.containsKey(MediaFormat.KEY_WIDTH) ? inputVideoFormat.getInteger(MediaFormat.KEY_WIDTH) : -1;
            int sourceHeight = inputVideoFormat.containsKey(MediaFormat.KEY_HEIGHT) ? inputVideoFormat.getInteger(MediaFormat.KEY_HEIGHT) : -1;

            // Determine actual source FPS and the final FPS for the encoder
            actualSourceFps = inputVideoFormat.containsKey(MediaFormat.KEY_FRAME_RATE) ? inputVideoFormat.getInteger(MediaFormat.KEY_FRAME_RATE) : 30;
            if (actualSourceFps <= 0) { // Sanity check for source FPS
                Log.w(TAG, "Source FPS is invalid (" + actualSourceFps + "), defaulting to 30 FPS.");
                actualSourceFps = 30;
            }

            if (targetUserFps <= 0) {
                finalEncoderFps = actualSourceFps;
                performFrameDroppingLogic = false;
                Log.d(TAG, "User target FPS (" + targetUserFps + ") is invalid or not set. Using source FPS: " + actualSourceFps);
            } else if (targetUserFps >= actualSourceFps) {
                finalEncoderFps = actualSourceFps; // Do not increase FPS by duplicating frames.
                performFrameDroppingLogic = false;
                Log.d(TAG, "User target FPS (" + targetUserFps + ") >= Source FPS (" + actualSourceFps + "). Using source FPS for encoder: " + finalEncoderFps);
            } else { // targetUserFps < actualSourceFps
                finalEncoderFps = targetUserFps;
                performFrameDroppingLogic = true;
                Log.d(TAG, "User target FPS (" + targetUserFps + ") < Source FPS (" + actualSourceFps + "). Frame dropping will be enabled. Encoder FPS: " + finalEncoderFps);
            }

            Log.d(TAG, "Source video: " + sourceWidth + "x" + sourceHeight + " @ " + actualSourceFps + "fps (actual/inferred)");
            Log.d(TAG, "Target video: " + targetWidth + "x" + targetHeight + " @ " + finalEncoderFps + "fps (encoder target), " + bitrateKbps + "kbps, codec: " + codecName);
            if(performFrameDroppingLogic) {
                Log.d(TAG, "Frame dropping is ON. Target FPS: " + targetUserFps);
            } else {
                Log.d(TAG, "Frame dropping is OFF.");
            }


            audioExtractor = new MediaExtractor();
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
            outputVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrateKbps * 1000);
            outputVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, finalEncoderFps); // Use the determined finalEncoderFps
            outputVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);

            try {
                outputVideoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            } catch (Exception e) {
                Log.w(TAG, "VBR mode not supported for " + codecName + " on this device, using default.");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                outputVideoFormat.setInteger(MediaFormat.KEY_PRIORITY, 0);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (codecName.equals(CODEC_H265)) {
                    outputVideoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.HEVCProfileMain);
                } else if (codecName.equals(CODEC_H264)) {
                    outputVideoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
                }
            }

            videoEncoder = MediaCodec.createEncoderByType(codecName);
            videoEncoder.configure(outputVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = videoEncoder.createInputSurface();
            videoEncoder.start();

            // 4. Setup Video Decoder
            String inputVideoMimeType = inputVideoFormat.getString(MediaFormat.KEY_MIME);
            videoDecoder = MediaCodec.createDecoderByType(inputVideoMimeType);
            videoDecoder.configure(inputVideoFormat, inputSurface, null, 0);
            videoDecoder.start();

            AtomicBoolean muxerStarted = new AtomicBoolean(false);
            MediaCodec.BufferInfo videoDecoderBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo videoEncoderBufferInfo = new MediaCodec.BufferInfo();

            boolean videoExtractorDone = false;
            boolean videoDecoderDone = false;
            boolean videoEncoderDone = false;

            MediaCodec.BufferInfo audioBufferInfo = null;
            ByteBuffer audioByteBuffer = null;
            boolean audioExtractorDone = false;
            if (audioTrackIndexExtractor != -1 && inputAudioFormat != null) {
                audioBufferInfo = new MediaCodec.BufferInfo();
                int maxAudioInputSize = inputAudioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE) ?
                        inputAudioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) : 16 * 1024;
                audioByteBuffer = ByteBuffer.allocateDirect(maxAudioInputSize);
            } else {
                audioExtractorDone = true;
            }

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

                // --- Process Video Decoder Output & Feed to Encoder's Surface ---
                if (!videoDecoderDone) {
                    int decoderOutputBufferId = videoDecoder.dequeueOutputBuffer(videoDecoderBufferInfo, TIMEOUT_USEC);
                    if (decoderOutputBufferId >= 0) {
                        if ((videoDecoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(TAG, "Video Decoder: Output EOS.");
                            videoEncoder.signalEndOfInputStream();
                            videoDecoderDone = true;
                        }

                        if (videoDecoderBufferInfo.size > 0) { // A valid data frame from decoder
                            decodedVideoFrameCount++;
                            boolean renderThisFrame = true;

                            if (performFrameDroppingLogic) {
                                // Condition to keep frame: renderedCount / decodedCount ~ targetFps / sourceFps
                                // renderedCount < decodedCount * (targetFps / sourceFps)
                                if (!(renderedVideoFrameCount < decodedVideoFrameCount * ((double)finalEncoderFps / actualSourceFps))) {
                                    renderThisFrame = false;
                                }
                            }

                            if (renderThisFrame) {
                                videoDecoder.releaseOutputBuffer(decoderOutputBufferId, true /* render */);
                                renderedVideoFrameCount++;
                            } else {
                                videoDecoder.releaseOutputBuffer(decoderOutputBufferId, false /* don't render, drop frame */);
                                Log.v(TAG, "Frame Dropping: Skipped source video frame " + decodedVideoFrameCount);
                            }
                        } else { // Not a data frame (e.g. EOS already handled, or just info)
                            videoDecoder.releaseOutputBuffer(decoderOutputBufferId, false /* No data to render */);
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
                            Log.d(TAG, "Video Encoder: Codec config buffer received. Size: " + videoEncoderBufferInfo.size);
                            videoEncoderBufferInfo.size = 0; // Muxer gets this from addTrack with format
                        }

                        if (videoEncoderBufferInfo.size > 0) {
                            if (videoTrackIndexMuxer == -1) {
                                throw new IllegalStateException("Muxer video track not initialized before receiving data.");
                            }
                            encodedDataBuffer.position(videoEncoderBufferInfo.offset);
                            encodedDataBuffer.limit(videoEncoderBufferInfo.offset + videoEncoderBufferInfo.size);
                            muxer.writeSampleData(videoTrackIndexMuxer, encodedDataBuffer, videoEncoderBufferInfo);
                        }

                        videoEncoder.releaseOutputBuffer(encoderOutputBufferId, false);

                        if ((videoEncoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(TAG, "Video Encoder: Output EOS received.");
                            videoEncoderDone = true;
                        }
                    } else if (encoderOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newEncoderFormat = videoEncoder.getOutputFormat();
                        if (videoTrackIndexMuxer == -1) {
                            videoTrackIndexMuxer = muxer.addTrack(newEncoderFormat);
                            Log.d(TAG, "Muxer: Added video track with format: " + newEncoderFormat);
                            startMuxerIfTracksReady(muxer, videoTrackIndexMuxer, audioTrackIndexMuxer, audioTrackIndexExtractor != -1, () -> muxerStarted.set(true));
                        }
                    }
                }

                // --- Process Audio Extractor & Feed to Muxer (Passthrough) ---
                if (audioTrackIndexExtractor != -1 && !audioExtractorDone && audioBufferInfo != null && audioByteBuffer != null && inputAudioFormat != null) {
                    if (audioTrackIndexMuxer == -1) {
                        audioTrackIndexMuxer = muxer.addTrack(inputAudioFormat);
                        Log.d(TAG, "Muxer: Added audio track with format: " + inputAudioFormat);
                        startMuxerIfTracksReady(muxer, videoTrackIndexMuxer, audioTrackIndexMuxer, true, () -> {
                            muxerStarted.set(true);
                        });
                    }

                    if (muxerStarted.get()) {
                        audioByteBuffer.clear();
                        int sampleSize = audioExtractor.readSampleData(audioByteBuffer, 0);
                        if (sampleSize < 0) {
                            Log.d(TAG, "Audio Extractor: End of stream.");
                            audioExtractorDone = true;
                        } else {
                            audioBufferInfo.offset = 0;
                            audioBufferInfo.size = sampleSize;
                            audioBufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
                            audioBufferInfo.flags = (audioExtractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0 ? MediaCodec.BUFFER_FLAG_KEY_FRAME : 0;

                            muxer.writeSampleData(audioTrackIndexMuxer, audioByteBuffer, audioBufferInfo);
                            totalAudioFramesProcessed++;
                            audioExtractor.advance();
                        }
                    }
                } else if (audioTrackIndexExtractor == -1) {
                    audioExtractorDone = true;
                }

                if (videoEncoderDone && audioExtractorDone) {
                    Log.d(TAG, "All processing done, breaking loop.");
                    break;
                }
            }

            long endTime = System.currentTimeMillis();
            Log.d(TAG, "Video/Audio processing finished. Decoded video frames: " + decodedVideoFrameCount +
                    ", Rendered video frames to encoder: " + renderedVideoFrameCount +
                    (audioTrackIndexExtractor != -1 ? ", Audio samples processed: " + totalAudioFramesProcessed : "") +
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
            try { if (videoExtractor != null) videoExtractor.release(); } catch (Exception e) { Log.e(TAG, "Error releasing video extractor", e); }
            try { if (audioExtractor != null) audioExtractor.release(); } catch (Exception e) { Log.e(TAG, "Error releasing audio extractor", e); }
            try { if (videoDecoder != null) { videoDecoder.stop(); videoDecoder.release(); } } catch (Exception e) { Log.e(TAG, "Error stopping/releasing video decoder", e); }
            try { if (videoEncoder != null) { videoEncoder.stop(); videoEncoder.release(); } } catch (Exception e) { Log.e(TAG, "Error stopping/releasing video encoder", e); }
            if (inputSurface != null) inputSurface.release();
            try {
                if (muxer != null) {
                    if (videoTrackIndexMuxer != -1 || audioTrackIndexMuxer != -1) {
                        try {
                            muxer.stop();
                        } catch (IllegalStateException ise) {
                            Log.w(TAG, "Muxer failed to stop, possibly not started or no samples written: " + ise.getMessage());
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
        try {
            if (muxer == null) return;

            boolean videoTrackReady = videoTrackIdxMuxer != -1;
            boolean audioTrackReady = hasAudioTrack ? (audioTrackIdxMuxer != -1) : true;

            if (videoTrackReady && audioTrackReady) {
                // Check if already started by checking a flag set by the callback.
                // This is a simplified check; a more robust solution might involve MediaMuxer's state if accessible,
                // or ensuring this method isn't called if already started.
                // For this example, relying on the fact that addTrack is usually called once per track.
                // If the callback was already triggered, we assume it's started.

                // The AtomicBoolean muxerStarted in the main method handles the "already started" state effectively.
                // So, we call start() and then the callback. If start() throws due to already being started,
                // the catch block handles it.
                Log.d(TAG, "All tracks ready for muxer. Starting muxer.");
                muxer.start();
                callback.onMuxerStarted(); // Signal that muxer has (or should have) started.
            } else {
                Log.d(TAG, "Muxer not started yet. Video ready: " + videoTrackReady + ", Audio ready: " + audioTrackReady + " (hasAudio: "+hasAudioTrack+")");
            }
        } catch (IllegalStateException e) {
            Log.w(TAG, "Could not start muxer (or already started): " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("started")) {
                // If it says "muxer has been [already] started", ensure our flag is set.
                callback.onMuxerStarted();
            }
            // Other IllegalStateExceptions might indicate a more serious issue.
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