package com.example.demo.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;
import android.view.Surface; // Only if decoding to surface, not used in this buffer-to-buffer approach

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCompressor {

    private static final String TAG = "VideoCompressor";
    private static final long TIMEOUT_USEC = 25000; // Microseconds (25ms)
    private static final int MAX_FRAME_RATE_FOR_HIGH_QUALITY = 30; // Example threshold

    // Mime types
    public static final String CODEC_H264 = "video/avc";
    public static final String CODEC_H265 = "video/hevc";


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

        MediaExtractor extractor = null;
        MediaCodec decoder = null;
        MediaCodec encoder = null;
        MediaMuxer muxer = null;

        int videoTrackIndexExtractor = -1;
        int videoTrackIndexMuxer = -1;

        long totalFramesProcessed = 0;
        long startTime = System.currentTimeMillis();

        try {
            // 1. Setup MediaExtractor
            extractor = new MediaExtractor();
            extractor.setDataSource(srcPath);
            videoTrackIndexExtractor = selectVideoTrack(extractor);
            if (videoTrackIndexExtractor == -1) {
                throw new IOException("No video track found in " + srcPath);
            }
            extractor.selectTrack(videoTrackIndexExtractor);
            MediaFormat inputFormat = extractor.getTrackFormat(videoTrackIndexExtractor);

            // Get original dimensions for logging or potential scaling decisions (scaling not implemented here)
            int sourceWidth = inputFormat.containsKey(MediaFormat.KEY_WIDTH) ? inputFormat.getInteger(MediaFormat.KEY_WIDTH) : targetWidth;
            int sourceHeight = inputFormat.containsKey(MediaFormat.KEY_HEIGHT) ? inputFormat.getInteger(MediaFormat.KEY_HEIGHT) : targetHeight;
            Log.d(TAG, "Source video: " + sourceWidth + "x" + sourceHeight);
            Log.d(TAG, "Target video: " + targetWidth + "x" + targetHeight + " @ " + fps + "fps, " + bitrateKbps + "kbps, codec: " + codecName);


            // 2. Setup MediaMuxer
            muxer = new MediaMuxer(tempPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            // 3. Setup Decoder
            // We decode to manipulate frames/timestamps before re-encoding.
            String inputMimeType = inputFormat.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(inputMimeType);
            decoder.configure(inputFormat, null /* surface */, null /* crypto */, 0 /* flags */);
            decoder.start();

            // 4. Setup Encoder
            MediaFormat outputFormat = MediaFormat.createVideoFormat(codecName, targetWidth, targetHeight);
            outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrateKbps * 1000); // Bitrate in bps
            outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
            outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); // Key frame interval in seconds

            // Some encoders might benefit from profile and level, but this makes it more complex
            // to ensure device compatibility. For H.264, Baseline profile is widely compatible.
            // if (codecName.equals(CODEC_H264)) {
            // outputFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
            // outputFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel31); // Adjust as needed
            // }

            // Set bitrate mode if desired (e.g., CBR). VBR is often default.
            // outputFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);

            encoder = MediaCodec.createEncoderByType(codecName);
            encoder.configure(outputFormat, null /* surface */, null /* crypto */, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encoder.start();


            // 5. Main processing loop: Extractor -> Decoder -> (Processing) -> Encoder -> Muxer
            MediaCodec.BufferInfo decoderBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo encoderBufferInfo = new MediaCodec.BufferInfo();

            boolean extractorDone = false;
            boolean decoderDone = false;
            boolean encoderDone = false;

            long outputFrameCount = 0;
            final long frameIntervalUs = 1_000_000L / fps; // Desired interval between frames in microseconds

            // Loop flags
            boolean decoderInputDone = false;
            boolean decoderOutputDone = false; // EOS from decoder
            boolean encoderInputDone = false; // EOS queued to encoder

            while (!encoderDone) {
                // --- Feed Extractor to Decoder ---
                if (!extractorDone && !decoderInputDone) {
                    int decoderInputBufferId = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                    if (decoderInputBufferId >= 0) {
                        ByteBuffer decoderInputBuffer = decoder.getInputBuffer(decoderInputBufferId);
                        if (decoderInputBuffer == null) {
                            Log.e(TAG, "Decoder input buffer is null");
                            throw new IOException("Decoder input buffer was null");
                        }
                        int sampleSize = extractor.readSampleData(decoderInputBuffer, 0);
                        long presentationTimeUs = extractor.getSampleTime();

                        if (sampleSize < 0) {
                            Log.d(TAG, "Extractor: End of stream.");
                            decoder.queueInputBuffer(decoderInputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            extractorDone = true;
                            decoderInputDone = true;
                        } else {
                            decoder.queueInputBuffer(decoderInputBufferId, 0, sampleSize, presentationTimeUs, extractor.getSampleFlags());
                            extractor.advance();
                        }
                    } else if (decoderInputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // Log.v(TAG, "Decoder: No input buffer available yet");
                    }
                }

                // --- Process Decoder Output & Feed to Encoder ---
                if (!decoderOutputDone && !encoderInputDone) {
                    int decoderOutputBufferId = decoder.dequeueOutputBuffer(decoderBufferInfo, TIMEOUT_USEC);
                    if (decoderOutputBufferId >= 0) {
                        if ((decoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(TAG, "Decoder: Output EOS.");
                            // Signal EOS to encoder
                            int encoderInputBufferId = encoder.dequeueInputBuffer(TIMEOUT_USEC);
                            if (encoderInputBufferId >= 0) {
                                encoder.queueInputBuffer(encoderInputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                encoderInputDone = true; // EOS has been queued to encoder
                                Log.d(TAG, "Encoder: Input EOS signaled.");
                            } else {
                                Log.w(TAG, "Encoder: No input buffer for EOS, will retry.");
                            }
                            decoderOutputDone = true; // Decoder has output its EOS
                        } else if (decoderBufferInfo.size > 0) {
                            ByteBuffer decodedDataBuffer = decoder.getOutputBuffer(decoderOutputBufferId);
                            if (decodedDataBuffer == null) {
                                throw new RuntimeException("decoder.getOutputBuffer() returned null for valid index.");
                            }

                            // !!! IMPORTANT: Frame Scaling / Resizing !!!
                            // If sourceWidth/Height != targetWidth/Height, you MUST scale the frame data here.
                            // `decodedDataBuffer` contains the raw frame (likely YUV).
                            // You would need to:
                            // 1. Understand its exact format (e.g., from decoder.getOutputFormat() or an Image object).
                            // 2. Implement/use a scaling algorithm (e.g., bilinear interpolation for Y, U, V planes).
                            // 3. Put the scaled data into the encoder's input buffer.
                            // This is a complex step not implemented here. This code assumes either:
                            //    a) source dimensions == target dimensions
                            //    b) you'll add scaling logic here.
                            // Without scaling, if dimensions differ, output will be distorted or codec might error.

                            // Example of getting an Image object (API 21+) for more detailed format info & pixel access:
                            // android.media.Image image = decoder.getOutputImage(decoderOutputBufferId);
                            // if (image != null) {
                            //     // Access image.getPlanes()[0].getBuffer() (Y), etc.
                            //     // Perform scaling on these planes.
                            //     // Copy scaled data to encoderInputBuffer.
                            //     // image.close(); // IMPORTANT!
                            // }

                            // For FPS control, we calculate new presentation time for the encoder.
                            long newPresentationTimeUs = outputFrameCount * frameIntervalUs;

                            int encoderInputBufferId = encoder.dequeueInputBuffer(TIMEOUT_USEC);
                            if (encoderInputBufferId >= 0) {
                                ByteBuffer encoderInputBuffer = encoder.getInputBuffer(encoderInputBufferId);
                                if (encoderInputBuffer == null) {
                                    throw new IOException("Encoder input buffer was null");
                                }
                                encoderInputBuffer.clear();
                                // Assuming decodedDataBuffer can be directly put. This is a HUGE simplification
                                // if color formats, strides, or dimensions differ.
                                if (decodedDataBuffer.remaining() > encoderInputBuffer.remaining()) {
                                    Log.w(TAG, "Decoded data larger than encoder input buffer. Truncating. Resizing needed!");
                                    decodedDataBuffer.limit(decodedDataBuffer.position() + encoderInputBuffer.remaining());
                                }
                                encoderInputBuffer.put(decodedDataBuffer);

                                encoder.queueInputBuffer(encoderInputBufferId, 0, encoderInputBuffer.position(), newPresentationTimeUs, 0);
                                outputFrameCount++;
                                totalFramesProcessed++;
                            } else {
                                // Log.v(TAG, "Encoder: No input buffer available yet for data");
                                // If encoder input is not available, we should ideally hold the decoded frame
                                // and retry, rather than just dropping it by releasing the decoder output buffer.
                                // For simplicity here, we release. This could lead to frame drops if encoder is slow.
                            }
                        }
                        decoder.releaseOutputBuffer(decoderOutputBufferId, false /* render */);
                    } else if (decoderOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newDecoderFormat = decoder.getOutputFormat();
                        Log.d(TAG, "Decoder: Output format changed to: " + newDecoderFormat);
                        // This format might contain actual color format and dimensions after decoding.
                    } else if (decoderOutputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // Log.v(TAG, "Decoder: No output buffer available yet");
                    }
                }


                // --- Process Encoder Output & Feed to Muxer ---
                if (!encoderDone) { // Only process encoder output if its EOS hasn't been received
                    int encoderOutputBufferId = encoder.dequeueOutputBuffer(encoderBufferInfo, TIMEOUT_USEC);
                    if (encoderOutputBufferId >= 0) {
                        ByteBuffer encodedDataBuffer = encoder.getOutputBuffer(encoderOutputBufferId);
                        if (encodedDataBuffer == null) {
                            throw new RuntimeException("encoder.getOutputBuffer() returned null for valid index.");
                        }

                        if ((encoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            // This data is important for the muxer to initialize the track.
                            // It's usually automatically handled when MediaMuxer.addTrack is called with MediaFormat.
                            Log.d(TAG, "Encoder: Codec config buffer received.");
                            encoderBufferInfo.size = 0; // Muxer doesn't need this as separate data if format is set.
                        }

                        if (encoderBufferInfo.size > 0 && videoTrackIndexMuxer != -1) { // Muxer track added
                            encodedDataBuffer.position(encoderBufferInfo.offset);
                            encodedDataBuffer.limit(encoderBufferInfo.offset + encoderBufferInfo.size);
                            muxer.writeSampleData(videoTrackIndexMuxer, encodedDataBuffer, encoderBufferInfo);
                            // Log.v(TAG, "Muxer: Wrote sample data, size=" + encoderBufferInfo.size + ", pts=" + encoderBufferInfo.presentationTimeUs);
                        } else if (videoTrackIndexMuxer == -1 && encoderBufferInfo.size > 0) {
                            Log.w(TAG, "Muxer: Track not added yet, but encoder output received. This shouldn't happen before INFO_OUTPUT_FORMAT_CHANGED.");
                        }


                        encoder.releaseOutputBuffer(encoderOutputBufferId, false);

                        if ((encoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.d(TAG, "Encoder: Output EOS received.");
                            encoderDone = true;
                        }
                    } else if (encoderOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newEncoderFormat = encoder.getOutputFormat();
                        if (videoTrackIndexMuxer == -1) { // Ensure track is added only once
                            videoTrackIndexMuxer = muxer.addTrack(newEncoderFormat);
                            muxer.start();
                            Log.d(TAG, "Muxer: Started with track format: " + newEncoderFormat);
                        } else {
                            Log.w(TAG, "Encoder: Output format changed again, but muxer track already added.");
                        }
                    } else if (encoderOutputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // Log.v(TAG, "Encoder: No output buffer available yet");
                    }
                }

                // Check for completion: if all components have signaled EOS and encoder has outputted its EOS.
                if (extractorDone && decoderOutputDone && encoderInputDone && encoderDone) {
                    Log.d(TAG, "All stages done, breaking loop.");
                    break;
                }
            }

            long endTime = System.currentTimeMillis();
            Log.d(TAG, "Video compression finished. Frames processed: " + totalFramesProcessed + ". Time: " + (endTime - startTime) + " ms.");
            return tempPath;

        } catch (IllegalStateException e) {
            Log.e(TAG, "MediaCodec IllegalStateException: " + e.getMessage(), e);
            throw new IOException("MediaCodec error during compression: " + e.getMessage(), e);
        } catch (Exception e) { // Catch generic exceptions to ensure cleanup
            Log.e(TAG, "Compression error: " + e.getMessage(), e);
            // Attempt to delete partially created output file
            File f = new File(tempPath);
            if (f.exists()) {
                f.delete();
            }
            throw new IOException("Compression failed: " + e.getMessage(), e);
        } finally {
            Log.d(TAG, "Cleaning up resources...");
            try {
                if (extractor != null) {
                    extractor.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error releasing extractor", e);
            }
            try {
                if (decoder != null) {
                    decoder.stop();
                    decoder.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping/releasing decoder", e);
            }
            try {
                if (encoder != null) {
                    encoder.stop();
                    encoder.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping/releasing encoder", e);
            }
            try {
                if (muxer != null) {
                    // Muxer stop can throw IllegalStateException if not properly started or no data written.
                    if (videoTrackIndexMuxer != -1) { // Only stop if it was started (track added)
                        muxer.stop();
                    }
                    muxer.release();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping/releasing muxer", e);
                // If muxer.stop() fails, the output file might be corrupted.
                // It might be prudent to delete it here if an error occurs during stop/release.
                File f = new File(tempPath);
                if (f.exists()) {
                    Log.w(TAG, "Muxer failed to stop/release cleanly, deleting potentially corrupt output file: " + tempPath);
                    f.delete();
                }
            }
            Log.d(TAG, "Cleanup complete.");
        }
    }

    private int selectVideoTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith("video/")) {
                Log.d(TAG, "Extractor: Found video track at index " + i + " with MIME: " + mime);
                return i;
            }
        }
        return -1;
    }

    // Example usage:
    // try {
    //     VideoCompressor compressor = new VideoCompressor();
    //     String outputPath = compressor.compressVideo(
    //         "/sdcard/Download/input.mp4",
    //         2000, // 2 Mbps bitrate
    //         30,   // 30 FPS
    //         VideoCompressor.CODEC_H264, // or VideoCompressor.CODEC_H265
    //         1280, // target width
    //         720,  // target height
    //         "/sdcard/Download/output_compressed.mp4"
    //     );
    //     Log.d(TAG, "Compression successful: " + outputPath);
    // } catch (IOException e) {
    //     Log.e(TAG, "Compression failed", e);
    // }
}
