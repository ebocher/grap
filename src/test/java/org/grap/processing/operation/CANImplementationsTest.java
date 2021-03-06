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
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.grap.processing.cellularAutomata.cam.ICAShort;
import org.grap.processing.cellularAutomata.parallelImpl.PCAN;
import org.grap.processing.cellularAutomata.seqImpl.SCAN;
import org.grap.processing.cellularAutomata.useless.CAGetAllSubWatershed;
import org.grap.processing.cellularAutomata.useless.CASlopesAccumulation;
import org.grap.processing.cellularAutomata.useless.CASlopesDirections;

import static org.junit.Assert.*;

public class CANImplementationsTest extends GrapTest {
	private short[] slopesDirections = null;

        @Test
	public void testSeqAndParImplementations() throws Exception {
		// load the DEM
		// geoRasterSrc.open();
		// final float[] DEM = geoRasterSrc.getPixelProvider().getProcessor().getFloatPixels();
		// final int nrows = geoRasterSrc.getHeight();
		// final int ncols = geoRasterSrc.getWidth();

		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		compareParAndSeqImpl(new CASlopesDirections(DEM, nrows, ncols));
		// TODO
		// compareParAndSeqImpl(new CASlope(DEM, nrows, ncols));
		compareParAndSeqImpl(new CASlopesAccumulation(slopesDirections, nrows,
				ncols));
		compareParAndSeqImpl(new CAGetAllSubWatershed(slopesDirections, nrows,
				ncols));
	}

	private void compareParAndSeqImpl(final ICA ca) {
		final ICAN scan = new SCAN(ca);
		final ICAN pcan = new PCAN(ca);

		// System.out.println(ca.getClass().getSimpleName());
		final int scanNbOfIter = scan.getStableState();
		final int pcanNbOfIter = pcan.getStableState();
		// TODO assertTrue(scanNbOfIter == pcanNbOfIter);

		if (ca instanceof ICAShort) {
			final short[] seq = (short[]) scan.getCANValues();
			final short[] par = (short[]) pcan.getCANValues();
                        assertEquals(seq.length, par.length);
			for (int i = 0; i < seq.length; i++) {
				assertEquals(seq[i], par[i]);
			}
		} else if (ca instanceof ICAFloat) {
			final float[] seq = (float[]) scan.getCANValues();
			final float[] par = (float[]) pcan.getCANValues();
			assertEquals(seq.length, par.length);
			for (int i = 0; i < seq.length; i++) {
				assertEquals(seq[i], par[i], 10e-5);
			}
		} else {
			fail();
		}

		if (ca instanceof CASlopesDirections) {
			slopesDirections = (short[]) scan.getCANValues();
		}
	}
}