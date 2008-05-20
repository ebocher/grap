/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.grap.processing.operation.hydrology;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.D8OpAllOutlets;
import org.grap.processing.operation.hydrology.D8OpAllWatersheds;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpWatershedsWithThreshold;

public class WatershedWithThresholdTest extends GrapTest {
	private GeoRaster geoRasterSrc;

	protected void setUp() throws Exception {
		super.setUp();
		geoRasterSrc = sampleDEM;
	}

	public void testWatershedFromOutletIndex() throws Exception {
		// load the DEM
		geoRasterSrc.open();

		// compute the slopes directions
		final Operation slopesDirections = new D8OpDirection();
		final GeoRaster grSlopesDirections = geoRasterSrc
				.doOperation(slopesDirections);

		// compute the slopes accumulations
		final Operation slopesAccumulations = new D8OpAccumulation();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);

		// find all the outlets
		final Operation allOutlets = new D8OpAllOutlets();
		final GeoRaster grAllOutlets = grSlopesDirections
				.doOperation(allOutlets);

		// compute all the watersheds
		final Operation allWatersheds = new D8OpAllWatersheds();
		final GeoRaster grAllWatersheds = grSlopesDirections
				.doOperation(allWatersheds);

		// extract some "big" watersheds
		int threshold = 49;
		Operation watershedsWithThreshold = new D8OpWatershedsWithThreshold(
				grAllWatersheds, grAllOutlets, threshold);
		GeoRaster grWatershedsWithThreshold = grSlopesAccumulations
				.doOperation(watershedsWithThreshold);

		// compare the computed watersheds with previous ones
		printGeoRasterAndArray(grWatershedsWithThreshold,
				otherAllWatershedsForDEM);
		compareGeoRasterAndArray(grWatershedsWithThreshold,
				otherAllWatershedsForDEM);

		// extract some "big" watersheds
		threshold = 50;
		watershedsWithThreshold = new D8OpWatershedsWithThreshold(grAllWatersheds,
				grAllOutlets, threshold);
		grWatershedsWithThreshold = grSlopesAccumulations
				.doOperation(watershedsWithThreshold);

		for (int r = 0; r < grWatershedsWithThreshold.getHeight(); r++) {
			for (int c = 0; c < grWatershedsWithThreshold.getWidth(); c++) {
				assertTrue(Float.isNaN(grWatershedsWithThreshold
						.getGrapImagePlus().getPixelValue(c, r)));
			}
		}
	}
}