/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.grap.archive;

import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import com.vividsolutions.jts.io.ParseException;

public class NodataValueRangeTest {

	/**
	 * @param args
	 * @throws NoninvertibleTransformException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		// String fileName = "..//datas2tests//geotif//440607.tif";

		String fileName = "..//datas2tests//grid//sample.asc";

		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(fileName);
		geoRaster.open();

		System.out.println(geoRaster.getMin());

		geoRaster.setRange(0, 1000);

		System.out.println(geoRaster.getMin());

		geoRaster.show();

		geoRaster.save("..//datas2tests//geotif//sample.png");

	}

}
