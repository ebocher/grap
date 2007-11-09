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
package org.grap.processing.hydrology;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.PixelProvider;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class AllOutlets implements Operation {
	public final static byte tmpNoDataValue = (byte) 0xff;
	public final static byte noDataValue = 0;
	public final static byte isAnOutletValue = 1;

	private PixelProvider ppSlopesDirections;
	private byte[] outlets;
	private int ncols;
	private int nrows;

	public GeoRaster execute(final GeoRaster grSlopesDirections)
			throws OperationException {
		try {
			final long startTime = System.currentTimeMillis();
			ppSlopesDirections = grSlopesDirections.getPixelProvider();
			final RasterMetadata rasterMetadata = grSlopesDirections
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfOutlets = computeAllOutlets();
			final GeoRaster grAllOutlets = GeoRasterFactory.createGeoRaster(
					outlets, ncols, nrows, rasterMetadata);
			grAllOutlets.setNodataValue(noDataValue);
			System.out.printf("%d outlets in %d ms\n", nbOfOutlets, System
					.currentTimeMillis()
					- startTime);
			return grAllOutlets;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeAllOutlets() throws IOException {
		outlets = new byte[nrows * ncols];
		int nbOfOutlets = 0;
		int i = 0;

		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if (Float.isNaN(ppSlopesDirections.getPixel(c, r))) {
					outlets[i] = tmpNoDataValue;
				} else if (0 == outlets[i]) {
					// current cell value has not been yet modified...
					nbOfOutlets += findOutlet(i);
				}
			}
		}

		for (int idx = 0; idx < outlets.length; idx++) {
			if (isAnOutletValue == outlets[idx]) {
				// System.err.println(idx);
			} else {
				outlets[idx] = noDataValue;
			}
		}
		return nbOfOutlets;
	}

	private int findOutlet(final int i) throws IOException {
		Integer curCellIdx = i;
		Integer prevCellIdx = i;

		do {
			final int r = curCellIdx / ncols;
			final int c = curCellIdx % ncols;

			if (Float.isNaN(ppSlopesDirections.getPixel(c, r))) {
				// previous cell is a new outlet...
				outlets[prevCellIdx] = isAnOutletValue;
				outlets[curCellIdx] = tmpNoDataValue;
				return 1;
			} else {
				if (0 == outlets[curCellIdx]) {
					// current cell value has not been yet modified...
					// current cell is now tagged as already visited
					outlets[curCellIdx] = tmpNoDataValue;
					prevCellIdx = curCellIdx;
					curCellIdx = SlopesComputations
							.fromCellSlopeDirectionToNextCellIndex(
									ppSlopesDirections, ncols, nrows,
									curCellIdx, c, r);
					if (null == curCellIdx) {
						// previous cell is a new outlet...
						outlets[prevCellIdx] = isAnOutletValue;
						return 1;
					} else {
						// join an already identified river...
						return 0;
					}
				} else {
					return 0;
				}
			}
		} while (true);
	}
}