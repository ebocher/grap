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
package org.grap.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.grap.model.GeoProcessorType;

public class FileReaderFactory {
	private static Set<String> worldFileExtensions;
	static {
		worldFileExtensions = new HashSet<String>();
		worldFileExtensions.add("tif");
		worldFileExtensions.add("tiff");
		worldFileExtensions.add("jpg");
		worldFileExtensions.add("jpeg");
		worldFileExtensions.add("gif");
		worldFileExtensions.add("bmp");
		worldFileExtensions.add("png");
	}

	public static FileReader create(final String fileName)
			throws FileNotFoundException, IOException {
		return create(fileName, GeoProcessorType.FLOAT);
	}

	public static FileReader create(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		final String fileNameExtension = getFileNameExtension(fileName);

		if (fileNameExtension.startsWith("asc")) {
			return new EsriGRIDReader(fileName, geoProcessorType);
		} else if (worldFileExtensions.contains(fileNameExtension)) {
			return new WorldImageReader(fileName);
		} else if (fileNameExtension.endsWith("xyz")) {
			throw new RuntimeException("need to be implemented");
		} else {
			throw new RuntimeException("Unknown filename extension !");
		}
	}

	private static String getFileNameExtension(final String fileName) {
		final int dotIndex = fileName.lastIndexOf('.');
		return fileName.substring(dotIndex + 1).toLowerCase();
	}
}