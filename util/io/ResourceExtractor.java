package util.io;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceExtractor {
    private ResourceExtractor() {
        // Prevent instantiation
    }
    /**
     * Copies all resources from the specified resource directory in the classpath to the target directory on the filesystem.
     * It handles both cases where the resources are inside a JAR file or directly in the filesystem (e.g., during development).
     *
     * @param targetDir The target directory on the filesystem where resources should be copied.
     * @throws IOException If an I/O error occurs during copying.
     */
    public static void copyResources(String targetDir) throws IOException {
        File target = new File(targetDir);
        if (!target.exists()) {
            target.mkdirs();
        }

        // Figure out if we're running from a JAR or from filesystem
        URL resource = Thread.currentThread().getContextClassLoader().getResource(targetDir.endsWith("/") ? targetDir.substring(0, targetDir.length() - 1) : targetDir);
        if (resource == null) {
            throw new FileNotFoundException("Resource directory 'python' not found in classpath.");
        }

        if (resource.getProtocol().equals("jar")) {
            // Running from inside a JAR
            String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
            try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.isDirectory()) continue;
                    if (!entry.getName().startsWith(targetDir)) continue;
                    if (entry.getName().endsWith(".java") || entry.getName().endsWith(".class")) continue;

                    InputStream in = jar.getInputStream(entry);
                    Path outFile = target.toPath().resolve(entry.getName().substring(targetDir.length())).normalize();
                    // Zip Slip prevention: ensure outFile is within target directory
                    if (!outFile.startsWith(target.toPath().normalize())) {
                        throw new IOException("Bad zip entry: " + entry.getName());
                    }
                    Files.createDirectories(outFile.getParent());
                    Files.copy(in, outFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } else {
            // Running from filesystem
            try {
                Path sourcePath = Paths.get(resource.toURI());
                Files.walk(sourcePath).forEach(path -> {
                    try {
                        if (Files.isDirectory(path)) return;
                        String fileName = path.getFileName().toString();
                        if (fileName.endsWith(".java") || fileName.endsWith(".class")) return;

                        Path dest = target.toPath().resolve(sourcePath.relativize(path).toString());
                        Files.createDirectories(dest.getParent());
                        Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (URISyntaxException e) {
                throw new IOException("Failed to resolve resource path.", e);
            }
        }
    }
}
