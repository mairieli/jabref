/*  Copyright (C) 2003-2011 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref.exporter;

import net.sf.jabref.model.database.BibDatabase;
import net.sf.jabref.MetaData;
import net.sf.jabref.logic.l10n.Localization;
import net.sf.jabref.logic.mods.MODSDatabase;
import net.sf.jabref.model.entry.BibEntry;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.OutputKeys;
import java.util.List;
import java.util.Objects;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.File;

/**
 * ExportFormat for exporting in MODS XML format.
 */
class ModsExportFormat extends ExportFormat {

    public ModsExportFormat() {
        super(Localization.lang("MODS"), "mods", null, null, ".xml");
    }

    @Override
    public void performExport(final BibDatabase database, final MetaData metaData, final String file,
            final Charset encoding, List<BibEntry> entries) throws IOException {
        Objects.requireNonNull(database);
        Objects.requireNonNull(entries);
        if (entries.isEmpty()) { // Only export if entries exist
            return;
        }

        SaveSession ss = new SaveSession(StandardCharsets.UTF_8, false);
        try (VerifyingWriter ps = ss.getWriter()) {
            MODSDatabase md = new MODSDatabase(database, entries);

            try {
                DOMSource source = new DOMSource(md.getDOMrepresentation());
                StreamResult result = new StreamResult(ps);
                Transformer trans = TransformerFactory.newInstance().newTransformer();
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                trans.transform(source, result);
            } catch (TransformerException | IllegalArgumentException | TransformerFactoryConfigurationError e) {
                throw new Error(e);
            }
            finalizeSaveSession(ss, new File(file));
        } catch (SaveException | IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}
