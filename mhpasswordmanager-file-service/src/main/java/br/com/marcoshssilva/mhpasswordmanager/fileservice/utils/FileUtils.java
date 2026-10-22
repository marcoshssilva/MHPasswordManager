package br.com.marcoshssilva.mhpasswordmanager.fileservice.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtils {
    private FileUtils() {}
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Creates a secure temporary file with permissions restricted only to the owner.
     *
     * @param prefix Temporary file prefix
     * @param suffix Temporary file suffix
     * @return The securely created File object
     * @throws IOException If any error occurs during creation or setting permissions
     */
    public static Path createSecureTempFile(String prefix, String suffix) throws IOException {
        if (SystemUtils.IS_OS_UNIX) {
            // In Unix environments (Linux/macOS), restricts POSIX permissions to rwx for the owner only
            FileAttribute<Set<java.nio.file.attribute.PosixFilePermission>> attr =
                    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
            return Files.createTempFile(prefix, suffix, attr);
        } else {
            // On Windows or other systems, creates the file and explicitly restricts it to the owner
            File file = Files.createTempFile(prefix, suffix).toFile();

            // The second parameter 'true' ensures the permission applies only to the owner (ownerOnly)
            boolean readable = file.setReadable(true, true);
            boolean writable = file.setWritable(true, true);
            boolean executable = file.setExecutable(true, true);

            if (!readable && !writable && !executable) {
                LOG.warn("Failure when trying to define permissions to file {}. Please check permissions.", file.getAbsolutePath());
            }

            return file.toPath();
        }
    }
}