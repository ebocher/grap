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

import java.io.IOException;
import java.util.Stack;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.ProgressMonitor;

public class D8OpDistanceToTheOutlet extends D8OpAbstract implements Operation {
	public final static float notProcessedYet = 0;

	private HydrologyUtilities hydrologyUtilities;
	private float[] d8Distances;
	private int ncols;
	private int nrows;

	@Override
	public GeoRaster evaluateResult(GeoRaster direction, ProgressMonitor pm)
			throws OperationException {
		try {
			hydrologyUtilities = new HydrologyUtilities(direction);

			final RasterMetadata rasterMetadata = direction.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			calculateDistances(pm);
			final GeoRaster grDistancesToTheOutlet = GeoRasterFactory
					.createGeoRaster(d8Distances, rasterMetadata);
			grDistancesToTheOutlet.setNodataValue(hydrologyUtilities.ndv);
			return grDistancesToTheOutlet;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private void calculateDistances(ProgressMonitor pm) throws IOException {
		// distances' array initialization
		d8Distances = new float[nrows * ncols];

		for (int y = 0, i = 0; y < nrows; y++) {

			if (y / 100 == y / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * y / nrows));
				}
			}

			for (int x = 0; x < ncols; x++, i++) {
				if (hydrologyUtilities.isABorder(x, y)
						|| Float.isNaN(hydrologyUtilities.getPixelValue(x, y))) {
					d8Distances[i] = hydrologyUtilities.ndv;
				} else if (notProcessedYet == d8Distances[i]) {
					// current cell value has not been yet modified...
					final Stack<HydroCell> path = new Stack<HydroCell>();
					HydroCell top = hydrologyUtilities.shortHydrologicalPath(i,
							path, d8Distances, 1);

					float accumulDist = (null == top) ? 0 : top.dist;
					while (!path.empty()) {
						HydroCell cell = path.pop();
						accumulDist += cell.dist;
						d8Distances[cell.index] = accumulDist;
					}
				}
			}
		}
	}
}
