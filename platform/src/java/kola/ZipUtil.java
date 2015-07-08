package kola;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.ArrayList;
import java.util.List;

/*
Taken from https://www.securecoding.cert.org/confluence/display/java/IDS04-J.+Safely+extract+files+from+ZipInputStream
*/
public class ZipUtil {
	private static final int BUFFER = 512;
	private static final int TOOBIG = 0x6400000; // Max size of unzipped data, 100MB
	private static final int TOOMANY = 1024;     // Max number of files

	public static final void unzip(ZipInputStream zis, File toDir) throws IOException {
		if (toDir.exists() && !toDir.isDirectory()) {
			throw new IOException("Unzip target '" + toDir.getCanonicalPath() + "' exists but is not a directory");
		}
		ZipEntry entry;
		int entries = 0;
		long total = 0;
		try {
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					System.out.println("Extracting: " + entry);
					int count;
					byte data[] = new byte[BUFFER];
					// Write the files to the disk, but ensure that the filename is valid,
					// and that the file is not insanely big
					File file = validateFilename(new File(toDir, entry.getName()), toDir);
					File parent = file.getParentFile();
					if (!parent.exists()) {
						System.out.println("Creating directory " + parent.getName());
						parent.mkdirs();
					}
					if (file.exists()) {
						System.out.println("--- OVERWRITING FILE " + file.getName());
					}
					BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(file), BUFFER);
					while (total + BUFFER <= TOOBIG && (count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
						total += count;
					}
					dest.flush();
					dest.close();
					zis.closeEntry();
					entries++;
					if (entries > TOOMANY) {
						throw new IllegalStateException("Too many files to unzip.");
					}
					if (total > TOOBIG) {
						throw new IllegalStateException("File being unzipped is too big.");
					}
				}
			}
		} finally {
			zis.close();
		}
	}

	public static String[] getFilenames(ZipInputStream zis, boolean includeDirs) throws IOException {
		List<String> result = new ArrayList<String>();
		ZipEntry entry;
		try {
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory() || includeDirs) {
					result.add(entry.getName());
				}
			}
		} finally {
			zis.close();
		}
		return result.toArray(new String[result.size()]);
	}

	private static File validateFilename(File file, File intendedDir) throws IOException {
		String canonicalPath = file.getCanonicalPath();
		String canonicalID = intendedDir.getCanonicalPath();

		if (canonicalPath.startsWith(canonicalID)) {
			return file.getAbsoluteFile();
		} else {
			throw new IllegalStateException("File is outside extraction target directory.");
		}
	}
}