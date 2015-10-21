package kola

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;

class ImageService {
	def grailsApplication

	def createThumbnailBytes(bytes) {
		return createThumbnailBytes(bytes, grailsApplication.config.kola.thumbnailSize)
	}

	def createThumbnailBytes(bytes, thumbnailSize) {
		def bis = new ByteArrayInputStream(bytes)
		def bos = new ByteArrayOutputStream()
		try {
			def image = ImageIO.read(bis)
			if (!image) {
				return null
			}
			if (image.width != image.height) {
				def minDimension = Math.min(image.width, image.height)
	            //println "--- min dim=$minDimension -> [" + ((int)((image.width - minDimension) / 2)) + ", " + ((int)((image.height - minDimension) / 2)) + ", $minDimension, $minDimension]"
	            image = image.getSubimage((int)((image.width - minDimension) / 2), (int)((image.height - minDimension) / 2), minDimension, minDimension)
	        }
	        BufferedImage resized = new BufferedImage(thumbnailSize, thumbnailSize, image.getType());
	        Graphics2D g = resized.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.drawImage(image, 0, 0, thumbnailSize, thumbnailSize, 0, 0, image.getWidth(), image.getHeight(), null);
	        g.dispose();

	        ImageIO.write(resized, "png", bos)
	        return bos.toByteArray()
	    }
	    finally {
	    	bis.close()
	    	bos.close()
	    }
	}

	def removeExifGPS(final byte[] imageData) throws Exception {
		def outputSet = Imaging.getMetadata(imageData)?.exif?.outputSet
		def gpsDirectory = outputSet?.GPSDirectory
        if (gpsDirectory) {
        	println "--- REMOVING GPS DATA FROM IMAGE"
        	GpsTagConstants.ALL_GPS_TAGS.each {
	 			gpsDirectory.removeField(it)
        	}
 			def bos = new ByteArrayOutputStream(imageData.length)
 			new ExifRewriter().updateExifMetadataLossless(imageData, bos, outputSet);
 			return bos.toByteArray()
		}
		else {
        	println "--- image has no gps coordinates"
			return imageData
		}
	}
}
