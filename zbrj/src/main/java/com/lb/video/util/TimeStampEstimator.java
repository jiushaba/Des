package com.lb.video.util;

import android.os.SystemClock;

//Frame smooth timestamp generator
public	class TimeStampEstimator {
		final int durationHistoryLength = 2048;
		private double durationHistory[];
		int durationHistoryIndex = 0;
		double durationHistorySum = 0;
		double lastFrameTiming = 0;
		long sequenceDuration = 0;

		public void update() {
			long currentFrameTiming = SystemClock.elapsedRealtime();
			double newDuration = currentFrameTiming - lastFrameTiming;
			lastFrameTiming = currentFrameTiming;

			durationHistorySum -= durationHistory[durationHistoryIndex];
			durationHistorySum += newDuration;
			durationHistory[durationHistoryIndex] = newDuration;
			durationHistoryIndex++;
			if (durationHistoryIndex >= durationHistoryLength)
				durationHistoryIndex = 0;
			sequenceDuration += (int) ((1.0 * durationHistorySum / durationHistoryLength));
		}

		public void setFirstFrameTiming() {
			lastFrameTiming = SystemClock.elapsedRealtime()
					- durationHistorySum / durationHistoryLength;
			sequenceDuration = 0;
		}

		public long getSequenceTimeStamp() {
			return sequenceDuration;
		}

		public void reset(double frameDuration) {
			if (durationHistory == null)
				durationHistory = new double[durationHistoryLength];
			durationHistorySum = 0;
			for (int i = 0; i < durationHistoryLength; i++) {
				durationHistory[i] = frameDuration; // us
				durationHistorySum += frameDuration;
			}
			lastFrameTiming = 0;
			sequenceDuration = 0;
			durationHistoryIndex = 0;
		}

		public TimeStampEstimator(double frameDuration) {
			reset(frameDuration);
		}
	}