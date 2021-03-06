package com.lz.oncon.api.core.im.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.provider.MediaStore.Images;
import com.lb.common.util.Log;

public class ThumbnailUtils {

	/* Maximum pixels size for created bitmap. */
	private static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 384;
	private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 128 * 128;
	private static final int UNCONSTRAINED = -1;

	/* Options used internally. */
	private static final int OPTIONS_NONE = 0x0;
	private static final int OPTIONS_SCALE_UP = 0x1;

	/**
	 * Constant used to indicate we should recycle the input in
	 * {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the
	 * input.
	 */
	public static final int OPTIONS_RECYCLE_INPUT = 0x2;

	/**
	 * Constant used to indicate the dimension of mini thumbnail.
	 * 
	 * @hide Only used by media framework and media provider internally.
	 */
	public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

	/**
	 * Constant used to indicate the dimension of micro thumbnail.
	 * 
	 * @hide Only used by media framework and media provider internally.
	 */
	public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

	/**
	 * This method first examines if the thumbnail embedded in EXIF is bigger
	 * than our target size. If not, then it'll create a thumbnail from original
	 * image. Due to efficiency consideration, we want to let MediaThumbRequest
	 * avoid calling this method twice for both kinds, so it only requests for
	 * MICRO_KIND and set saveImage to true.
	 * 
	 * This method always returns a "square thumbnail" for MICRO_KIND thumbnail.
	 * 
	 * @param filePath
	 *            the path of image file
	 * @param kind
	 *            could be MINI_KIND or MICRO_KIND
	 * @return Bitmap
	 * 
	 * @hide This method is only used by media framework and media provider
	 *       internally.
	 */
	public static Bitmap createImageThumbnail(String filePath, int kind,
			boolean isThumb) {
		boolean wantMini = (kind == Images.Thumbnails.MINI_KIND);
		int targetSize = 0;
		int maxPixels = 0;
		if (isThumb) {
			targetSize = TARGET_SIZE_MICRO_THUMBNAIL;
			maxPixels = MAX_NUM_PIXELS_MICRO_THUMBNAIL;
		} else {
			targetSize = TARGET_SIZE_MINI_THUMBNAIL;
			maxPixels = MAX_NUM_PIXELS_THUMBNAIL;
		}
		SizedThumbnailBitmap sizedThumbnailBitmap = new SizedThumbnailBitmap();
		Bitmap bitmap = null;
		// MediaFileType fileType = MediaFile.getFileType(filePath);
		// if (fileType != null && fileType.fileType ==
		// MediaFile.FILE_TYPE_JPEG) {
		if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
			createThumbnailFromEXIF(filePath, targetSize, maxPixels,
					sizedThumbnailBitmap);
			bitmap = sizedThumbnailBitmap.mBitmap;
		}

		if (bitmap == null) {
			try {
				FileDescriptor fd = new FileInputStream(filePath).getFD();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.inJustDecodeBounds = true;

				if (options.mCancel || options.outWidth == -1
						|| options.outHeight == -1) {
					return null;
				}
				BitmapFactory.decodeFile(filePath, options);
				options.inSampleSize = computeSampleSize(options, targetSize,
						maxPixels);
				options.inJustDecodeBounds = false;

				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
				if (bitmap == null) {
					bitmap = BitmapFactory.decodeFile(filePath, options);// .decodeFileDescriptor(fd,
																			// null,
																			// options);
				}

			} catch (IOException ex) {

			}
		}

//		if (kind == Images.Thumbnails.MICRO_KIND) {
//			// now we make it a "square thumbnail" for MICRO_KIND thumbnail
//			bitmap = extractThumbnail(bitmap, TARGET_SIZE_MICRO_THUMBNAIL,
//					TARGET_SIZE_MICRO_THUMBNAIL, OPTIONS_RECYCLE_INPUT);
//		}
		return bitmap;
	}

	// /**
	// * Create a video thumbnail for a video. May return null if the video is
	// * corrupt or the format is not supported.
	// *
	// * @param filePath the path of video file
	// * @param kind could be MINI_KIND or MICRO_KIND
	// */
	// public static Bitmap createVideoThumbnail(String filePath, int kind) {
	// Bitmap bitmap = null;
	// MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	// try {
	// retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
	// retriever.setDataSource(filePath);
	// bitmap = retriever.captureFrame();
	// } catch (IllegalArgumentException ex) {
	// // Assume this is a corrupt video file
	// } catch (RuntimeException ex) {
	// // Assume this is a corrupt video file.
	// } finally {
	// try {
	// retriever.release();
	// } catch (RuntimeException ex) {
	// // Ignore failures while cleaning up.
	// }
	// }
	// if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
	// bitmap = extractThumbnail(bitmap,
	// TARGET_SIZE_MICRO_THUMBNAIL,
	// TARGET_SIZE_MICRO_THUMBNAIL,
	// OPTIONS_RECYCLE_INPUT);
	// }
	// return bitmap;
	// }

	/**
	 * Creates a centered bitmap of the desired size.
	 * 
	 * @param source
	 *            original bitmap source
	 * @param width
	 *            targeted width
	 * @param height
	 *            targeted height
	 */
	public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
		return extractThumbnail(source, width, height, OPTIONS_NONE);
	}

	/**
	 * Creates a centered bitmap of the desired size.
	 * 
	 * @param source
	 *            original bitmap source
	 * @param width
	 *            targeted width
	 * @param height
	 *            targeted height
	 * @param options
	 *            options used during thumbnail extraction
	 */
	public static Bitmap extractThumbnail(Bitmap source, int width, int height,
			int options) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap thumbnail = transform(matrix, source, width, height,
				OPTIONS_SCALE_UP | options);
		return thumbnail;
	}

	/*
	 * Compute the sample size as a function of minSideLength and
	 * maxNumOfPixels. minSideLength is used to specify that minimal width or
	 * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
	 * pixels that is tolerable in terms of memory usage.
	 * 
	 * The function returns a sample size based on the constraints. Both size
	 * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
	 * indicates no care of the corresponding constraint. The functions prefers
	 * returning a sample size that generates a smaller bitmap, unless
	 * minSideLength = IImage.UNCONSTRAINED.
	 * 
	 * Also, the function rounds up the sample size to a power of 2 or multiple
	 * of 8 because BitmapFactory only honors sample size this way. For example,
	 * BitmapFactory downsamples an image by 2 even though the request is 3. So
	 * we round up the sample size to avoid OOM.
	 */
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		
		if(roundedSize/2 <1){
			roundedSize = 1;
		}else{
			roundedSize = roundedSize/2;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
				.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == UNCONSTRAINED)
				&& (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * Transform source Bitmap to targeted width and height.
	 */
	private static Bitmap transform(Matrix scaler, Bitmap source,
			int targetWidth, int targetHeight, int options) {
		boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
		boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
					+ Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
					- dstY);
			c.drawBitmap(source, src, dst, null);
			if (recycle) {
				source.recycle();
			}
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1 = null;
		Bitmap b2 = null;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			try {
				b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
						source.getHeight(), scaler, true);
			} catch (Throwable e) {
				System.out.println("OutOfMemoryError"+ e.getStackTrace());
			}
		} else {
			b1 = source;
		}

		if (b1 != null) {
			if (recycle && b1 != source) {
				source.recycle();
			}

			int dx1 = Math.max(0, b1.getWidth() - targetWidth);
			int dy1 = Math.max(0, b1.getHeight() - targetHeight);

			b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
					targetHeight);

			if (b2 != b1) {
				if (recycle || b1 != source) {
					b1.recycle();
				}
			}
		}

		return b2;
	}

	/**
	 * SizedThumbnailBitmap contains the bitmap, which is downsampled either
	 * from the thumbnail in exif or the full image. mThumbnailData,
	 * mThumbnailWidth and mThumbnailHeight are set together only if mThumbnail
	 * is not null.
	 * 
	 * The width/height of the sized bitmap may be different from
	 * mThumbnailWidth/mThumbnailHeight.
	 */
	private static class SizedThumbnailBitmap {
		public byte[] mThumbnailData;
		public Bitmap mBitmap;
		public int mThumbnailWidth;
		public int mThumbnailHeight;
	}

	/**
	 * Creates a bitmap by either downsampling from the thumbnail in EXIF or the
	 * full image. The functions returns a SizedThumbnailBitmap, which contains
	 * a downsampled bitmap and the thumbnail data in EXIF if exists.
	 */
	private static void createThumbnailFromEXIF(String filePath,
			int targetSize, int maxPixels, SizedThumbnailBitmap sizedThumbBitmap) {
		if (filePath == null)
			return;

		ExifInterface exif = null;
		byte[] thumbData = null;
		try {
			exif = new ExifInterface(filePath);
			if (exif != null) {
				thumbData = exif.getThumbnail();
			}
		} catch (IOException ex) {
			// Log.w(TAG, ex);
		}

		BitmapFactory.Options fullOptions = new BitmapFactory.Options();
		BitmapFactory.Options exifOptions = new BitmapFactory.Options();
		int exifThumbWidth = 0;
		int fullThumbWidth = 0;

		// Compute exifThumbWidth.
		if (thumbData != null) {
			exifOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length,
					exifOptions);
			exifOptions.inSampleSize = computeSampleSize(exifOptions,
					targetSize, maxPixels);
			exifThumbWidth = exifOptions.outWidth / exifOptions.inSampleSize;
		}

		// Compute fullThumbWidth.
		fullOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, fullOptions);
		fullOptions.inSampleSize = computeSampleSize(fullOptions, targetSize,
				maxPixels);
		fullThumbWidth = fullOptions.outWidth / fullOptions.inSampleSize;

		// Choose the larger thumbnail as the returning sizedThumbBitmap.
		if (thumbData != null && exifThumbWidth >= fullThumbWidth) {
			int width = exifOptions.outWidth;
			int height = exifOptions.outHeight;
			exifOptions.inJustDecodeBounds = false;
			sizedThumbBitmap.mBitmap = BitmapFactory.decodeByteArray(thumbData,
					0, thumbData.length, exifOptions);
			if (sizedThumbBitmap.mBitmap != null) {
				sizedThumbBitmap.mThumbnailData = thumbData;
				sizedThumbBitmap.mThumbnailWidth = width;
				sizedThumbBitmap.mThumbnailHeight = height;
			}
		} else {
			fullOptions.inJustDecodeBounds = false;
			sizedThumbBitmap.mBitmap = BitmapFactory.decodeFile(filePath,
					fullOptions);
		}
	}

	public static boolean createImageThumbnail(String dstFile, String srcFile,
			int kind, boolean isThumb) {
		FileOutputStream fos = null;
		try {
			Bitmap bitmap = ThumbnailUtils.createImageThumbnail(srcFile, kind,
					isThumb);
			if (bitmap == null) {
				return false;
			}
			File destF = new File(dstFile);
			File destPF = destF.getParentFile();
			if (!destPF.exists()) {
				destPF.mkdirs();
			}
			fos = new FileOutputStream(dstFile);
			byte[] b;
			if(isThumb){
				b = compressImage(bitmap, 40);
			}else{
				b = compressImage(bitmap, 100);
			}
			
			if (b != null) {
				fos.write(b);
				fos.flush();
			}
			return true;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
			}
		}
	}

	private static byte[] compressImage(Bitmap image, int size) {
		byte[] b = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 90;
			while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				if (options > 10) {
					options -= 10;
				} else if (options > 1) {
					options -= 1;
				} else {
					break;
				}
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			}
			b = baos.toByteArray();
		} catch (Exception e) {

		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (Exception e) {
			}
			if(image!=null && !image.isRecycled()){
				image.recycle();
				image = null;
			}
		}
		return b;
	}
	
}