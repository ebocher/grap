/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.grap.processing.operation;

import org.junit.Test;
import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

import static org.junit.Assert.*;

public class GeoRasterCalculatortest extends GrapTest {

        @Test
	public void testSubtract() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/smallsample.asc");
		geoRasterSrc.open();

		final Operation subtract = new GeoRasterCalculator(geoRasterSrc,
				GeoRasterCalculator.SUBSTRACT);
		final GeoRaster gResult = geoRasterSrc.doOperation(subtract);

		float[] pixels = gResult.getFloatPixels();

		for (float pixel : pixels) {
			assertTrue((0 == pixel) || (Float.isNaN(pixel)));
		}
	}
}