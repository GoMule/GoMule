/*******************************************************************************
 *
 * Copyright 2007 Randall
 *
 * This file is part of gomule.
 *
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *
 ******************************************************************************/
package randall.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco
 * @version 1.0
 */
public class RandallFileFilter extends FileFilter {

    private final List<String> filteredExtensions = new ArrayList<>();
    private final String description;

    public RandallFileFilter(String description) {
        this.description = description;
    }

    public boolean accept(File file) {
        return file.isDirectory() || extensionMatches(file);
    }

    private boolean extensionMatches(File file) {
        for (String extension : filteredExtensions) {
            if (file.getAbsolutePath().toLowerCase().endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void addExtension(String extension) {
        filteredExtensions.add(extension);
    }

    public String getDescription() {
        return description;
    }
}