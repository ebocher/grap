/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.model;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.io.CachedImagePlusProvider;
import org.grap.io.DirectImagePlusProvider;
import org.grap.io.FileReader;
import org.grap.io.FileReaderFactory;
import org.grap.io.GeoreferencingException;
import org.grap.io.ImagePlusProvider;
import org.grap.io.WorldFile;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.utilities.EnvelopeUtil;
import org.grap.utilities.JTSConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * A GeoRaster object is composed of an ImageJ ImagePlus object and some spatial
 * fields such as : a projection system, an envelop, a pixel size...
 */
class DefaultGeoRaster implements GeoRaster {

	private RasterMetadata rasterMetadata;

	private FileReader fileReader;

	private ImagePlusProvider imagePlusProvider;

	private float noDataValue = Float.NaN;

	// constructors
	DefaultGeoRaster(final String fileName) throws FileNotFoundException,
			IOException {
		this(fileName, GeoProcessorType.FLOAT);
	}

	DefaultGeoRaster(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		fileReader = FileReaderFactory.create(fileName, geoProcessorType);
		imagePlusProvider = new DirectImagePlusProvider(fileReader);
	}

	DefaultGeoRaster(final ImagePlus impResult, final RasterMetadata metadata) {
		this.imagePlusProvider = new CachedImagePlusProvider(impResult);
		this.rasterMetadata = metadata;
	}

	// public methods
	public void open() throws GeoreferencingException, IOException {
		if (null != fileReader) {
			rasterMetadata = fileReader.readRasterMetadata();
		} else {
			// Ignore open for results in memory
		}
	}

	public RasterMetadata getMetadata() {
		return rasterMetadata;
	}

	public void setRange(final double min, final double max) {
		// imagePlus.getProcessor().setThreshold(min, max,
		// ImageProcessor.RED_LUT);
		// WindowManager.setTempCurrentImage(imagePlus);
		// IJ.run("NaN Background");
	}

	public void setNodataValue(final float value) {
		this.noDataValue = value;
	}

	public Point2D pixelToWorldCoord(final int xpixel, final int ypixel) {
		return rasterMetadata.toWorld(xpixel, ypixel);
	}

	public Point2D getPixelCoords(final double mouseX, final double mouseY) {
		return rasterMetadata.toPixel(mouseX, mouseY);
	}

	public void save(final String dest) throws IOException {
		final int dotIndex = dest.lastIndexOf('.');
		final String localFileNamePrefix = dest.substring(0, dotIndex);
		final String localFileNameExtension = dest.substring(dotIndex + 1);
		final FileSaver fileSaver = new FileSaver(imagePlusProvider
				.getImagePlus());

		final String tmp = localFileNameExtension.toLowerCase();
		if (tmp.endsWith("tif") || (tmp.endsWith("tiff"))) {
			fileSaver.saveAsTiff(dest);
			WorldFile.save(localFileNamePrefix + ".tfw", rasterMetadata);
		} else if (tmp.endsWith("png")) {
			fileSaver.saveAsPng(dest);
			WorldFile.save(localFileNamePrefix + ".pgw", rasterMetadata);
		} else if (tmp.endsWith("jpg") || (tmp.endsWith("jpeg"))) {
			fileSaver.saveAsJpeg(dest);
			WorldFile.save(localFileNamePrefix + ".jgw", rasterMetadata);
		} else if (tmp.endsWith("gif")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(localFileNamePrefix + ".gfw", rasterMetadata);
		} else if (tmp.endsWith("bmp")) {
			fileSaver.saveAsGif(dest);
			WorldFile.save(localFileNamePrefix + ".bpw", rasterMetadata);
		} else {
			throw new RuntimeException("Unknown file name extension : "
					+ localFileNameExtension);
		}
	}

	public void show() throws IOException {
		imagePlusProvider.getImagePlus().show();
	}

	public void setLUT(final ColorModel colorModel) throws IOException {
		imagePlusProvider.setLUT(colorModel);
	}

	public GeoRaster doOperation(final Operation operation)
			throws OperationException {
		return operation.execute(this);
	}

	public int getType() throws IOException {
		return imagePlusProvider.getType();
	}

	public boolean isEmpty() {
		return false;
	}

	public GeoRaster convolve(float[] kernel, int kernelWidth, int kernelHeight)
			throws OperationException {
		try {
			final ImageProcessor dup = imagePlusProvider.getImagePlus()
					.getProcessor().duplicate();
			dup.convolve(kernel, kernelWidth, kernelHeight);
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public GeoRaster convolve3x3(int[] kernel) throws OperationException {
		try {
			final ImageProcessor dup = imagePlusProvider.getImagePlus()
					.getProcessor().duplicate();
			dup.convolve3x3(kernel);
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private GeoRaster createGeoRaster(ImageProcessor dup,
			RasterMetadata rasterMetadata) throws IOException {
		final int width = dup.getWidth();
		final int height = dup.getHeight();
		final ColorModel cm = dup.getColorModel();
		switch (getType()) {
		case ImagePlus.GRAY8:
		case ImagePlus.COLOR_256:
			final byte[] bytePixels = (byte[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(bytePixels, width, height,
					cm, rasterMetadata);
		case ImagePlus.GRAY16:
			final short[] shortPixels = (short[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(shortPixels, width, height,
					cm, rasterMetadata);
		case ImagePlus.GRAY32:
		case ImagePlus.COLOR_RGB:
			final float[] floatPixels = (float[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(floatPixels, width, height,
					cm, rasterMetadata);
		default:
			throw new IllegalStateException("Unknown type: " + getType());
		}
	}

	public GeoRaster crop(final LinearRing ring) throws OperationException {
		try {
			final Geometry geomEnvelope = new GeometryFactory().createPolygon(
					(LinearRing) EnvelopeUtil.toGeometry(rasterMetadata
							.getEnvelope()), null);

			if (geomEnvelope.intersects(ring)) {
				final PolygonRoi roi = JTSConverter.toPolygonRoi(toPixel(ring));

				final ImageProcessor processor = imagePlusProvider
						.getProcessor().duplicate();
				processor.setRoi(roi);
				final ImageProcessor result = processor.crop();
				final Envelope newEnvelope = geomEnvelope.intersection(ring)
						.getEnvelopeInternal();
				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();
				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, rasterMetadata.getPixelSize_X(),
						rasterMetadata.getPixelSize_Y(), result.getWidth(),
						result.getHeight(), rasterMetadata.getRotation_X(),
						rasterMetadata.getRotation_Y());

				return createGeoRaster(result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private LinearRing toPixel(LinearRing ring) {
		final Coordinate[] coords = ring.getCoordinates();
		final Coordinate[] transformedCoords = new Coordinate[coords.length];
		for (int i = 0; i < transformedCoords.length; i++) {
			final Point2D p = rasterMetadata.toPixel(coords[i].x, coords[i].y);
			transformedCoords[i] = new Coordinate(p.getX(), p.getY());
		}

		return new GeometryFactory().createLinearRing(transformedCoords);
	}

	public GeoRaster crop(final Rectangle2D roi) throws OperationException {
		try {
			final Envelope roiEnv = new Envelope(new Coordinate(roi.getMinX(),
					roi.getMinY()),
					new Coordinate(roi.getMaxX(), roi.getMaxY()));
			if (roiEnv.intersects(rasterMetadata.getEnvelope())) {

				final Rectangle2D pixelRoi = getRectangleInPixels(roi);
				final ImageProcessor processor = imagePlusProvider
						.getProcessor().duplicate();
				processor.setRoi((int) pixelRoi.getMinX(), (int) pixelRoi
						.getMinY(), (int) pixelRoi.getWidth(), (int) pixelRoi
						.getHeight());

				final ImageProcessor result = processor.crop();
				final Envelope newEnvelope = new Envelope(new Coordinate(roi
						.getMinX(), roi.getMinY()), new Coordinate(roi
						.getMaxX(), roi.getMaxY()));
				final Point2D coordinates = this.pixelToWorldCoord(
						(int) newEnvelope.getMinX(), (int) newEnvelope
								.getMaxY());
				final double originX = coordinates.getX();
				final double originY = coordinates.getY();
				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, rasterMetadata.getPixelSize_X(),
						rasterMetadata.getPixelSize_Y(), result.getWidth(),
						result.getHeight(), rasterMetadata.getRotation_X(),
						rasterMetadata.getRotation_Y());

				return createGeoRaster(result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private Rectangle2D getRectangleInPixels(Rectangle2D rectangle) {
		final Point2D min = getPixelCoords(rectangle.getMinX(), rectangle
				.getMinY());
		final Point2D max = getPixelCoords(rectangle.getMaxX(), rectangle
				.getMaxY());

		final double minx = Math.min(min.getX(), max.getX());
		final double maxx = Math.max(min.getX(), max.getX());
		final double miny = Math.min(min.getY(), max.getY());
		final double maxy = Math.max(min.getY(), max.getY());

		return new Rectangle((int) minx, (int) miny, (int) (maxx - minx),
				(int) (maxy - miny));
	}

	public GeoRaster erode() throws OperationException {
		try {
			final ImageProcessor dup = imagePlusProvider.getImagePlus()
					.getProcessor().duplicate();
			dup.erode();
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public GeoRaster smoth() throws OperationException {
		try {
			final ImageProcessor dup = imagePlusProvider.getImagePlus()
					.getProcessor().duplicate();
			dup.smooth();
			return createGeoRaster(dup, rasterMetadata.duplicate());
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	public double getMax() throws IOException {
		return imagePlusProvider.getMax();
	}

	public double getMin() throws IOException {
		return imagePlusProvider.getMin();
	}

	public int getHeight() throws IOException {
		return imagePlusProvider.getWidth();
	}

	public int getWidth() throws IOException {
		return imagePlusProvider.getHeight();
	}

	public ColorModel getColorModel() throws IOException {
		return imagePlusProvider.getColorModel();
	}

	public PixelProvider getPixelProvider() throws IOException {
		return new DefaultPixelProvider(imagePlusProvider.getImagePlus()
				.getImage(), imagePlusProvider.getProcessor(), getType(),
				noDataValue);
	}
}