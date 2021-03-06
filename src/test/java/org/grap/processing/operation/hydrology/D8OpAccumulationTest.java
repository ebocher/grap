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
package org.grap.processing.operation.hydrology;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

@Ignore("These tests shall disappear with the hydrology code in grap: they will be implemented on top of JAI.")
public class D8OpAccumulationTest extends D8Commons {
        @Test
	public void testExecute() throws Exception {
		assertTrue(test("hydrology/TauDEM/d8direction_1.asc","hydrology/TauDEM/d8accumulation_1.asc"));
		assertTrue(test("hydrology/TauDEM/d8direction.asc","hydrology/TauDEM/d8accumulation.asc"));
	}

	private boolean test(String inFile, String refFile) throws Exception {
		GeoRaster direction = GeoRasterFactory.createGeoRaster(GrapTest.internalData + inFile);
		direction.open();
		Operation d8OpAccumulation = new D8OpAccumulation();
		GeoRaster accumulationCalc = direction.doOperation(d8OpAccumulation);

		// compare to the reference
		GeoRaster accumulationRef = GeoRasterFactory.createGeoRaster(GrapTest.internalData + refFile);
		return equals(accumulationRef, accumulationCalc, true);
	}
}