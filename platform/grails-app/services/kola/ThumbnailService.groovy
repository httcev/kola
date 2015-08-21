package kola

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints

class ThumbnailService {
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
}
